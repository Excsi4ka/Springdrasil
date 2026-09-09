package dev.excsi.springdrasil.service

import dev.excsi.springdrasil.dto.TextureDto
import org.springframework.stereotype.Service
import tools.jackson.databind.ObjectMapper
import java.security.MessageDigest
import java.util.Base64

@Service
class TextureService(
    val objectMapper: ObjectMapper,
) {

    fun textureDtoToString(textureDto: TextureDto): String {
        val jsonString = objectMapper.writeValueAsString(textureDto)
        return Base64.getEncoder().encodeToString(jsonString.toByteArray())
    }

    fun hashTexture(bytes: ByteArray): String {
        val digest = MessageDigest.getInstance("SHA-256").digest(bytes)

        return digest.joinToString("") {
            byte -> "%02x".format(byte)
        }
    }
}