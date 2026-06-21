package art.bangmarcel.passwordlesskotlin.viewmodels

import art.bangmarcel.passwordlesskotlin.repositories.TokenRepo
import cafe.adriel.voyager.core.model.ScreenModel

class MainLayoutViewModel(private val tRepo: TokenRepo): ScreenModel {
    val user = tRepo.user
    val authState = tRepo.authState

    fun logout() {
        tRepo.logout()
    }
}