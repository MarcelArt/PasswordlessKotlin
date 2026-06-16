package art.bangmarcel.passwordlesskotlin.models

import android.app.Activity
import android.content.Context
import androidx.credentials.CreatePublicKeyCredentialRequest
import androidx.credentials.CredentialManager
import androidx.credentials.GetCredentialRequest
import androidx.credentials.GetPasswordOption
import androidx.credentials.GetPublicKeyCredentialOption
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

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

    actual suspend fun authenticatePasskey(optionsJson: String, sessionId: String): LoginResult = withContext(Dispatchers.Main.immediate) {
        val credentialManager = CredentialManager.create(context)

        // Android engine parses standard W3C WebAuthn assertion data from a raw JSON string
        val publicKeyOption = GetPublicKeyCredentialOption(optionsJson)

        val getCredentialRequest = GetCredentialRequest(
            credentialOptions = listOf(publicKeyOption),
            preferImmediatelyAvailableCredentials = false,
        )

        try {
            // Displays the native Android fingerprint / biometric sheet listing available passkeys
            val result = credentialManager.getCredential(context = context, request = getCredentialRequest)

            val responseJson = result.credential.data.getString("androidx.credentials.BUNDLE_KEY_AUTHENTICATION_RESPONSE_JSON")
                ?: throw Exception("Authentication payload missing")

            LoginResult(responseJson, sessionId)
        } catch (e: Exception) {
            throw Exception("Android Authentication Failed: ${e.message}")
        }
    }
}