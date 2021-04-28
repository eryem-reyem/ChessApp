class Rating {
    var rating = 0

    fun rate(board: Board, move: Pair<Int, Int>): Int {
        if(move in board.piecePositions){
            val capturePiece = board.piecePositions[move]
            if (capturePiece != null) {
                rating = capturePiece.evaluation
            }
        }
        return rating
    }
}