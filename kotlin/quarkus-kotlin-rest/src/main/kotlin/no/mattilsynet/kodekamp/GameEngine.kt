package no.mattilsynet.kodekamp

import jakarta.enterprise.context.ApplicationScoped
import kotlin.random.Random

@ApplicationScoped
class GameEngine {

    fun process(gameRequest: GameRequest): List<Ordre> {
        val ordre = mutableListOf<Ordre>()

        for (i in 0 until gameRequest.friendlyUnits.size) {
            val friendlyUnit = gameRequest.friendlyUnits[i]
            val enemyUnit = gameRequest.enemyUnits.firstOrNull { enemyUnit -> isEnemyInRange(friendlyUnit, enemyUnit) }
            if (enemyUnit != null) {
                ordre.add(attack(friendlyUnit, enemyUnit))
                continue
            }
        }

        for (i in 0 until gameRequest.moveActionsAvailable) {
            val friendlyUnit = gameRequest.friendlyUnits[i]
            val enemyUnit = gameRequest.enemyUnits[0]
            ordre.add(move(friendlyUnit, enemyUnit))
        }

        for (i in 0 until gameRequest.friendlyUnits.size) {
            val friendlyUnit = gameRequest.friendlyUnits[i]
            val enemyUnit = gameRequest.enemyUnits.firstOrNull { enemyUnit -> isEnemyInRange(friendlyUnit, enemyUnit) }
            if (enemyUnit != null) {
                ordre.add(attack(friendlyUnit, enemyUnit))
                continue
            }
        }

        return ordre
    }

    private fun moveVertically(friendlyUnit: FriendlyUnit, enemyUnit: EnemyUnit): Ordre {
        return Ordre(
            unit = friendlyUnit.id,
            action = "move",
            x = friendlyUnit.x - 1,
            y = enemyUnit.y
        )
    }

    private fun moveHorizontally(friendlyUnit: FriendlyUnit, enemyUnit: EnemyUnit): Ordre {
        return Ordre(
            unit = friendlyUnit.id,
            action = "move",
            x = enemyUnit.x,
            y = friendlyUnit.y - 1
        )
    }



    private fun move(friendlyUnit: FriendlyUnit, enemyUnit: EnemyUnit): Ordre {
        return Ordre(
            unit = friendlyUnit.id,
            action = "move",
            x = enemyUnit.x - 1,
            y = enemyUnit.y
        )
    }

    private fun attack(friendlyUnit: FriendlyUnit, enemyUnit: EnemyUnit): Ordre {
        return Ordre(
            unit = friendlyUnit.id,
            action = "attack",
            x = enemyUnit.x,
            y = enemyUnit.y
        )
    }

    private fun isEnemyInRange(friendlyUnit: FriendlyUnit, enemyUnit: EnemyUnit): Boolean {
        if (friendlyUnit.x == enemyUnit.x && Math.abs(friendlyUnit.y - enemyUnit.y) == 1) {
            return true
        } else if (friendlyUnit.y == enemyUnit.y && Math.abs(friendlyUnit.x - enemyUnit.x) == 1) {
            return true
        }
        return false
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