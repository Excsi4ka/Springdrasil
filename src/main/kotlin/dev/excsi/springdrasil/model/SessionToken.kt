package dev.excsi.springdrasil.model

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.Table
import java.time.Instant

@Entity
@Table(name = "session_tokens")
class SessionToken(

    @field:Id
    var accessToken: String,

    @field:Column(name = "client_token", nullable = false)
    var clientToken: String,

    @field:ManyToOne(optional = false)
    @field:JoinColumn(name = "bound_profiles", nullable = false)
    var boundProfile: Profile,

    @field:Column(name = "issued_at", nullable = false)
    var issuedAt: Instant = Instant.now(),
)