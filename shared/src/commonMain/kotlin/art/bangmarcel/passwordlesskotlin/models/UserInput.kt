package art.bangmarcel.passwordlesskotlin.models

import kotlinx.serialization.Serializable

@Serializable
data class UserInput(
    val username: String,
    val email: String,
    val password: String = "",
)
