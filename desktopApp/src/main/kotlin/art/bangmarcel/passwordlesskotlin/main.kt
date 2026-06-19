package art.bangmarcel.passwordlesskotlin

import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import art.bangmarcel.passwordlesskotlin.models.PasskeyManager
import art.bangmarcel.passwordlesskotlin.stores.SecureTokenManager

fun main() = application {
    Window(
        onCloseRequest = ::exitApplication,
        title = "PasswordlessKotlin",
    ) {
        App(SecureTokenManager())
    }
}