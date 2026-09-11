package dev.excsi.springdrasil.model

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.FetchType
import jakarta.persistence.Id
import jakarta.persistence.OneToMany
import jakarta.persistence.OneToOne
import jakarta.persistence.Table
import java.util.Locale
import java.util.UUID

@Entity
@Table(name = "profiles")
class Profile(

    @field:Id
    var id: UUID = UUID.randomUUID(),

    @field:Column(name = "profile_username", unique = true, nullable = false)
    var profileUsername: String,

    @field:Enumerated(EnumType.STRING)
    @field:Column(name = "skin_type", nullable = false)
    var skinType: SkinType = SkinType.DEFAULT,

    @field:OneToMany(mappedBy = "profile", fetch = FetchType.LAZY)
    var textures: MutableSet<Texture> = mutableSetOf(),

    @field:OneToOne(mappedBy = "profile", fetch = FetchType.LAZY)
    var user: User? = null,
)

enum class SkinType {

    DEFAULT,

    SLIM;

    fun name(): String {
        return name.lowercase(Locale.getDefault())
    }
}