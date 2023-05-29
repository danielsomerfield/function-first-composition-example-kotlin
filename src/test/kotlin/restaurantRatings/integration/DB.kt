package restaurantRatings.integration

import org.testcontainers.containers.BindMode
import org.testcontainers.containers.PostgreSQLContainer
import java.sql.Connection
import java.sql.PreparedStatement

class DB {
    private val dbUsername = "postgres"
    private val dbPassword = "postgres"
    private val databaseName = "postgres"

    private val container = PostgreSQLContainer("postgres:9.6.12").withFileSystemBind(
        "${System.getProperty("user.dir")}/db",
        "/db",
        BindMode.READ_ONLY
    )
        .withUsername(dbUsername)
        .withPassword(dbPassword)
        .withDatabaseName(databaseName)
        .withInitScript("db/init.sql")

    fun exec(sql: String, binder: (stmt: PreparedStatement) -> Unit) {

        val connection = getConnection()
        val stmt =
            connection.prepareStatement(sql)
        binder(stmt)
        stmt.executeUpdate()
    }

    fun getConnection(): Connection {
        return container.jdbcDriverInstance.connect(
            container.jdbcUrl,
            mapOf("user" to dbUsername, "password" to dbPassword).toProperties()
        )
    }

    fun start() {
        container.start()
    }

    fun stop() {
        container.stop()
    }

}

object Users {
    fun create(user: User, db: DB) {
        db.exec("insert into \"user\" (id, name, trusted) values (?, ?, ?)") { stmt ->
            stmt.setString(1, user.id)
            stmt.setString(2, user.name)
            stmt.setBoolean(3, user.trusted)
        }
    }
}

object Restaurants {
    fun create(r: Restaurant, db: DB) {
        db.exec("insert into restaurant (id, name) values (?, ?)") { stmt ->
            stmt.setString(1, r.id)
            stmt.setString(2, r.name)
        }
    }
}

object Ratings {
    fun create(r: RatingByUser, db: DB): Unit {
        db.exec("insert into restaurant_rating (id, restaurant_id, rating, rated_by_user_id, city) values (?, ?, ?, ?, ?)") { stmt ->
            stmt.setString(1, r.id)
            stmt.setString(2, r.restaurant.id)
            stmt.setString(3, r.rating)
            stmt.setString(4, r.user.id)
            stmt.setString(5, "vancouverbc")
        }
    }
}