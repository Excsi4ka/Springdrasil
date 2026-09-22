package dev.excsi.springdrasil.repository

import dev.excsi.springdrasil.model.JwtRefreshToken
import jakarta.persistence.LockModeType
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Lock
import java.util.UUID

interface JwtRefreshTokenRepository : JpaRepository<JwtRefreshToken, UUID> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    fun findLockedById(id: UUID): JwtRefreshToken?

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    fun findLockedByUserId(userId: UUID): List<JwtRefreshToken>
}
