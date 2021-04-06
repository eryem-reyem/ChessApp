import java.io.File

class Fen {
    private var newFen = "rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1"
    private val fen = newFen.split(' ')

    private var piecePosition = fen[0]
    var activeColor = fen[1]
    var castling = fen[2]
    var enPassant = fen[3]
    var halfMoveClock = fen[4].toInt()
    var fullMoveNumber = fen [5].toInt()

    // fügt aus dem String "piecePositions" die Pieces dem Dict/Map "board/piecePositions" hinzu
    fun fen2Board(board: Board){
        var y = 0
        var x = 0

        for(element in piecePosition){
            if(element.isDigit()) {
                x += Character.getNumericValue(element)
                continue
            }
            else if(element.isLetter()){
                val color = if(element.isLowerCase()) 'b' else 'w'
                var piece: Player? = null

                when {
                    element.toLowerCase() == 'k' -> piece = King(Pair(y,x), element, color)
                    element.toLowerCase() == 'q' -> piece = Queen(Pair(y,x), element, color)
                    element.toLowerCase() == 'b' -> piece = Bishop(Pair(y,x), element, color)
                    element.toLowerCase() == 'n' -> piece = Knight(Pair(y,x), element, color)
                    element.toLowerCase() == 'r' -> piece = Rook(Pair(y,x), element, color)
                    element.toLowerCase() == 'p' -> piece = Pawn(Pair(y,x), element, color)
                }

                if(piece != null) {
                    board.piecePositions[Pair(y, x)] = piece
                    x += 1
                }
            }
            else if(element == '/'){
                y +=1
                x = 0
            }
        }
    }


    fun loadFen(board: Board, fen: String){
        piecePosition = fen.split(' ')[0]
        activeColor = fen.split(' ')[1]
        castling = fen.split(' ')[2]
        enPassant = fen.split(' ')[3]
        halfMoveClock = fen.split(' ')[4].toInt()
        fullMoveNumber = fen.split(' ')[5].toInt()
        fen2Board(board)
    }


    fun saveFen(board: Board, color: Char){
        fun board2Fen(board: Board, color: Char): String {
            var string = ""
            var count = 0

            for(y in board.board){
                for(x in y){
                    if(x.status[2] == '_') count += 1
                    else{
                        if(count != 0) string += count.toString()
                        string += x.status[2]
                        count = 0
                    }
                }
                if(count != 0) string += count
                string += '/'
                count = 0
            }

            string = string.subSequence(0, string.length).toString()

            if(color == 'w'){
                string += " b"
                activeColor = "b"
            }
            else{
                string += " w"
                activeColor = "w"
            }

            string += " $castling"

            string += " $enPassant"

            string += " $halfMoveClock"

            string += " $fullMoveNumber"
            return string
        }

        board2Fen(board, color)
    }


    fun setEnPassant(input: Pair<Int, Int>, board: Board, color: Char, possibleMoves: List<Pair<Int, Int>>) {
        val rightSide = Pair(input.first, input.second + 1)
        val leftSide = Pair(input.first, input.second - 1)
        val pieceRight = board.piecePositions[rightSide]
        val pieceLeft = board.piecePositions[leftSide]

        enPassant = if (pieceRight is Pawn && pieceRight.color != color ||
            pieceLeft is Pawn && pieceLeft.color != color) {
            board.xyToBoardposition(possibleMoves[0])
            } else "-"
    }


}