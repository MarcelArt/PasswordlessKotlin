package art.bangmarcel.passwordlesskotlin.screens.main

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
//import androidx.compose.material.icons.Icons
//import androidx.compose.material.icons.filled.Check
//import androidx.compose.material.icons.filled.Close
//import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.CompositingStrategy
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import art.bangmarcel.passwordlesskotlin.enums.ViewModelStatus
import art.bangmarcel.passwordlesskotlin.viewmodels.QrScannerViewModel
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.koin.koinScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import kotlinx.coroutines.delay
import org.jetbrains.compose.resources.painterResource
import org.publicvalue.multiplatform.qrcode.CameraPosition
import org.publicvalue.multiplatform.qrcode.CodeType
import org.publicvalue.multiplatform.qrcode.ScannerWithPermissions
import passwordlesskotlin.shared.generated.resources.Res
import passwordlesskotlin.shared.generated.resources.check_24px
import passwordlesskotlin.shared.generated.resources.close_24px
import kotlin.time.Duration.Companion.milliseconds

class QrScannerScreen: Screen {
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val viewModel = koinScreenModel<QrScannerViewModel>()

        val status by viewModel.status.collectAsState()
        val error by viewModel.error.collectAsState()

        val primaryColor = MaterialTheme.colorScheme.primary
        val scrimColor = MaterialTheme.colorScheme.scrim

        // Auto-pop back to the main flow on success after 2 seconds
        LaunchedEffect(status) {
            if (status == ViewModelStatus.SUCCESS) {
                delay(2000.milliseconds)
                navigator.pop()
            }
        }

        Box(modifier = Modifier.fillMaxSize()) {
            // 1. Camera QR Scanner component
            ScannerWithPermissions(
                onScanned = { sessionId ->
                    if (status == ViewModelStatus.NONE) {
                        viewModel.qrApprove(
                            sessionId = sessionId,
                            onError = { println("Approval failed: $it") },
                            onSuccess = { println("Approval success: $it") }
                        )
                        true
                    } else {
                        false
                    }
                },
                types = listOf(CodeType.QR),
                cameraPosition = CameraPosition.BACK,
                enableTorch = false,
            )

            // 2. Translucent Cutout Overlay & Scanning Reticle Animation
            val infiniteTransition = rememberInfiniteTransition()
            val scanLineProgress by infiniteTransition.animateFloat(
                initialValue = 0f,
                targetValue = 1f,
                animationSpec = infiniteRepeatable(
                    animation = tween(durationMillis = 2000, easing = LinearEasing),
                    repeatMode = RepeatMode.Reverse
                )
            )

            Canvas(
                modifier = Modifier
                    .fillMaxSize()
                    .graphicsLayer(compositingStrategy = CompositingStrategy.Offscreen)
            ) {
                // Dim camera feed surrounding the target area
                drawRect(color = scrimColor.copy(alpha = 0.7f))

                val cutoutSizePx = 280.dp.toPx()
                val x = (size.width - cutoutSizePx) / 2
                val y = (size.height - cutoutSizePx) / 2

                // Clear pixels in the center to create scanning viewfinder
                drawRoundRect(
                    color = Color.Transparent,
                    topLeft = Offset(x, y),
                    size = Size(cutoutSizePx, cutoutSizePx),
                    cornerRadius = CornerRadius(24f, 24f),
                    blendMode = BlendMode.Clear
                )

                // Neon primary border frame around the cutout
                drawRoundRect(
                    color = primaryColor,
                    topLeft = Offset(x - 2, y - 2),
                    size = Size(cutoutSizePx + 4, cutoutSizePx + 4),
                    cornerRadius = CornerRadius(24f, 24f),
                    style = Stroke(width = 3.dp.toPx())
                )

                // Scanning laser line animation
                val scanLineY = y + 12.dp.toPx() + (cutoutSizePx - 24.dp.toPx()) * scanLineProgress
                drawLine(
                    brush = Brush.horizontalGradient(
                        colors = listOf(
                            Color.Transparent,
                            primaryColor.copy(alpha = 0.8f),
                            Color.Transparent
                        )
                    ),
                    start = Offset(x + 12.dp.toPx(), scanLineY),
                    end = Offset(x + cutoutSizePx - 12.dp.toPx(), scanLineY),
                    strokeWidth = 3.dp.toPx()
                )
            }

            // 3. UI Controls and Guidelines
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp)
            ) {
                // Top Action Bar: Close Button
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Start
                ) {
                    IconButton(
                        onClick = { navigator.pop() },
                        colors = IconButtonDefaults.iconButtonColors(
                            containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.6f),
                            contentColor = MaterialTheme.colorScheme.onSurface
                        )
                    ) {
                        Icon(
                            painter = painterResource(Res.drawable.close_24px),
                            contentDescription = "Close Scanner"
                        )
                    }
                }

                Spacer(modifier = Modifier.weight(1f))

                // Informative Card for Instructions
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.9f)
                    ),
                    shape = MaterialTheme.shapes.large,
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "Scan Web QR",
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Point your camera at the QR code displayed on the web app to log in automatically.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f),
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }

            // 4. Overlays representing different authentication phases
            when (status) {
                ViewModelStatus.PENDING -> {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(scrimColor.copy(alpha = 0.85f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Card(
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.surface
                            ),
                            shape = MaterialTheme.shapes.large,
                            modifier = Modifier
                                .width(300.dp)
                                .padding(24.dp)
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(24.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                CircularProgressIndicator(
                                    color = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(48.dp)
                                )
                                Spacer(modifier = Modifier.height(24.dp))
                                Text(
                                    text = "Approving Login",
                                    style = MaterialTheme.typography.titleMedium,
                                    color = MaterialTheme.colorScheme.onSurface,
                                    textAlign = TextAlign.Center
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = "Authorizing access to the web application...",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    textAlign = TextAlign.Center
                                )
                            }
                        }
                    }
                }

                ViewModelStatus.SUCCESS -> {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(scrimColor.copy(alpha = 0.85f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Card(
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.surface
                            ),
                            shape = MaterialTheme.shapes.large,
                            modifier = Modifier
                                .width(300.dp)
                                .padding(24.dp)
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(24.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Icon(
                                    painter = painterResource(Res.drawable.check_24px),
                                    contentDescription = "Success",
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier
                                        .size(64.dp)
                                        .background(
                                            color = MaterialTheme.colorScheme.primaryContainer,
                                            shape = MaterialTheme.shapes.small
                                        )
                                        .padding(12.dp)
                                )
                                Spacer(modifier = Modifier.height(24.dp))
                                Text(
                                    text = "Login Approved!",
                                    style = MaterialTheme.typography.titleLarge,
                                    color = MaterialTheme.colorScheme.onSurface,
                                    textAlign = TextAlign.Center
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = "You are now logged in on the web app.",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    textAlign = TextAlign.Center
                                )
                            }
                        }
                    }
                }

                ViewModelStatus.ERROR -> {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(scrimColor.copy(alpha = 0.85f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Card(
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.surface
                            ),
                            shape = MaterialTheme.shapes.large,
                            modifier = Modifier
                                .width(300.dp)
                                .padding(24.dp)
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(24.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Icon(
                                    painter = painterResource(Res.drawable.close_24px),
                                    contentDescription = "Error",
                                    tint = MaterialTheme.colorScheme.error,
                                    modifier = Modifier
                                        .size(64.dp)
                                        .background(
                                            color = MaterialTheme.colorScheme.errorContainer,
                                            shape = MaterialTheme.shapes.small
                                        )
                                        .padding(12.dp)
                                )
                                Spacer(modifier = Modifier.height(24.dp))
                                Text(
                                    text = "Login Failed",
                                    style = MaterialTheme.typography.titleLarge,
                                    color = MaterialTheme.colorScheme.onSurface,
                                    textAlign = TextAlign.Center
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = error.ifEmpty { "An unexpected error occurred during authorization." },
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    textAlign = TextAlign.Center
                                )
                                Spacer(modifier = Modifier.height(24.dp))
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    OutlinedButton(
                                        onClick = { navigator.pop() },
                                        modifier = Modifier.weight(1f)
                                    ) {
                                        Text("Cancel")
                                    }
                                    Button(
                                        onClick = {
                                            // Recreate the screen to reset state
                                            navigator.replace(QrScannerScreen())
                                        },
                                        modifier = Modifier.weight(1f)
                                    ) {
                                        Text("Retry")
                                    }
                                }
                            }
                        }
                    }
                }
                else -> { /* Do nothing */ }
            }
        }
    }
}