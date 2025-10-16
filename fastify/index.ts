import fastify from 'fastify'

const server = fastify()

server.post('/endpoint', async (request, reply) => {
    const body = request.body as Request
    console.log('Request: ', body)
    const response: Actions = play(body)
    console.log('Response: ', response)
    return response
})

server.listen({port: 8080, host: '10.113.234.50'}, (err, address) => { // Remember to change host to your IP
    if (err) {
        console.error(err)
        process.exit(1)
    }
    console.log(`Server listening at ${address}`)
})

type CharacterTypes = 'Warrior' | 'Mage' | 'Rogue'
type Unit = {
    y: number,
    moves: number,
    maxHealth: number,
    attackStrength: number,
    id: string,
    kind: CharacterTypes,
    health: number,
    side: string,
    armor: number,
    x: number,
    attacks: number
}
type Request = {
    turnNumber: number,
    yourId: string,
    enemyUnits: Unit[],
    boardSize: {
        w: number
        h: number
    },
    player1: {
        name: string,
        experience: string
    }
    player2: {
        name: string,
        experience: string
    },
    moveActionsAvailable: number,
    attackActionsAvailable: number,
    friendlyUnits: Unit[]
    uuid: string
}

type Actions = {
    unit: string,
    action: Action
    x: number
    y: number
}[]

type Action = 'move' | 'attack'

const checkForEnemyInRangeWarrior = (friendlyUnit: Unit, enemyUnit: Unit): boolean => {
    const distanceX = Math.abs(enemyUnit.x - friendlyUnit.x)
    const distanceY = Math.abs(enemyUnit.y - friendlyUnit.y)
    return (distanceX === 1 && distanceY === 0 || distanceX === 0 && distanceY === 1)
}


const play = (body: Request): Actions => {
    let attackAvailable = body.attackActionsAvailable
    let moveAvailable = body.moveActionsAvailable

    const actions: Actions = []


    for (const friendlyUnit of body.friendlyUnits) {
        let hasMoved = false
        let unitAttacksAvailable = friendlyUnit.attacks
        let unitMovesAvailable = friendlyUnit.moves
        for (const enemyUnit of body.enemyUnits) {
            const enemyInRange = checkForEnemyInRangeWarrior(friendlyUnit, enemyUnit)
            if (enemyInRange && attackAvailable > 0 && unitAttacksAvailable > 0) {
                actions.push(
                    {
                        unit: friendlyUnit.id,
                        action: 'attack',
                        x: enemyUnit.x,
                        y: enemyUnit.y
                    }
                )
                attackAvailable--
                unitAttacksAvailable--
            }
        }

        if (!hasMoved && moveAvailable > 0 && unitMovesAvailable > 0) {
            const currentX = friendlyUnit.x
            const currentY = friendlyUnit.y
            const deltas = [-1, 0, 1]
            let dx = 0
            let dy = 0
            // ensure at least one axis changes
            do {
                dx = deltas[Math.floor(Math.random() * deltas.length)]
                dy = deltas[Math.floor(Math.random() * deltas.length)]
            } while (dx === 0 && dy === 0)

            let newX = currentX + dx
            let newY = currentY + dy

            // keep inside board
            if (newX < 0) newX = 0
            if (newY < 0) newY = 0
            if (newX >= body.boardSize.w) newX = body.boardSize.w - 1
            if (newY >= body.boardSize.h) newY = body.boardSize.h - 1

            actions.push({
                unit: friendlyUnit.id,
                action: 'move',
                x: newX,
                y: newY
            })
            moveAvailable--
            unitMovesAvailable--
            hasMoved = true
        }

    }
    return actions
}