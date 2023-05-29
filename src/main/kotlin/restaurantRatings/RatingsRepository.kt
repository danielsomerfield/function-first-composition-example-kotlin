package restaurantRatings

import org.slf4j.LoggerFactory
import restaurantRatings.domain.Rating
import restaurantRatings.domain.RatingsByRestaurant
import restaurantRatings.domain.RestaurantRating
import restaurantRatings.domain.User
import java.sql.Connection
import java.sql.ResultSet
import java.util.*

object RatingsRepository {

    private val logger = LoggerFactory.getLogger(RatingsRepository.javaClass)

    private val findRatingsByRestaurantSQL = """
        select
            rr.restaurant_id,
            rr.rating,
            rr.rated_by_user_id,
            u.trusted as is_trusted  
        from restaurant_rating rr
        inner join "user" u on rr.rated_by_user_id = u.id
    """.trimIndent()

    interface Dependencies {
        fun getConnection(): Connection
        fun releaseConnection(connection: Connection)
    }

    fun createFindRatingsByRestaurant(dependencies: Dependencies): () -> List<RatingsByRestaurant> {
        return {
            dependencies.getConnection().use { conn ->
                val stmt = conn.prepareStatement(findRatingsByRestaurantSQL)
                val rslt = stmt.executeQuery()
                val ratings = mutableListOf<Pair<String, RestaurantRating>>()
                while (rslt.next()) {
                    val maybeRating = createRestaurantRating(rslt).map { rslt.getString(1) to it }
                    maybeRating.ifPresentOrElse(ratings::add) {
                        logger.warn(
                            "Failed to create rating from db table, id = ${
                                rslt.getString(
                                    1
                                )
                            }"
                        )
                    }
                }
                ratings
            }.groupBy { it.first }.entries.map { entry ->
                RatingsByRestaurant(
                    entry.key,
                    entry.value.map { it.second })
            }
        }
    }

    private fun createRestaurantRating(rslt: ResultSet): Optional<RestaurantRating> {
        val ratingString = rslt.getString(2)
        val userId = rslt.getString(3)
        val trusted = rslt.getBoolean(4)

        val rating = try {
            Optional.of(Rating.valueOf(ratingString))
        } catch (e: IllegalArgumentException) {
            Optional.empty()
        }
        return rating.map { RestaurantRating(it, User(userId, trusted)) }

    }
}

