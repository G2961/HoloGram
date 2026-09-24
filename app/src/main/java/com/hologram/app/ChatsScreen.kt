package com.hologram.app

import android.content.Context
import android.graphics.Color
import android.graphics.Typeface
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.ListView
import android.widget.TextView

class ChatsScreen(private val c: Context) {

    private lateinit var adapter: BaseAdapter

    fun build(): View {
        val column = LinearLayout(c).apply {
            orientation = LinearLayout.VERTICAL
        }
        column.addView(
            ActionBar.build(
                c,
                title = "HoloGram",
                overflow = listOf(
                    "Настройки" to { (c as MainActivity).showSettings() },
                    "О приложении" to { (c as MainActivity).showToast("HoloGram 1.0 — holo is love") }
                )
            )
        )
        column.addView(Holo.divider(c))

        // поиск
        val search = EditText(c).apply {
            hint = "Поиск"
            setTextColor(Holo.textPrimary)
            setHintTextColor(Holo.textHint)
            textSize = 14f
            background = null
            setSingleLine(true)
            setPadding(c.dip(16), c.dip(10), c.dip(16), c.dip(10))
        }
        val searchRow = LinearLayout(c).apply {
            orientation = LinearLayout.HORIZONTAL
            setBackgroundColor(Holo.bgLight)
            addView(search, LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT
            ))
        }
        column.addView(searchRow)
        column.addView(Holo.divider(c))

        adapter = object : BaseAdapter() {
            override fun getCount(): Int {
                val q = search.text.toString()
                return if (q.isEmpty()) ChatStore.chats.size
                else ChatStore.chats.count { it.name.contains(q, ignoreCase = true) }
            }

            override fun getItem(position: Int): Chat {
                val q = search.text.toString()
                return if (q.isEmpty()) ChatStore.chats[position]
                else ChatStore.chats.filter { it.name.contains(q, ignoreCase = true) }[position]
            }

            override fun getItemId(position: Int) = getItem(position).id

            override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
                val chat = getItem(position)
                val row = convertView as? LinearLayout ?: LinearLayout(c).apply {
                    orientation = LinearLayout.HORIZONTAL
                    gravity = Gravity.CENTER_VERTICAL
                    setPadding(c.dip(12), c.dip(10), c.dip(12), c.dip(10))
                }

                // сбрасываем фон ряда (после holo-подсветки)
                row.background = null

                // аватар
                val avatar = getOrCreate(row, 0) {
                    TextView(c).apply {
                        layoutParams = LinearLayout.LayoutParams(c.dip(40), c.dip(40)).apply {
                            marginEnd = c.dip(12)
                        }
                        gravity = Gravity.CENTER
                        typeface = Typeface.DEFAULT_BOLD
                    }
                }
                avatar.text = chat.name.take(1)
                avatar.setTextColor(Holo.textPrimary)
                avatar.background = Holo.roundedBg(avatarColor(chat.name), 2)

                // имя + последнее сообщение
                val col = getOrCreate(row, 1) {
                    LinearLayout(c).apply {
                        orientation = LinearLayout.VERTICAL
                        layoutParams = LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f)
                    }
                }
                col.removeAllViews()
                col.addView(Holo.text(c, chat.name, 16, Holo.textPrimary))
                col.addView(
                    Holo.text(c, chat.messages.lastOrNull()?.text ?: "", 13, Holo.textSecondary)
                )

                // время
                val time = getOrCreate(row, 2) {
                    TextView(c).apply {
                        layoutParams = LinearLayout.LayoutParams(
                            ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT
                        ).apply { marginStart = c.dip(8) }
                    }
                }
                time.text = chat.messages.lastOrNull()?.time ?: ""
                time.setTextColor(Holo.textSecondary)
                time.textSize = 11f

                row.setOnClickListener { (c as MainActivity).showChat(chat) }
                return row
            }
        }

        val list = ListView(c).apply {
            layoutParams = LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, 0, 1f
            )
            divider = android.graphics.drawable.ColorDrawable(Holo.divider)
            dividerHeight = c.dip(1)
            setBackgroundColor(Holo.background)
        }
        list.adapter = adapter

        search.addTextChangedListener(object : android.text.TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, a: Int, b: Int, d: Int) {}
            override fun onTextChanged(s: CharSequence?, a: Int, b: Int, d: Int) {}
            override fun afterTextChanged(s: android.text.Editable?) {
                adapter.notifyDataSetChanged()
            }
        })

        column.addView(list)
        return column
    }

    private fun <T : View> getOrCreate(row: LinearLayout, index: Int, create: () -> T): T {
        if (row.childCount > index) {
            @Suppress("UNCHECKED_CAST")
            return row.getChildAt(index) as T
        }
        val v = create()
        row.addView(v)
        return v
    }

    private fun avatarColor(name: String): Int {
        val hue = ((name.hashCode() % 360) + 360) % 360
        return Color.HSVToColor(floatArrayOf(hue.toFloat(), 0.35f, 0.3f))
    }
}
