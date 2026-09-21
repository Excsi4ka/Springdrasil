package dev.excsi.springdrasil.service

import dev.excsi.springdrasil.configuration.properties.AuthlibConfigurationProperties
import org.springframework.stereotype.Service
import java.security.KeyFactory
import java.security.PrivateKey
import java.security.Signature
import java.security.spec.PKCS8EncodedKeySpec
import java.util.Base64

@Service
class SignatureService(
    authlibConfigurationProperties: AuthlibConfigurationProperties,
) {

    private val privateKey = parsePrivateKey(authlibConfigurationProperties.yggdrasilSignaturePrivateKey)

    val publicKeyPem: String = authlibConfigurationProperties.yggdrasilSignaturePublicKey

    fun sign(value: String): String {
        val signer = Signature.getInstance("SHA1withRSA")
        signer.initSign(privateKey)
        signer.update(value.toByteArray(Charsets.UTF_8))
        return Base64.getEncoder().encodeToString(signer.sign())
    }

    private fun parsePrivateKey(pem: String): PrivateKey {
        val der = decodePem(pem)
        return KeyFactory.getInstance("RSA")
            .generatePrivate(PKCS8EncodedKeySpec(der))
    }

    private fun decodePem(pem: String): ByteArray {
        val body = pem
            .replace(Regex("-----BEGIN [^-]+-----"), "")
            .replace(Regex("-----END [^-]+-----"), "")
            .replace(Regex("\\s"), "")

        return Base64.getDecoder().decode(body)
    }
}