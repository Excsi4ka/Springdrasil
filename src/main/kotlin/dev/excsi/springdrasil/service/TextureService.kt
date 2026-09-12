package dev.excsi.springdrasil.service

import dev.excsi.springdrasil.dto.Property
import dev.excsi.springdrasil.dto.TextureData
import dev.excsi.springdrasil.dto.TextureDto
import dev.excsi.springdrasil.model.Profile
import dev.excsi.springdrasil.model.Texture
import dev.excsi.springdrasil.model.TextureType
import dev.excsi.springdrasil.unhyphenatedString
import org.springframework.stereotype.Service
import tools.jackson.databind.ObjectMapper
import java.security.MessageDigest
import java.util.Base64

@Service
class TextureService(
    val objectMapper: ObjectMapper,
) {

    fun toTexturesProperty(profile: Profile, textureBaseUrl: String): Property? {
        if (profile.textures.isEmpty()) {
            return null
        }

        val textureDto = TextureDto(
            profileId = profile.id.unhyphenatedString(),
            profileName = profile.profileUsername,
            textures = profile.textures.associate { texture ->
                texture.textureType to toTextureData(profile, texture, textureBaseUrl)
            }
        )

        return Property(
            name = "textures",
            value = textureDtoToString(textureDto)
        )
    }

    fun textureDtoToString(textureDto: TextureDto): String {
        val jsonString = objectMapper.writeValueAsString(textureDto)
        return Base64.getEncoder().encodeToString(jsonString.toByteArray(Charsets.UTF_8))
    }

    fun hashTexture(bytes: ByteArray): String {
        val digest = MessageDigest.getInstance("SHA-256").digest(bytes)

        return digest.joinToString("") {
            byte -> "%02x".format(byte)
        }
    }

    private fun toTextureData(profile: Profile, texture: Texture, textureBaseUrl: String): TextureData {
        val metadata = if (texture.textureType == TextureType.SKIN) {
            mapOf("model" to profile.skinType.name())
        } else null

        return TextureData(
            url = textureUrl(textureBaseUrl, texture.textureHash),
            metadata = metadata
        )
    }

    private fun textureUrl(textureBaseUrl: String, textureHash: String): String {
        return "${textureBaseUrl.trimEnd('/')}/textures/$textureHash"
    }
}
