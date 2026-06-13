package art.bangmarcel.passwordlesskotlin

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.tooling.preview.Preview
import art.bangmarcel.passwordlesskotlin.configs.initPasskeyManager
import art.bangmarcel.passwordlesskotlin.models.PasskeyManager
import art.bangmarcel.passwordlesskotlin.repositories.AuthRepo
import art.bangmarcel.passwordlesskotlin.screens.BeginRegisterScreen
import cafe.adriel.voyager.navigator.Navigator
import io.ktor.client.*
import io.ktor.client.engine.cio.CIO
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.json.Json

@Composable
fun App(passkeyManager: PasskeyManager) {
    val client = HttpClient(CIO) {
        install(ContentNegotiation) {
            json(Json {
                prettyPrint = false
                isLenient = true
                ignoreUnknownKeys = true
            })
        }
    }

    val aRepo = AuthRepo(client)

    MaterialTheme {
        Navigator(BeginRegisterScreen(aRepo, passkeyManager))
    }
}