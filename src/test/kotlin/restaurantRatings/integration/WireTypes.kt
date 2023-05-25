package restaurantRatings.integration

import kotlinx.serialization.Serializable

@Serializable
data class ResponsePayload(val restaurants: List<RestaurantPayload>)

@Serializable
data class RestaurantPayload(val id: String, val name: String)