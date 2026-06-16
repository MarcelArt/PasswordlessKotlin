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
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import art.bangmarcel.passwordlesskotlin.models.PasskeyManager
import art.bangmarcel.passwordlesskotlin.repositories.AuthRepo
import art.bangmarcel.passwordlesskotlin.viewmodels.LoginViewModel
import cafe.adriel.voyager.core.model.rememberScreenModel
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow

class LoginScreen(private val repo: AuthRepo, private val passkeyManager: PasskeyManager): Screen {
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val viewModel = rememberScreenModel { LoginViewModel(repo, passkeyManager) }

        var username by remember { mutableStateOf("") }
        val isPending by viewModel.isPending.collectAsState()
        val isError by viewModel.isError.collectAsState()
        val error by viewModel.error.collectAsState()
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
                    text = "Sign In with Passkey",
                    style = MaterialTheme.typography.headlineMedium,
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                Text(
                    text = "Sign in securely using your registered passkey.",
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
                    enabled = !isPending
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Feedback Message Display
                if (isError && error.isNotEmpty()) {
                    Text(
                        text = error,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )
                }

                if (isSuccess) {
                    Text(
                        text = "Signed in successfully!",
                        color = MaterialTheme.colorScheme.primary,
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )
                }

                // Submit Button
                Button(
                    onClick = {
                        viewModel.login(
                            username = username,
                            onFailure = { e ->
                                println("Login failed: $e")
                            },
                            onSuccess = {
                                println("Welcome ${it.user.username}")
                            }
                        )
//                        viewModel.loginBegin(
//                            username = username,
//                            onFailure = { e ->
//                                println("Login failed: $e")
//                            },
//                            onSuccess = {
//                                viewModel.loginFinish(
//                                    loginData = it,
//                                    username = username,
//                                    onFailure = { e ->
//                                        println("Login failed: $e")
//                                    },
//                                    onSuccess = { user ->
//                                        println("Welcome $user")
//                                    }
//                                )
//                            }
//                        )
                    },
                    modifier = Modifier.fillMaxWidth().height(50.dp),
                    enabled = !isPending && username.isNotBlank()
                ) {
                    if (isPending) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(24.dp),
                            color = MaterialTheme.colorScheme.onPrimary
                        )
                    } else {
                        Text("Sign In")
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                TextButton(
                    onClick = {
                        navigator.push(RegisterScreen(repo, passkeyManager))
                    },
                    enabled = !isPending
                ) {
                    Text("Don't have an account? Register")
                }
            }
        }
    }
}