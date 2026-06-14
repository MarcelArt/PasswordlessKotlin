package art.bangmarcel.passwordlesskotlin.models

import kotlinx.serialization.Serializable

@Serializable
data class LoginResponse(
    val accessToken: String,
    val refreshToken: String,
    val user: User,
)
