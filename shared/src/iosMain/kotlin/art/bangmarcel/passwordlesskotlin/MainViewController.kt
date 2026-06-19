package art.bangmarcel.passwordlesskotlin

import androidx.compose.ui.window.ComposeUIViewController
import art.bangmarcel.passwordlesskotlin.models.PasskeyManager
import art.bangmarcel.passwordlesskotlin.stores.SecureTokenManager

fun MainViewController() = ComposeUIViewController { App(SecureTokenManager()) }