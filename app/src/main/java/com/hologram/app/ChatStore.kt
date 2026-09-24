package com.hologram.app

import android.os.Handler
import android.os.Looper
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.random.Random

enum class Sender { ME, THEM }

data class Message(
    val id: Long,
    val text: String,
    val sender: Sender,
    val time: String
)

data class Chat(
    val id: Long,
    val name: String,
    val isBot: Boolean = false,
    val messages: MutableList<Message> = mutableListOf()
)

/**
 * In-memory состояние: чаты + настройки. Для демо.
 */
object ChatStore {
    val chats = mutableListOf<Chat>()

    // настройки
    var notificationsEnabled = true
    var soundEnabled = true
    var vibrateEnabled = false
    var fontSize = 0.5f // 0..1

    /** Колбэк для UI: вызывается при изменении сообщений. */
    var onChatChanged: ((Chat) -> Unit)? = null

    private val mainHandler = Handler(Looper.getMainLooper())

    fun chatById(id: Long): Chat? = chats.firstOrNull { it.id == id }

    fun now(): String = SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date())

    fun sendMessage(chat: Chat, text: String) {
        chat.messages.add(Message(System.nanoTime(), text, Sender.ME, now()))
        onChatChanged?.invoke(chat)
        if (chat.isBot) scheduleReply(chat)
    }

    private fun scheduleReply(chat: Chat) {
        Thread {
            try {
                Thread.sleep(1000L + Random.nextLong(2200))
            } catch (_: InterruptedException) {
            }
            val pool = Bot.replies[chat.name] ?: Bot.generic
            val msg = Message(System.nanoTime(), pool.random(), Sender.THEM, now())
            chat.messages.add(msg)
            mainHandler.post { onChatChanged?.invoke(chat) }
        }.start()
    }

    fun seed() {
        if (chats.isNotEmpty()) return
        chats.add(
            Chat(
                1, "Серёга", true, mutableListOf(
                    Message(1, "привет", Sender.THEM, "12:01"),
                    Message(2, "го кску сегодня?", Sender.THEM, "12:01"),
                    Message(3, "го, во сколько?", Sender.ME, "12:04"),
                    Message(4, "часам к 8", Sender.THEM, "12:05")
                )
            )
        )
        chats.add(
            Chat(
                2, "Мама", true, mutableListOf(
                    Message(5, "сынок, ты поел?", Sender.THEM, "09:30"),
                    Message(6, "да, мам)", Sender.ME, "09:41"),
                    Message(7, "я тебе пирожков напекла", Sender.THEM, "09:42")
                )
            )
        )
        chats.add(
            Chat(
                3, "Катя", true, mutableListOf(
                    Message(8, "смотри какая кошечка", Sender.THEM, "вчера"),
                    Message(9, "милота)", Sender.ME, "вчера")
                )
            )
        )
        chats.add(
            Chat(
                4, "Работа", true, mutableListOf(
                    Message(10, "мит в 15:00, не опаздывай", Sender.THEM, "пн")
                )
            )
        )
        chats.add(
            Chat(
                5, "Заметки", false, mutableListOf(
                    Message(11, "купить хлеб", Sender.ME, "пт")
                )
            )
        )
    }
}

/**
 * Echo-боты: отвечают фразами в духе эпохи.
 */
object Bot {
    val generic = listOf(
        "ок",
        "понял",
        "ахах, ну ты даёшь",
        "серьёзно?",
        "ща гляну",
        "++",
        "я в игре, потом",
        "го завтра?",
        "не, я сегодня дома",
        "ты видел какой Nexus 5 вышел?",
        "у меня 4.2.2 — всё летает",
        "дай вайфай пароль)"
    )

    val replies = mapOf(
        "Мама" to listOf(
            "сынок, ты поел?",
            "не сиди долго за компьютером",
            "позвони, как сможешь",
            "я тебе пирожков напекла"
        ),
        "Серёга" to listOf(
            "го кску сегодня",
            "у меня пинг 300, езжай",
            "не, я на велике сегодня",
            "Nexus 5 видел? огонь же"
        ),
        "Катя" to listOf(
            "очень смешно)",
            "спокойной ночи)",
            "угу",
            "и что дальше?"
        ),
        "Работа" to listOf(
            "отчёт до вечера",
            "мит в 15:00, не опаздывай",
            "таску повесил, глянь",
            "сервер упал. опять."
        )
    )
}
