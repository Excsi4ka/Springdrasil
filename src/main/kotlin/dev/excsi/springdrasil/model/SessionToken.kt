package dev.excsi.springdrasil.model

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.FetchType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.Table
import java.time.Instant

@Entity
@Table(name = "session_tokens")
class SessionToken(

    @field:Id
    @field:Column(name = "session_token", nullable = false)
    var accessToken: String,

    @field:Column(name = "client_token", nullable = false)
    var clientToken: String,

    @field:ManyToOne(fetch = FetchType.LAZY, optional = false)
    @field:JoinColumn(name = "profile_uuid", nullable = false)
    var boundProfile: Profile,

    @field:Column(name = "issued_at", nullable = false)
    var issuedAt: Instant = Instant.now(),

    @field:Column(name = "expires_at", nullable = false)
    var expiresAt: Instant,

    @field:Enumerated(EnumType.STRING)
    @field:Column(name = "token_state", nullable = false)
    var state: TokenState = TokenState.VALID
)

enum class TokenState {

    VALID,

    TEMPORARILY_INVALID,

    INVALID,
}