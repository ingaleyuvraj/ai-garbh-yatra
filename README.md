# मराठी गर्भसंस्कार (Marathi Garbhsanskar)

A Marathi-first Android wellness, educational and spiritual support app for pregnant women and couples.

> **हे app वैद्यकीय निदान (medical diagnosis) app नाही.** हे एक wellness, सकारात्मक गर्भधारणा-आधार, दैनंदिन दिनचर्या आणि prenatal engagement app आहे. कोणत्याही आरोग्यविषयक निर्णयासाठी कृपया तुमच्या डॉक्टरांचा सल्ला घ्या.

---

## What this repository contains

This is a **product + content + engineering design package** — everything needed to build the app, organized as documentation and ready-to-ingest content.

| Folder / File | Purpose |
|---|---|
| [docs/01-PRD.md](docs/01-PRD.md) | Product vision, personas, feature list, prioritization, monetization, roadmap, risk & compliance |
| [docs/02-information-architecture.md](docs/02-information-architecture.md) | IA, full sitemap, onboarding flow, home wireframe, bottom navigation |
| [docs/03-ux-copy-marathi.md](docs/03-ux-copy-marathi.md) | Screen-by-screen Marathi UX copy |
| [docs/04-content-strategy.md](docs/04-content-strategy.md) | Content strategy, daily routine engine, trimester mapping, reminder system |
| [docs/05-architecture.md](docs/05-architecture.md) | Android tech stack, backend, admin panel, API list, Firebase/Supabase, AI roadmap |
| [docs/06-data-model.md](docs/06-data-model.md) | Entity schema, DB tables, JSON content structure |
| [docs/07-design-system.md](docs/07-design-system.md) | Theme, app icon, component specs, accessibility checklist |
| [docs/08-play-store-and-legal.md](docs/08-play-store-and-legal.md) | Play Store listing (Marathi), privacy/consent/disclaimer copy |
| [docs/09-handoff-and-qa.md](docs/09-handoff-and-qa.md) | Developer handoff notes, QA checklist, launch roadmap |
| [content/30-days-marathi.md](content/30-days-marathi.md) | Human-readable 30 days of Marathi content |
| [content/schema.json](content/schema.json) | JSON schema for content ingestion |
| [content/days-01-30.json](content/days-01-30.json) | Machine-ingestible 30 days of content |

---

## Core principles (non-negotiable)

1. **Marathi-first** — entire UI in Marathi, with accessible large fonts and audio support.
2. **Doctor-respectful** — every health-adjacent screen shows "कृपया डॉक्टरांचा सल्ला घ्या".
3. **No harm** — zero gender selection, zero "मुलगा/मुलगी" claims, zero superstition, zero unverified medical claims.
4. **Calm & inclusive** — loving, non-judgmental, culturally rooted but not over-religious.
5. **Offline-first for key content** — audio + text for daily routine, mantras, affirmations available offline.

---

## Quick start for engineering

See [docs/05-architecture.md](docs/05-architecture.md) for the recommended stack:
- **App:** Kotlin + Jetpack Compose, MVVM + Clean Architecture, Room (offline), WorkManager (reminders), Media3/ExoPlayer (audio).
- **Backend:** Supabase (Postgres + Auth + Storage) or Firebase (Firestore + Auth + Storage + FCM). Recommended: Supabase for relational content + Firebase FCM for push.
- **Admin:** Lightweight Next.js / Retool / Supabase Studio for content authoring.

---

## Android app (this repository)

A runnable **Kotlin + Jetpack Compose** app implementing the documented architecture (Phase 1 of the product: onboarding, daily routine, गर्भसंवाद, आराम, trackers, more). It is **offline-first** — the 30-day Marathi content ships bundled in `app/src/main/assets/content/`.

### Tech stack

- **Kotlin 2.0.20**, **Jetpack Compose** (Material 3), Navigation Compose
- **MVVM** with manual dependency injection (`AppContainer`)
- **Room** for offline trackers (water, mood, journal, routine completion)
- **DataStore** for onboarding + user profile
- **kotlinx.serialization** to parse bundled content JSON
- `minSdk 24`, `compile/targetSdk 35`, Java 17, `applicationId com.garbhyatra.app`

### Project layout

```
app/src/main/java/com/garbhyatra/app/
├─ GarbhyatraApplication.kt      # holds AppContainer
├─ MainActivity.kt               # edge-to-edge + theme + nav host
├─ di/AppContainer.kt            # manual DI graph
├─ data/
│  ├─ prefs/                     # DataStore user prefs
│  ├─ local/                     # Room db, entities, daos, TrackerRepository
│  └─ content/                   # serializable models + ContentRepository (assets)
├─ domain/
│  ├─ model/Stage.kt
│  └─ routine/RoutineEngine.kt   # deterministic day-of-program selection
├─ ui/
│  ├─ theme/                     # Color, Type, Theme (Marathi-tuned)
│  ├─ components/                # SoftCard, SectionHeader, DisclaimerBanner
│  ├─ navigation/                # routes + bottom-nav scaffold
│  └─ AppViewModelProvider.kt
└─ feature/
   ├─ onboarding/                # 4-step intro/name/stage/consent
   ├─ today/                     # आजचा दिवस — affirmation, samvad, routine, water, tip
   ├─ samvad/                    # गर्भसंवाद library
   ├─ calm/                      # आराम — mantra, music, meditation
   ├─ me/                        # माझे — water/mood/journal trackers
   └─ more/                      # अधिक — diet, partner, safety, settings
app/src/main/assets/content/days-01-30.json   # bundled offline content (30 days)
```

### Build & run

This repository contains all Gradle config and a version catalog, but **not** the committed Gradle wrapper JAR, JDK, or Android SDK. The simplest path is Android Studio, which provides these automatically.

1. Open the project root in **Android Studio** (Koala / 2024.1.1 or newer).
2. Let it sync — Android Studio generates `gradle/wrapper/gradle-wrapper.jar`, downloads the Android SDK, and resolves dependencies.
3. Select a device/emulator running **Android 7.0 (API 24)** or higher.
4. Press **Run ▶**.

To build from the command line you need a local JDK 17 + Android SDK (`ANDROID_HOME` set), then:

```bash
./gradlew :app:assembleDebug      # if the wrapper jar is present
# or, with a system Gradle 8.9+:
gradle :app:assembleDebug
```

### Updating bundled content

The app reads `content/days-01-30.json` from assets. To refresh it after editing the source content:

```bash
mkdir -p app/src/main/assets/content
cp content/days-01-30.json app/src/main/assets/content/days-01-30.json
```

### Safety guarantees in the build

- Every health-adjacent screen renders a mandatory `DisclaimerBanner` ("कृपया डॉक्टरांचा सल्ला घ्या").
- Consent + age confirmation are required during onboarding before the app opens.
- No gender selection, superstition, or unverified medical claims anywhere in UI or content.

