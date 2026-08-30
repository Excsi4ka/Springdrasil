package dev.excsi.springdrasil.repository

import dev.excsi.springdrasil.model.Player
import org.springframework.data.jpa.repository.JpaRepository
import java.util.UUID

interface PlayerRepository : JpaRepository<Player, UUID> {


}