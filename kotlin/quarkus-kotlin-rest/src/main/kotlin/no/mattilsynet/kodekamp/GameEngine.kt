package no.mattilsynet.kodekamp

import jakarta.enterprise.context.ApplicationScoped
import kotlin.random.Random

@ApplicationScoped
class GameEngine {

    fun process(gameRequest: GameRequest): List<Ordre> {
        val ordre = mutableListOf<Ordre>()
        val friendlyUnit = gameRequest.friendlyUnits[0]
        val enemyUnit = gameRequest.enemyUnits[0]

        for (i in 0 until gameRequest.moveActionsAvailable) {
            ordre.add(move(friendlyUnit, enemyUnit))
        }
        
        for (i in 0 until gameRequest.attackActionsAvailable) {
            ordre.add(attack(friendlyUnit, enemyUnit))
        }
        return ordre
    }

    private fun move(friendlyUnit: FriendlyUnit, enemyUnit: EnemyUnit): Ordre {
        return Ordre(
            unit = friendlyUnit.id,
            action = "move",
            x = enemyUnit.x - 1,
            y = enemyUnit.y
        )
    }

    private fun attack(friendlyUnit: FriendlyUnit,  enemyUnit: EnemyUnit): Ordre {
        return Ordre(
            unit = friendlyUnit.id,
            action = "attack",
            x = enemyUnit.x,
            y = enemyUnit.y
        )
    }
}