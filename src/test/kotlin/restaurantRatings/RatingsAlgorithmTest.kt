package restaurantRatings

import org.junit.jupiter.api.Test
import rateMyMeal.restaurantRatings.RatingsAlgorithm.calculateRatingForRestaurant
import rateMyMeal.restaurantRatings.domain.Rating
import rateMyMeal.restaurantRatings.domain.RatingsByRestaurant
import rateMyMeal.restaurantRatings.domain.RestaurantRating
import rateMyMeal.restaurantRatings.domain.User
import kotlin.test.expect

class RatingsAlgorithmTest {

    @Test
    fun `it handles empty strings`() {
        val overallRating = calculateRatingForRestaurant(
            RatingsByRestaurant(
                restaurantId = "restaurant1",
                ratings = listOf(),
            )
        )
        expect(0) { overallRating }
    }

    @Test
    fun `it passes through the rating for an untrusted user`() {
        val overallRating = calculateRatingForRestaurant(
            RatingsByRestaurant(
                restaurantId = "restaurant1",
                ratings = listOf(RestaurantRating(rating = Rating.EXCELLENT, User("user1", false)))
            )
        )
        expect(Rating.EXCELLENT.numericValue) { overallRating }
    }

    @Test
    fun `it provides additional weighting for a trusted user`() {
        val overallRating = calculateRatingForRestaurant(
            RatingsByRestaurant(
                restaurantId = "restaurant1",
                ratings = listOf(RestaurantRating(rating = Rating.EXCELLENT, User("user1", true)))
            )
        )
        expect(Rating.EXCELLENT.numericValue * 4) { overallRating }
    }
}