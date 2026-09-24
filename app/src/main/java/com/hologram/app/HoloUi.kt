package com.hologram.app

import android.content.Context
import android.graphics.Color
import android.graphics.Typeface
import android.graphics.drawable.GradientDrawable
import android.util.TypedValue
import android.view.Gravity
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView

/**
 * Палитра и UI-хелперы Holo Dark (Android 4.2.2).
 */
object Holo {
    val background = Color.BLACK
    val bgLight = Color.parseColor("#171717")
    val bgPanel = Color.parseColor("#2A2A2A")
    val bgMenu = Color.parseColor("#222222")
    val divider = Color.parseColor("#2A2A2A")
    val blue = Color.parseColor("#33B5E5")
    val blueDark = Color.parseColor("#0099CC")
    val textPrimary = Color.parseColor("#DEDEDE")
    val textSecondary = Color.parseColor("#888888")
    val textHint = Color.parseColor("#666666")
    val textOnBlue = Color.parseColor("#0A2C36")
    val bubbleThem = Color.parseColor("#333333")

    // градиент action bar, как в нативном Holo
    val actionBarColors = intArrayOf(
        Color.parseColor("#0A3D4D"),
        Color.parseColor("#08303C"),
        Color.parseColor("#041E26")
    )

    fun dp(c: Context, v: Int): Int =
        TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, v.toFloat(), c.resources.displayMetrics)
            .toInt()

    fun dpF(c: Context, v: Int): Float =
        TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, v.toFloat(), c.resources.displayMetrics)

    /** Тонкий разделитель на всю ширину. */
    fun divider(c: Context): View =
        View(c).apply {
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, dp(c, 1)
            )
            setBackgroundColor(divider)
        }

    /** Текст с Holo-типографикой. */
    fun text(
        c: Context,
        text: String,
        size: Int,
        color: Int = textPrimary,
        bold: Boolean = false,
        light: Boolean = false
    ): TextView =
        TextView(c).apply {
            setText(text)
            setTextColor(color)
            textSize = size.toFloat()
            if (bold) setTypeface(typeface, Typeface.BOLD)
            if (light) setTypeface(Typeface.create("sans-serif-light", Typeface.NORMAL))
        }

    /** Прямоугольник с фоном и скруглением 2dp (фирменный Holo-радиус). */
    fun roundedBg(color: Int, radiusDp: Int = 2): GradientDrawable =
        GradientDrawable().apply {
            setColor(color)
            cornerRadius = radiusDp.toFloat()
        }
}

/**
 * Обёртка для ripple-free «нажатия» в Holo-стиле: фон подсвечивается синим.
 */
fun View.holoPressable(pressedColor: Int = Holo.blue, onClick: () -> Unit) {
    val defaultBg = background
    setOnClickListener { onClick() }
    setOnTouchListener { v, event ->
        when (event.actionMasked) {
            android.view.MotionEvent.ACTION_DOWN -> v.setBackgroundColor(pressedColor)
            android.view.MotionEvent.ACTION_UP,
            android.view.MotionEvent.ACTION_CANCEL -> v.background = defaultBg
        }
        false // не съедаем событие, чтобы клик сработал
    }
}

/** Размер шрифта сообщений из настройки 0..1 -> 13..19sp */
fun messageSp(fontSizeSetting: Float): Float = 13f + fontSizeSetting * 6f

fun Context.dip(v: Int): Int = Holo.dp(this, v)
