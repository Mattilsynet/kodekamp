package no.mattilsynet.kodekamp

import io.quarkus.logging.Log
import jakarta.enterprise.context.ApplicationScoped
import org.jctools.queues.MessagePassingQueue.Supplier
import kotlin.math.absoluteValue
import kotlin.random.Random

@ApplicationScoped
class GameEngine {


    private fun isPathBlocked(gameBoard: GameBoard, x: Int, y: Int): Boolean {
        return (gameBoard.friendlyUnits + gameBoard.enemyUnits).fold(false) { acc, value ->
            if (acc) {
                true
            } else {
                value.x == x && value.y == y
            }
        }
    }

    private fun wizardDamage(friend: Character, list: List<Character>, x: Int, y: Int): Int {
        var damage = 0
        for (ca in list) {
            if (ca.x == x && (ca.y == y || ca.y == y + 1 || ca.y == y - 1)) {
                damage += friend.attackStrength
            }
            if (ca.y == y && (ca.x == x || ca.x == x + 1 || ca.x == x - 1)) {
                damage += friend.attackStrength
            }
        }
        return damage
    }


    private fun damageDealt(friend: Character, enemy: Character): Int =
        if (friend.isPiercing()) {
            friend.attackStrength
        } else {
            if (friend.attackStrength - enemy.armor < 0) 0 else friend.attackStrength - enemy.armor
        }

    private fun armorDamage(friend: Character, enemy: Character): Int =
        if (!friend.isPiercing()) {
            if (enemy.armor - friend.attackStrength < 0) 0 else enemy.armor - friend.attackStrength
        } else {
            0
        }


    private fun rageTarget(gameBoard: GameBoard, character: Character): Character? {
        var least: Character? = null
        for (enemy in gameBoard.enemyUnits) {
            if (character.isWizard() && (character.x - enemy.x).absoluteValue + (character.y - enemy.y).absoluteValue > 3) {
                continue
            }
            if (character.isWizard()) {
                val fredlyDame = wizardDamage(character, gameBoard.friendlyUnits, enemy.x, enemy.y)
                val enemyDame = wizardDamage(character, gameBoard.enemyUnits, enemy.x, enemy.y)
                if (enemyDame < fredlyDame) {
                    continue
                }
            }

            if ((character.x - enemy.x).absoluteValue + (character.y - enemy.y).absoluteValue > 4) {
                continue
            }

            if (enemy.isDead()) {
                continue
            }

            val health = enemy.health
            if (least == null) {
                least = enemy
            } else if (health < least.health) {
                least = enemy
            }
        }
        return least
    }


    private fun numberOfHittableEnemy(gameBoard: GameBoard, character: Character): Int {
        var count = 0
        for (enemy in gameBoard.enemyUnits) {
            if (enemy.x == character.x && (enemy.y + 1 == character.y || enemy.y - 1 == character.y)) {
                count++
            }
            if (enemy.y == character.y && (enemy.x + 1 == character.x || enemy.x - 1 == character.x)) {
                count++
            }
        }
        return count
    }


    private fun findHittableEnemy(gameBoard: GameBoard, character: Character): Character? {
        var hittable: Character? = null

        for (enemy in gameBoard.enemyUnits) {

            if (enemy.isDead()) {
                Log.info("Dead")
                continue
            }

            if (character.x == enemy.x && (character.y + 1 == enemy.y || character.y - 1 == enemy.y) || character.y == enemy.y && (character.x + 1 == enemy.x || character.x - 1 == enemy.x)) {
                if (hittable == null) {
                    hittable = enemy
                } else if (character.attackStrength - enemy.armor > character.attackStrength - hittable.armor) {
                    hittable = enemy
                }
            }
        }
        return hittable
    }

    fun process(gameBoard: GameBoard): List<Moves> {


        var moveActionsAvailable = gameBoard.moveActionsAvailable
        var attackActionsAvailable = gameBoard.attackActionsAvailable

        val list = mutableListOf<PMoves>()

        val friendlyUnits = gameBoard.friendlyUnits.sortedByDescending { it.attackStrength }
        val enemyUnits = gameBoard.enemyUnits


        for (index in 0..friendlyUnits.size - 1) {

            if (moveActionsAvailable > 0) {

                var friend = friendlyUnits[index]
                var enemy = enemyUnits[Random.nextInt(until = enemyUnits.size)]

                if (friend.isRage() && friendlyUnits.size != 1) {
                    continue
                }

                if (friend.isRage() && rageTarget(gameBoard, friend) != null) {
                    continue
                }

                var x = if (enemy.x - friend.x < 0) -1 else 1
                var y = if (enemy.y - friend.y < 0) -1 else 1

                val moveY = Supplier {
                    Log.info(friend.moves)
                    if(friend.moves < 1){
                        Log.info(friend.moves.toString() + " > > " + false )
                        false
                    }else if (!isPathBlocked(gameBoard, friend.x, friend.y + y)) {
                        if (numberOfHittableEnemy(gameBoard, friend.copy(y = friend.y + y)) > 1) {
                            return@Supplier false
                        }
                        list.add(PMoves(friend.id, "move", friend.x, friend.y + y))
                        friend.y += y
                        friend.moves -= 1
                        true
                    } else {
                        false
                    }
                }
                val moveX = Supplier {
                    Log.info(friend.moves)
                    if(friend.moves < 1){
                        Log.info(friend.moves.toString() + " > > " + false )
                        false
                    }else if (!isPathBlocked(gameBoard, friend.x + x, friend.y)) {
                        if (numberOfHittableEnemy(gameBoard, friend.copy(x = friend.x + x)) > 1) {
                            return@Supplier false
                        }
                        list.add(PMoves(friend.id, "move", friend.x + x, friend.y))
                        friend.x += x
                        friend.moves -= 1
                        true
                    } else {
                        false
                    }
                }


                if (Random.nextBoolean()) {
                    if (friend.y != enemy.y + y) {
                        if (moveY.get()) moveActionsAvailable--
                    }
                    if (friend.x != enemy.x + x) {
                        if (moveX.get()) moveActionsAvailable--
                    }
                } else {
                    if (friend.x != enemy.x + x) {
                        if (moveX.get()) moveActionsAvailable--
                    }
                    if (friend.y != enemy.y + y) {
                        if (moveY.get()) moveActionsAvailable--
                    }
                }
            }
        }


        for (index in 0..friendlyUnits.size - 1) {

            var friend = friendlyUnits[index]

            while (friend.attacks > 0) {

                Log.info("Finding enemy for " + friend.id + " (${friend.kind})")

                val enemy =
                    if (friend.isRage()) rageTarget(gameBoard, friend) else findHittableEnemy(
                        gameBoard,
                        friend
                    )

                if (friend.isBarbarian()) {
                    Log.info("Enemy $enemy")
                }

                if (enemy != null) {
                    list.add(PMoves(friend.id, "attack", enemy.x, enemy.y, damageDealt(friend, enemy)))
                    friend.attacks -= 1
                    enemy.health -= damageDealt(friend, enemy)
                    enemy.armor = armorDamage(friend, enemy)
                    attackActionsAvailable--
                } else {
                    break
                }
            }
        }


        for (index in 0..friendlyUnits.size - 1) {
            if (moveActionsAvailable > 0) {

                var friend = friendlyUnits[index]

                if (numberOfHittableEnemy(gameBoard, friend) > 1 && friend.moves > 0) {

                    var x = friend.x + 1

                    if (x in 0..3) {

                        var next = friend.copy(x = x)

                        if (numberOfHittableEnemy(gameBoard, next) <= 1 && !isPathBlocked(gameBoard, next.x, next.y)) {
                            list.add(PMoves(friend.id, "move", next.x, next.y))
                            friend.x = next.x
                            friend.y = next.y
                            friend.moves -= 1
                            moveActionsAvailable--
                        }
                    }
                    x = friend.x - 1

                    if (x in 0..3) {
                        var next = friend.copy(x = x)

                        if (numberOfHittableEnemy(gameBoard, next) <= 1 && !isPathBlocked(gameBoard, next.x, next.y)) {
                            list.add(PMoves(friend.id, "move", next.x, next.y))
                            friend.x = next.x
                            friend.y = next.y
                            friend.moves -= 1
                            moveActionsAvailable--
                        }
                    }

                    var y = friend.y + 1
                    if (y in 0..3) {
                        var next = friend.copy(y = y)

                        if (numberOfHittableEnemy(gameBoard, next) <= 1 && !isPathBlocked(gameBoard, next.x, next.y)) {
                            list.add(PMoves(friend.id, "move", next.x, next.y))
                            friend.x = next.x
                            friend.y = next.y
                            friend.moves -= 1
                            moveActionsAvailable--
                        }
                    }
                    y = friend.y - 1
                    if (y in 0..3) {
                        var next = friend.copy(y = y)
                        if (numberOfHittableEnemy(gameBoard, next) <= 1 && !isPathBlocked(gameBoard, next.x, next.y)) {
                            list.add(PMoves(friend.id, "move", next.x, next.y))
                            friend.x = next.x
                            friend.y = next.y
                            friend.moves -= 1
                            moveActionsAvailable--
                        }
                    }
                }

            }
        }


        return (list).map { it.toMove() }
    }
}