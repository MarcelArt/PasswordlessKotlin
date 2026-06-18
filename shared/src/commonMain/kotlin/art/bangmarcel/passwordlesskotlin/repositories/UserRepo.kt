package art.bangmarcel.passwordlesskotlin.repositories

import art.bangmarcel.passwordlesskotlin.BuildKonfig
import art.bangmarcel.passwordlesskotlin.models.JsonResponse
import art.bangmarcel.passwordlesskotlin.models.UserInput
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType

class UserRepo(private val client: HttpClient) {
    private val baseUrl = BuildKonfig.apiBaseUrl

    suspend fun create(input: UserInput): JsonResponse<Int> {
        return try {
            client.post("$baseUrl/v1/users/") {
                contentType(ContentType.Application.Json)
                setBody(input)
            }.body()
        } catch (e: Exception) {
            JsonResponse(
                null,
                false,
                e.message ?: "Unknown error",
            )
        }
    }
}