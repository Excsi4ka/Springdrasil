package dev.excsi.springdrasil.service.texture

import dev.excsi.springdrasil.exception.YggdrasilException
import dev.excsi.springdrasil.model.Profile
import dev.excsi.springdrasil.model.Texture
import dev.excsi.springdrasil.model.TextureType
import dev.excsi.springdrasil.repository.TextureRepository
import org.apache.tika.Tika
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import java.awt.AlphaComposite
import java.awt.Graphics2D
import java.awt.image.BufferedImage
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.security.MessageDigest
import javax.imageio.ImageIO

private const val MAX_TEXTURE_SIZE_BYTES = 1 * 1024 * 1024

@Service
class TextureStorageService(
    val textureRepository: TextureRepository,
) {

    private val tika = Tika()

    fun sanitize(textureType: TextureType, bytes: ByteArray): ByteArray {
        if (bytes.isEmpty()) {
            throw YggdrasilException(HttpStatus.BAD_REQUEST, "IllegalArgumentException", "Invalid bytes")
        }

        if (bytes.size > MAX_TEXTURE_SIZE_BYTES) {
            throw YggdrasilException(HttpStatus.BAD_REQUEST, "IllegalArgumentException", "Image too large")
        }

        val detectedType = try {
            tika.detect(ByteArrayInputStream(bytes))
        } catch (exception: IOException) {
            throw YggdrasilException(HttpStatus.BAD_REQUEST, "IllegalArgumentException", "Could not inspect file")

        }

        if (detectedType != "image/png") {
            throw YggdrasilException(HttpStatus.BAD_REQUEST, "IllegalArgumentException", "Texture must be a PNG")
        }

        val sourceImage = readAndValidatePng(textureType, bytes)

        val sanitizedImage = BufferedImage(
            sourceImage.width,
            sourceImage.height,
            BufferedImage.TYPE_INT_ARGB,
        )

        val graphics: Graphics2D = sanitizedImage.createGraphics()
        try {
            graphics.composite = AlphaComposite.Src
            graphics.drawImage(sourceImage, 0, 0, null)
        } finally {
            graphics.dispose()
        }

        val output = ByteArrayOutputStream()
        try {
            if (!ImageIO.write(sanitizedImage, "PNG", output)) {
                throw YggdrasilException(HttpStatus.BAD_REQUEST, "IllegalArgumentException", "Texture could not be encoded.")
            }
        } catch (exception: IOException) {
            throw YggdrasilException(HttpStatus.BAD_REQUEST, "IllegalArgumentException", "Texture could not be encoded.")
        }

        if (output.size() > MAX_TEXTURE_SIZE_BYTES) {
            throw YggdrasilException(HttpStatus.BAD_REQUEST, "IllegalArgumentException", "Sanitized texture must be no larger than 1 Mb.")
        }

        return output.toByteArray()
    }

    fun save(profile: Profile, textureType: TextureType, bytes: ByteArray): Texture {
        val textureHash = hashTexture(bytes)
        val existingTexture = textureRepository.findTextureByProfileIdAndTextureType(profile.id, textureType)

        val texture = existingTexture?.apply {
            this.textureHash = textureHash
            this.byteArray = bytes
        } ?: Texture(
            profile = profile,
            textureType = textureType,
            textureHash = textureHash,
            byteArray = bytes,
        )

        return textureRepository.save(texture)
    }

    private fun readAndValidatePng(textureType: TextureType, bytes: ByteArray): BufferedImage {
        val imageInput = ImageIO.createImageInputStream(ByteArrayInputStream(bytes))
            ?: throw YggdrasilException(HttpStatus.BAD_REQUEST, "IllegalArgumentException", "Could not read PNG image")

        val readers = ImageIO.getImageReadersByFormatName("PNG")

        if (!readers.hasNext()) {
            imageInput.close()
            throw YggdrasilException(HttpStatus.BAD_REQUEST, "IllegalArgumentException", "PNG decoding is unavailable.")
        }

        val reader = readers.next()
        try {
            reader.setInput(imageInput, true, true)
            val width = reader.getWidth(0)
            val height = reader.getHeight(0)

            if (!isValidDimensions(textureType, width, height)) {
                throw YggdrasilException(
                    HttpStatus.BAD_REQUEST,
                    "IllegalArgumentException",
                    "Invalid texture dimensions."
                )
            }

            return reader.read(0) ?: throw YggdrasilException(HttpStatus.BAD_REQUEST, "IllegalArgumentException")

        } catch (exception: IOException) {
            throw YggdrasilException(HttpStatus.BAD_REQUEST, "IllegalArgumentException", "Texture is not a valid PNG.")
        } finally {
            reader.dispose()
        }
    }

    private fun isValidDimensions(textureType: TextureType, width: Int, height: Int): Boolean {
        return when (textureType) {
            TextureType.SKIN -> width == 64 && (height == 64 || height == 32)
            TextureType.CAPE -> width == 64 && height == 32
        }
    }

    fun findByHash(hash: String): Texture {
        return textureRepository.findTextureByTextureHash(hash)
            ?: throw YggdrasilException(HttpStatus.NOT_FOUND, "TextureNotFound", "Texture not found.")
    }

    fun delete(texture: Texture) {
        texture.profile.textures.remove(texture)
        textureRepository.delete(texture)
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
