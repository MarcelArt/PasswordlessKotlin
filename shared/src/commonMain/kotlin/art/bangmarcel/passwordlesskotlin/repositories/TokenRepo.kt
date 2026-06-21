package art.bangmarcel.passwordlesskotlin.repositories

import art.bangmarcel.passwordlesskotlin.BuildKonfig
import art.bangmarcel.passwordlesskotlin.enums.AuthState
import art.bangmarcel.passwordlesskotlin.models.JsonResponse
import art.bangmarcel.passwordlesskotlin.models.LoginResponse
import art.bangmarcel.passwordlesskotlin.models.User
import art.bangmarcel.passwordlesskotlin.stores.SecureTokenManager
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.headers
import io.ktor.client.request.post
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class TokenRepo(private val secureTokenManager: SecureTokenManager, private val client: HttpClient, private val appScope: CoroutineScope) {
    private val _user = MutableStateFlow<User?>(null)
    val user = _user.asStateFlow()

    private val _authState = MutableStateFlow(AuthState.UNAUTHENTICATED)
    val authState = _authState.asStateFlow()

    private val baseUrl = BuildKonfig.apiBaseUrl

    init {
        appScope.launch {
            refreshTokens()
        }
    }

    suspend fun refreshTokens() {
        try {
            _authState.value = AuthState.LOADING
            val rt = secureTokenManager.getRefreshToken()
            if (rt == null) {
                _user.value = null
                _authState.value = AuthState.UNAUTHENTICATED
                return
            }

            val res = client.post("$baseUrl/v1/users/refresh") {
                headers {
                    append("X-Refresh-Token", rt)
                }
            }.body<JsonResponse<LoginResponse>>()

            val tokens = res.unwrap()

            _user.value = tokens.user
            secureTokenManager.saveTokens(tokens.accessToken, tokens.refreshToken)
            _authState.value = AuthState.AUTHENTICATED
        }
        catch (e: Exception) {
            println(e.message)
            _user.value = null
            secureTokenManager.clearTokens()
            _authState.value = AuthState.UNAUTHENTICATED
        }
    }

    fun onLoginSuccess(user: User, access: String, refresh: String) {
        secureTokenManager.saveTokens(access, refresh)
        _user.value = user
        _authState.value = AuthState.AUTHENTICATED
    }

    fun logout() {
        secureTokenManager.clearTokens()
        _user.value = null
        _authState.value = AuthState.UNAUTHENTICATED
    }

    suspend fun current(): JsonResponse<User> {
        return try {
            val res = client.get("$baseUrl/v1/users/current").body<JsonResponse<User>>()
            val user = res.unwrap()
            _user.value = user
            _authState.value = AuthState.AUTHENTICATED
            res
        } catch (e: Exception) {
            _user.value = null
            _authState.value = AuthState.UNAUTHENTICATED
            JsonResponse(
                null,
                false,
                e.message ?: "Unknown error",
            )
        }
    }
}