package com.example.budgetapp

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.os.Build
import android.util.AttributeSet
import android.view.View
import androidx.annotation.RequiresApi
import java.time.LocalDate
import java.util.*

class SpendingGraph @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    var maxWidth: Int = 0
    var maxHeight: Int = 0


    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        maxWidth = w
        maxHeight = h
    }

    @RequiresApi(Build.VERSION_CODES.O)
    @SuppressLint("DrawAllocation")
    override fun onDraw(canvas: Canvas?) {
        val originX = 75F
        val originY = maxHeight - 40F
        val endX = maxWidth - 40F
        val endY = 40F
        val paint = Paint()
        paint.color = Color.BLACK
        paint.strokeWidth = 5F


        drawAxes(canvas, originX, originY, endX, paint, endY)

        paint.textSize = 40F

        val now = LocalDate.now()
        val maxLength = now.lengthOfMonth()

        drawDaysOfMonthLabels(maxLength, canvas, originX, originY, endY, paint)

        val daysBalances = createAndFillDayBalances(maxLength)

        val (min, max) = calculateMinAndMaxDailyBalances(daysBalances)

        drawMinAndMaxBalancesPerDay(
            min, max,
            canvas,
            originX,
            originY,
            paint,
            endX
        )

        drawZeroBalanceLine(min, max, endX, originX, paint, canvas, endY, originY)
        drawBalanceBetweenDays(
            daysBalances,
            paint,
            min,
            max,
            endX,
            originX,
            maxLength,
            originY,
            endY,
            canvas
        )
    }

    private fun calculateMinAndMaxDailyBalances(daysBalances: DoubleArray): Pair<Double, Double> {
        var min = daysBalances[0]
        var max = daysBalances[0]

        for (i in daysBalances.indices) {
            if (daysBalances[i] > max) {
                max = daysBalances[i]
            }
            if (daysBalances[i] < min) {
                min = daysBalances[i]
            }
        }

        if (min == max) {
            max += 0.0000001
        }
        return Pair(min, max)
    }

    private fun drawBalanceBetweenDays(
        daysBalances: DoubleArray,
        paint: Paint,
        min: Double,
        max: Double,
        endX: Float,
        originX: Float,
        maxLength: Int,
        originY: Float,
        endY: Float,
        canvas: Canvas?
    ) {
        var previousPoint: Pair<Float, Float> = Pair(0.0F, 0.0F)

        daysBalances.forEachIndexed { index, value ->
            if (value > 0) {
                paint.color = Color.GREEN
            }

            if (value < 0) {
                paint.color = Color.RED
            }

            val x = normalize(
                min.toFloat(),
                max.toFloat(),
                value.toFloat()
            ) * (endX - originX)
            val y = normalize(1.toFloat(), maxLength.toFloat(), index.toFloat()+1) * (originY - endY)

            canvas?.drawCircle(originX + x, originY - y, 10F, paint)
            if (index != 0) {
                canvas?.drawLine(
                    previousPoint.first + originX,
                    originY - previousPoint.second,
                    x + originX,
                    originY - y,
                    paint
                )
            }
            previousPoint = Pair(x, y)
        }
    }

    private fun drawZeroBalanceLine(
        min: Double,
        max: Double,
        endX: Float,
        originX: Float,
        paint: Paint,
        canvas: Canvas?,
        endY: Float,
        originY: Float
    ) {
        val zeroLine = normalize(min.toFloat(), max.toFloat(), 0f) * (endX - originX) + originX
        paint.color = Color.BLUE
        canvas?.drawLine(zeroLine, endY, zeroLine, originY, paint)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun createAndFillDayBalances(maxLength: Int): DoubleArray {
        val daysBalances = DoubleArray(maxLength) {
            0.0
        }
        val calendar = Calendar.getInstance()
        val currentMonth = calendar.get(Calendar.MONTH) + 1
        Shared.financialOperations.forEach {
            if (it.date.monthValue == currentMonth)
                daysBalances[it.date.dayOfMonth] += it.amount
        }
        return daysBalances
    }

    private fun drawMinAndMaxBalancesPerDay(
        min: Double,
        max: Double,
        canvas: Canvas?,
        originX: Float,
        originY: Float,
        paint: Paint,
        endX: Float
    ) {
        canvas?.drawText(
            "%.2f".format(min),
            originX,
            originY + 40F,
            paint
        )

        canvas?.drawText(
            "%.2f".format(max),
            endX - 55,
            originY + 40F,
            paint
        )
    }

    private fun drawDaysOfMonthLabels(
        maxLength: Int,
        canvas: Canvas?,
        originX: Float,
        originY: Float,
        endY: Float,
        paint: Paint
    ) {
        for (i in maxLength downTo 1) canvas?.drawText(
            i.toString(),
            originX - 55,
            (1 - normalize(
                1.toFloat(),
                maxLength.toFloat(),
                i.toFloat()
            )) * (originY - endY) + endY + 10,
            paint
        )
    }

    private fun drawAxes(
        canvas: Canvas?,
        originX: Float,
        originY: Float,
        endX: Float,
        paint: Paint,
        endY: Float
    ) {
        canvas?.drawLine(originX - 2.5F, originY, endX + 30, originY, paint)
        canvas?.drawLine(originX, endY - 30, originX, originY + 2.5F, paint)
    }

    private fun normalize(minValue: Float, maxValue: Float, value: Float) =
        (value - minValue) / (maxValue - minValue)

}