class Rook(position: Pair<Int, Int>, sign: Char, override val color: Char = 'w'): Player(position, sign) {
    override val moves = listOf(
        Pair(0, 1),
        Pair(0, -1),
        Pair(-1, 0),
        Pair(1, 0)
    )
    override val maxSteps: Int = 8
}