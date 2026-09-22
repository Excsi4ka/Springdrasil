package dev.excsi.springdrasil.controller.admin

import dev.excsi.springdrasil.dto.RegisterUserRequest
import dev.excsi.springdrasil.dto.RegisterUserResponse
import dev.excsi.springdrasil.dto.UserData
import dev.excsi.springdrasil.model.Role
import dev.excsi.springdrasil.model.Status
import dev.excsi.springdrasil.service.user.UserAdministrationService
import dev.excsi.springdrasil.service.user.UserService
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PatchMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import java.util.UUID

@RestController
@RequestMapping("admin/api")
class AdminApiController(
    val userService: UserService,
    val userAdministrationService: UserAdministrationService
) {

    @GetMapping("users")
    fun getUsers(
        @RequestParam(required = false) status: Status?,
        @RequestParam(required = false) name: String?,
        @RequestParam(required = false) email: String?,
        pageable: Pageable
    ): Page<UserData> {
        return userService.getUsersData(status, name, email, pageable)
    }

    @GetMapping("user/{userId}")
    fun getUser(@PathVariable userId: UUID): UserData {
        return userService.getUserData(userId)
    }

    @PatchMapping("user/{userId}/role")
    fun updateUserRole(@PathVariable userId: UUID, @RequestBody role: Role) {
        userAdministrationService.updateUserRole(userId, role)
    }

    @PatchMapping("user/{userId}/status")
    fun updateUserStatus(@PathVariable userId: UUID, @RequestBody status: Status) {
        userAdministrationService.updateUserStatus(userId, status)
    }

    @PostMapping("user")
    fun createUserManually(
        @RequestBody registerRequest: RegisterUserRequest
    ): RegisterUserResponse {
        return userService.createUserManually(registerRequest.email, registerRequest.profileName, registerRequest.password)
    }
}