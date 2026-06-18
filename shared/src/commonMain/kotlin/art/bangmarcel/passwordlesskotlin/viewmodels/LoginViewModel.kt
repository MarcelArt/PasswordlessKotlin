package art.bangmarcel.passwordlesskotlin.viewmodels

import androidx.compose.runtime.mutableStateOf
import art.bangmarcel.passwordlesskotlin.enums.ViewModelStatus
import art.bangmarcel.passwordlesskotlin.models.LoginInput
import art.bangmarcel.passwordlesskotlin.models.LoginResponse
import art.bangmarcel.passwordlesskotlin.repositories.UserRepo
import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class LoginViewModel(private val repo: UserRepo): ScreenModel {
    private val _status = MutableStateFlow(ViewModelStatus.NONE)
    val status = _status.asStateFlow()

    private val _error = MutableStateFlow("")
    val error = _error.asStateFlow()

    fun login(username: String, password: String, isRemember: Boolean, onError: (String) -> Unit, onSuccess: (LoginResponse) -> Unit) {
        resetState()
        _status.value = ViewModelStatus.PENDING

        val input = LoginInput(username, password, isRemember)

        screenModelScope.launch {
            try {
                val res = repo.login(input)
                if (!res.isSuccess  || res.items == null) throw Exception(res.message)

                _status.value = ViewModelStatus.SUCCESS
                onSuccess(res.items)
            }
            catch (e: Exception) {
                _status.value = ViewModelStatus.ERROR
                _error.value = e.message ?: "unexpected error"

                onError(_error.value)
            }
        }
    }

    private fun resetState() {
        _status.value = ViewModelStatus.NONE
        _error.value = ""
    }
}