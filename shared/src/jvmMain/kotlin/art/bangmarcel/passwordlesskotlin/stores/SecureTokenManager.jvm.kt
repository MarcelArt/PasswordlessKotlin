package art.bangmarcel.passwordlesskotlin.stores

import java.io.File
import java.nio.charset.StandardCharsets
import java.security.spec.KeySpec
import java.util.Base64
import java.util.Properties
import javax.crypto.Cipher
import javax.crypto.SecretKey
import javax.crypto.SecretKeyFactory
import javax.crypto.spec.GCMParameterSpec
import javax.crypto.spec.PBEKeySpec
import javax.crypto.spec.SecretKeySpec

actual class SecureTokenManager {

    private val appDirectory = File(System.getProperty("user.home"), ".your_app_secure_storage")
    private val configFile = File(appDirectory, "config.enc")

    // Salt for key derivation (Keep this constant across application launches)
    private val salt = byteArrayOf(0x43, 0x76, -0x2f, 0x5a, 0x11, 0x7e, 0x3d, -0x12)
    private val ivLength = 12
    private val tLen = 128

    init {
        if (!appDirectory.exists()) {
            appDirectory.mkdirs()
        }
    }

    actual fun saveTokens(accessToken: String, refreshToken: String) {
        val properties = Properties()
        properties.setProperty("access_token", accessToken)
        properties.setProperty("refresh_token", refreshToken)

        val rawData = properties.toXmlString()
        val encryptedData = encrypt(rawData)

        configFile.writeText(encryptedData)
    }

    actual fun getAccessToken(): String? = loadProperties()?.getProperty("access_token")

    actual fun getRefreshToken(): String? = loadProperties()?.getProperty("refresh_token")

    actual fun clearTokens() {
        if (configFile.exists()) {
            configFile.delete()
        }
    }

    private fun loadProperties(): Properties? {
        if (!configFile.exists()) return null
        return try {
            val encryptedData = configFile.readText()
            val decryptedXml = decrypt(encryptedData) ?: return null
            val properties = Properties()
            properties.loadFromXML(decryptedXml.byteInputStream(StandardCharsets.UTF_8))
            properties
        } catch (e: Exception) {
            null // File corrupted or key changed
        }
    }

    // --- Cryptography Engine (AES-GCM-256) ---

    private fun getSecretKey(): SecretKey {
        // Generate a password based on machine-specific parameters to tie encryption to the hardware
        val hardwarePassword = (System.getProperty("user.name") + System.getProperty("os.name")).toCharArray()
        val factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256")
        val spec: KeySpec = PBEKeySpec(hardwarePassword, salt, 65536, 256)
        val tmp = factory.generateSecret(spec)
        return SecretKeySpec(tmp.encoded, "AES")
    }

    private fun encrypt(plainText: String): String {
        val cipher = Cipher.getInstance("AES/GCM/NoPadding")
        cipher.init(Cipher.ENCRYPT_MODE, getSecretKey())
        val iv = cipher.iv
        val encryptedBytes = cipher.doFinal(plainText.toByteArray(StandardCharsets.UTF_8))

        // Combine IV and Encrypted Bytes so it can be safely stored together
        val combined = ByteArray(iv.size + encryptedBytes.size)
        System.arraycopy(iv, 0, combined, 0, iv.size)
        System.arraycopy(encryptedBytes, 0, combined, iv.size, encryptedBytes.size)

        return Base64.getEncoder().encodeToString(combined)
    }

    private fun decrypt(encryptedBase64: String): String? {
        return try {
            val combined = Base64.getDecoder().decode(encryptedBase64)
            val iv = combined.copyOfRange(0, ivLength)
            val encryptedBytes = combined.copyOfRange(ivLength, combined.size)

            val cipher = Cipher.getInstance("AES/GCM/NoPadding")
            val spec = GCMParameterSpec(tLen, iv)
            cipher.init(Cipher.DECRYPT_MODE, getSecretKey(), spec)

            String(cipher.doFinal(encryptedBytes), StandardCharsets.UTF_8)
        } catch (e: Exception) {
            null
        }
    }

    // Helper extension to serialize properties map cleanly
    private fun Properties.toXmlString(): String {
        val outputStream = java.io.ByteArrayOutputStream()
        this.storeToXML(outputStream, "Tokens", "UTF-8")
        return outputStream.toString("UTF-8")
    }
}