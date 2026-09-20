package dev.excsi.springdrasil.service.user

import dev.excsi.springdrasil.model.Profile
import dev.excsi.springdrasil.model.Role
import dev.excsi.springdrasil.model.Status
import dev.excsi.springdrasil.model.User
import dev.excsi.springdrasil.repository.ProfileRepository
import dev.excsi.springdrasil.repository.UserRepository
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class UserService(
    val userRepository: UserRepository,
    val profileRepository: ProfileRepository,
    val passwordEncoder: PasswordEncoder
) {

    fun findByEmail(email: String) : User? {
        return userRepository.findByEmail(email)
    }

    @Transactional
    fun createUser(email: String, profileName: String, password: String) {
        TODO()
    }

    @Transactional
    fun createAdminUser(email: String, profileName: String, password: String) {
        userRepository.findByEmail(email)?.let {
            return
        }

        if (email.isBlank()) {
            throw RuntimeException("No email given.")
        }

        if (profileName.isBlank()) {
            throw RuntimeException("No profile name given.")
        }

        if (password.isBlank()) {
            throw RuntimeException("No password given.")
        }

        val passwordHash = passwordEncoder.encode(password) ?: return

        val profile = Profile(
            profileUsername = profileName
        )

        val user = User(
            email = email,
            passwordHash = passwordHash,
            profile = profile,
            role = Role.ADMIN,
            status = Status.ACTIVE
        )

        profileRepository.save(profile)
        userRepository.save(user)
    }

    fun changePassword(username: String, oldPassword: String, newPassword: String) {
        TODO()
    }
}