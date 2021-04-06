abstract class Player(var position: Pair<Int, Int>, val sign: Char) {
    var isDead: Boolean = false
    abstract val moves: List<Pair<Int, Int>>
    abstract val maxSteps: Int
    abstract val color: Char



    open fun getPossibleMoves(board: Board): List<Pair<Int, Int>> {
        var possibleMoves = mutableListOf<Pair<Int, Int>>()
        for (i in moves) {
            for (j in 1..maxSteps) {
                val a: Int = position.first + i.first * j
                val b: Int = position.second + i.second * j

                if(Pair(a, b) !in board.coordinates) break

                if (board.board[a][b].status == "| _ |") {
                    possibleMoves.add(Pair(a, b))
                } else {
                    if(color == 'w' && board.board[a][b].status[2].isUpperCase()) break
                    else if(color == 'b' && board.board[a][b].status[2].isLowerCase()) break
                    else{
                        possibleMoves.add(Pair(a, b))
                        break
                    }
                }
            }
        }

        //possibleMoves = realPossible(board, possibleMoves)

        return possibleMoves
    }


}