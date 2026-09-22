package dev.excsi.springdrasil.service.user

import dev.excsi.springdrasil.dto.RegisterUserResponse
import dev.excsi.springdrasil.dto.UserData
import dev.excsi.springdrasil.model.Profile
import dev.excsi.springdrasil.model.Role
import dev.excsi.springdrasil.model.Status
import dev.excsi.springdrasil.model.User
import dev.excsi.springdrasil.repository.ProfileRepository
import dev.excsi.springdrasil.repository.UserRepository
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.http.HttpStatus
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.server.ResponseStatusException
import java.util.UUID

@Service
class UserService(
    val userRepository: UserRepository,
    val profileRepository: ProfileRepository,
    val passwordEncoder: PasswordEncoder
) {

    fun findById(id: UUID): User? {
        return userRepository.findById(id).orElse(null)
    }

    fun findByEmail(email: String) : User? {
        return userRepository.findByEmail(email)
    }

    fun findByProfileName(name: String) : User? {
        return userRepository.findByProfileProfileUsername(name)
    }

    fun getUserData(uuid: UUID) : UserData {
        val user = findById(uuid)
            ?: throw ResponseStatusException(HttpStatus.NOT_FOUND, "User not found")

        val profile = user.profile

        return UserData(
            email = user.email,
            userId = user.id,
            gameProfileUUID = profile.id,
            profileName = profile.profileUsername,
            creationDate = user.createdAt.toString(),
            status = user.status,
            role = user.role,
        )
    }

    fun getUsersData(
        status: Status?,
        name: String?,
        email: String?,
        pageable: Pageable
    ): Page<UserData> {
        val normalizedName = name?.trim()?.takeIf {
            it.isNotEmpty()
        }
        val normalizedEmail = email?.trim()?.takeIf {
            it.isNotEmpty()
        }

        return userRepository.findFiltered(
            status = status,
            name = normalizedName,
            email = normalizedEmail,
            pageable = pageable
        ).map {
            UserData(
                email = it.email,
                userId = it.id,
                gameProfileUUID = it.profile.id,
                profileName = it.profile.profileUsername,
                creationDate = it.createdAt.toString(),
                status = it.status,
                role = it.role,
            )
        }
    }

    //User created manually by an admin user
    @Transactional
    fun createUserManually(email: String, profileName: String, password: String): RegisterUserResponse {
        userRepository.findByEmail(email)?.let {
            throw ResponseStatusException(HttpStatus.CONFLICT, "User already exists")
        }

        if (email.isBlank()) {
            throw ResponseStatusException(HttpStatus.BAD_REQUEST, "Email is blank")
        }

        if (profileName.isBlank()) {
            throw ResponseStatusException(HttpStatus.BAD_REQUEST, "Profile name is blank")
        }

        if (password.isBlank()) {
            throw ResponseStatusException(HttpStatus.BAD_REQUEST, "Password is blank")
        }

        val passwordHash = passwordEncoder.encode(password)
            ?: throw ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR)

        val profile = Profile(
            profileUsername = profileName
        )

        val user = User(
            email = email,
            passwordHash = passwordHash,
            profile = profile,
            role = Role.USER,
            status = Status.ACTIVE
        )

        profileRepository.save(profile)
        userRepository.save(user)

        return RegisterUserResponse(
            email = email,
            profileName = profileName,
            creationDate = user.createdAt.toString(),
        )
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
