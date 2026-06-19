package art.bangmarcel.passwordlesskotlin.models

import kotlinx.serialization.Serializable

@Serializable
data class JsonResponse<T>(
    val items: T? = null,
    val isSuccess: Boolean,
    val message: String,
) {
    fun ok() = isSuccess && items != null
    fun unwrap(): T {
        if (!ok()) throw Exception(message)
        return items!!
    }
}
