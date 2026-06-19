package art.bangmarcel.passwordlesskotlin.configs

import art.bangmarcel.passwordlesskotlin.repositories.UserRepo
import art.bangmarcel.passwordlesskotlin.viewmodels.LoginViewModel
import art.bangmarcel.passwordlesskotlin.viewmodels.RegisterViewModel
import io.ktor.client.HttpClient
import io.ktor.client.engine.cio.CIO
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import org.koin.core.context.startKoin
import org.koin.dsl.module

fun buildHttpClient(): HttpClient {
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

val appModule = module {
    single { buildHttpClient() }
    single { UserRepo(get()) }
    factory { LoginViewModel(get()) }
    factory { RegisterViewModel(get()) }
}

fun initKoin() {
    startKoin {
        modules(appModule)
    }
}