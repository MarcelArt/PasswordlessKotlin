package art.bangmarcel.passwordlesskotlin.utils

import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import kotlinx.serialization.json.longOrNull

fun extractPublicKey(options: JsonElement): String {
    val cleanOptionsString = Json.encodeToString(JsonElement.serializer(), options)
    val json = Json { ignoreUnknownKeys = true }
    val root = json.parseToJsonElement(cleanOptionsString).jsonObject.toMutableMap()
    return root["publicKey"]?.jsonObject.toString()
}