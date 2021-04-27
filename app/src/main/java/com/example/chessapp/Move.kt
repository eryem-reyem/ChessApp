open class Move {
    private var capturedWhitePieces = mutableListOf<Player>()
    private var capturedBlackPieces = mutableListOf<Player>()


    // führt einen temporären Zug in "board.board" aus und checked ob der eigene König danch im Schach steht
    fun checkMyKing(board: Board, move: Pair<Int, Int>, piece: Player, king: Player): Boolean {
        var validMove = true

        val pos = piece.position
        if(piece is King) piece.position = move


        val sign = board.board[move.first][move.second].status
        board.board[pos.first][pos.second].emptyField()
        board.board[move.first][move.second].char2Piece(piece.sign)

        val tempMoves = tempPossibleMoves(board)

        val tempList = if(piece.color == 'w') tempMoves.second
                        else tempMoves.first

        for(a_piece in tempList){
            if(a_piece.key.position == move) continue
            for(a_move in a_piece.value){
                if(a_move == king.position){
                    validMove = false
                    break
                }
            }
            if(!validMove) break
        }

        board.board[move.first][move.second].string2Piece(sign)
        board.board[pos.first][pos.second].char2Piece(piece.sign)

        if(piece is King) piece.position = pos

        return validMove
    }


    // gibt listen mit gültigen Zügen für eine Figur zurück
    fun allPossibleMoves(board: Board, piece: Player): List<Pair<Int, Int>> {
        val tempPossibleMoves = tempPossibleMoves(board)
        val allPossibleWhiteMoves = tempPossibleMoves.first as HashMap<Player, List<Pair<Int, Int>>>
        val allPossibleBlackMoves = tempPossibleMoves.second as HashMap<Player, List<Pair<Int, Int>>>
        var king: Player? = null
        val realPossibleMoves = mutableListOf<Pair<Int, Int>>()


        for(a_piece in board.piecePositions) if(a_piece.value is King && a_piece.value.color == piece.color) king = a_piece.value

        if(king != null) {
            if (piece.color == 'w') {
                for (move in allPossibleWhiteMoves[piece]!!) {
                    if(checkMyKing(board, move, piece, king)) realPossibleMoves.add(move)
                }
            }
            else{
                for (move in allPossibleBlackMoves[piece]!!) {
                    if(checkMyKing(board, move, piece, king)) realPossibleMoves.add(move)
                }
            }
        }
        return realPossibleMoves
    }


    // gibt listen mit möglichen Zügen für eine Figur zurück
    fun tempPossibleMoves(board: Board): Pair<Map<Player, List<Pair<Int, Int>>>, Map<Player, List<Pair<Int, Int>>>> {
        val allPossibleWhiteMoves = hashMapOf<Player, List<Pair<Int, Int>>>()
        val allPossibleBlackMoves = hashMapOf<Player, List<Pair<Int, Int>>>()
        for (element in board.piecePositions) {
            val possibleMoves = element.value.getPossibleMoves(board)
            if (element.value.color == 'w') allPossibleWhiteMoves[element.value] = possibleMoves
            else if (element.value.color == 'b') allPossibleBlackMoves[element.value] = possibleMoves
        }

        return Pair<Map<Player, List<Pair<Int, Int>>>, Map<Player, List<Pair<Int, Int>>>>(
            allPossibleWhiteMoves,
            allPossibleBlackMoves )
    }


    // wandelt einen Bauern um, wenn dieser auf ein Promotion-Feld kommt
    fun promotionPawn(piece: Player, board: Board, choices: String): Player {
        val newPiece =  if(choices == "Queen"){
            val sign = if(piece.sign.isLowerCase()) 'q'
            else 'Q'
            Queen(piece.position, sign, piece.color)
        }
        else if(choices== "Rook"){
            val sign = if(piece.sign.isLowerCase()) 'r'
            else 'R'
            Rook(piece.position, sign, piece.color)
        }
        else if(choices== "Knight"){
            val sign = if(piece.sign.isLowerCase()) 'n'
            else 'N'
            Knight(piece.position, sign, piece.color)
        }
        else {
            val sign = if(piece.sign.isLowerCase()) 'b'
            else 'B'
            Bishop(piece.position, sign, piece.color)
        }

        board.piecePositions[piece.position] = newPiece
        return newPiece
    }



    // führt einen Zug aus und ändert alle nötigen Variablen
    fun setMove(input: Pair<Int, Int>, possibleMoves: List<Pair<Int, Int>>, piece: Player, board: Board): Pair<Boolean, String>{
        var massage = ""

        // Schlägt eine gegnerische Figur, wenn diese auf dem Zugfeld steht
        fun capturePiece(input: Pair<Int, Int>): Player? {
            val toCapture = board.piecePositions[input]

            if (toCapture != null) {                            // Setzt Piece auf Tod und fügt es einer Liste für tote Pieces zu
                toCapture.isDead = true
                if(toCapture.color == 'w') capturedWhitePieces.add(toCapture)
                else capturedBlackPieces.add(toCapture)

                board.piecePositions.remove(input)             // löscht das geschlagene Piece aus dem Dictionary/Map mit activen Figuren

                board.fen.halfMoveClock = 0

                return toCapture
            }
            return null
        }

        // Schlägt einen Pawn EnPassant
        fun captureEnPassant(){
            if(piece.color == 'b'){
                capturePiece(Pair(input.first-1, input.second))
                board.board[input.first-1][input.second].emptyField()
            }
            else if(piece.color == 'w'){
                capturePiece(Pair(input.first+1, input.second))
                board.board[input.first+1][input.second].emptyField()
            }
        }

        // führt Castle aus und ändert "fen.casteling"
        fun casteling(piece: Player, board: Board, input: Pair<Int, Int>): Boolean {
            var castle = false

            fun castle(): Boolean {
                if(input == Pair(0, 2)){
                    println("Black castled Queenside")
                    val king = board.piecePositions[Pair(0, 4)]
                    val rook = board.piecePositions[Pair(0, 0)]

                    if (king != null && rook != null) {
                        // für König
                        board.piecePositions[Pair(0, 2)] = king
                        board.piecePositions.minusAssign(Pair(0, 4))
                        board.board[0][2].char2Piece(king.sign)
                        board.board[0][4].emptyField()
                        board.board[0][6].emptyField()
                        king.position = Pair(0, 2)

                        // für rook
                        board.piecePositions[Pair(0, 3)] = rook
                        board.piecePositions.minusAssign(Pair(0, 0))
                        board.board[0][3].char2Piece(rook.sign)
                        board.board[0][0].emptyField()
                        rook.position = Pair(0, 3)


                        return true
                    }
                } // black Quennside

                else if(input == Pair(0, 6)){
                    println("Black castled Kingside")
                    val king = board.piecePositions[Pair(0, 4)]
                    val rook = board.piecePositions[Pair(0, 7)]

                    if (king != null && rook != null) {
                        // für König
                        board.piecePositions[Pair(0, 6)] = king
                        board.piecePositions.minusAssign(Pair(0, 4))
                        board.board[0][6].char2Piece(king.sign)
                        board.board[0][4].emptyField()
                        board.board[0][2].emptyField()
                        king.position = Pair(0, 6)

                        // für rook
                        board.piecePositions[Pair(0, 5)] = rook
                        board.piecePositions.minusAssign(Pair(0, 7))
                        board.board[0][5].char2Piece(rook.sign)
                        board.board[0][7].emptyField()
                        rook.position = Pair(0, 5)

                        return true
                    }
                } // black Kingside

                else if(input == Pair(7, 2)){
                    println("White castled Queenside")
                    val king = board.piecePositions[Pair(7, 4)]
                    val rook = board.piecePositions[Pair(7, 0)]

                    if (king != null && rook != null) {
                        // für König
                        board.piecePositions[Pair(7, 2)] = king
                        board.piecePositions.minusAssign(Pair(7, 4))
                        board.board[7][2].char2Piece(king.sign)
                        board.board[7][4].emptyField()
                        board.board[7][6].emptyField()
                        king.position = Pair(7, 2)

                        // für rook
                        board.piecePositions[Pair(7, 3)] = rook
                        board.piecePositions.minusAssign(Pair(7, 0))
                        board.board[7][3].char2Piece(rook.sign)
                        board.board[7][0].emptyField()
                        rook.position = Pair(7, 3)

                        return true
                    }
                } // withe Quennside

                else if(input == Pair(7, 6)){
                    println("White castled Kingside")
                    val king = board.piecePositions[Pair(7, 4)]
                    val rook = board.piecePositions[Pair(7, 7)]

                    if (king != null && rook != null) {
                        // für König
                        board.piecePositions[Pair(7, 6)] = king
                        board.piecePositions.minusAssign(Pair(7, 4))
                        board.board[7][6].char2Piece(king.sign)
                        board.board[7][4].emptyField()
                        board.board[7][2].emptyField()
                        king.position = Pair(7, 6)

                        // für rook
                        board.piecePositions[Pair(7, 5)] = rook
                        board.piecePositions.minusAssign(Pair(7, 7))
                        board.board[7][5].char2Piece(rook.sign)
                        board.board[7][7].emptyField()
                        rook.position = Pair(7, 5)

                        return true
                    }
                } // black Kingside

                return false
            }

            if(piece is King) {
                if (piece.castelingPositions.size != 0 && input in piece.castelingPositions) {
                    castle = castle()
                }

                if (piece.color == 'w') {
                    if ("Q" in board.fen.castling) board.fen.castling = board.fen.castling.replace("Q", "")
                    if ("K" in board.fen.castling) board.fen.castling = board.fen.castling.replace("K", "")
                }           // removes castling on fen for white
                else if (piece.color == 'b') {
                    if ("q" in board.fen.castling) board.fen.castling = board.fen.castling.replace("q", "")
                    if ("k" in board.fen.castling) board.fen.castling = board.fen.castling.replace("k", "")
                }       // removes castling on fen for black

                return castle
            }

            if(piece is Rook){
                if("k" in board.fen.castling && piece.position == Pair(0, 7)) board.fen.castling = board.fen.castling.replace("k", "")
                else if("q" in board.fen.castling && piece.position == Pair(0, 0)) board.fen.castling =  board.fen.castling.replace("q", "")
                else if("K" in board.fen.castling && piece.position == Pair(7, 7)) board.fen.castling = board.fen.castling.replace("K", "")
                else if("Q" in board.fen.castling && piece.position == Pair(7, 0)) board.fen.castling = board.fen.castling.replace("Q", "")
            }

            return castle
        }


        if(input in possibleMoves){
            // Schlägt eine gegnerische Figur, wenn diese auf dem Zugfeld steht
            if(input in board.piecePositions){
                val captured = capturePiece(input)
                if(captured != null) massage = "${piece.sign} schlägt ${captured.sign} auf ${board.xyToBoardposition(input)}."
            }else massage = "${piece.sign} zieht auf ${board.xyToBoardposition(input)}."


            // Schlägt einen Pon EnPassant
            if(piece is Pawn && board.xyToBoardposition(input) == board.fen.enPassant) captureEnPassant()


            // Setzt EnPassant Variable in der Klasse Fen
            if(piece is Pawn && possibleMoves.size == 2 && possibleMoves[1] == input){
                board.fen.setEnPassant(input, board, piece.color, possibleMoves)
            }else board.fen.enPassant = "-"


            // Casteling
            if(board.fen.castling != "-"){
                val casteling = casteling(piece, board, input)
                if(casteling) return Pair(casteling, massage)
            }


            board.piecePositions.minusAssign(Pair(piece.position.first, piece.position.second))
            board.piecePositions[input] = piece
            piece.position = input

            board.setPlayerOnBoard()

            if(piece.color == 'b') board.fen.fullMoveNumber += 1

            board.fen.halfMoveClock += 1
            if(piece is Pawn) board.fen.halfMoveClock = 0


            println(board.piecePositions)
            return Pair(true, massage)
        }
        else{
            return Pair(false, massage)
        }
    }


}