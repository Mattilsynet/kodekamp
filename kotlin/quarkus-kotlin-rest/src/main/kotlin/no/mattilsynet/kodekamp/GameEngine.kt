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

    private fun isEnemyInRange(friendlyUnit: FriendlyUnit, enemyUnit: EnemyUnit): Boolean {
        return friendlyUnit.x - enemyUnit.x == 1 || friendlyUnit.y - enemyUnit.y == 1
    }

    private fun nearestEnemy(friendlyUnits: List<FriendlyUnit>, enemyUnits: List<EnemyUnit>): FriendlyEnemyPair {
        var minDistance = Double.MAX_VALUE
        var closestPair: FriendlyEnemyPair? = null

        for (friendlyUnit in friendlyUnits) {
            for (enemyUnit in enemyUnits) {
                val distance = Math.sqrt(Math.pow((friendlyUnit.x - enemyUnit.x).toDouble(), 2.0) + Math.pow((friendlyUnit.y - enemyUnit.y).toDouble(), 2.0))
                if (distance < minDistance) {
                    minDistance = distance
                    closestPair = FriendlyEnemyPair(friendlyUnit, enemyUnit)
                }
            }
        }

        return closestPair!!
    }
}

class FriendlyEnemyPair(
    val friendlyUnit: FriendlyUnit,
    val enemyUnit: EnemyUnit
)