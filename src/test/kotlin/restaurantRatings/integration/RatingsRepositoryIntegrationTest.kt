package restaurantRatings.integration

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Tag
import org.junit.jupiter.api.Test
import rateMyMeal.restaurantRatings.RatingsRepository
import rateMyMeal.restaurantRatings.RatingsRepository.createFindRatingsByRestaurant
import rateMyMeal.restaurantRatings.domain.Rating
import java.sql.Connection
import kotlin.test.expect

@Tag("integration")
@OptIn(ExperimentalCoroutinesApi::class)
class RatingsRepositoryIntegrationTest {

    private val db = DB()
    private val users = arrayOf(
        User(id = "user1", name = "User 1", trusted = true),
        User(id = "user2", name = "User 2", trusted = false),
        User(id = "user3", name = "User 3", trusted = false),
    )

    private val cafeGloucester = Restaurant(id = "cafegloucesterid", name = "Cafe Gloucester")
    private val redTuna = Restaurant(id = "redTuna", name = "RedTuna")
    private val restaurants = arrayOf(
        redTuna,
        cafeGloucester,
    )

    private val ratingsByUsers = arrayOf(
        RatingByUser("rating1", users[0], cafeGloucester, "EXCELLENT"),
        RatingByUser("rating2", users[1], cafeGloucester, "TERRIBLE"),
        RatingByUser("rating3", users[2], cafeGloucester, "AVERAGE"),
        RatingByUser("rating4", users[2], redTuna, "ABOVE_AVERAGE"),
    )


    @BeforeEach
    fun setup() {
        db.start()
        users.forEach { Users.create(it, db) }
        restaurants.forEach { Restaurants.create(it, db) }
        ratingsByUsers.forEach { Ratings.create(it, db) }
    }

    @AfterEach
    fun shutdown() {
        db.stop()
    }

    @Test
    fun `it loads ratings by restaurant`() = runTest {
        val dependencies = object : RatingsRepository.Dependencies {
            override val getConnection: () -> Connection = { db.getConnection() }
        }
        val findRatings = createFindRatingsByRestaurant(dependencies)
        val ratings = findRatings("vancouverbc").sortedBy { it.restaurantId }

        expect(2) { ratings.size }

        expect(cafeGloucester.id) { ratings[0].restaurantId }
        val cafeGRatings = ratings[0].ratings
        expect(3) { cafeGRatings.size }
        expect(cafeGRatings.map { it.rating }.sortedBy { it.name }) {
            listOf(
                Rating.AVERAGE,
                Rating.EXCELLENT,
                Rating.TERRIBLE
            )
        }

        expect(redTuna.id) { ratings[1].restaurantId }
        val rtRatings = ratings[1].ratings
        expect(1) { rtRatings.size }
        expect(Rating.ABOVE_AVERAGE) { rtRatings[0].rating }
    }
}