package art.bangmarcel.passwordlesskotlin.viewmodels

import art.bangmarcel.passwordlesskotlin.enums.ViewModelStatus
import art.bangmarcel.passwordlesskotlin.models.User
import art.bangmarcel.passwordlesskotlin.repositories.TokenRepo
import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class HomeViewModel(private val tRepo: TokenRepo): ScreenModel {
    private val _status = MutableStateFlow(ViewModelStatus.NONE)
    val status = _status.asStateFlow()

    private val _error = MutableStateFlow<String>("")
    val error = _error.asStateFlow()

    private val _data = MutableStateFlow<User?>(null)
    val data = _data.asStateFlow()

    fun getCurrentUser() {
        _status.value = ViewModelStatus.PENDING
        if (tRepo.user.value != null) {
            _data.value = tRepo.user.value
            _status.value = ViewModelStatus.SUCCESS
            return
        }

        screenModelScope.launch {
            try {
                val user = tRepo.current()
                _data.value = user.unwrap()
                _status.value = ViewModelStatus.SUCCESS
            }
            catch (e: Exception) {
                _status.value = ViewModelStatus.ERROR
                _error.value = e.message ?: "unknown error"
            }
        }
    }

    fun logout() {
        _status.value = ViewModelStatus.PENDING
        tRepo.logout()
        _status.value = ViewModelStatus.SUCCESS
    }
}