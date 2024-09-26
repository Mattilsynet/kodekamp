package no.mattilsynet.kodekamp

import io.quarkus.logging.Log
import jakarta.enterprise.context.ApplicationScoped

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

    private fun shouldAttack(gameBoard: GameBoard, x: Int, y: Int): Boolean {
        return (gameBoard.enemyUnits).fold(false) { acc, value ->
            if (acc) {
                true
            } else {
                value.x == x && value.y == y
            }
        }
    }

    private fun findHittableEnemy(gameBoard: GameBoard, character: Character): Character? {
        return gameBoard.enemyUnits.fold(null) { acc, enemy ->
            if (acc != null) {
                return acc
            } else if (character.x == enemy.x && (character.y == enemy.y + 1 || character.y == enemy.y - 1)) {
                return enemy
            }  else if (character.y == enemy.y && (character.x == enemy.x + 1 || character.x == enemy.x - 1)) {
                return enemy
            } else {
                return null
            }
        }
    }





    fun process(gameBoard: GameBoard): List<Moves> {


        var moveActionsAvailable = gameBoard.moveActionsAvailable
        var attackActionsAvailable = gameBoard.attackActionsAvailable

        val list = mutableListOf<Moves>()

        val friendlyUnits = gameBoard.friendlyUnits
        val enemyUnits = gameBoard.enemyUnits


        for (index in 0..friendlyUnits.size - 1) {
            if (attackActionsAvailable > 0) {
                var friend = friendlyUnits[index]
                var enemy = enemyUnits[if (enemyUnits.size - 1 < 0) 0 else enemyUnits.size - 1]

                var x = if (enemy.x - friend.x < 0) -1 else 1
                var y = if (enemy.y - friend.y < 0) -1 else 1

                if (friend.y != enemy.y) {
                    if (!isPathBlocked(gameBoard, friend.x, friend.y + y)) {
                        list.add(Moves(friend.id, "move", friend.x, friend.y + y))
                        friend.y += y
                        friend.moves--
                        moveActionsAvailable--
                    }
                }
                if (friend.x != enemy.x - x) {
                    if (!isPathBlocked(gameBoard, friend.x + x, friend.y)) {
                        list.add(Moves(friend.id, "move", friend.x + x, friend.y))
                        friend.x += x
                        friend.moves--
                        moveActionsAvailable--
                    }
                }
            }
        }


        for (index in 0..friendlyUnits.size - 1) {
            if (attackActionsAvailable > 0) {
                var friend = friendlyUnits[index]

                while (friend.attacks > 0) {
                    val enemy = findHittableEnemy(gameBoard, friend)

                    if (enemy != null) {
                        list.add(Moves(friend.id, "attack", enemy.x, enemy.y))
                        friend.attacks--
                        attackActionsAvailable--
                    }
                }
            }

        }



        Log.info(list)

        return list
    }
}