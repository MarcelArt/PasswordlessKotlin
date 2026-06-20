package art.bangmarcel.passwordlesskotlin

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import art.bangmarcel.passwordlesskotlin.configs.initKoin
import art.bangmarcel.passwordlesskotlin.screens.auth.LoginScreen
import art.bangmarcel.passwordlesskotlin.screens.auth.RegisterScreen
import art.bangmarcel.passwordlesskotlin.screens.main.MainLayoutScreen
import art.bangmarcel.passwordlesskotlin.stores.SecureTokenManager
import cafe.adriel.voyager.navigator.Navigator

@Composable
fun App(secureTokenManager: SecureTokenManager) {
    initKoin(secureTokenManager)
    val rt = secureTokenManager.getRefreshToken()
    MaterialTheme(
        colorScheme = darkColorScheme()
    ) {
        Navigator(if (rt == null) LoginScreen() else MainLayoutScreen())
    }
}