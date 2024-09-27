import fastify from 'fastify'

interface efUnit {
    y: number,
    moves: number,
    maxHealth: number,
    attackStrength: number,
    id: string,
    kind: string,
    health: number,
    side: string,
    armor: number,
    x: number,
    attacks: number
}

interface position {
    x: number,
    y: number,
}

interface action {
    unit: string,
    action: "move" | "attack",
    x: number,
    y: number
}

const server = fastify()

const move = (unit: string, posNext: position): action => {
    return {unit: unit, action: "move", x: posNext.x, y: posNext.y};
}
const attack = (unit: string, posNext: position): action => {
    return {unit: unit, action: "attack", x: posNext.x, y: posNext.y};
}

const myReachablePos = (myPos: position) => {
    const reachables: position[] = []



    const isValidMove = (position: position) => {
        if (position.x > 3 || position.x < 0 ) {
            return false
        } else if (position.y > 3 || position.y < 0 ){
            return false
        } else return true
    }


    const newUp = {
        y: myPos.y + 1,
        x: myPos.x,
    }
    const newDown = {
        y: myPos.y - 1,
        x: myPos.x,
    }
    const newRight = {
        y: myPos.y,
        x: myPos.x +1 ,
    }
    const newleft = {
        y: myPos.y,
        x: myPos.x - 1,
    }

    if (isValidMove(newUp)) {
        reachables.push(newUp)
    }
    if( isValidMove(newDown)){
        reachables.push(newDown)
    }
    if( isValidMove(newRight)){
        reachables.push(newRight)
    }
    if( isValidMove(newleft)){
        reachables.push(newleft)
    }
    return reachables;
}


server.post('/', async (request, reply) => {
    const unitActions: action[] = []


    // @ts-ignore
    const maxMoves = request.body.moveActionsAvailable;
    // @ts-ignore
    const maxAttack = request.body.attackActionsAvailable;
    // @ts-ignore
    const enemyUnits = request.body.enemyUnits;
    // @ts-ignore
    const friendlyUnits = request.body.friendlyUnits;

    const enemyPositions: position[] = [];
    const friendPositions: position[] = [];

    enemyUnits.map((enemy:efUnit) => {
        enemyPositions.push({
            x: enemy.x,
            y: enemy.y
        })}
    );
    friendlyUnits.map((friend:efUnit) => {
        friendPositions.push({
            x: friend.x,
            y: friend.y
        })}
    );

    const isEnemyPosition = (newPos:position) => {
       return enemyPositions.some(enemyPosition => (newPos.x === enemyPosition.x && newPos.y === enemyPosition.y))
    }
    const isFriendPosition = (newPos:position) => {
       return friendPositions.some(friendPosition => (newPos.x === friendPosition.x && newPos.y === friendPosition.y))
    }

    let globalAttackCount = 0;
    let globalMoveCount = 0;


    //Finne fiende ved siden av meg
    friendlyUnits.map((friend: efUnit) => {
        let unitMoveCount = 0;
        let unitAttackCount = 0;
        const unitMaxAttack = friend.attacks;
        const unitMaxMoves = friend.moves;

        const myPos = {x: friend.x , y: friend.y}
        const reachablePos = myReachablePos(myPos)


        reachablePos.forEach(reachable => {
            if (isFriendPosition(reachable)){
                return;
            }
            if (isEnemyPosition(reachable)) {
                for(let i = 0; i < unitMaxAttack; i++){
                    if(globalAttackCount < maxAttack && unitAttackCount < unitMaxAttack){
                        unitActions.push(attack(friend.id, reachable));
                        unitAttackCount++;
                        globalAttackCount++;
                    }
                }
            }
            if(!isEnemyPosition(reachable)) { // tomPosisjon
                const nextReachablePos = myReachablePos(reachable);

                nextReachablePos.forEach(nextReachable => {
                    if (isFriendPosition(nextReachable)) {
                        return;
                    }
                    if (isEnemyPosition(nextReachable)) {
                        if(globalMoveCount < maxMoves && unitMoveCount < unitMaxMoves) {
                            unitActions.push(move(friend.id, reachable))
                            unitMoveCount++;
                            globalMoveCount++;
                        }
                        for (let i = 0; i < unitMaxAttack; i++) {
                            if(globalAttackCount < maxAttack && unitAttackCount < unitMaxAttack){
                                unitActions.push(attack(friend.id, nextReachable));
                                unitAttackCount++;
                                globalAttackCount++;
                            }
                        }
                    }
                    if(!isEnemyPosition(nextReachable) && friend.kind === "archer") {
                        const next2ReachablePos = myReachablePos(nextReachable);

                        next2ReachablePos.forEach(next2Reachable => {
                            if (isEnemyPosition(next2Reachable)) {
                                for (let i = 0; i < unitMaxAttack; i++) {
                                    if (globalAttackCount < maxAttack  && unitAttackCount < unitMaxAttack) {
                                        unitActions.push(attack(friend.id, next2Reachable));
                                        unitAttackCount++;
                                        globalAttackCount++;
                                    }
                                }
                            }
                            if(!isEnemyPosition(next2Reachable)){
                                if(globalMoveCount < maxMoves && unitMoveCount < unitMaxMoves) {
                                    unitActions.push(move(friend.id, reachable))
                                    unitMoveCount++;
                                    globalMoveCount++;
                                }
                            }
                        })
                    }
                    if(!isEnemyPosition(nextReachable)) {
                        const next2ReachablePos = myReachablePos(nextReachable);

                        next2ReachablePos.forEach(next2Reachable => {
                            if (isEnemyPosition(next2Reachable)) {
                                for (let i = 0; i < unitMaxAttack; i++) {
                                    if (globalAttackCount < maxAttack  && unitAttackCount < unitMaxAttack) {
                                        unitActions.push(attack(friend.id, next2Reachable));
                                        unitAttackCount++;
                                        globalAttackCount++;
                                    }
                                }
                            }
                            if(!isEnemyPosition(next2Reachable)){
                                if(globalMoveCount < maxMoves && unitMoveCount < unitMaxMoves) {
                                    unitActions.push(move(friend.id, reachable))
                                    unitMoveCount++;
                                    globalMoveCount++;
                                }
                            }
                        })
                    }
                })
            }
        })
    })

    console.log("unitActions: ", unitActions)
    return unitActions
});

server.listen({ port: 8080}, (err, address) => { // Remember to change host to your IP
    if (err) {
        console.error(err)
        process.exit(1)
    }
    console.log(`Server listening at ${address}`)
})
