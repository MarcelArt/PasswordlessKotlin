package art.bangmarcel.passwordlesskotlin.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.autofill.ContentType
import androidx.compose.ui.autofill.contentType
import androidx.compose.ui.unit.dp
import art.bangmarcel.passwordlesskotlin.models.PasskeyManager
import art.bangmarcel.passwordlesskotlin.repositories.AuthRepo
import art.bangmarcel.passwordlesskotlin.viewmodels.BeginRegisterViewModel
import cafe.adriel.voyager.core.model.rememberScreenModel
import cafe.adriel.voyager.core.screen.Screen

class BeginRegisterScreen(private val repo: AuthRepo, private val passkeyManager: PasskeyManager): Screen {
    @Composable
    override fun Content() {
        val viewModel = rememberScreenModel { BeginRegisterViewModel(repo, passkeyManager) }

        var username by remember { mutableStateOf("") }
        var email by remember { mutableStateOf("") }
        var isLoading by remember { mutableStateOf(false) }
        val statusMessage by viewModel.statusMessage.collectAsState()
        val isSuccess by viewModel.isSuccess.collectAsState()

        Scaffold { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(24.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Title and Description
                Text(
                    text = "Register with Passkey",
                    style = MaterialTheme.typography.headlineMedium,
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                Text(
                    text = "Secure your account using your device's biometrics.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(bottom = 24.dp)
                )

                // Input Field
                OutlinedTextField(
                    value = username,
                    onValueChange = { username = it },
                    label = { Text("Username") },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = !isLoading
                )
                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it },
                    label = { Text("Email") },
                    modifier = Modifier.fillMaxWidth().contentType(ContentType.EmailAddress),
                    enabled = !isLoading
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Feedback Message Display
                statusMessage?.let { message ->
                    Text(
                        text = message,
                        color = if (isSuccess) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )
                }

                // Submit Button
                Button(
                    onClick = {
                        isLoading = true

                        viewModel.registerBegin(username, email) {
                            viewModel.registerFinish(
                                it,
                                username,
                                email,
                                { e ->
                                    println("failed: $e")
                                },
                            ) {
                                println("Registered successfully")
                            }
                        }

                        isLoading = false
                    },
                    modifier = Modifier.fillMaxWidth().height(50.dp),
                    enabled = !isLoading
                ) {
                    if (isLoading) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(24.dp),
                            color = MaterialTheme.colorScheme.onPrimary
                        )
                    } else {
                        Text("Create Passkey")
                    }
                }
            }
        }
    }

}