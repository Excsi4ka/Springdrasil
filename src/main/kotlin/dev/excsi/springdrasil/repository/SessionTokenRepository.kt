package dev.excsi.springdrasil.repository

import dev.excsi.springdrasil.model.SessionToken
import org.springframework.data.jpa.repository.JpaRepository
import java.util.UUID

interface SessionTokenRepository : JpaRepository<SessionToken, String> {

    fun countByBoundProfileId(profileId : UUID) : Long

    fun findFirstByBoundProfileIdOrderByIssuedAtAsc(profileId: UUID): SessionToken?
}