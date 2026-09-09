package dev.excsi.springdrasil.service

import dev.excsi.springdrasil.model.User
import dev.excsi.springdrasil.repository.UserRepository
import org.springframework.stereotype.Service

@Service
class UserService(
    val userRepository: UserRepository
) {

    fun findByUsername(username: String) : User? {
        return userRepository.findByUsername(username)
    }

    fun registerUser(email: String, username: String, password: String) {
        TODO()
    }

    fun changePassword(username: String, oldPassword: String, newPassword: String) {
        TODO()
    }
}
