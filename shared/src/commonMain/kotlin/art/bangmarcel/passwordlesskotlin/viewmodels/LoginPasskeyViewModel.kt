package art.bangmarcel.passwordlesskotlin.viewmodels

import art.bangmarcel.passwordlesskotlin.models.BeginRegisterWebAuthn
import art.bangmarcel.passwordlesskotlin.models.LoginResponse
import art.bangmarcel.passwordlesskotlin.models.PasskeyManager
import art.bangmarcel.passwordlesskotlin.repositories.AuthRepo
import art.bangmarcel.passwordlesskotlin.utils.extractPublicKey
import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class LoginPasskeyViewModel(private val repo: AuthRepo, private val passkeyManager: PasskeyManager): ScreenModel {
    private val _isPending = MutableStateFlow(false)
    val isPending: StateFlow<Boolean> = _isPending

    private val _isError = MutableStateFlow(false)
    val isError: StateFlow<Boolean> = _isError
    private val _error = MutableStateFlow("")
    val error: StateFlow<String> = _error

    private val _isSuccess = MutableStateFlow(false)
    val isSuccess: StateFlow<Boolean> = _isSuccess

    fun loginBegin(username: String, onFailure: (String) -> Unit, onSuccess: (BeginRegisterWebAuthn) -> Unit) {
        resetStates()
        _isPending.value = true
        screenModelScope.launch {
            try {
                val res = repo.loginBegin(username)

                if (!res.isSuccess || res.items == null) throw Exception(res.message)

                _isSuccess.value = true
                onSuccess(res.items)
            }
            catch (e: Exception) {
                _isError.value = true
                _error.value = e.message ?: "An unexpected error occurred"
                onFailure(_error.value)
            }
            finally {
                _isPending.value = false
            }
        }
    }

    fun loginFinish(loginData: BeginRegisterWebAuthn, username: String, onFailure: (String) -> Unit, onSuccess: (LoginResponse) -> Unit) {
        resetStates()
        _isPending.value = true
        val publicKey = extractPublicKey(loginData.options)

        screenModelScope.launch {
            try {
                val nativeResult = passkeyManager.authenticatePasskey(publicKey, loginData.sessionId)

                val user = repo.loginFinish(nativeResult.sessionId, username, nativeResult.loginResponseJson)

                if (!user.isSuccess || user.items == null) throw Exception(user.message)

                _isSuccess.value = true
                onSuccess(user.items)
            }
            catch (e: Exception) {
                _isError.value = true
                _error.value = e.message ?: "An unexpected error occurred"
                onFailure(_error.value)
            }
            finally {
                _isPending.value = false
            }
        }
    }

    fun login(username: String, onFailure: (String) -> Unit, onSuccess: (LoginResponse) -> Unit) {
        resetStates()
        _isPending.value = true

        screenModelScope.launch {
            try {
                val res = repo.loginBegin(username)

                if (!res.isSuccess || res.items == null) throw Exception(res.message)

                val item = res.items

                val publicKey = extractPublicKey(item.options)
                val nativeResult = passkeyManager.authenticatePasskey(publicKey, item.sessionId)

                val user = repo.loginFinish(nativeResult.sessionId, username, nativeResult.loginResponseJson)

                if (!user.isSuccess || user.items == null) throw Exception(user.message)

                _isSuccess.value = true
                onSuccess(user.items)
            }
            catch (e: Exception) {
                _isError.value = true
                _error.value = e.message ?: "An unexpected error occurred"
                onFailure(_error.value)
            }
            finally {
                _isPending.value = false
            }
        }
    }

    private fun resetStates() {
        _isSuccess.value = false
        _isError.value = false
        _isPending.value = false
        _error.value = ""
    }
}