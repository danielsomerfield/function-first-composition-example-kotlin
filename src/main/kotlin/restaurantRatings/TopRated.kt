package restaurantRatings

import restaurantRatings.domain.RatingsByRestaurant
import restaurantRatings.domain.Restaurant

object TopRated {

    interface Dependencies {
        suspend fun getRestaurantById(id: String): Restaurant?
        suspend fun findRatingsByRestaurant(city: String): List<RatingsByRestaurant>
        fun calculateRatingForRestaurant(ratings: RatingsByRestaurant): Int
    }


    fun create(dependencies: Dependencies): suspend (city: String) -> List<Restaurant> {
        fun calculateRatings(
            ratingsByRestaurant: List<RatingsByRestaurant>,
            calculateRatingForRestaurant: (ratings: RatingsByRestaurant) -> Int,
        ): List<OverallRating> {
            return ratingsByRestaurant.map { OverallRating(it.restaurantId, calculateRatingForRestaurant(it)) }
        }

        return { city ->
            val ratingsByRestaurant = dependencies.findRatingsByRestaurant(city)
            val overallRatings = calculateRatings(ratingsByRestaurant, dependencies::calculateRatingForRestaurant)
            overallRatings
                .sortedWith { r1, r2 -> r2.rating - r1.rating }
                .mapNotNull { dependencies.getRestaurantById(it.restaurantId) }

        }
    }
}

private data class OverallRating(val restaurantId: String, val rating: Int)
