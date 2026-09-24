# Промпт для новой сессии агента (HoloGram)

Скопируй текст ниже в новую сессию. Он содержит весь контекст проекта.

---

## Контекст

Проект **HoloGram** — мессенджер в стиле Holo UI (нативный дизайн Android 4.2.2,
Holo Dark). Находится в `F:\vapecoding\HoloGram`. Это нативное Android-приложение
на Kotlin, но написанное на **чистых View без Compose, AndroidX и любых внешних
зависимостей** — единственная зависимость `kotlin-stdlib`. Так получилось не
случайно: у машины разработчика очень плохой интернет (Google Maven нестабилен),
поэтому проект специально устроен так, чтобы собираться полностью офлайн.

Ключевые решения, которые нельзя сломать:

1. **Ноль зависимостей кроме kotlin-stdlib.** Не добавлять Compose, AndroidX,
   material-библиотеки без крайней необходимости. Весь UI — программные View
   (`LinearLayout`, `TextView`, `ListView`, `ScrollView` и т.д.), без layout-XML.
2. **Офлайн-сборка через `android.aapt2FromMavenOverride`** в `gradle.properties`
   — указывает на `F:/AndroidSDK/build-tools/36.0.0/aapt2.exe`. Это позволяет
   не скачивать aapt2 из Google Maven. Свойство называется именно
   `android.aapt2FromMavenOverride` (не `android.buildSettings.*`).
3. **Стиль Holo Dark, а не Material**: чёрный фон, акцент `#33B5E5`,
   градиентный action bar (`Holo.actionBarColors`), тонкие разделители 1dp,
   скругление 2dp, шрифт `sans-serif-light` для заголовков. Палитра в
   `HoloUi.kt` (объект `Holo`).
4. **minSdk 21** (ниже нельзя — Kotlin/AGP требуют), targetSdk 36.

## Структура

```
app/src/main/java/com/hologram/app/
├── MainActivity.kt      — Activity, навигация (showChats/showChat/showSettings),
│                          объект ActionBar (градиентный бар + overflow-меню)
├── ChatsScreen.kt       — список чатов (ListView + BaseAdapter, поиск через EditText)
├── ChatScreen.kt        — переписка (ScrollView с баблами, поле ввода, отправка)
├── SettingsScreen.kt    — настройки в стиле Holo Preferences (Switch, SeekBar)
├── ChatStore.kt         — in-memory состояние чатов + echo-боты (Bot.replies)
└── HoloUi.kt            — палитра (объект Holo), хелперы (dp, divider, text, roundedBg)
```

Навигация — ручная: методы `MainActivity.show*()` пересобирают корень.
Обновление UI при ответе бота — через колбэк `ChatStore.onChatChanged`.

## Сборка и деплой (машина разработчика)

```bash
# JAVA_HOME обязательно JDK 21 (системная Java — 25, она НЕ подходит)
export JAVA_HOME="F:/jdk-21"

# Gradle лежит локально в кеше, wrapper-дистрибутив тоже распакован:
/f/.gradle/wrapper/dists/gradle-8.14.2-all/dr11dm7xlo0xvn0pss07yt6r1/gradle-8.14.2/bin/gradle assembleDebug --offline --no-daemon
# либо ./gradlew.bat assembleDebug (скачает то же самое из сети)

# на телефоне по ADB Wi-Fi:
F:/AndroidSDK/platform-tools/adb.exe install -r app/build/outputs/apk/debug/app-debug.apk
```

- SDK: `F:/AndroidSDK` (платформы 34–36, build-tools 34–36)
- JDK 21: `F:/jdk-21` (также есть `D:/AndroidSDK/jdk-21.0.4+7`)
- GRADLE_USER_HOME=`F:\.gradle` — там весь кеш, включая AGP 8.13.1 и Kotlin 2.2.20
- ADB иногда подключён по Wi-Fi (адрес показывает `adb devices`); телефон может
  спать — будить `input keyevent KEYCODE_WAKEUP`, скриншоты через
  `adb exec-out screencap -p > file.png`
- В Git Bash пути вида `/sdcard/...` надо экранировать: `//sdcard/...`

## Статус и что дальше

Готово: список чатов с аватарами и поиском, экран переписки с баблами
(синие справа/серые слева), настройки с рабочим seekbar размера шрифта,
overflow-меню, echo-боты с разными характерами. CI-сборка в
`.github/workflows/build.yml`.

Известный фикс: корневой View экрана добавляется в `FrameLayout` через
`addFullScreen()` с явными MATCH_PARENT — иначе WRAP_CONTENT схлопывает экран.

Идеи развития: уведомления о новых сообщениях, сохранение истории
(например в JSON во internal storage), Roboto Condensed в заголовках,
световая Holo Light тема, HTTPS-транспорт для реального обмена сообщениями.

При работе: не ломать офлайн-сборку (проверять `--offline`), держать
стиль Holo (никаких Material-виджетов), маленькие файлы — большие писались
неаккуратно и пришлось переписывать.
