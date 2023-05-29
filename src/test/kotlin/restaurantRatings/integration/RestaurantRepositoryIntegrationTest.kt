package restaurantRatings.integration

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Tag
import org.junit.jupiter.api.Test
import restaurantRatings.RestaurantRepository
import restaurantRatings.RestaurantRepository.createGetRestaurantById
import java.sql.Connection
import kotlin.test.expect

@Tag("integration")
@OptIn(ExperimentalCoroutinesApi::class)
class RestaurantRepositoryIntegrationTest {

    private val db = DB()

    private val cafeGloucester = Restaurant(id = "cafegloucesterid", name = "Cafe Gloucester")
    private val redTuna = Restaurant(id = "redTuna", name = "RedTuna")
    private val restaurants = arrayOf(
        redTuna,
        cafeGloucester,
    )

    @BeforeEach
    fun setup() {
        db.start()
        restaurants.forEach { Restaurants.create(it, db) }
    }

    @AfterEach
    fun shutdown() {
        db.stop()
    }

    @Test
    fun `it loads a restaurant by id`() = runTest {
        val dependencies = object : RestaurantRepository.Dependencies {
            override fun getConnection(): Connection = db.getConnection()
            override fun releaseConnection(connection: Connection): Unit = connection.close()
        }
        val maybeRestaurant = createGetRestaurantById(dependencies)(cafeGloucester.id)
        expect(true) { maybeRestaurant.isPresent }
        val restaurant = maybeRestaurant.get()

        expect(cafeGloucester.id) { restaurant.id }
        expect(cafeGloucester.name) { restaurant.name }
    }

    @Test
    fun `it returns empty for non-existent restaurant`() = runTest {
        val dependencies = object : RestaurantRepository.Dependencies {
            override fun getConnection(): Connection = db.getConnection()
            override fun releaseConnection(connection: Connection): Unit = connection.close()
        }
        val maybeRestaurant = createGetRestaurantById(dependencies)("non-existent-restaurant")
        expect(false) { maybeRestaurant.isPresent }
    }

}