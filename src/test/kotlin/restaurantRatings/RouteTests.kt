package restaurantRatings

import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.server.testing.*
import kotlinx.serialization.Serializable
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import org.junit.jupiter.api.Test
import kotlin.test.expect


class RouteTests {

    @Test
    fun `the ratings route provides a JSON response with ratings`() {
        val barAvignon = Restaurant(
            id = "baravignonid",
            name = "Bar Avignon",
        )
        val cafeGloucester = Restaurant(
            id = "cafegloucesterid",
            name = "Cafe Gloucester",
        )
        val sortedVancouverRestaurants = listOf(
            barAvignon,
            cafeGloucester,
        )

        val topRestaurants = listOf("vancouverbc" to sortedVancouverRestaurants)

        val dependenciesStub = object : Controller.Dependencies {
            override suspend fun getTopRestaurants(city: String): List<Restaurant> {
                return topRestaurants.filter { it.first == city }.flatMap { it.second }
            }
        }

        testApplication {
            application {
                restaurantRatings(factories = object : Factories {
                    override val controllerCreate: (Controller.Dependencies) -> KTORController = { _ ->
                        Controller.createTopRatedController(dependenciesStub)
                    }

                })
            }
            val response = client.get("/vancouverbc/restaurants/recommended")
            expect(200) { response.status.value }
            expect(ContentType.Application.Json) { response.contentType()?.withoutParameters() }
            expect(
                ResponseWireType(
                    listOf(
                        RestaurantWireType(barAvignon.id, barAvignon.name),
                        RestaurantWireType(cafeGloucester.id, cafeGloucester.name)
                    )
                )
            ) { Json.decodeFromString<ResponseWireType>(response.bodyAsText()) }
        }
    }

    @Test
    fun `the route returns a 500 when there is an unexpected error`() {
        testApplication {
            application {
                restaurantRatings(factories = object : Factories {
                    override val controllerCreate: (Controller.Dependencies) -> KTORController = { _ ->
                        Controller.createTopRatedController(object : Controller.Dependencies {
                            override suspend fun getTopRestaurants(city: String): List<Restaurant> {
                                throw RuntimeException("Something unexpected failed")
                            }
                        })
                    }

                })
            }
            val response = client.get("/vancouverbc/restaurants/recommended")
            expect(500) { response.status.value }
            expect(ContentType.Application.Json) { response.contentType()?.withoutParameters() }
            expect("UNEXPECTED_ERROR") { Json.decodeFromString<ErrorResponseWireType>(response.bodyAsText()).statusCode }
        }
    }
}

@Serializable
data class RestaurantWireType(val id: String, val name: String)

@Serializable
data class ResponseWireType(val restaurants: List<RestaurantWireType>)

@Serializable
data class ErrorResponseWireType(val statusCode: String, val message: String)

data class Restaurant(val id: String, val name: String)