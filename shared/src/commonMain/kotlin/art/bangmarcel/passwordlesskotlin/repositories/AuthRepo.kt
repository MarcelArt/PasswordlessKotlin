package art.bangmarcel.passwordlesskotlin.repositories

import art.bangmarcel.passwordlesskotlin.BuildKonfig
import art.bangmarcel.passwordlesskotlin.models.BeginRegisterWebAuthn
import art.bangmarcel.passwordlesskotlin.models.JsonResponse
import art.bangmarcel.passwordlesskotlin.models.UserInput
import art.bangmarcel.passwordlesskotlin.utils.toJsonElement
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.parameter
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType

class AuthRepo(private val client: HttpClient) {
    private val baseUrl = BuildKonfig.apiBaseUrl

    suspend fun registerBegin(input: UserInput): JsonResponse<BeginRegisterWebAuthn> {
        try {
            return client.post("$baseUrl/v1/auth/register/begin") {
                contentType(ContentType.Application.Json)
                setBody(input)
            }.body()
        }
        catch (e: Exception) {
            println(e)
            return JsonResponse(
                items = null,
                isSuccess = false,
                message = e.message ?: "Unknown error",
            )
        }
    }

    suspend fun registerFinish(sessionId: String, username: String, registerResponseJson: String): JsonResponse<Unit> {
        try {
            return client.post("$baseUrl/v1/auth/register/finish") {
                parameter("session_id", sessionId)
                parameter("username", username)

                contentType(ContentType.Application.Json)
                setBody(registerResponseJson.toJsonElement())
            }.body()
        }
        catch (e: Exception) {
            return JsonResponse(
                items = null,
                isSuccess = false,
                message = e.message ?: "Unknown error",
            )
        }
    }
}