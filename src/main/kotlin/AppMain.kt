fun main() {
    WebAppConfig(
            dbUrl = System.getProperty("DB_URL"),
            port = System.getProperty("PORT")?.toInt() ?: 8080
    ).start()
}
