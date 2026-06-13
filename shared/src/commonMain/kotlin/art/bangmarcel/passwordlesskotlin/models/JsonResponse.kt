package art.bangmarcel.passwordlesskotlin.models

import kotlinx.serialization.Serializable

@Serializable
data class JsonResponse<T>(
    val items: T? = null,
    val isSuccess: Boolean,
    val message: String,
)
