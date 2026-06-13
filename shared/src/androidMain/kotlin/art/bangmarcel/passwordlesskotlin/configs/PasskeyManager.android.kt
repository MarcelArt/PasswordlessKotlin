package art.bangmarcel.passwordlesskotlin.configs

import android.content.Context
import art.bangmarcel.passwordlesskotlin.models.PasskeyManager

lateinit var appContext: Context
actual fun newPasskeyManager(): PasskeyManager {
    return PasskeyManager(appContext)
}