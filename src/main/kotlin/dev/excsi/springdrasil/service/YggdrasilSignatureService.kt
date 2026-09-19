package dev.excsi.springdrasil.service

import dev.excsi.springdrasil.configuration.AuthlibConfigurationValues
import org.springframework.stereotype.Service
import java.security.KeyFactory
import java.security.PrivateKey
import java.security.PublicKey
import java.security.Signature
import java.security.spec.PKCS8EncodedKeySpec
import java.security.spec.X509EncodedKeySpec
import java.util.Base64

@Service
class YggdrasilSignatureService(
    authlibConfigurationValues: AuthlibConfigurationValues,
) {
    private val privateKey = parsePrivateKey(authlibConfigurationValues.yggdrasilSignaturePrivateKey)

    private val publicKey = parsePublicKey(authlibConfigurationValues.yggdrasilSignaturePublicKey)

    val publicKeyPem: String = authlibConfigurationValues.yggdrasilSignaturePublicKey

    fun sign(value: String): String = sign(value.toByteArray(Charsets.UTF_8))

    private fun sign(value: ByteArray): String {
        val signer = Signature.getInstance("SHA1withRSA")
        signer.initSign(privateKey)
        signer.update(value)
        return Base64.getEncoder().encodeToString(signer.sign())
    }

    private fun verify(value: ByteArray, encodedSignature: String): Boolean {
        val verifier = Signature.getInstance("SHA1withRSA")
        verifier.initVerify(publicKey)
        verifier.update(value)
        return verifier.verify(Base64.getDecoder().decode(encodedSignature))
    }

    private fun parsePrivateKey(pem: String): PrivateKey {
        val der = decodePem(pem)
        return KeyFactory.getInstance("RSA")
            .generatePrivate(PKCS8EncodedKeySpec(der))
    }

    private fun parsePublicKey(pem: String): PublicKey {
        val der = decodePem(pem)
        return KeyFactory.getInstance("RSA")
            .generatePublic(X509EncodedKeySpec(der))
    }

    private fun decodePem(pem: String): ByteArray {
        val body = pem
            .replace(Regex("-----BEGIN [^-]+-----"), "")
            .replace(Regex("-----END [^-]+-----"), "")
            .replace(Regex("\\s"), "")

        return Base64.getDecoder().decode(body)
    }
}
