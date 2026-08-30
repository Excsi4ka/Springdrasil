package dev.excsi.springdrasil.model

import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.userdetails.UserDetails
import java.util.UUID

@Entity
@Table(name = "players")
class Player {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    var id: UUID
}