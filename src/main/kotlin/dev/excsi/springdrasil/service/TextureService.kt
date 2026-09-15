package dev.excsi.springdrasil.service

import dev.excsi.springdrasil.dto.Property
import dev.excsi.springdrasil.dto.TextureData
import dev.excsi.springdrasil.dto.TextureDto
import dev.excsi.springdrasil.exception.YggdrasilException
import dev.excsi.springdrasil.isUnhyphenatedUuidValid
import dev.excsi.springdrasil.model.Profile
import dev.excsi.springdrasil.model.SkinModel
import dev.excsi.springdrasil.model.Texture
import dev.excsi.springdrasil.model.TextureType
import dev.excsi.springdrasil.repository.TextureRepository
import dev.excsi.springdrasil.unhyphenatedString
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.multipart.MultipartFile
import tools.jackson.databind.ObjectMapper
import java.security.MessageDigest
import java.util.Base64
import java.util.LinkedHashMap

@Service
class TextureService(
    val objectMapper: ObjectMapper,
    val yggdrasilSignatureService: YggdrasilSignatureService,
    val textureRepository: TextureRepository,
    val sessionTokenService: SessionTokenService,
) {

    @Transactional
    fun uploadTexture(
        uuid: String,
        textureType: TextureType,
        authorizationHeader: String,
        file: MultipartFile,
        model: String?
    ) {
        if (!uuid.isUnhyphenatedUuidValid()) {
            throw YggdrasilException(HttpStatus.BAD_REQUEST, "IllegalArgumentException", "Unhyphenated UUID required.")
        }
        val accessToken = sanitizeAuthorizationHeader(authorizationHeader)

        val profile = sessionTokenService.validateTokenAgainstProfileId(accessToken, uuid)



        val existingTexture = textureRepository.findTextureByProfileIdAndTextureType(profile.id, textureType)

        if (textureType == TextureType.SKIN) {
            profile.skinModel =
                if (model.equals("slim", ignoreCase = true))
                    SkinModel.SLIM
                else
                    SkinModel.DEFAULT
        }

        val textureData = file.bytes
        val textureHash = hashTexture(textureData)
        val texture = existingTexture?.apply {
            this.textureHash = textureHash
            this.byteArray = textureData
        } ?: Texture(
            profile = profile,
            textureType = textureType,
            textureHash = textureHash,
            byteArray = textureData
        )

        textureRepository.save(texture)
    }

    @Transactional
    fun deleteTexture(
        uuid: String,
        textureType: TextureType,
        authorizationHeader: String
    ) {
        if (!uuid.isUnhyphenatedUuidValid()) {
            throw YggdrasilException(HttpStatus.BAD_REQUEST, "IllegalArgumentException", "Unhyphenated UUID required.")
        }
        val accessToken = sanitizeAuthorizationHeader(authorizationHeader)

        val profile = sessionTokenService.validateTokenAgainstProfileId(accessToken, uuid)
        val texture = profile.textures.firstOrNull {
            it.textureType == textureType
        }

        texture?.let {
            profile.textures.remove(it)
            textureRepository.delete(it)
        }
    }

    fun getTexture(textureHash: String): Texture {
        return textureRepository.findTextureByTextureHash(textureHash)
            ?: throw YggdrasilException(HttpStatus.NOT_FOUND, "TextureNotFound", "Texture not found.")
    }

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
            skinMetadata["model"] = profile.skinModel.name()
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

    private fun sanitizeAuthorizationHeader(value: String): String {
        val token = if (value.startsWith("Bearer "))
            value.substringAfter("Bearer ")
        else
            value

        if (!token.isUnhyphenatedUuidValid()) {
            throw YggdrasilException(HttpStatus.UNAUTHORIZED, "IllegalArgumentException", "Unhyphenated UUID required.")
        }

        return token
    }
}
