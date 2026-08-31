package dev.excsi.springdrasil.model

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table
import java.util.UUID

@Entity
@Table(name = "accounts")
class Account(

    @field:Id
    var id: UUID,

    @field:Column(name = "email", unique = true, nullable = false)
    var email: String,

    @field:Column(name = "password_hash", nullable = false)
    var passwordHash: String


)