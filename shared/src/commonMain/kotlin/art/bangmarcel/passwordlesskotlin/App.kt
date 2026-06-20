package art.bangmarcel.passwordlesskotlin

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import art.bangmarcel.passwordlesskotlin.configs.initKoin
import art.bangmarcel.passwordlesskotlin.enums.AuthState
import art.bangmarcel.passwordlesskotlin.repositories.TokenRepo
import art.bangmarcel.passwordlesskotlin.screens.auth.LoginScreen
import art.bangmarcel.passwordlesskotlin.screens.auth.RegisterScreen
import art.bangmarcel.passwordlesskotlin.screens.main.MainLayoutScreen
import art.bangmarcel.passwordlesskotlin.stores.SecureTokenManager
import cafe.adriel.voyager.navigator.Navigator
import org.koin.compose.koinInject

@Composable
fun App(secureTokenManager: SecureTokenManager) {
    initKoin(secureTokenManager)
    val rt = secureTokenManager.getRefreshToken()

    val tRepo = koinInject<TokenRepo>()

    val authState by tRepo.authState.collectAsState()

    MaterialTheme(
        colorScheme = darkColorScheme()
    ) {
        when (authState) {
            AuthState.LOADING -> {
                Scaffold { paddingValues ->
                    Box(
                        modifier = Modifier.fillMaxSize().padding(paddingValues),
                        contentAlignment = Alignment.Center,
                    ) {
                        CircularProgressIndicator()
                    }
                }
            }
            AuthState.UNAUTHENTICATED -> Navigator(LoginScreen())
            AuthState.AUTHENTICATED -> Navigator(MainLayoutScreen())
        }
    }
}