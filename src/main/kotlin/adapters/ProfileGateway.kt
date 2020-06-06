package adapters

import com.fasterxml.jackson.databind.ObjectMapper
import domain.Profile
import domain.toEmail
import java.net.URI
import java.net.http.HttpClient.newHttpClient
import java.net.http.HttpRequest
import java.net.http.HttpRequest.BodyPublishers.ofString
import java.net.http.HttpResponse.BodyHandlers.discarding
import java.net.http.HttpResponse.BodyHandlers.ofString

class ProfileGateway(private val apiUrl: String) {

    private val objectMapper = ObjectMapper()
    private val newHttpClient = newHttpClient()

    fun fetchProfile(id: String): Profile {
        val httpRequest = HttpRequest.newBuilder()
            .uri(URI.create("$apiUrl/profile/$id"))
            .GET()

        val response = newHttpClient.send(httpRequest.build(), ofString())
        check(response.statusCode() == 200) { "status is not 200 OK" }

        return response.body().toProfile()
    }

    private fun String.toProfile() =
        objectMapper.readTree(this).let {
            Profile(
                id = it.get("id").asText(),
                email = it.get("email").asText().toEmail()
            )
        }

    fun saveProfile(profile: Profile) {
        val httpRequest = HttpRequest.newBuilder()
            .uri(URI.create("$apiUrl/profile"))
            .header("Content-type", "application/json")
            .POST(ofString(objectMapper.writeValueAsString(profile.toProfileRepresenter())))

        val response = newHttpClient.send(httpRequest.build(), discarding())
        check(response.statusCode() == 201) { "status is not 200 OK" }
    }

    private fun Profile.toProfileRepresenter() = mapOf(
        "email" to email.value, "id" to id
    )
}
