package art.bangmarcel.passwordlesskotlin

import androidx.compose.ui.window.ComposeUIViewController
import art.bangmarcel.passwordlesskotlin.models.PasskeyManager

fun MainViewController() = ComposeUIViewController { App(PasskeyManager()) }