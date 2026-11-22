package com.example.corax.memorina

import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val layout = LinearLayout(applicationContext).apply {
            orientation = LinearLayout.VERTICAL
        }

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
                    setOnClickListener(colorListener)
                })
            }
            layout.addView(row)
        }

        setContentView(layout)
    }

    private val colorListener = View.OnClickListener { clicked ->
        val view = clicked as ImageView
        if (isChecking || openCards.size == 2) return@OnClickListener

        view.setImageResource(view.tag as Int)
        view.isClickable = false
        openCards.add(view)

        if (openCards.size == 2) {
            isChecking = true
            val first = openCards[0]
            val second = openCards[1]

            if (first.tag == second.tag) {
                openCards.clear()
                isChecking = false
            } else {
                first.postDelayed({
                    first.setImageResource(R.drawable.cover)
                    second.setImageResource(R.drawable.cover)
                    first.isClickable = true
                    second.isClickable = true
                    openCards.clear()
                    isChecking = false
                }, FLIP_DELAY_MS)
            }
        }
    }

    companion object {
        private const val ROWS = 4
        private const val COLUMNS = 4
        private const val FLIP_DELAY_MS = 1000L
    }
}
