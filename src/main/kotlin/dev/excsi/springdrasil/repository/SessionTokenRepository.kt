package dev.excsi.springdrasil.repository

import dev.excsi.springdrasil.model.SessionToken
import org.springframework.data.jpa.repository.JpaRepository
import java.util.UUID

interface SessionTokenRepository : JpaRepository<SessionToken, String> {

    fun findAllByBoundProfileIdOrderByIssuedAtAsc(profileId: UUID): List<SessionToken>

    fun findAllByBoundProfileId(profileId: UUID): List<SessionToken>
}