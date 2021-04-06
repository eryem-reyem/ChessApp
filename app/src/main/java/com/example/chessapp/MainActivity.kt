package com.example.chessapp

import Game
import King
import Pawn
import Player
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageView
import androidx.appcompat.widget.AppCompatImageButton
import android.widget.LinearLayout
import android.widget.TextView

const val Tag = "MainActivity"


class MainActivity : AppCompatActivity(), ChessDelegate {
    override lateinit var game: Game
    override var possibleMoves = mutableListOf<Pair<Int, Int>>()
    override var gameActive: Boolean = true
    var piece: Player? = null
    lateinit var setMove: Pair<Boolean, String>

    lateinit var tvPlayer: TextView
    lateinit var tvMove1: TextView
    lateinit var tvMove2: TextView
    lateinit var tvGameStatus: TextView

    private lateinit var chessView: ChessView

    lateinit var viewStart: LinearLayout
    lateinit var viewGame: LinearLayout
    lateinit var viewPromotionPawn: LinearLayout

    lateinit var ivLogo: ImageView

    fun newGame(){
        game = Game()
        game.board.fen.fen2Board(game.board)
        game.board.setPlayerOnBoard()
        tvMove1.text = " "
        tvMove2.text = " "

    }


    fun setPlayer(fen: String){
        if(fen == "w") {
            tvPlayer.setText(R.string.p1)
        } else tvPlayer.setText(R.string.p2)
    }


    fun checkStatus(piece: Player) {
        tvGameStatus.text = " "
        game.checkGamestatus(piece)

        if(game.isCheck){
            tvGameStatus.text = "${game.player} bietet Schach!"
            game.isCheck = false
        }

        if(game.isCheckmate){
            game.board.printBoard()
            tvGameStatus.text = "Schachmatt! ${game.player} hat gewonnen."
            gameActive = false

        }

        if(!game.gameActiv){
            game.board.printBoard()
            tvGameStatus.text = "Unendschieden!"
            gameActive = false
        }
    }

    fun promotionPawn(piece: Player){
        //promotionPawnView()
        if(piece is Pawn) {
            if (piece.position.first == 0 && piece.color == 'w' ||
                piece.position.first == 7 && piece.color == 'b') {
                promotionPawnView()
            }
        }
    }


    fun clearCastelingPositions(){
        for(piece in game.board.piecePositions.values){
            if(piece is King) piece.castelingPositions.clear()
        }
    }


    fun gameView(){
        viewPromotionPawn.visibility = View.INVISIBLE
        viewGame.visibility = View.VISIBLE
        game.board.setPlayerOnBoard()
        chessView.invalidate()
    }


    fun promotionPawnView(){
        viewGame.visibility = View.INVISIBLE
        viewPromotionPawn.visibility = View.VISIBLE
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        tvPlayer = findViewById(R.id.tvPlayer)
        tvMove1= findViewById(R.id.tvMove1)
        tvMove2= findViewById(R.id.tvMove2)
        tvGameStatus = findViewById(R.id.tvGameStatus)

        chessView = findViewById(R.id.chess_view)
        chessView.chessDelegate = this

        viewStart = findViewById(R.id.viewStart)
        viewGame = findViewById(R.id.viewGame)
        viewPromotionPawn = findViewById(R.id.viewPromotionPawn)

        ivLogo = findViewById(R.id.ivLogo)


        newGame()
        setPlayer(game.board.fen.activeColor)


        //start Button
        findViewById<Button>(R.id.btnStartGame).setOnClickListener {
            viewStart.visibility = View.INVISIBLE
            viewGame.visibility = View.VISIBLE
        }

        // Reset Button
        findViewById<Button>(R.id.btnReset).setOnClickListener {
            newGame()
            gameActive = true
            chessView.invalidate()
        }

        // promotion Pawn Buttons
        findViewById<AppCompatImageButton>(R.id.btnQueen).setOnClickListener {
            game.promotionPawn(piece!!, game.board, "Queen")
            gameView()
        }
        findViewById<AppCompatImageButton>(R.id.btnRook).setOnClickListener {
            game.promotionPawn(piece!!, game.board, "Rook")
            gameView()
        }
        findViewById<AppCompatImageButton>(R.id.btnKnight).setOnClickListener {
            game.promotionPawn(piece!!, game.board, "Knight")
            gameView()
        }
        findViewById<AppCompatImageButton>(R.id.btnBishop).setOnClickListener {
            game.promotionPawn(piece!!, game.board, "Bishop")
            gameView()
        }
    }


    override fun movePlayer(pos: Pair<Int, Int>): Boolean{

        val color = game.board.fen.activeColor.single()
        setMove = Pair(false, "")

        if(possibleMoves.size == 0) {
            piece = game.board.piecePositions[pos]
            if(piece!!.color != color) return false
            possibleMoves = game.allPossibleMoves(game.board, piece!!).toMutableList()
            chessView.invalidate()
            println(possibleMoves)
        }
        else if(possibleMoves.size != 0 && pos in possibleMoves && piece != null){
            setMove = game.setMove(pos, possibleMoves, piece!!, game.board)
            possibleMoves.clear()
            if(setMove.first){
                game.board.fen.saveFen(game.board, piece!!.color)
                checkStatus(piece!!)
                setPlayer(game.board.fen.activeColor)
                tvMove1.text = "Player 1: ${setMove.second}"
                promotionPawn(piece!!)
                clearCastelingPositions()
                chessView.invalidate()
                println(game.board.fen.castling)
                return true
            }
        }
        else if(possibleMoves.size != 0 && pos !in possibleMoves){
            try{
                possibleMoves.clear()
                piece = game.board.piecePositions[pos]
                if(piece!!.color != color) return false
                possibleMoves = game.allPossibleMoves(game.board, piece!!).toMutableList()
                chessView.invalidate()
            }catch (e: Exception){
                possibleMoves.clear()
                chessView.invalidate()
                println(e)
            }

        }




        return false
    }


    override fun moveComputer() {
        val move = game.moveComputer(game.board.fen.activeColor)
        piece = move.first
        game.board.fen.saveFen(game.board, piece!!.color)
        if(piece is Pawn) {
            if ((piece as Pawn).position.first == 0 && (piece as Pawn).color == 'w' ||
                (piece as Pawn).position.first == 7 && (piece as Pawn).color == 'b'
            ) {
                game.promotionPawn(piece!!, game.board, "Queen")
            }
        }
        clearCastelingPositions()
        checkStatus(piece!!)
        setPlayer(game.board.fen.activeColor)
        tvMove2.text = "Player 2: ${move.second}"
        chessView.invalidate()
    }
}