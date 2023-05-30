package restaurantRatings.integration

import com.github.kittinunf.fuel.Fuel
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import org.junit.jupiter.api.*
import rateMyMeal.Configuration
import rateMyMeal.RatingsDb
import rateMyMeal.Server
import java.util.*
import kotlin.test.expect

@Tag("integration")
class E2EIntegrationTest {

    private val server = Server()
    private val db = DB()
    private val port = Random().nextInt(9000, 9999)

    private val users = arrayOf(
        User(id = "user1", name = "User 1", trusted = true),
        User(id = "user2", name = "User 2", trusted = false),
        User(id = "user3", name = "User 3", trusted = false),
    )

    private val restaurants = arrayOf(
        Restaurant(id = "cafegloucesterid", name = "Cafe Gloucester"),
        Restaurant(id = "burgerkingid", name = "Burger King"),
    )

    private val ratingsByUsers = arrayOf(
        RatingByUser("rating1", users[0], restaurants[0], "EXCELLENT"),
        RatingByUser("rating2", users[1], restaurants[0], "TERRIBLE"),
        RatingByUser("rating3", users[2], restaurants[0], "AVERAGE"),
        RatingByUser("rating4", users[2], restaurants[1], "ABOVE_AVERAGE"),
    )

    @BeforeEach
    fun setup() {
        db.start()

        users.forEach { Users.create(it, db) }
        restaurants.forEach { Restaurants.create(it, db) }
        ratingsByUsers.forEach { Ratings.create(it, db) }

        server.start({
            Configuration(
                port, RatingsDb(
                    user = "postgres",
                    password = "postgres",
                    jdbcUrl = "jdbc:postgresql://localhost:${db.port()}/postgres"
                )
            )
        }
        )
    }

    @AfterEach
    fun teardown() {
        server.stop()
        db.stop()
    }

    @Test
    fun testRestaurantRankings() {
        val (_, response, _) = Fuel.get("http://localhost:${port}/vancouverbc/restaurants/recommended").response()
        expect(200) { response.statusCode }
        val restaurantsResponse = Json.decodeFromString<ResponsePayload>(response.body().asString("application/json"))
        expect(listOf("cafegloucesterid", "burgerkingid")) { restaurantsResponse.restaurants.map { it.id } }
    }
}
