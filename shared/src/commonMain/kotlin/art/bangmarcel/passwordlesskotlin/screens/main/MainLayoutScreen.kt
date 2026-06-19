package art.bangmarcel.passwordlesskotlin.screens.main

import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.Navigator

class MainLayoutScreen: Screen {
    @Composable
    override fun Content() {
        Scaffold { paddingValues ->
            Navigator(QrScannerScreen())
        }
    }

}