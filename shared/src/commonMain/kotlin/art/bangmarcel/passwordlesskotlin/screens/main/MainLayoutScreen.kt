package art.bangmarcel.passwordlesskotlin.screens.main

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import art.bangmarcel.passwordlesskotlin.components.icons.home
import art.bangmarcel.passwordlesskotlin.components.icons.qr_code_scanner
import art.bangmarcel.passwordlesskotlin.enums.AuthState
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

        val authState by viewModel.authState.collectAsState()

        // Handle auto-redirection back to LoginScreen when user logs out
        LaunchedEffect(authState) {
            if (authState == AuthState.UNAUTHENTICATED) {
                navigator.pop()
            }
        }

        Navigator(HomeScreen()) { nestedNavigator ->
            Scaffold(
                bottomBar = {
                    NavigationBar {
                        NavigationBarItem(
                            selected = nestedNavigator.lastItem is HomeScreen,
                            onClick = {
                                if (nestedNavigator.lastItem !is HomeScreen) {
                                    nestedNavigator.replaceAll(HomeScreen())
                                }
                            },
                            icon = {
                                Icon(
                                    imageVector = home,
                                    contentDescription = "Home"
                                )
                            },
                            label = { Text("Home") }
                        )
                        NavigationBarItem(
                            selected = nestedNavigator.lastItem is QrScannerScreen,
                            onClick = {
                                if (nestedNavigator.lastItem !is QrScannerScreen) {
                                    nestedNavigator.replaceAll(QrScannerScreen())
                                }
                            },
                            icon = {
                                Icon(
                                    imageVector = qr_code_scanner,
                                    contentDescription = "Scan QR"
                                )
                            },
                            label = { Text("Scan QR") }
                        )
                    }
                }
            ) { paddingValues ->
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                ) {
                    CurrentScreen()
                }
            }
        }
    }
}