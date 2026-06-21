package art.bangmarcel.passwordlesskotlin.screens.main

import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import art.bangmarcel.passwordlesskotlin.screens.auth.LoginScreen
import art.bangmarcel.passwordlesskotlin.viewmodels.MainLayoutViewModel
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.koin.koinScreenModel
import cafe.adriel.voyager.navigator.CurrentScreen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.Navigator
import cafe.adriel.voyager.navigator.currentOrThrow

class MainLayoutScreen: Screen {
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val viewModel = koinScreenModel<MainLayoutViewModel>()

        val user = viewModel.user.collectAsState()
        val isLoggedIn = viewModel.isLoggedIn.collectAsState()


        Navigator(HomeScreen()) {
            Scaffold(
                bottomBar = {
                    BottomAppBar {  }
                }
            ) {
                CurrentScreen()
            }
        }
    }

}