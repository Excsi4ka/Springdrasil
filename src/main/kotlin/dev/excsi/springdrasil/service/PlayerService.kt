package dev.excsi.springdrasil.service

import dev.excsi.springdrasil.model.Player
import dev.excsi.springdrasil.repository.PlayerRepository
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service

@Service
class PlayerService(
    private val playerRepository: PlayerRepository,
    private val passwordEncoder: PasswordEncoder
) {

    fun findPlayerByEmail(email: String): Player {
        TODO("Implement me")
    }
}