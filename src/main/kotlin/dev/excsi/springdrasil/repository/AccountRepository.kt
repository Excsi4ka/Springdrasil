package dev.excsi.springdrasil.repository

import dev.excsi.springdrasil.model.Account
import org.springframework.data.jpa.repository.JpaRepository
import java.util.UUID

interface AccountRepository : JpaRepository<Account, UUID> {


}