package art.bangmarcel.passwordlesskotlin.models

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonElement

@Serializable
data class BeginRegisterWebAuthn(
    val sessionId: String,
    val options: JsonElement,
)
