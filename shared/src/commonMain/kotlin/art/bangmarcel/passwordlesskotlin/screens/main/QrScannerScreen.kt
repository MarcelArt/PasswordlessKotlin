package art.bangmarcel.passwordlesskotlin.screens.main

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import art.bangmarcel.passwordlesskotlin.viewmodels.QrScannerViewModel
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.koin.koinScreenModel
import org.publicvalue.multiplatform.qrcode.CameraPosition
import org.publicvalue.multiplatform.qrcode.CodeType
import org.publicvalue.multiplatform.qrcode.ScannerWithPermissions

class QrScannerScreen: Screen {
    @Composable
    override fun Content() {
        val viewModel = koinScreenModel<QrScannerViewModel>()

        Box(modifier = Modifier.fillMaxSize()) {
            ScannerWithPermissions(
                onScanned = {
                    viewModel.qrApprove(
                        sessionId = it,
                        onError = {},
                        onSuccess = {},
                    )
                    true
                }, // return true to disable the scanner, false to continue scanning
                types = listOf(CodeType.QR),
                cameraPosition = CameraPosition.BACK,
                enableTorch = false, // toggle this to enable/disable the flashlight
            )
        }
    }
}