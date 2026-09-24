# HoloGram

Мессенджер в стиле **Holo UI** — нативный дизайн Android 4.2.2 (Holo Dark).

Чёрный фон, синий акцент `#33B5E5`, градиентный action bar, тонкие разделители,
лёгкий Roboto в заголовках. Никакого Material Design — только аутентичный Holo.

## Скриншоты

_(скриншоты будут добавлены)_

## Возможности

- Список чатов с аватарами-плитками и поиском
- Переписка: синие баблы справа (мои), серые слева (собеседник)
- Echo-боты — у каждого контакта свой характер и фразы
- Настройки в стиле нативных Holo Preferences:
  свитчи, seekbar размера шрифта (работает), синие заголовки категорий
- Overflow-меню в action bar

## Технические детали

- **Чистые Android View** — никаких Compose, AndroidX и внешних зависимостей.
  Единственная зависимость — `kotlin-stdlib`.
- `minSdk 21`, `targetSdk 36`
- Весь UI собирается программно, без layout-XML
- Сборка работает **полностью офлайн** (см. ниже)

## Сборка

```bash
./gradlew assembleDebug
```

APK: `app/build/outputs/apk/debug/app-debug.apk` (~870 KB)

### Офлайн-сборка

Проект использует `android.aapt2FromMavenOverride` в `gradle.properties`,
указывающий на локальный `aapt2.exe` из SDK build-tools — это позволяет
собирать без доступа к Google Maven. На другой машине путь нужно поправить
или удалить свойство (тогда aapt2 скачается из сети).

```properties
android.aapt2FromMavenOverride=F:/AndroidSDK/build-tools/36.0.0/aapt2.exe
```

CI собирает проект через стандартный Gradle wrapper (там сеть есть).

## Структура

```
app/src/main/java/com/hologram/app/
├── MainActivity.kt      — Activity, навигация, action bar, overflow-меню
├── ChatsScreen.kt       — список чатов
├── ChatScreen.kt        — переписка с баблами
├── SettingsScreen.kt    — настройки (Holo Preferences)
├── ChatStore.kt         — состояние, echo-боты
└── HoloUi.kt            — палитра и UI-хелперы
```

## Идея на будущее

- Уведомления о новых сообщениях
- Сохранение истории между запусками
- Roboto Condensed для заголовков
- Holo Light тема (спорное решение, но вдруг)
