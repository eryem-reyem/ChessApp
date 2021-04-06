class Board {
    val fen = Fen()
    val board = Array(8){ Array(8){ Field()}}  // Brett mit 64 Feldern
    var piecePositions = hashMapOf<Pair<Int, Int>, Player>()   // Dictionary/Map mit activen Figuren
    //var defendCheck = mutableListOf<Pair<Int, Int>>()
    val coordinates = getCoordinate()


    // Funktion die alle Coordinaten generiert
    private fun getCoordinate(): List<Pair<Int, Int>> {
        val tempCoordinates = mutableListOf<Pair<Int, Int>>()
        for(row in 0..7){
            for(column in 0..7){
                tempCoordinates.add(Pair(row, column))
            }
        }
        return tempCoordinates
    }

    // Initialisiert die Zeichen der Pieces auf dem Brett
    fun setPlayerOnBoard(){
        for(coordinate in coordinates){
            if(coordinate in piecePositions){
                board[coordinate.first][coordinate.second].char2Piece(piecePositions[coordinate]!!.sign)
            }else board[coordinate.first][coordinate.second].emptyField()
        }
    }

    // Shell eingabe für Position im Format a2... returned xy Koordinate
    fun inputPositionToXy(player: String, massage: String): Pair<Int, Int> {
        //if(defendCheck.size != 0) println("$player dein König steht im Schach!")
        print("$player $massage: ")

        val aInput = readLine()
        val input: String

        if(aInput != null && aInput.length == 2){
            input = aInput
        }
        else return Pair(11, 11)

        return boardposition2Xy(input)

    }

    // wandelt ein Position(z.B. a2) in eine Koordinate um.(z.B. Pair(0, 3))
    fun boardposition2Xy(input: String): Pair<Int, Int> {
        val letters = listOf('a', 'b', 'c', 'd', 'e', 'f', 'g', 'h')
        val numbers = listOf(8, 7, 6 , 5, 4, 3, 2, 1)

        val a = input[1]
        val a2: Int
        val b = input[0]
        val b2: Int

        return if(a.isDigit() && b in letters && Character.getNumericValue(a) in numbers) {
            a2 = numbers.indexOf(Character.getNumericValue(a))
            b2 = letters.indexOf(input[0].toLowerCase())
            Pair(a2, b2)
        } else{
            Pair( 10, 10)
        }
    }

    // wandelt eine Koordinate (z.B. Pair(0, 3)) in eine Position um.(z.B. a2)
    fun xyToBoardposition(position: Pair<Int, Int>): String {
        val letters = listOf('a', 'b', 'c', 'd', 'e', 'f', 'g', 'h')
        val numbers = listOf(8, 7, 6 , 5, 4, 3, 2, 1)
        if(position.first in 0..7 && position.second in 0..7) return "${letters[position.second]}${numbers[position.first]}"
        return "Keine gültige Position"
    }

    // Printed in der Shell
    fun printBoard(){
        var count = 8
        println("   ----------------------------------------")
        println("   | A || B || C || D || E || F || G || H |")
        println("   ----------------------------------------")
        for(i in board){
            print("$count  ")
            for(j in i){
                print((j.status))
            }
            print("  $count")
            count -= 1
            println()
        }
        println("   ----------------------------------------")
        println("   | A || B || C || D || E || F || G || H |")
        println("   ----------------------------------------")

    }

    // Verändert "Field.status" im "board" um mögliche Züge einer Figur in der Shell darzustellen
    fun showPossibleMoves(possibleMoves: List<Pair<Int, Int>>){
        for(i in possibleMoves){
            if(board[i.first][i.second].status == "| _ |"){
                board[i.first][i.second].string2Piece("|(_)|")
            }
            else{
                val status = "|(${board[i.first][i.second].status[2]})|"
                board[i.first][i.second].string2Piece(status)
            }
        }
        printBoard()
    }

}