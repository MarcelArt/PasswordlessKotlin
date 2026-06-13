package art.bangmarcel.passwordlesskotlin.models

import android.content.Context
import androidx.credentials.CreatePublicKeyCredentialRequest
import androidx.credentials.CredentialManager

actual class PasskeyManager(private val context: Context) {
    actual suspend fun registerPasskey(options: String, sessionId: String): RegistrationResult {
        val credentialManager = CredentialManager.create(context)
        val request = CreatePublicKeyCredentialRequest(options)

        // Opens the native Android fingerprint / biometric sheet
        val result = credentialManager.createCredential(context = context, request = request)
        val responseJson = result.data.getString("androidx.credentials.BUNDLE_KEY_REGISTRATION_RESPONSE_JSON")
            ?: throw Exception("Registration payload missing")

        return RegistrationResult(responseJson, sessionId)
    }
}