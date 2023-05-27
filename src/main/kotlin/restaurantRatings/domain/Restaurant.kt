package restaurantRatings.domain

data class User(val id: String, val trusted: Boolean)
data class Restaurant(val id: String, val name: String)
data class RestaurantRating(val restaurantId: String, val ratedByUser: User)

enum class Rating(val numericValue: Number) {
    EXCELLENT(2),
    ABOVE_AVERAGE(1),
    AVERAGE(0),
    BELOW_AVERAGE(1),
    TERRIBLE(2),
}