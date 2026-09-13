package dev.excsi.springdrasil.model

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.OneToOne
import jakarta.persistence.Table
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.userdetails.UserDetails
import java.util.UUID

@Entity
@Table(name = "users")
class User(

    @field:Id
    var id: UUID = UUID.randomUUID(),

    @field:Column(name = "email", unique = true, nullable = false)
    var email: String,

    @field:Column(name = "password_hash", nullable = false)
    var passwordHash: String,

    @field:OneToOne(optional = false)
    @field:JoinColumn(name = "profile_uuid", nullable = false)
    var profile: Profile
) : UserDetails {

    override fun getAuthorities(): Collection<GrantedAuthority> {
        return emptyList()
    }

    override fun getPassword(): String? {
        return passwordHash
    }

    override fun getUsername(): String {
        return email
    }
}