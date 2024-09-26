package no.mattilsynet.kodekamp

data class GameRequest(

    val turnNumber: String,
    val friendlyUnits: List<FriendlyUnit>,
    val enemyUnits: List<EnemyUnit>,
    var moveActionsAvailable: Int,
    var attackActionsAvailable: Int,
    val boardSize: BoardSize
)


data class BoardSize (
    val w: Int,
    val h: Int
)
//"moveActionsAvailable": 2,
//"attackActionsAvailable": 1,