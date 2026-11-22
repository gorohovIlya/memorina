package com.example.corax.memorina

import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    private val faceImages = intArrayOf(
        R.drawable.kitten1,
        R.drawable.kitten2,
        R.drawable.kitten3,
        R.drawable.kitten4,
        R.drawable.kitten5,
        R.drawable.kitten6,
        R.drawable.kitten7,
        R.drawable.kitten8
    )

    private val openCards = mutableListOf<ImageView>()
    private var isChecking = false
    private var gameFinished = false
    private var matchedPairs = 0
    private var flipBackRunnable: Runnable? = null
    private lateinit var board: LinearLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        board = LinearLayout(applicationContext).apply {
            orientation = LinearLayout.VERTICAL
        }
        setContentView(board)

        startNewGame()
    }

    override fun onDestroy() {
        flipBackRunnable?.let { board.removeCallbacks(it) }
        super.onDestroy()
    }

    private fun startNewGame() {
        flipBackRunnable?.let { board.removeCallbacks(it) }
        flipBackRunnable = null
        openCards.clear()
        isChecking = false
        gameFinished = false
        matchedPairs = 0
        board.removeAllViews()

        val rowParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            0,
            1f
        )
        val cardParams = LinearLayout.LayoutParams(
            0,
            LinearLayout.LayoutParams.MATCH_PARENT,
            1f
        )

        val cards = (faceImages + faceImages).toList().shuffled()

        for (rowIndex in 0 until ROWS) {
            val row = LinearLayout(applicationContext).apply {
                orientation = LinearLayout.HORIZONTAL
                layoutParams = rowParams
            }
            for (columnIndex in 0 until COLUMNS) {
                val faceResId = cards[rowIndex * COLUMNS + columnIndex]
                row.addView(ImageView(applicationContext).apply {
                    setImageResource(R.drawable.cover)
                    scaleType = ImageView.ScaleType.FIT_CENTER
                    layoutParams = cardParams
                    tag = faceResId
                    setOnClickListener(cardListener)
                })
            }
            board.addView(row)
        }
    }

    private val cardListener = View.OnClickListener { clicked ->
        val view = clicked as ImageView
        if (isChecking || gameFinished || openCards.size == 2) return@OnClickListener

        view.setImageResource(view.tag as Int)
        view.isClickable = false
        openCards.add(view)

        if (openCards.size == 2) {
            isChecking = true
            val first = openCards[0]
            val second = openCards[1]

            if (first.tag == second.tag) {
                first.visibility = View.INVISIBLE
                second.visibility = View.INVISIBLE
                openCards.clear()
                isChecking = false
                matchedPairs++

                if (matchedPairs == faceImages.size) {
                    gameFinished = true
                    showWinDialog()
                }
            } else {
                val flipBack = Runnable {
                    first.setImageResource(R.drawable.cover)
                    second.setImageResource(R.drawable.cover)
                    first.isClickable = true
                    second.isClickable = true
                    openCards.clear()
                    isChecking = false
                    flipBackRunnable = null
                }
                flipBackRunnable = flipBack
                board.postDelayed(flipBack, FLIP_DELAY_MS)
            }
        }
    }

    private fun showWinDialog() {
        AlertDialog.Builder(this)
            .setTitle(R.string.win_title)
            .setMessage(R.string.win_message)
            .setCancelable(false)
            .setPositiveButton(R.string.play_again) { dialog, _ ->
                dialog.dismiss()
                startNewGame()
            }
            .show()
    }

    companion object {
        private const val ROWS = 4
        private const val COLUMNS = 4
        private const val FLIP_DELAY_MS = 1000L
    }
}
