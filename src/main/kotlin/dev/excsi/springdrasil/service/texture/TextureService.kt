package dev.excsi.springdrasil.service.texture

import dev.excsi.springdrasil.exception.YggdrasilException
import dev.excsi.springdrasil.isUnhyphenatedUuidValid
import dev.excsi.springdrasil.model.SkinModel
import dev.excsi.springdrasil.model.Texture
import dev.excsi.springdrasil.model.TextureType
import dev.excsi.springdrasil.service.yggdrasil.YggdrasilSessionValidationService
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class TextureService(
    val textureStorageService: TextureStorageService,
    val yggdrasilSessionValidationService: YggdrasilSessionValidationService,
) {

    @Transactional
    fun uploadTexture(
        uuid: String,
        textureType: TextureType,
        authorizationHeader: String,
        bytes: ByteArray,
        model: String?,
    ): Texture {
        if (!uuid.isUnhyphenatedUuidValid()) {
            throw YggdrasilException(HttpStatus.BAD_REQUEST, "IllegalArgumentException", "Unhyphenated UUID required.")
        }
        val accessToken = sanitizeAuthorizationHeader(authorizationHeader)

        val profile = yggdrasilSessionValidationService.validateTokenAgainstProfileId(accessToken, uuid)

        if (textureType == TextureType.SKIN) {
            profile.skinModel =
                if (model.equals("slim", ignoreCase = true))
                    SkinModel.SLIM
                else
                    SkinModel.DEFAULT
        }

        val sanitizedBytes = textureStorageService.sanitize(textureType, bytes)
        return textureStorageService.save(profile, textureType, sanitizedBytes)
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

        val profile = yggdrasilSessionValidationService.validateTokenAgainstProfileId(accessToken, uuid)
        val texture = profile.textures.firstOrNull {
            it.textureType == textureType
        }

        texture?.let {
            profile.textures.remove(it)
            textureStorageService.delete(it)
        }
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

    fun getTexture(textureHash: String): Texture {
        return textureStorageService.findByHash(textureHash)
    }
}