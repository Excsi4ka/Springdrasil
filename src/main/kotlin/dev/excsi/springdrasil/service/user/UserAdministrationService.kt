package dev.excsi.springdrasil.service.user

import dev.excsi.springdrasil.model.Role
import dev.excsi.springdrasil.model.Status
import dev.excsi.springdrasil.service.web.JwtRefreshTokenService
import dev.excsi.springdrasil.service.yggdrasil.YggdrasilSessionTokenService
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.server.ResponseStatusException
import java.util.UUID

@Service
class UserAdministrationService(
    val userService: UserService,
    val jwtRefreshTokenService: JwtRefreshTokenService,
    val yggdrasilSessionTokenService: YggdrasilSessionTokenService
) {

    @Transactional
    fun suspendAccount(uuid: UUID) {
        val user = userService.findById(uuid)
            ?: throw ResponseStatusException(HttpStatus.NOT_FOUND, "User not found")

        user.status = Status.SUSPENDED
        jwtRefreshTokenService.invalidateAllTokens(user.id)
        yggdrasilSessionTokenService.invalidateAllTokensForProfile(user.profile)
    }

    @Transactional
    fun updateUserRole(uuid: UUID, role: Role) {
        val user = userService.findById(uuid)
            ?: throw ResponseStatusException(HttpStatus.NOT_FOUND, "User not found")

        user.role = role
    }

    @Transactional
    fun updateUserStatus(uuid: UUID, status: Status) {
        val user = userService.findById(uuid)
            ?: throw ResponseStatusException(HttpStatus.NOT_FOUND, "User not found")

        if (user.status == Status.ACTIVE && status == Status.SUSPENDED) {
            suspendAccount(uuid)
        }

        user.status = status
    }
}