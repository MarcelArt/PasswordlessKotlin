package art.bangmarcel.passwordlesskotlin.screens.main

import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import cafe.adriel.voyager.core.screen.Screen

class MainLayoutScreen: Screen {
    @Composable
    override fun Content() {
        Scaffold { paddingValues ->
            Text("Welcome back")
        }
    }

}