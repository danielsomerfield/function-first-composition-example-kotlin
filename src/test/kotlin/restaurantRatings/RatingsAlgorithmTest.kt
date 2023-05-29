package restaurantRatings

import org.junit.jupiter.api.Test
import restaurantRatings.RatingsAlgorithm.calculateRatingForRestaurant
import restaurantRatings.domain.Rating
import restaurantRatings.domain.RatingsByRestaurant
import restaurantRatings.domain.RestaurantRating
import restaurantRatings.domain.User
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