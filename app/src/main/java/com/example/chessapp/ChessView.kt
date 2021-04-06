package com.example.chessapp

import Game
import android.content.Context
import android.graphics.BitmapFactory
import android.graphics.*
import android.os.Build
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.view.View
import androidx.annotation.RequiresApi
import java.lang.Float.min
import kotlin.math.min

class ChessView(context: Context?, attrs: AttributeSet?) : View(context, attrs) {
    val scaleFactor = 1f
    var originX = 60f
    var originY = 10f
    var cellSide: Float = 120f
    var chessBoardSide: Float = 120f

    lateinit var chessDelegate: ChessDelegate

    var fromCol = -1
    var fromRow = -1

    var move = false

    val pieces = mutableMapOf(
            'b' to R.drawable.bb,
            'k' to R.drawable.bk,
            'n' to R.drawable.bn,
            'p' to R.drawable.bp,
            'q' to R.drawable.bq,
            'r' to R.drawable.br,
            'B' to R.drawable.wb,
            'K' to R.drawable.wk,
            'N' to R.drawable.wn,
            'P' to R.drawable.wp,
            'Q' to R.drawable.wq,
            'R' to R.drawable.wr,
            'C' to R.drawable.circle,
    )

    val images = mutableMapOf(
            "logo" to R.drawable.logo,
            "og" to R.drawable.orangegrey
    )

    var boardImg = images["og"]!!
    val paint = Paint()

    val bitmaps = mutableMapOf<Int, Bitmap?>()


    init {
        println("init")
        loadBitmaps()
    }


    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        val size = min(widthMeasureSpec, heightMeasureSpec)
        setMeasuredDimension(size, (size+10))
    }


    override fun onTouchEvent(event: MotionEvent?): Boolean {
        event ?: return false

        when(event.action){
            MotionEvent.ACTION_DOWN -> {
                fromCol = ((event.x - originX) / cellSide).toInt()
                fromRow = ((event.y - originY) / cellSide).toInt()
                try {
                    move = chessDelegate.movePlayer(Pair(fromRow, fromCol))
                    return true


                    println("Down")
                }catch (e: Exception){
                    println(e)

                }

            }
            MotionEvent.ACTION_UP -> {
                val col = ((event.x - originX) / cellSide).toInt()
                val row = ((event.y - originY) / cellSide).toInt()
                Log.d(Tag, "from $fromRow, $fromCol to  $row, $col")


            }
            MotionEvent.ACTION_MOVE -> {

                Log.d(Tag, "move")
            }
        }
        return true
    }

    override fun onDraw(canvas: Canvas){
        println("OnDraw")
        chessBoardSide = min(height, width) * scaleFactor
        cellSide = chessBoardSide / 8f
        originX = (width - chessBoardSide)

        drawChessboard(canvas, boardImg)
        drawPieces(canvas)
        drawPossibleMoves(canvas)

        if(move && chessDelegate.gameActive) {
            chessDelegate.moveComputer()
            move = false
        }
    }

    fun drawPossibleMoves(canvas: Canvas){
        for(pos in chessDelegate.possibleMoves){
            drawPieceAt(canvas, pos.first, pos.second, pieces['C']!!)
        }
    }


    fun drawPieces(canvas: Canvas){
        println("drawPieces")

        for (element in chessDelegate.game.board.piecePositions){
            drawPieceAt(canvas, element.key.first, element.key.second, pieces[element.value.sign]!!)
        }
    }


    fun drawPieceAt(canvas: Canvas, row: Int, column: Int, resID: Int){
        canvas.drawBitmap(bitmaps[resID]!!, null, RectF(originX + column * cellSide,originY + (row) * cellSide,originX + (column + 1) * cellSide,originY + ((row) + 1) * cellSide), paint)
    }


    fun drawChessboard(canvas: Canvas, resID: Int){
        canvas.drawBitmap(bitmaps[resID]!!, null, RectF(originX, originY, originX + chessBoardSide, originY + chessBoardSide), paint)
    }


    fun loadBitmaps(){
        pieces.forEach {
            bitmaps[it.value] = BitmapFactory.decodeResource(resources, it.value)
        }
        images.forEach{
            bitmaps[it.value] = BitmapFactory.decodeResource(resources, it.value)
        }
    }
}