package art.bangmarcel.passwordlesskotlin

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import art.bangmarcel.passwordlesskotlin.models.PasskeyManager

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)

        val passkeyManager = PasskeyManager(this)

        setContent {
            App(passkeyManager)
        }
    }
}

@Preview
@Composable
fun AppAndroidPreview() {
//    App()
}