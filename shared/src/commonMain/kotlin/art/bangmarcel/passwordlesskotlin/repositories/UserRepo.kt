package art.bangmarcel.passwordlesskotlin.repositories

import art.bangmarcel.passwordlesskotlin.BuildKonfig
import art.bangmarcel.passwordlesskotlin.models.JsonResponse
import art.bangmarcel.passwordlesskotlin.models.LoginInput
import art.bangmarcel.passwordlesskotlin.models.LoginResponse
import art.bangmarcel.passwordlesskotlin.models.User
import art.bangmarcel.passwordlesskotlin.models.UserInput
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType

class UserRepo(private val client: HttpClient) {
    private val baseUrl = BuildKonfig.apiBaseUrl
    private val routeGroup = "$baseUrl/v1/users"

    suspend fun create(input: UserInput): JsonResponse<Int> {
        return try {
            client.post("$routeGroup/") {
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

    suspend fun login(input: LoginInput): JsonResponse<LoginResponse> {
        return try {
            client.post("$routeGroup/login") {
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