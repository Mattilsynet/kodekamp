package no.mattilsynet.kodekamp

data class GameBoard(
    val turnNumber: Int,
    val yourId: String,
    val player2: String,
    val player1: String,
    val moveActionsAvailable: Int,
    val attackActionsAvailable: Int,
    val uuid: String,
    val enemyUnits: List<Character>,
    val friendlyUnits: List<Character>
)

data class Character(
    var y: Int,
    var moves: Int,
    var maxHealth: Int,
    var attackStrength: Int,
    var id: String,
    var kind: String,
    var health: Int,
    var side: String,
    var armor: Int,
    var x: Int,
    var attacks: Int,
    private val isPiercing: Boolean? = null
) {

    fun isPiercing() = isPiercing ?: false

    fun isArcher() = kind == "archer"
    fun isBarbarian() = kind == "barbarian"

    fun isWizard() = kind == "wizard"

    fun isDead() = health < 1

    fun isRage() = isArcher() || isWizard()

}

data class Moves(
    var unit: String,
    var action: String,
    var x: Int,
    var y: Int
)

data class PMoves(
    var unit: String,
    var action: String,
    var x: Int,
    var y: Int,
    var damage: Int? = null
)

fun PMoves.toMove() = Moves(
    unit = this@toMove.unit,
    action = this@toMove.action,
    x = this@toMove.x,
    y = this@toMove.y,
)