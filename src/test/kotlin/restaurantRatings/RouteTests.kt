package restaurantRatings

import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.server.testing.*
import org.junit.jupiter.api.Test
import kotlin.test.expect

class RouteTests {

    @Test
    fun `the ratings route provides a JSON response with ratings`() {
        testApplication {
            application {
                restaurantRatings()
            }
            val response = client.get("/vancouverbc/restaurants/recommended")
            expect(200) { response.status.value }
            expect("application/json") { response.headers["content-type"] }
            expect("") { response.bodyAsText() }
        }
    }
}