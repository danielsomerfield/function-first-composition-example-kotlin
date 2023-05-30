package rateMyMeal.restaurantRatings

import rateMyMeal.restaurantRatings.domain.Restaurant
import java.sql.Connection
import java.util.*

object RestaurantRepository {
    interface Dependencies {
        val getConnection: () -> Connection
    }

    private val findRestaurantByIdSQL = """
        select
            r.name
        from restaurant r WHERE id = ?
    """.trimIndent()

    fun createGetRestaurantById(dependencies: Dependencies): suspend (id: String) -> Optional<Restaurant> {
        return { id ->
            dependencies.getConnection().use { conn ->
                val stmt = conn.prepareStatement(findRestaurantByIdSQL)
                stmt.setString(1, id)
                val rslt = stmt.executeQuery()
                if (rslt.next()) {
                    Optional.of(Restaurant(id, rslt.getString(1)))
                } else {
                    Optional.empty()
                }
            }
        }
    }
}