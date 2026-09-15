package dev.excsi.springdrasil.controller.authlib

import dev.excsi.springdrasil.exception.YggdrasilException
import dev.excsi.springdrasil.model.TextureType
import dev.excsi.springdrasil.service.TextureService
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestPart
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.multipart.MultipartFile

@RestController
@RequestMapping($$"${springdrasil.application.authlib-prefix}")
class TextureController(
    val textureService: TextureService,
) {

    @GetMapping("textures/{textureHash}")
    fun getTexture(@PathVariable textureHash: String): ResponseEntity<ByteArray> {
        val texture = textureService.getTexture(textureHash)

        return ResponseEntity.ok()
            .contentType(MediaType.IMAGE_PNG)
            .body(texture.byteArray)
    }

    @PutMapping("api/user/profile/{uuid}/{textureType}", consumes = [MediaType.MULTIPART_FORM_DATA_VALUE])
    fun uploadTexture(
        @PathVariable uuid: String,
        @PathVariable textureType: TextureType,
        @RequestHeader("Authorization") authorizationHeaderVal: String,
        @RequestPart("model", required = false) model: String?,
        @RequestPart("file") file: MultipartFile,
    ): ResponseEntity<Void> {
        if (file.contentType != MediaType.IMAGE_PNG_VALUE) {
            throw YggdrasilException(HttpStatus.BAD_REQUEST, "IllegalArgumentException", "Texture must be a PNG image.")
        }

        textureService.uploadTexture(
            uuid,
            textureType,
            authorizationHeaderVal,
            file,
            model
        )

        return ResponseEntity.noContent().build()
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping("api/user/profile/{uuid}/{textureType}")
    fun deleteTexture(
        @PathVariable uuid: String,
        @PathVariable textureType: TextureType,
        @RequestHeader("Authorization") authorizationHeaderVal: String
    ) {
        textureService.deleteTexture(uuid, textureType, authorizationHeaderVal)
    }
}