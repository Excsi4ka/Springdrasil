package dev.excsi.springdrasil.repository

import dev.excsi.springdrasil.model.JwtRefreshToken
import org.springframework.data.jpa.repository.JpaRepository
import java.util.UUID

interface JwtRefreshTokenRepository : JpaRepository<JwtRefreshToken, UUID> {
}