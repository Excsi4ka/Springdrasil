package dev.excsi.springdrasil.model

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.Table
import java.time.Instant
import java.util.UUID

@Entity
@Table(name = "jwt_refresh_tokens")
class JwtRefreshToken(

    @field:Id
    var id: UUID = UUID.randomUUID(),

    @field:ManyToOne(fetch = FetchType.EAGER, optional = false)
    @field:JoinColumn(name = "user_id", nullable = false)
    var user: User,

    @field:Column(name = "created_at", nullable = false)
    var createdAt: Instant = Instant.now(),

    @field:Column(name = "expires_at", nullable = false)
    var expiresAt: Instant,
)
