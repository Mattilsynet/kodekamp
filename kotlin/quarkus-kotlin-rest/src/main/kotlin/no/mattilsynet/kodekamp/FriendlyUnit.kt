package no.mattilsynet.kodekamp

data class FriendlyUnit(
    var y: Int,
    var moves: Int,
    val maxHealth: Int,
    val attackStrength: Int,
    val id: String,
    val kind: String,
    val health: Int,
    val side: String,
    val armor: Int,
    var x: Int,
    var attacks: Int
) {

    fun move(x: Int, y: Int): Ordre {
        this.y = y
        this.x = x
        this.moves--
        return Ordre(id, "move", x, y)
    }

    fun attackEnemies(enemy: EnemyUnit, gameRequest: GameRequest): List<Ordre> {
        val ordre: MutableList<Ordre> = mutableListOf()
        if (canAttack(enemy)) {
            for (i in 0 until Math.min(attacks, gameRequest.attackActionsAvailable)) {
                ordre.add(attack(enemy, gameRequest))
                gameRequest.attackActionsAvailable--
                println("Enemy health: ${enemy.health}")
            }
        }
        return ordre
    }

    fun attack(enemy: EnemyUnit, gameRequest: GameRequest): Ordre {
        this.attacks--
        gameRequest.attackActionsAvailable--
        enemy.health = calculateAttack(enemy)
        return Ordre(
            unit = id,
            action = "attack",
            x = enemy.x,
            y = enemy.y
        )
    }

    private fun calculateAttack(enemy: EnemyUnit): Int {
        val armor = if (kind == "archer") 0 else enemy.armor
        return (enemy.health + armor) - attackStrength
    }

    fun act(gameRequest: GameRequest): List<Ordre> {
        if (kind == "archer") {
            return actArcher2(gameRequest)
        } else if (kind == "knight") {
            return actArcher2(gameRequest)
        } else if (kind == "warrior") {
            return actArcher2(gameRequest)
        } else if (kind == "barbarian") {
            return actArcher2(gameRequest)
        } else if (kind == "wizard") {
            return actArcher2(gameRequest)
        }
        return emptyList()
    }

    private fun actBarbarian(gameRequest: GameRequest): List<Ordre> {
        val ordre: MutableList<Ordre> = mutableListOf()
        // Finn nærmeste fiende
        val nearestEnemy = nearestEnemy(gameRequest.enemyUnits)
        // Move
        ordre.addAll(moveToEnemy(nearestEnemy.enemyUnit, gameRequest))
        // Attack
        ordre.addAll(attackEnemies(nearestEnemy.enemyUnit, gameRequest))

        return ordre
    }

    private fun actWarrior(gameRequest: GameRequest): List<Ordre> {
        val ordre: MutableList<Ordre> = mutableListOf()
        // Finn nærmeste fiende
        val nearestEnemy = nearestEnemy(gameRequest.enemyUnits)
        // Move
        ordre.addAll(moveToEnemy(nearestEnemy.enemyUnit, gameRequest))
        // Attack
        ordre.addAll(attackEnemies(nearestEnemy.enemyUnit, gameRequest))
        return ordre
    }

    private fun actKnight(gameRequest: GameRequest): List<Ordre> {
        val ordre: MutableList<Ordre> = mutableListOf()
        // Finn nærmeste fiende
        val nearestEnemy = nearestEnemy(gameRequest.enemyUnits)
        // Move
        ordre.addAll(moveToEnemy(nearestEnemy.enemyUnit, gameRequest))
        // Attack
        ordre.addAll(attackEnemies(nearestEnemy.enemyUnit, gameRequest))
        return ordre
    }

    private fun actArcher(gameRequest: GameRequest): List<Ordre> {
        val ordre: MutableList<Ordre> = mutableListOf()
        // Finn nærmeste fiende

        val nearestEnemy = nearestEnemy(gameRequest.enemyUnits)
        // Move
        ordre.addAll(moveToEnemy(nearestEnemy.enemyUnit, gameRequest))
        // Attack
        ordre.addAll(attackEnemies(nearestEnemy.enemyUnit, gameRequest))
        println("Archer ordre antall: ${ordre.size}")
        println("Archer ordre: ${ordre.joinToString { it.toString() }}")
        return ordre
    }

    private fun actArcher2(gameRequest: GameRequest): List<Ordre> {
        val ordre: MutableList<Ordre> = mutableListOf()
        // Finn nærmeste fiende

        while (Math.min(gameRequest.moveActionsAvailable, moves) > 0 && Math.min(gameRequest.attackActionsAvailable, attacks) > 0) {
            val nearestEnemy = nearestEnemy(gameRequest.enemyUnits)
            // Move
            ordre.addAll(moveToEnemy(nearestEnemy.enemyUnit, gameRequest))
            // Attack
            while (nearestEnemy.enemyUnit.health > 0 && Math.min(gameRequest.attackActionsAvailable, attacks) > 0) {
                ordre.add(attack(nearestEnemy.enemyUnit, gameRequest))
            }

            println("ordre antall: ${ordre.size}")
            println("ordre: ${ordre.joinToString { it.toString() }}")
        }

        return ordre
    }

    fun canAttackType(enemyUnit: EnemyUnit): Boolean {
        if (kind == "knight" && enemyUnit.kind == "knight") {
            return false
        }
        return true
    }

    fun canAttack(enemyUnit: EnemyUnit): Boolean {
        if (kind =="archer" && Math.abs(x - enemyUnit.x) + Math.abs(y - enemyUnit.y) <= 4) {
            return true
        }
        if (enemyUnit.health <= 0){
            return false
        }

        if (canAttackType(enemyUnit) && x == enemyUnit.x && Math.abs(y - enemyUnit.y) == 1) {
            return true
        } else if (canAttackType(enemyUnit) && y == enemyUnit.y && Math.abs(x - enemyUnit.x) == 1) {
            return true
        }
        return false
    }

    private fun nearestEnemy(enemyUnits: List<EnemyUnit>): FriendlyEnemyPair {
        var minDistance = Double.MAX_VALUE
        var closestPair = FriendlyEnemyPair(this, enemyUnits[0])

            if (attacks > 0 && moves > 0) {
                for (enemyUnit in enemyUnits) {
                    if (canAttackType(enemyUnit) && enemyUnit.health > 0) {
                        val distance = Math.sqrt(Math.pow((x - enemyUnit.x).toDouble(), 2.0) + Math.pow((y - enemyUnit.y).toDouble(), 2.0))
                        if (distance < minDistance) {
                            minDistance = distance
                            closestPair = FriendlyEnemyPair(this, enemyUnit)
                        }
                    }
                }
            }

        return closestPair
    }

    private fun moveToEnemy(enemyUnit: EnemyUnit, gameRequest: GameRequest): List<Ordre> {
        var moves = Math.min(gameRequest.moveActionsAvailable, moves)
        val orders = mutableListOf<Ordre>()

        while (moves > 0 && !anyEnemiesInRange(this, gameRequest.enemyUnits)) {

            if (x < enemyUnit.x && validMove(x + 1, y, gameRequest)) {
                orders.add(move(x = x + 1, y = y))
            } else if (x > enemyUnit.x && validMove(x - 1, y, gameRequest)) {
                orders.add(move(x = x - 1, y = y))
            }
            else if (y < enemyUnit.y && validMove(x, y + 1, gameRequest)) {
                orders.add(move(x = x, y = y + 1))
            } else if (y > enemyUnit.y && validMove(x, y - 1, gameRequest)) {
                orders.add(move(x = x, y = y - 1))
            }
            gameRequest.moveActionsAvailable--
            moves--
        }

        return orders
    }

    private fun anyEnemiesInRange(friendlyUnit: FriendlyUnit, enemyUnits: List<EnemyUnit>): Boolean {
        return enemyUnits.any { enemyUnit -> friendlyUnit.canAttack(enemyUnit) }
    }

    fun validMove(x: Int, y: Int, gameRequest: GameRequest): Boolean {
        // Krasjer vi med noen vennlige?
        if (gameRequest.friendlyUnits.any { it.x == x && it.y == y }) {
            return false
        }

        if (gameRequest.enemyUnits.any { it.x == x && it.y == y }) {
            return false
        }

        // Går vi utenfor bounds?
        if (x < 0 || x >= gameRequest.boardSize.w || y < 0 || y >= gameRequest.boardSize.h) {
            return false
        }
        return true
    }

}

class FriendlyEnemyPair(
    val friendlyUnit: FriendlyUnit,
    val enemyUnit: EnemyUnit
)

//
//"y": 2,
//"moves": 2,
//"maxHealth": 7,
//"attackStrength": 3,
//"id": "unit-0",
//"kind": "warrior",
//"health": 7,
//"side": "player-1",
//"armor": 1,
//"x": 1,
//"attacks": 1