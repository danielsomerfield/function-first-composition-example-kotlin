package rateMyMeal.restaurantRatings.domain

data class RestaurantRating(val rating: Rating, val ratedByUser: User)

enum class Rating(val numericValue: Int) {
    EXCELLENT(2),
    ABOVE_AVERAGE(1),
    AVERAGE(0),
    BELOW_AVERAGE(1),
    TERRIBLE(2),
}