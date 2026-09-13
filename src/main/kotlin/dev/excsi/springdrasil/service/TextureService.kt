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
import java.util.LinkedHashMap

@Service
class TextureService(
    val objectMapper: ObjectMapper,
    val yggdrasilSignatureService: YggdrasilSignatureService,
) {

    fun toTexturesProperty(profile: Profile, textureBaseUrl: String, sign: Boolean): Property? {
        if (profile.textures.isEmpty()) {
            return null
        }

        val textures = LinkedHashMap<TextureType, TextureData>()
        for (texture in profile.textures) {
            val textureData = toTextureData(profile, texture, textureBaseUrl)
            textures[texture.textureType] = textureData
        }

        val textureDto = TextureDto(
            profileId = profile.id.unhyphenatedString(),
            profileName = profile.profileUsername,
            textures = textures,
        )

        val value = textureDtoToString(textureDto)

        return Property(
            name = "textures",
            value = value,
            signature = if (sign) yggdrasilSignatureService.sign(value) else null,
        )
    }

    fun textureDtoToString(textureDto: TextureDto): String {
        val jsonString = objectMapper.writeValueAsString(textureDto)
        return Base64.getEncoder().encodeToString(jsonString.toByteArray(Charsets.UTF_8))
    }

    fun hashTexture(bytes: ByteArray): String {
        val digest = MessageDigest.getInstance("SHA-256").digest(bytes)
        val hash = StringBuilder()

        for (byte in digest) {
            hash.append("%02x".format(byte))
        }

        return hash.toString()
    }

    private fun toTextureData(profile: Profile, texture: Texture, textureBaseUrl: String): TextureData {
        val metadata: Map<String, String>?
        if (texture.textureType == TextureType.SKIN) {
            val skinMetadata = HashMap<String, String>()
            skinMetadata["model"] = profile.skinType.name()
            metadata = skinMetadata
        } else {
            metadata = null
        }

        return TextureData(
            url = textureUrl(textureBaseUrl, texture.textureHash),
            metadata = metadata
        )
    }

    private fun textureUrl(textureBaseUrl: String, textureHash: String): String {
        return "${textureBaseUrl.trimEnd('/')}/textures/$textureHash"
    }
}
