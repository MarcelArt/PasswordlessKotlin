package art.bangmarcel.passwordlesskotlin.stores

expect class SecureTokenManager {
    fun saveTokens(accessToken: String, refreshToken: String)
    fun getAccessToken(): String?
    fun getRefreshToken(): String?
    fun clearTokens()
}