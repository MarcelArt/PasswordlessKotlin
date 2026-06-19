package art.bangmarcel.passwordlesskotlin

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import art.bangmarcel.passwordlesskotlin.configs.initKoin
import art.bangmarcel.passwordlesskotlin.screens.auth.RegisterScreen
import art.bangmarcel.passwordlesskotlin.stores.SecureTokenManager
import cafe.adriel.voyager.navigator.Navigator

@Composable
fun App(secureTokenManager: SecureTokenManager) {
    initKoin(secureTokenManager)
    MaterialTheme(
        colorScheme = darkColorScheme()
    ) {
        Navigator(RegisterScreen())
    }
}