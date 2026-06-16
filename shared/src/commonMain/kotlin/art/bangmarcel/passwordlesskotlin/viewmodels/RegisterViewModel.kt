package art.bangmarcel.passwordlesskotlin.viewmodels

import art.bangmarcel.passwordlesskotlin.models.BeginRegisterWebAuthn
import art.bangmarcel.passwordlesskotlin.models.PasskeyManager
import art.bangmarcel.passwordlesskotlin.models.UserInput
import art.bangmarcel.passwordlesskotlin.repositories.AuthRepo
import art.bangmarcel.passwordlesskotlin.utils.extractPublicKey
import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.jsonObject

class RegisterViewModel(private val repo: AuthRepo, private val passkeyManager: PasskeyManager): ScreenModel {
    private val _statusMessage = MutableStateFlow<String?>(null)
    val statusMessage: StateFlow<String?> = _statusMessage

    private val _isSuccess = MutableStateFlow(false)
    val isSuccess: StateFlow<Boolean> = _isSuccess

    fun registerBegin(username: String, email: String, onSuccess: (BeginRegisterWebAuthn) -> Unit) {
        val input = UserInput(username, email)

        screenModelScope.launch {
            val res = repo.registerBegin(input)
            if (res.isSuccess && res.items != null) {
                _isSuccess.value = true
                onSuccess(res.items)
            } else {
                _isSuccess.value = false
                _statusMessage.value = res.message
            }
        }
    }

    fun registerFinish(registrationData: BeginRegisterWebAuthn, username: String, email: String, onFailure: (String) -> Unit, onSuccess: () -> Unit) {
        screenModelScope.launch {
            try {
                val publicKey = extractPublicKey(registrationData.options)

                val nativeResult = passkeyManager.registerPasskey(publicKey, registrationData.sessionId)

                val res = repo.registerFinish(nativeResult.sessionId, username, email, nativeResult.registrationResponseJson)

                if (res.isSuccess) {
                    _isSuccess.value = true
                    onSuccess()
                } else {
                    _isSuccess.value = false
                    onFailure(res.message)
                }
                _statusMessage.value = res.message
            }
            catch (e: Exception) {
                _isSuccess.value = false
                _statusMessage.value = e.message
                onFailure(e.toString())
            }
        }
    }
}