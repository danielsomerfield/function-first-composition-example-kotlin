package restaurantRatings

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.netty.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun main(args: Array<String>) = EngineMain.main(args)


fun Application.restaurantRatings() {
    routing {
        get("/vancouverbc/restaurants/recommended") {
            call.response.header(HttpHeaders.ContentType, "application/json")
            call.respond<HttpStatusCode>(HttpStatusCode.OK)
        }
    }
}