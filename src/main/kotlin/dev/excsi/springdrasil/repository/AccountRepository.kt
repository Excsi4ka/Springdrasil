package dev.excsi.springdrasil.repository

import dev.excsi.springdrasil.model.Account
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import java.util.UUID

interface AccountRepository : JpaRepository<Account, UUID> {

    fun findByUsername(username: String): Account?

    @Query("select a from Account a join fetch a.profile where a.username = :username")
    fun findByUsernameWithProfile(@Param("username") username: String): Account?
}
