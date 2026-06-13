package art.bangmarcel.passwordlesskotlin.utils

import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonElement

fun String.toJsonElement(): JsonElement = Json.parseToJsonElement(this)