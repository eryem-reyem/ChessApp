class King(position: Pair<Int, Int>, sign: Char, override val color: Char = 'w'): Player(position, sign) {
    override val moves = listOf(
        Pair(1, 1),
        Pair(1, -1),
        Pair(-1, 1),
        Pair(-1, -1),
        Pair(0, 1),
        Pair(0, -1),
        Pair(-1, 0),
        Pair(1, 0)
    )
    override val maxSteps: Int = 1
    var castelingPositions = mutableListOf<Pair<Int, Int>>()


    fun getCastelingPositions(board: Board){
        if(color == 'w' && 'K' in board.fen.castling && board.board[7][5].status == "| _ |" &&
                board.board[7][6].status == "| _ |"){
            castelingPositions.add(Pair(7, 6))
        }
        else if(color == 'b' && 'k' in board.fen.castling && board.board[0][5].status == "| _ |" &&
            board.board[0][6].status == "| _ |"){
            castelingPositions.add(Pair(0, 6))
        }

        if(color == 'w' && 'Q' in board.fen.castling && board.board[7][1].status == "| _ |" &&
            board.board[7][2].status == "| _ |" && board.board[7][3].status == "| _ |"){
            castelingPositions.add(Pair(7, 2))
        }
        else if(color == 'b' && 'q' in board.fen.castling && board.board[0][1].status == "| _ |" &&
            board.board[0][2].status == "| _ |" && board.board[0][3].status == "| _ |"){
            castelingPositions.add(Pair(0, 2))
        }
    }

    override fun getPossibleMoves(board: Board): List<Pair<Int, Int>> {
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

        //if(board.defendCheck.size == 0)
        getCastelingPositions(board)

        if(castelingPositions.size != 0) {
            for(position in castelingPositions) {
                if(position !in possibleMoves){
                    possibleMoves.add(position)
                }
            }
        }
        return possibleMoves
    }


}