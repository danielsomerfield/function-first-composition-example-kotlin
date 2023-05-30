package restaurantRatings

import restaurantRatings.domain.RatingsByRestaurant
import restaurantRatings.domain.Restaurant
import java.util.*

object TopRated {

    interface Dependencies {
        val getRestaurantById: suspend (id: String) -> Optional<Restaurant>
        val findRatingsByRestaurant: suspend (city: String) -> List<RatingsByRestaurant>
        val calculateRatingForRestaurant: (ratings: RatingsByRestaurant) -> Int
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
            val overallRatings = calculateRatings(ratingsByRestaurant, dependencies.calculateRatingForRestaurant)
            overallRatings
                .sortedWith { r1, r2 -> r2.rating - r1.rating }
                .mapNotNull { dependencies.getRestaurantById(it.restaurantId) }
                .map { it.get() }


        }
    }
}

private data class OverallRating(val restaurantId: String, val rating: Int)
