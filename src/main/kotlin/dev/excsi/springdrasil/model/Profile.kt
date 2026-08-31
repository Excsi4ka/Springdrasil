package dev.excsi.springdrasil.model

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.OneToOne
import jakarta.persistence.Table
import java.util.Locale.getDefault
import java.util.UUID

@Entity
@Table(name = "profiles")
class Profile(

    @field:Id
    var id: UUID,

    @field:Column(name = "email", unique = true, nullable = false)
    var username: String,

    @field:OneToOne(optional = false)
    @field:JoinColumn(name = "account_id", nullable = false)
    var account: Account,

    @field:Enumerated(EnumType.STRING)
    @field:Column(name = "skin_type", nullable = false)
    var skinType: SkinType = SkinType.DEFAULT,
)

enum class SkinType {

    DEFAULT,

    SLIM;

    fun name(): String {
        return name.lowercase(getDefault());
    }
}