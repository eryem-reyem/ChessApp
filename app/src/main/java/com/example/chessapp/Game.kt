class Game: Move(){
    val board: Board = Board()
    var color: Char = 'w'
    var player = ""
    var gameActiv = true
    var isCheck = false
    var isCheckmate = false
    private lateinit var piece: Player
    private lateinit var possibleMoves: List<Pair<Int, Int>>

    // überprüft den game Status
    fun checkGamestatus(piece: Player){

        // überprüft ob eine Figur im Schach steht
        fun checkKingIsCheck(piece: Player) {
            var king: Player? = null
            val tempPossibleMoves = if(piece.color == 'w') tempPossibleMoves(board).first
                                    else tempPossibleMoves(board).second


            for(a_piece in board.piecePositions){
                if(a_piece.value is King && a_piece.value.color != piece.color) king = a_piece.value
            }


            if(king != null){
                for(a_piece in tempPossibleMoves){
                    for(a_move in a_piece.value){
                        if(a_move == king.position){
                            isCheck = true
                            break
                        }
                    }
                    if(isCheck) break
                }
            }
        }

        // überprüft ob ein Schachmatt noch verhindert werden kann
        fun checkKingIsCheckmate(piece: Player){
            var king: Player? = null
            val tempPossibleMoves = if(piece.color == 'w') tempPossibleMoves(board).second
                                    else tempPossibleMoves(board).first

            for(a_piece in board.piecePositions){
                if(a_piece.value is King && a_piece.value.color != piece.color) king = a_piece.value
                if(king != null) break
            }


            if(king != null){
                for(a_piece in tempPossibleMoves){
                    for(a_move in a_piece.value){
                        isCheckmate = !checkMyKing(board, a_move, a_piece.key, king!!)
                        if(!isCheckmate) break
                    }
                    if(!isCheckmate) break
                }
            }
        }

        // überprüft unterschiedliche Patt Situationen
        fun checkPutt(piece: Player): Boolean {
            if(board.fen.halfMoveClock == 75){
                gameActiv = false
                return true
            }

            if(board.piecePositions.size == 2){
                gameActiv = false
                return true
            }

            val tempPossibleMoves = if(piece.color == 'w') tempPossibleMoves(board).second
                                    else tempPossibleMoves(board).first
            var movesPossible = false

            for(a_piece in tempPossibleMoves){
                if(a_piece.value.isNotEmpty()){
                    movesPossible = true
                }
            }
            if(!movesPossible){
                gameActiv = false
                return true
            }

            if(board.piecePositions.size == 3){
                for(a_piece in board.piecePositions.values){
                    if(a_piece is Knight || a_piece is Bishop){
                        gameActiv = false
                        return true
                    }
                }
            }

            return false
        }


        checkKingIsCheck(piece)

        if(isCheck) checkKingIsCheckmate(piece)

        if(isCheckmate) isCheck = false
        else checkPutt(piece)


    }

    // Shell input Funktion für reale Spieler
    fun movePlayer(activeColor: String): Player {
        for(piece in board.piecePositions.values){
            if(piece is King) piece.castelingPositions.clear()
        }

        color = activeColor.single()

        player = if(activeColor == "w") "Player 1" else "Player 2"

        while (true){
            // Nutzereingbe welche figur ziehen soll
            fun pieceFrom(): Boolean {
                val input = board.inputPositionToXy(player, "welche Figur willst du bewegen?")

                if(input in board.piecePositions && board.piecePositions[input]?.color == color){
                    piece = board.piecePositions[input]!!
                }
                else {
                    println("Not a valid Move!")
                    return false
                }
                return true
            }


            // shell Ausgabe des Boards
            board.printBoard()


            // aus der Nutzereingabe wird eine Figur der Variablen "piece zugewiesen"
            if(!pieceFrom()) continue


            // Liste mit Positionen auf die das piece ziehen kann
            possibleMoves = allPossibleMoves(board, piece)


            // unterbricht den Loop, wenn keine Züge möglich sind
            if(possibleMoves.isEmpty()) {
                println("Keine gültigen Züge mit der Figur ${piece.sign}!")
                continue
            }


            // Ausgabe der Liste möglicher Züge
            print("${piece.sign} hat folgende Möglichkeiten zu Ziehen: ")
            for(move in possibleMoves) print("${board.xyToBoardposition(move)} /")
            println()


            // zeigt mögliche Züge auf dem Board
            board.showPossibleMoves(possibleMoves)


            // hier findet die Eingabe statt auf welches Feld gezogen werden soll
            val inputToXy = board.inputPositionToXy(player,  "wohin willst du ziehen?")


            //  setzt werte im Bord zurück die in "board.showPossibleMoves" gändert wurden
            //board.returnShowPossibleMoves(possibleMoves)
            board.setPlayerOnBoard()


            // führt den gewählten Zug aus, wenn er gültig ist
            if(inputToXy in board.coordinates) {
                if (setMove(inputToXy, possibleMoves, piece, board).first) {
                    if (board.fen.castling == "") board.fen.castling = "-"
                    return piece
                } else println("Kein gültiger Zug!")
            }
        }
    }

    fun moveComputer(activeColor: String): Pair<Player, String> {
        while(true) {
            color = activeColor.single()

            player = if(activeColor == "w") "Player 1" else "Player 2"

            val piece = board.piecePositions.entries.shuffled().first()


            if(piece.value.color != color) continue

            // Liste mit Positionen auf die das piece ziehen kann
            possibleMoves = allPossibleMoves(board, piece.value)

            if(possibleMoves.isEmpty()) continue

            val move = possibleMoves.shuffled()[0]

            val setMove = setMove(move, possibleMoves, piece.value, board)
            if (setMove.first) {
                if (board.fen.castling == "") board.fen.castling = "-"
                return Pair(piece.value, setMove.second)
            } else println("Kein gültiger Zug!")

        }
    }


}