package dev.excsi.springdrasil.repository

import dev.excsi.springdrasil.model.SessionToken
import org.springframework.data.jpa.repository.JpaRepository

interface SessionTokensRepository : JpaRepository<SessionToken, String> {
}