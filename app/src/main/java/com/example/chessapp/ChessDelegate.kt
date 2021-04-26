package com.example.chessapp

import Game

interface ChessDelegate {
    val game: Game
    var possibleMoves: MutableList<Pair<Int, Int>>
    var gameActive: Boolean

    fun movePlayer(pos: Pair<Int, Int>): Boolean
    fun moveComputer()

}