package dev.excsi.springdrasil.component

import dev.excsi.springdrasil.service.user.UserService
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.ApplicationArguments
import org.springframework.boot.ApplicationRunner
import org.springframework.stereotype.Component

@Component
class AdminUserInitializer(

    @Value($$"${springdrasil.admin.email}")
    val adminEmail: String,

    @Value($$"${springdrasil.admin.profile-name}")
    val adminProfileName: String,

    @Value($$"${springdrasil.admin.password}")
    val adminPassword: String,

    val logger: Logger = LoggerFactory.getLogger(AdminUserInitializer::class.java),

    val userService: UserService

) : ApplicationRunner {

    //TODO add an optional command line admin creation
    override fun run(args: ApplicationArguments) {
        try {
            userService.createAdminUser(adminEmail, adminProfileName, adminPassword)
        } catch (exception: Exception) {
            logger.error("Error while creating an admin user.")
            throw exception
        }
    }
}