package dev.excsi.springdrasil.service

import dev.excsi.springdrasil.model.Account
import dev.excsi.springdrasil.repository.AccountRepository
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service

@Service
class AccountService(
    private val playerRepository: AccountRepository,
    private val passwordEncoder: PasswordEncoder
) {

    fun findPlayerByEmail(email: String): Account {
        TODO("Implement me")
    }
}