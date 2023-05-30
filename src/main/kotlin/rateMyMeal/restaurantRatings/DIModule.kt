package rateMyMeal.restaurantRatings

import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.routing.*
import kotlinx.serialization.json.Json
import rateMyMeal.Configuration
import rateMyMeal.restaurantRatings.domain.RatingsByRestaurant
import rateMyMeal.restaurantRatings.domain.Restaurant
import java.sql.Connection
import java.util.*

interface Factories {
    val controllerCreate: (Controller.Dependencies) -> KTORController
    val topRatedCreate: (TopRated.Dependencies) -> suspend (city: String) -> List<Restaurant>
}

object ProductionFactories : Factories {
    override val controllerCreate: (Controller.Dependencies) -> KTORController = Controller::createTopRatedController
    override val topRatedCreate: (TopRated.Dependencies) -> suspend (city: String) -> List<Restaurant> =
        TopRated::create
}

fun Application.initRestaurantRatings(
    configuration: Configuration,
    factories: Factories = ProductionFactories,
) {

    val dbDependencies = object : RatingsRepository.Dependencies, RestaurantRepository.Dependencies {
        private val dbConfig = configuration.ratingsDb
        private val hikariConfig = HikariConfig(
            mapOf(
                "jdbcUrl" to dbConfig.jdbcUrl,
                "username" to dbConfig.user,
                "password" to dbConfig.password,
            ).toProperties()
        )
        private val datasource = HikariDataSource(hikariConfig)

        override val getConnection: () -> Connection = {
            datasource.connection
        }
    }

    val topRatedDependencies = object : TopRated.Dependencies {
        override val getRestaurantById: suspend (id: String) -> Optional<Restaurant> =
            RestaurantRepository.createGetRestaurantById(dbDependencies)
        override val findRatingsByRestaurant: suspend (city: String) -> List<RatingsByRestaurant> =
            RatingsRepository.createFindRatingsByRestaurant(dbDependencies)
        override val calculateRatingForRestaurant: (ratings: RatingsByRestaurant) -> Int =
            RatingsAlgorithm::calculateRatingForRestaurant
    }

    val controllerDependencies = object : Controller.Dependencies {
        override val getTopRestaurants: suspend (String) -> List<Restaurant> =
            factories.topRatedCreate(topRatedDependencies)
    }

    install(ContentNegotiation) {
        json(Json {
            prettyPrint = true
        })
    }
    routing {
        get("/{city}/restaurants/recommended", factories.controllerCreate(controllerDependencies))
    }
}