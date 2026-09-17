package dev.excsi.springdrasil.service

import dev.excsi.springdrasil.exception.YggdrasilException
import dev.excsi.springdrasil.isUnhyphenatedUuidValid
import dev.excsi.springdrasil.model.SkinModel
import dev.excsi.springdrasil.model.Texture
import dev.excsi.springdrasil.model.TextureType
import dev.excsi.springdrasil.repository.TextureRepository
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.multipart.MultipartFile
import java.security.MessageDigest

@Service
class TextureService(
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

    fun hashTexture(bytes: ByteArray): String {
        val digest = MessageDigest.getInstance("SHA-256").digest(bytes)
        val hash = StringBuilder()

        for (byte in digest) {
            hash.append("%02x".format(byte))
        }

        return hash.toString()
    }
}
