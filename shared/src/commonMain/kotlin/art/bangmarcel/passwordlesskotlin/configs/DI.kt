package art.bangmarcel.passwordlesskotlin.configs

import art.bangmarcel.passwordlesskotlin.BuildKonfig
import art.bangmarcel.passwordlesskotlin.models.JsonResponse
import art.bangmarcel.passwordlesskotlin.models.LoginResponse
import art.bangmarcel.passwordlesskotlin.repositories.UserRepo
import art.bangmarcel.passwordlesskotlin.stores.SecureTokenManager
import art.bangmarcel.passwordlesskotlin.viewmodels.LoginViewModel
import art.bangmarcel.passwordlesskotlin.viewmodels.RegisterViewModel
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.engine.cio.CIO
import io.ktor.client.plugins.auth.Auth
import io.ktor.client.plugins.auth.providers.BearerTokens
import io.ktor.client.plugins.auth.providers.bearer
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.post
import io.ktor.http.headers
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import org.koin.core.context.startKoin
import org.koin.core.qualifier.named
import org.koin.dsl.module

fun buildBareClient(): HttpClient {
    val client = HttpClient(CIO) {
        install(ContentNegotiation) {
            json(Json {
                prettyPrint = false
                isLenient = true
                ignoreUnknownKeys = true
            })
        }
    }

    return client
}

fun buildHttpClient(secureTokenManager: SecureTokenManager, bareClient: HttpClient): HttpClient {
    val client = HttpClient(CIO) {
        install(ContentNegotiation) {
            json(Json {
                prettyPrint = false
                isLenient = true
                ignoreUnknownKeys = true
            })
        }
        install(Auth) {
            bearer {
                loadTokens {
                    val access = secureTokenManager.getAccessToken()
                    val refresh = secureTokenManager.getRefreshToken()
                    if (access != null && refresh != null) BearerTokens(access, refresh) else null
                }
                refreshTokens {
                    val oldRefreshToken = secureTokenManager.getRefreshToken() ?: return@refreshTokens null
                    val baseUrl = BuildKonfig.apiBaseUrl

                    try {
                        val res = bareClient.post("$baseUrl/v1/users/refresh") {
                            headers {
                                append("X-Refresh-Token", oldRefreshToken)
                            }
                        }.body<JsonResponse<LoginResponse>>()

                        if (!res.isSuccess || res.items == null) throw Exception(res.message)

                        BearerTokens(res.items.accessToken, res.items.refreshToken)
                    }
                    catch (e: Exception) {
                        println(e.message)
                        secureTokenManager.clearTokens()
                        null
                    }
                }
            }
        }
    }

    return client
}


fun initKoin(secureTokenManager: SecureTokenManager) {
    val appModule = module {
        single { secureTokenManager }
        single(named("bareClient")) { buildBareClient() }
        single { buildHttpClient(get(), get(named("bareClient"))) }
        single { UserRepo(get()) }
        factory { LoginViewModel(get()) }
        factory { RegisterViewModel(get()) }
    }

    startKoin {
        modules(appModule)
    }
}