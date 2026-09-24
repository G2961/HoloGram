package com.hologram.app

import android.content.Context
import android.graphics.Typeface
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.TextView

class ChatScreen(
    private val c: Context,
    private val chat: Chat
) {

    fun build(): View {
        val column = LinearLayout(c).apply {
            orientation = LinearLayout.VERTICAL
        }
        column.addView(
            ActionBar.build(
                c,
                title = chat.name,
                subtitle = "был(а) недавно",
                showBack = true,
                onBack = { (c as MainActivity).showChats() },
                overflow = listOf(
                    "Очистить историю" to {
                        chat.messages.clear()
                        (c as MainActivity).showChat(chat)
                    },
                    "Настройки" to { (c as MainActivity).showSettings() }
                )
            )
        )
        column.addView(Holo.divider(c))

        // лента сообщений
        val feed = LinearLayout(c).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(c.dip(8), c.dip(8), c.dip(8), c.dip(8))
        }
        chat.messages.forEach { feed.addView(bubble(it)) }

        val scroller = ScrollView(c).apply {
            layoutParams = LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, 0, 1f
            )
        }
        scroller.addView(feed)
        column.addView(scroller)
        column.addView(Holo.divider(c))

        // поле ввода + отправка
        val input = EditText(c).apply {
            hint = "Сообщение"
            setTextColor(Holo.textPrimary)
            setHintTextColor(Holo.textHint)
            textSize = 15f
            background = Holo.roundedBg(Holo.bgPanel, 2)
            setSingleLine(true)
            setPadding(c.dip(10), c.dip(10), c.dip(10), c.dip(10))
        }

        fun scrollToBottom() {
            scroller.post { scroller.fullScroll(ScrollView.FOCUS_DOWN) }
        }
        scrollToBottom()

        val send = TextView(c).apply {
            text = "\u25B6"
            setTextColor(Holo.blue)
            textSize = 22f
            gravity = Gravity.CENTER
            layoutParams = LinearLayout.LayoutParams(c.dip(44), c.dip(44)).apply {
                marginStart = c.dip(8)
            }
        }
        fun doSend() {
            val text = input.text.toString().trim()
            if (text.isNotEmpty()) {
                ChatStore.sendMessage(chat, text)
                feed.addView(bubble(chat.messages.last()))
                input.setText("")
                scrollToBottom()
                input.clearFocus()
            }
        }
        send.setOnClickListener { doSend() }
        input.setOnEditorActionListener { _, _, _ ->
            doSend()
            true
        }

        val inputRow = LinearLayout(c).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER_VERTICAL
            setPadding(c.dip(8), c.dip(6), c.dip(8), c.dip(6))
            setBackgroundColor(Holo.bgLight)
        }
        inputRow.addView(input, LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f))
        inputRow.addView(send)
        column.addView(inputRow)

        ChatStore.onChatChanged = { changed ->
            if (changed.id == chat.id && (c as MainActivity).currentTag == "chat:${chat.id}") {
                // добавляем только новые сообщения
                while (feed.childCount < changed.messages.size) {
                    feed.addView(bubble(changed.messages[feed.childCount]))
                }
                scrollToBottom()
            }
        }

        return column
    }

    /** Бабл: мои — синие справа, чужие — тёмно-серые слева. */
    private fun bubble(msg: Message): View {
        val mine = msg.sender == Sender.ME
        val row = LinearLayout(c).apply {
            orientation = LinearLayout.HORIZONTAL
        }
        val bubbleText = TextView(c).apply {
            text = msg.text
            setTextColor(if (mine) Holo.textOnBlue else Holo.textPrimary)
            textSize = messageSp(ChatStore.fontSize)
            background = Holo.roundedBg(if (mine) Holo.blue else Holo.bubbleThem, 2)
            setPadding(c.dip(10), c.dip(7), c.dip(10), c.dip(7))
        }
        val time = TextView(c).apply {
            text = msg.time
            setTextColor(Holo.textHint)
            textSize = 10f
        }
        val col = LinearLayout(c).apply {
            orientation = LinearLayout.VERTICAL
            layoutParams = LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT
            ).apply {
                marginEnd = c.dip(if (mine) 40 else 0)
                marginStart = c.dip(if (mine) 0 else 40)
            }
        }
        col.gravity = if (mine) Gravity.END else Gravity.START
        col.addView(bubbleText)
        col.addView(time)

        if (mine) row.gravity = Gravity.END else row.gravity = Gravity.START
        row.addView(col)
        return row
    }
}
