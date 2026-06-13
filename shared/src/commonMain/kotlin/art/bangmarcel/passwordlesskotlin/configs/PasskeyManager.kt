package art.bangmarcel.passwordlesskotlin.configs

import art.bangmarcel.passwordlesskotlin.models.PasskeyManager

expect fun newPasskeyManager(): PasskeyManager

fun initPasskeyManager(): PasskeyManager {
    return newPasskeyManager()
}