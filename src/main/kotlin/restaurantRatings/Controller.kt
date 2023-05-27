package restaurantRatings

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import kotlinx.serialization.Serializable
import restaurantRatings.domain.Restaurant


typealias KTORController = suspend io.ktor.util.pipeline.PipelineContext<Unit, ApplicationCall>.(Unit) -> Unit

object Controller {

    interface Dependencies {
        suspend fun getTopRestaurants(city: String): List<Restaurant>
    }

    fun createTopRatedController(dependencies: Dependencies): KTORController {
        return {
            try {
                val city = this.call.parameters["city"]
                val topRestaurants = dependencies.getTopRestaurants(city!!)
                val wireTypes = topRestaurants.map { RestaurantWireType(it.id, it.name) }
                call.respond<RestaurantResponseWireType>(RestaurantResponseWireType(wireTypes))
            } catch (e: Throwable) {
                call.respond<ErrorResponseWireType>(
                    HttpStatusCode.InternalServerError,
                    ErrorResponseWireType(
                        StatusCode.UNEXPECTED_ERROR,
                        e.message ?: "Unknown error"
                    )
                )
            }
        }
    }

    @Serializable
    data class RestaurantWireType(val id: String, val name: String)

    @Serializable
    data class RestaurantResponseWireType(val restaurants: List<RestaurantWireType>)

    @Serializable
    data class ErrorResponseWireType(val statusCode: StatusCode, val message: String)
}

