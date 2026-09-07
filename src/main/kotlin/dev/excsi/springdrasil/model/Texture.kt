package dev.excsi.springdrasil.model

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.FetchType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.Table
import java.time.Instant
import java.util.UUID

@Entity
@Table(name = "textures")
class Texture(

    @field:Id
    var id: UUID = UUID.randomUUID(),

    @field:ManyToOne(fetch = FetchType.LAZY, optional = false)
    @field:JoinColumn(name = "profile_uuid", nullable = false)
    var profile: Profile,

    @field:Enumerated(EnumType.STRING)
    @field:Column(name = "texture_type", nullable = false)
    var textureType: TextureType,

    @field:Column(name = "texture_hash", nullable = false)
    var textureHash: String,

    @field:Column(name = "skin_bytes", nullable = false)
    var byteArray: ByteArray,

    @field:Column(name = "created_at", nullable = false)
    var timestamp: Instant = Instant.now(),
)

enum class TextureType {
    SKIN,
    CAPE
}
