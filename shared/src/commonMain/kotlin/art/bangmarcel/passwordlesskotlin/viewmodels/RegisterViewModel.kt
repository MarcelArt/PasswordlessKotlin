package art.bangmarcel.passwordlesskotlin.viewmodels

import art.bangmarcel.passwordlesskotlin.models.UserInput
import art.bangmarcel.passwordlesskotlin.repositories.UserRepo
import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class RegisterViewModel(private val repo: UserRepo): ScreenModel {
    private val _isSuccess = MutableStateFlow(false )
    val isSuccess = _isSuccess.asStateFlow()

    private val _isPending = MutableStateFlow(false)
    val isPending = _isPending.asStateFlow()

    private val _isError = MutableStateFlow(false)
    val isError = _isError.asStateFlow()
    private val _error = MutableStateFlow("")
    val error = _error.asStateFlow()

    fun register(username: String, email: String, password: String, onError: (String) -> Unit, onSuccess: () -> Unit) {
        resetStates()
        _isPending.value = true

        val input = UserInput(username, email, password)

        screenModelScope.launch {
            try {
                val res = repo.create(input)
                if (!res.isSuccess) throw Exception(res.message)

                _isSuccess.value = true
                onSuccess()
            }
            catch (e: Exception) {
                _isError.value = true
                _error.value = e.message ?: "unexpected error"
                onError(_error.value)
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