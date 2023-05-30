package rateMyMeal

import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import rateMyMeal.Configuration.Companion.getConfigurationFromEnvVars
import rateMyMeal.restaurantRatings.initRestaurantRatings

fun main() {
    Server().start(wait = true)
}

class Server {

    private lateinit var netty: NettyApplicationEngine

    fun start(getConfiguration: () -> Configuration = ::getConfigurationFromEnvVars, wait: Boolean = false) {
        val configuration = getConfiguration()
        netty =
            embeddedServer(Netty, port = configuration.serverPort, module = { this.server(getConfiguration) }).start(
                wait
            )
    }

    fun stop() {
        netty.stop()
    }
}

fun Application.server(getConfiguration: () -> Configuration) {
    initRestaurantRatings(getConfiguration())
}

