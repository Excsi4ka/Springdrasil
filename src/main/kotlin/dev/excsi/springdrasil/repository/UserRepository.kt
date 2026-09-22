package dev.excsi.springdrasil.repository

import dev.excsi.springdrasil.model.User
import dev.excsi.springdrasil.model.Status
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import java.util.UUID

interface UserRepository : JpaRepository<User, UUID> {

    fun findByEmail(username: String): User?

    fun findByProfileProfileUsername(username: String): User?

    @Query("""
        SELECT u
        FROM User u
        JOIN u.profile p
        WHERE (:status IS NULL OR u.status = :status)
          AND (:name IS NULL OR LOWER(p.profileUsername) LIKE LOWER(CONCAT(:name, '%')))
          AND (:email IS NULL OR LOWER(u.email) LIKE LOWER(CONCAT(:email, '%')))
    """)
    fun findFiltered(
        @Param("status") status: Status?,
        @Param("name") name: String?,
        @Param("email") email: String?,
        pageable: Pageable
    ): Page<User>
}
