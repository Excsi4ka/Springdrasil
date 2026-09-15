package dev.excsi.springdrasil.repository

import dev.excsi.springdrasil.model.Texture
import dev.excsi.springdrasil.model.TextureType
import org.springframework.data.jpa.repository.JpaRepository
import java.util.UUID

interface TextureRepository : JpaRepository<Texture, UUID> {

    fun findTextureByTextureHash(textureHash: String): Texture?

    fun findTextureByProfileIdAndTextureType(profileId: UUID, textureType: TextureType): Texture?
}