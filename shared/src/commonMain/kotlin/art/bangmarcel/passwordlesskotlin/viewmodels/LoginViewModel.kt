package art.bangmarcel.passwordlesskotlin.viewmodels

import art.bangmarcel.passwordlesskotlin.repositories.AuthRepo
import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class LoginViewModel(private val repo: AuthRepo): ScreenModel {
    private val _isPending = MutableStateFlow(false)
    val isPending: StateFlow<Boolean> = _isPending

    private val _isError = MutableStateFlow(false)
    val isError: StateFlow<Boolean> = _isError
    private val _error = MutableStateFlow("")
    val error: StateFlow<String> = _error

    private val _isSuccess = MutableStateFlow(false)
    val isSuccess: StateFlow<Boolean> = _isSuccess

    fun login(username: String, onFailure: (String) -> Unit, onSuccess: () -> Unit) {
        _isPending.value = true
        screenModelScope.launch {
            try {
                val res = repo.loginBegin(username)

                if (!res.isSuccess && res.items == null) {
                    _isError.value = true
                    _error.value = res.message
                    onFailure(res.message)
                    return@launch
                }

                val item = res.items!!

                val user = repo.loginFinish(item.sessionId, username, item.options.toString())
                onSuccess()
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
}