package dev.excsi.springdrasil.repository

import dev.excsi.springdrasil.model.SessionToken
import dev.excsi.springdrasil.model.TokenState
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import java.util.UUID

interface SessionTokenRepository : JpaRepository<SessionToken, String> {

    fun findAllByBoundProfileIdAndStateNotOrderByIssuedAtAsc(
        profileId: UUID,
        state: TokenState,
        pageable: Pageable,
    ): List<SessionToken>

    fun findAllByBoundProfileIdAndStateNot(profileId: UUID, state: TokenState): List<SessionToken>
}