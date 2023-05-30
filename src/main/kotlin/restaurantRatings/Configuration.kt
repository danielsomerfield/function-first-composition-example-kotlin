package restaurantRatings

data class Configuration(val serverPort: Int, val ratingsDb: RatingsDb) {

    companion object {
        fun getConfigurationFromEnvVars(): Configuration = Configuration(
            serverPort = 8080,
            RatingsDb(
                user = getRequiredConfiguration("REVIEW_DATABASE_USER"),
                password = getRequiredConfiguration("REVIEW_DATABASE_PASSWORD"),
                jdbcUrl = getRequiredConfiguration("REVIEW_DATABASE_JDBC_URL"),
            )
        )

        private fun getRequiredConfiguration(name: String): String {
            return System.getenv(name) ?: throw RuntimeException("$name is a required environment variable")
        }
    }
}

data class RatingsDb(
    val user: String,
    val password: String,
    val jdbcUrl: String,
)

