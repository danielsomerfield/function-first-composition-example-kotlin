package restaurantRatings

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Test
import restaurantRatings.domain.*
import restaurantRatings.domain.Restaurant
import kotlin.test.expect

@OptIn(ExperimentalCoroutinesApi::class)
class TopRatedTest {

    private val restaurant1 = Restaurant("1", "restaurant 1")
    private val restaurant2 = Restaurant("2", "restaurant 2")

    private val restaurantsById = mapOf(
        restaurant1.id to restaurant1,
        restaurant2.id to restaurant2
    )

    private val ratings = listOf(
        restaurant1.id to listOf(RestaurantRating(Rating.EXCELLENT, User("user1", true))),
        restaurant2.id to listOf(RestaurantRating(Rating.EXCELLENT, User("user2", false))),
    )

    private val ratingsByCity = listOf("vancouverbc" to ratings)

    @Test
    fun `The top rated restaurant list is calculated from our proprietary ratings algorithm`() = runTest {
        val dependencies = object : TopRated.Dependencies {
            override suspend fun getRestaurantById(id: String): Restaurant? = restaurantsById[id]

            override suspend fun findRatingsByRestaurant(city: String): List<RatingsByRestaurant> {
                return ratingsByCity
                    .filter { it.first == city }
                    .flatMap { it.second }
                    .map { RatingsByRestaurant(it.first, it.second) }
            }

            override fun calculateRatingForRestaurant(ratings: RatingsByRestaurant): Int {
                // I don't know how this is going to work, so I'll use a dumb but predictable stub
                return if (ratings.restaurantId === restaurant1.id) {
                    10
                } else if (ratings.restaurantId == restaurant2.id) {
                    5
                } else {
                    throw RuntimeException("Unknown restaurant");
                }
            }
        }

        val getTopRated = TopRated.create(dependencies)
        val topRestaurants = getTopRated("vancouverbc");
        expect(2) { topRestaurants.size }
        expect(restaurant1.id) { topRestaurants[0].id }
        expect(restaurant1.name) { topRestaurants[0].name }
        expect(restaurant2.id) { topRestaurants[1].id }
        expect(restaurant2.name) { topRestaurants[1].name }
    }
}