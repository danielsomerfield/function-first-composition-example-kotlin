package restaurantRatings.integration

data class User(val id: String, val name: String, val trusted: Boolean)

data class Restaurant(val id: String, val name: String)

data class RatingByUser(val id: String, val user: User, val restaurant: Restaurant, val rating: String)