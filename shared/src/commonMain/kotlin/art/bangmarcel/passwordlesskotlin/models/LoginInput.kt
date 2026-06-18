package art.bangmarcel.passwordlesskotlin.models

import kotlinx.serialization.Serializable

@Serializable
data class LoginInput(
    val username: String,
    val password: String,
    val isRemember: Boolean,
)
