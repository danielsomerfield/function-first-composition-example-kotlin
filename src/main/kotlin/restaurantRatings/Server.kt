package restaurantRatings

import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import restaurantRatings.Configuration.Companion.getConfigurationFromEnvVars

fun main() {
    Server().start()
}

class Server {

    private lateinit var netty: NettyApplicationEngine

    fun start(getConfiguration: () -> Configuration = ::getConfigurationFromEnvVars) {
        val configuration = getConfiguration()
        netty = embeddedServer(Netty, port = configuration.serverPort, module = { this.server(getConfiguration) }).start(wait = false)
    }

    fun stop() {
        netty.stop()
    }
}

fun Application.server(getConfiguration: () -> Configuration) {
    restaurantRatings(getConfiguration())
}

