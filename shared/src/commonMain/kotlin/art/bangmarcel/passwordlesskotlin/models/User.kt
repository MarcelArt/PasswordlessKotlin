package art.bangmarcel.passwordlesskotlin.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonElement

@Serializable
data class User(
    @SerialName("ID") val id: String,
    val username: String,
    val email: String,
    val credentials: JsonElement? = null,
)
