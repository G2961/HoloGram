package com.hologram.app

import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.SeekBar
import android.widget.Switch
import android.widget.TextView

class SettingsScreen(private val c: Context) {

    fun build(): View {
        val column = LinearLayout(c).apply {
            orientation = LinearLayout.VERTICAL
        }
        column.addView(
            ActionBar.build(
                c,
                title = "Настройки",
                showBack = true,
                onBack = { (c as MainActivity).showChats() }
            )
        )
        column.addView(Holo.divider(c))

        val scroll = android.widget.ScrollView(c).apply {
            layoutParams = LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, 0, 1f
            )
        }
        val list = LinearLayout(c).apply { orientation = LinearLayout.VERTICAL }
        scroll.addView(list)

        // ---- Уведомления ----
        list.addView(category("Уведомления"))
        list.addView(
            switchPref(
                "Уведомления",
                "Показывать уведомления о новых сообщениях",
                ChatStore.notificationsEnabled
            ) { ChatStore.notificationsEnabled = it }
        )
        list.addView(Holo.divider(c))
        list.addView(
            switchPref(
                "Звук",
                "Звук при новом сообщении",
                ChatStore.soundEnabled
            ) { ChatStore.soundEnabled = it }
        )
        list.addView(Holo.divider(c))
        list.addView(
            switchPref(
                "Вибрация",
                "Вибрировать при новом сообщении",
                ChatStore.vibrateEnabled
            ) { ChatStore.vibrateEnabled = it }
        )
        list.addView(Holo.divider(c))

        // ---- Внешний вид ----
        list.addView(category("Внешний вид"))
        list.addView(
            switchPref(
                "Тёмная тема",
                "Holo Dark — единственная правильная",
                true
            ) { }
        )
        list.addView(Holo.divider(c))
        list.addView(
            seekPref(
                "Размер шрифта",
                ChatStore.fontSize
            ) { ChatStore.fontSize = it }
        )
        list.addView(Holo.divider(c))

        // ---- О приложении ----
        list.addView(category("О приложении"))
        list.addView(plainPref("Версия", "HoloGram 1.0 (сборка 1)"))
        list.addView(Holo.divider(c))
        list.addView(plainPref("Сборка", "JRO03R // Holo forever"))
        list.addView(Holo.divider(c))

        return column
    }

    private fun category(title: String): TextView =
        Holo.text(c, title, 13, Holo.blue).apply {
            layoutParams = LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT
            )
            setBackgroundColor(Holo.bgLight)
            setPadding(c.dip(12), c.dip(6), c.dip(12), c.dip(6))
        }

    private fun switchPref(
        title: String,
        summary: String,
        checked: Boolean,
        onChange: (Boolean) -> Unit
    ): View {
        val row = LinearLayout(c).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = android.view.Gravity.CENTER_VERTICAL
            setPadding(c.dip(12), c.dip(8), c.dip(12), c.dip(8))
        }
        val col = LinearLayout(c).apply {
            orientation = LinearLayout.VERTICAL
            layoutParams = LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f)
        }
        col.addView(Holo.text(c, title, 15, Holo.textPrimary))
        col.addView(Holo.text(c, summary, 12, Holo.textSecondary))
        row.addView(col)

        val sw = Switch(c).apply {
            isChecked = checked
            setOnCheckedChangeListener { _, checkedNow -> onChange(checkedNow) }
        }
        row.addView(sw)
        return row
    }

    private fun seekPref(title: String, value: Float, onChange: (Float) -> Unit): View {
        val col = LinearLayout(c).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(c.dip(12), c.dip(8), c.dip(12), c.dip(8))
        }
        col.addView(Holo.text(c, title, 15, Holo.textPrimary))

        val row = LinearLayout(c).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = android.view.Gravity.CENTER_VERTICAL
        }
        row.addView(Holo.text(c, "A", 12, Holo.textSecondary))

        val seek = SeekBar(c).apply {
            max = 100
            progress = (value * 100).toInt()
            setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
                override fun onProgressChanged(sb: SeekBar?, p: Int, fromUser: Boolean) {
                    onChange(p / 100f)
                }

                override fun onStartTrackingTouch(sb: SeekBar?) {}
                override fun onStopTrackingTouch(sb: SeekBar?) {}
            })
        }
        row.addView(seek, LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f))
        row.addView(Holo.text(c, "A", 20, Holo.textPrimary))
        col.addView(row)
        return col
    }

    private fun plainPref(title: String, summary: String): View {
        val col = LinearLayout(c).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(c.dip(12), c.dip(8), c.dip(12), c.dip(8))
        }
        col.addView(Holo.text(c, title, 15, Holo.textPrimary))
        col.addView(Holo.text(c, summary, 12, Holo.textSecondary))
        return col
    }
}
