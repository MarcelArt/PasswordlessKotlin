package art.bangmarcel.passwordlesskotlin

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import art.bangmarcel.passwordlesskotlin.models.PasskeyManager
import art.bangmarcel.passwordlesskotlin.stores.SecureTokenManager

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)

        val passkeyManager = PasskeyManager(this)
        val secureTokenManager = SecureTokenManager(this)

        setContent {
            App(secureTokenManager)
        }
    }
}

@Preview
@Composable
fun AppAndroidPreview() {
//    App()
}