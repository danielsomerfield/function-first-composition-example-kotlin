package restaurantRatings.domain

data class RatingsByRestaurant(
    val restaurantId: String,
    val ratings: List<RestaurantRating>
)