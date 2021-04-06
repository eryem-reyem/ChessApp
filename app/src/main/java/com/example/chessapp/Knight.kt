class Knight(position: Pair<Int, Int>, sign: Char, override val color: Char = 'w'): Player(position, sign) {
    override val moves = listOf(
        Pair(1, 2),
        Pair(1, -2),
        Pair(2, 1),
        Pair(2, -1),
        Pair(-1, 2),
        Pair(-1, -2),
        Pair(-2, 1),
        Pair(-2, -1)
    )
    override val maxSteps: Int = 1
}