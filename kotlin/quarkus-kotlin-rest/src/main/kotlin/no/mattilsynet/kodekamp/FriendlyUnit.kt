package no.mattilsynet.kodekamp

data class FriendlyUnit(
    val y: Int,
    val moves: Int,
    val maxHealth: Int,
    val attackStrength: Int,
    val id: String,
    val kind: String,
    val health: Int,
    val side: String,
    val armor: Int,
    val x: Int,
    val attacks: Int
) {

    fun move() {
        println("Unit $id moves")
    }

    fun attack() {
        println("Unit $id attacks")
    }

}

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