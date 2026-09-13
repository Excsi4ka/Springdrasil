package dev.excsi.springdrasil.controller.authlib

import dev.excsi.springdrasil.service.TextureService
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RestController

@RestController
class TextureController(
    val textureService: TextureService,
) {

    @GetMapping("/textures/{textureHash}")
    fun getTexture(@PathVariable textureHash: String): ResponseEntity<ByteArray> {
        val texture = textureService.getTexture(textureHash)

        return ResponseEntity.ok()
            .contentType(MediaType.IMAGE_PNG)
            .body(texture.byteArray)
    }
}