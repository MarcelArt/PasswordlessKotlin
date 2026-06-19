package art.bangmarcel.passwordlesskotlin.stores

actual class SecureTokenManager {
    actual fun saveTokens(accessToken: String, refreshToken: String) {
    }

    actual fun getAccessToken(): String? {
        TODO("Not yet implemented")
    }

    actual fun getRefreshToken(): String? {
        TODO("Not yet implemented")
    }

    actual fun clearTokens() {
    }
}