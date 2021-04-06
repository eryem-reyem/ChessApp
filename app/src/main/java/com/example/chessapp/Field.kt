// status des Feldes in "board.board"
class Field {
    var status: String = "| _ |"

    // für Felder mit Pieces
    fun char2Piece(char: Char){
        status = "|_${char}_|"
    }


    // für Felder die geschlagen werden können
    fun string2Piece(string: String){
        status = string
    }


    //für leere Felder
    fun emptyField(){
        status = "| _ |"
    }
}