package composition

import com.github.kittinunf.fuel.Fuel
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.testcontainers.containers.BindMode
import org.testcontainers.containers.PostgreSQLContainer
import kotlin.test.expect

class E2EIntegrationTest {

    val server = Server()

    @BeforeEach
    fun setup() {
        DB.start()
        server.start()
    }

    @AfterEach
    fun teardown() {
        server.stop()
        DB.stop()
    }

    @Test
    fun testRestaurantRankings() {
        val (_, response, _) = Fuel.get("http://localhost:8080/vancouverbc/restaurants/recommended")
            .response()
        expect(200) { response.statusCode }
    }
}

object DB {

    private val container = PostgreSQLContainer("postgres:9.6.12")
        .withFileSystemBind("${System.getProperty("user.dir")}/db", "/docker-entrypoint-initdb.d", BindMode.READ_ONLY)

    data class DBConfiguration(
        val username: String,
        val password: String,
        val database: String,
        val host: String,
    )

    fun start() {
        container.start()
    }

    fun stop() {
        container.stop()
    }
}

class Server {

    private lateinit var netty: NettyApplicationEngine

    fun start() {
        netty = embeddedServer(Netty, port = 8080, module = Application::Server).start(wait = false)
    }

    fun stop() {
        netty.stop()
    }
}

fun Application.Server() {
    routing {
        get("/{city}/restaurants/recommended") {
            call.respond(HttpStatusCode.OK)
        }
    }
}
