package dev.excsi.springdrasil.repository

import dev.excsi.springdrasil.model.Profile
import org.springframework.data.jpa.repository.JpaRepository
import java.util.UUID

interface ProfileRepository : JpaRepository<Profile, UUID> {
}