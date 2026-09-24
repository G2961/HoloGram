package com.hologram.app

import android.app.Activity
import android.content.Context
import android.graphics.Color
import android.graphics.Typeface
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.view.Gravity
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast

class MainActivity : Activity() {

    lateinit var root: FrameLayout
    var currentTag = "chats"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ChatStore.seed()
        ChatStore.onChatChanged = { chat ->
            if (currentTag == "chat:${chat.id}") showChat(chat)
        }
        root = FrameLayout(this).apply { setBackgroundColor(Holo.background) }
        setContentView(root)
        showChats()
    }

    fun showChats() {
        currentTag = "chats"
        root.removeAllViews()
        addFullScreen(ChatsScreen(this).build())
    }

    fun showChat(chat: Chat) {
        currentTag = "chat:${chat.id}"
        root.removeAllViews()
        addFullScreen(ChatScreen(this, chat).build())
    }

    fun showSettings() {
        currentTag = "settings"
        root.removeAllViews()
        addFullScreen(SettingsScreen(this).build())
    }

    /** FrameLayout по умолчанию даёт детям WRAP_CONTENT — выставляем размеры явно. */
    private fun addFullScreen(v: android.view.View) {
        root.addView(
            v,
            android.widget.FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
        )
    }

    fun showToast(text: String) {
        Toast.makeText(this, text, Toast.LENGTH_SHORT).show()
    }
}

/**
 * Holo action bar: тёмный градиент, лёгкий заголовок, overflow-кнопка.
 */
object ActionBar {

    fun build(
        c: Context,
        title: String,
        subtitle: String? = null,
        showBack: Boolean = false,
        onBack: (() -> Unit)? = null,
        overflow: List<Pair<String, () -> Unit>> = emptyList()
    ): View {
        val bar = LinearLayout(c).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER_VERTICAL
            layoutParams = LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, c.dip(52)
            )
            background = GradientDrawable(
                GradientDrawable.Orientation.TOP_BOTTOM,
                Holo.actionBarColors
            )
        }
        bar.setPadding(c.dip(12), 0, c.dip(4), 0)

        if (showBack) {
            bar.addView(iconButton(c, "\u2190") { onBack?.invoke() })
        }

        val titleCol = LinearLayout(c).apply {
            orientation = LinearLayout.VERTICAL
            layoutParams = LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f)
        }
        val titleView = Holo.text(c, title, 19, Holo.textPrimary)
        titleView.typeface = Typeface.create("sans-serif-light", Typeface.NORMAL)
        titleCol.addView(titleView)
        if (subtitle != null) {
            titleCol.addView(Holo.text(c, subtitle, 12, Holo.textSecondary))
        }
        bar.addView(titleCol)

        if (overflow.isNotEmpty()) {
            bar.addView(iconButton(c, "\u22EE") { showMenu(c, overflow) })
        }
        return bar
    }

    /** Кнопка-иконка (символ + синяя подсветка при касании). */
    private fun iconButton(c: Context, glyph: String, onClick: () -> Unit): TextView {
        val size = c.dip(40)
        return TextView(c).apply {
            text = glyph
            setTextColor(Color.WHITE)
            textSize = 20f
            gravity = Gravity.CENTER
            layoutParams = LinearLayout.LayoutParams(size, size).apply {
                marginEnd = c.dip(4)
            }
            setOnClickListener { onClick() }
            setOnTouchListener { v, ev ->
                when (ev.actionMasked) {
                    MotionEvent.ACTION_DOWN -> v.setBackgroundColor(Holo.blueDark)
                    MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL ->
                        v.setBackgroundColor(Color.TRANSPARENT)
                }
                false
            }
        }
    }

    /** Выпадающее Holo-меню: тёмная панель у правого края. */
    private fun showMenu(c: Context, items: List<Pair<String, () -> Unit>>) {
        val activity = c as MainActivity
        val scrim = FrameLayout(c).apply {
            layoutParams = FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT
            )
            setBackgroundColor(Color.parseColor("#40000000"))
        }
        scrim.setOnClickListener { activity.root.removeView(scrim) }

        val menu = LinearLayout(c).apply {
            orientation = LinearLayout.VERTICAL
            layoutParams = FrameLayout.LayoutParams(c.dip(220), ViewGroup.LayoutParams.WRAP_CONTENT)
                .apply {
                    gravity = Gravity.TOP or Gravity.END
                    topMargin = c.dip(56)
                    marginEnd = c.dip(8)
                }
            background = Holo.roundedBg(Holo.bgMenu, 2)
        }
        items.forEachIndexed { i, (label, action) ->
            val row = Holo.text(c, label, 15, Holo.textPrimary).apply {
                layoutParams = LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT
                )
                setPadding(c.dip(20), c.dip(14), c.dip(20), c.dip(14))
                setOnClickListener {
                    activity.root.removeView(scrim)
                    action()
                }
                setOnTouchListener { v, ev ->
                    when (ev.actionMasked) {
                        MotionEvent.ACTION_DOWN -> v.setBackgroundColor(Holo.blueDark)
                        MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL ->
                            v.setBackgroundColor(Holo.bgMenu)
                    }
                    false
                }
            }
            menu.addView(row)
            if (i < items.size - 1) menu.addView(Holo.divider(c))
        }
        scrim.addView(menu)
        activity.root.addView(scrim)
    }
}
