package restaurantRatings

import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*
import io.ktor.server.netty.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.routing.*
import kotlinx.serialization.json.Json
import restaurantRatings.domain.Restaurant

fun main(args: Array<String>) {
    EngineMain.main(args)
}

interface Factories {
    val controllerCreate: (Controller.Dependencies) -> KTORController
}

object ProductionFactories : Factories {
    override val controllerCreate: (Controller.Dependencies) -> KTORController = Controller::createTopRatedController
}


private val dependencies: Controller.Dependencies = object : Controller.Dependencies {
    override suspend fun getTopRestaurants(city: String): List<Restaurant> {
        TODO("Not yet implemented")
    }

}

fun Application.restaurantRatings(factories: Factories = ProductionFactories) {
    install(ContentNegotiation) {
        json(Json {
            prettyPrint = true
        })
    }
    routing {
        get("/{city}/restaurants/recommended", factories.controllerCreate(dependencies))
    }
}