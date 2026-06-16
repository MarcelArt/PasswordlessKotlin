package art.bangmarcel.passwordlesskotlin.models

import kotlinx.serialization.Serializable

@Serializable
data class RegistrationResult(
    val registrationResponseJson: String, // Sent to /register/finish
    val sessionId: String
)

@Serializable
data class LoginResult(
    val loginResponseJson: String, // To send back to /login/finish
    val sessionId: String
)

expect class PasskeyManager {
    suspend fun registerPasskey(options: String, sessionId: String): RegistrationResult
    suspend fun authenticatePasskey(optionsJson: String, sessionId: String): LoginResult
}