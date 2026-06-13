package art.bangmarcel.passwordlesskotlin

import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import art.bangmarcel.passwordlesskotlin.models.PasskeyManager

fun main() = application {
    Window(
        onCloseRequest = ::exitApplication,
        title = "PasswordlessKotlin",
    ) {
        App(PasskeyManager())
    }
}