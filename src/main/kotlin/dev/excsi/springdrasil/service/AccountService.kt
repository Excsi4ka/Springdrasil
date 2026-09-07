package dev.excsi.springdrasil.service

import dev.excsi.springdrasil.model.Account
import dev.excsi.springdrasil.repository.AccountRepository
import org.springframework.stereotype.Service

@Service
class AccountService(
    val accountRepository: AccountRepository
) {

    fun findByUsername(username: String) : Account? {
        return accountRepository.findByUsername(username)
    }

    fun findByUsernameAndLock(username: String) : Account? {
        return accountRepository.findByUsernameForUpdate(username)
    }

    fun registerUser(email: String, username: String, password: String) {
        TODO()
    }
}