package restaurantRatings

import restaurantRatings.domain.RatingsByRestaurant

object RatingsAlgorithm {
    fun calculateRatingForRestaurant(ratings: RatingsByRestaurant): Int {
        return ratings.ratings.sumOf { it.rating.numericValue * if (it.ratedByUser.trusted) 4 else 1 }
    }
}