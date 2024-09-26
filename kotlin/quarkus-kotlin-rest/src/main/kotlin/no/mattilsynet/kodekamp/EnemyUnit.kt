package no.mattilsynet.kodekamp

data class EnemyUnit(
    val y: Int,
    val moves: Int,
    val maxHealth: Int,
    val attackStrength: Int,
    val id: String,
    val kind: String,
    var health: Int,
    val side: String,
    val armor: Int,
    val x: Int,
    val attacks: Int
)


//"y": 1,
//"moves": 2,
//"maxHealth": 7,
//"attackStrength": 3,
//"id": "unit-3",
//"kind": "warrior",
//"health": 7,
//"side": "player-2",
//"armor": 1,
//"x": 2,
//"attacks": 1