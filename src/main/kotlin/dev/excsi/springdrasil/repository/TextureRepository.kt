package dev.excsi.springdrasil.repository

import dev.excsi.springdrasil.model.Texture
import org.springframework.data.jpa.repository.JpaRepository
import java.util.UUID

interface TextureRepository : JpaRepository<Texture, UUID> {
}