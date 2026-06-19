package art.bangmarcel.passwordlesskotlin.viewmodels

import art.bangmarcel.passwordlesskotlin.enums.ViewModelStatus
import art.bangmarcel.passwordlesskotlin.repositories.AuthRepo
import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class QrScannerViewModel(private val repo: AuthRepo): ScreenModel {
    private val _status = MutableStateFlow(ViewModelStatus.NONE)
    val status = _status.asStateFlow()

    private val _error = MutableStateFlow("")
    val error = _error.asStateFlow()

    fun qrApprove(sessionId: String, onError: (String) -> Unit, onSuccess: (Boolean) -> Unit) {
        resetState()
        _status.value = ViewModelStatus.PENDING

        screenModelScope.launch {
            try {
                val result = repo.qrApprove(sessionId)
                val ok = result.unwrap()

                _status.value = ViewModelStatus.SUCCESS
                onSuccess(ok)
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