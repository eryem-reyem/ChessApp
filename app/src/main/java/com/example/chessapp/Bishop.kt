class Bishop(position: Pair<Int, Int>, sign: Char, override val color: Char = 'w'): Player(position, sign) {
    override val moves = listOf(
        Pair(1, 1),
        Pair(1, -1),
        Pair(-1, 1),
        Pair(-1, -1)
    )
    override val maxSteps: Int = 8
}