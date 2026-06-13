package art.bangmarcel.passwordlesskotlin.models

actual class PasskeyManager {
    actual suspend fun registerPasskey(
        options: String,
        sessionId: String
    ): RegistrationResult {
        TODO("Not yet implemented")
    }
}