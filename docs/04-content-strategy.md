# Content Strategy, Daily Routine Engine, Trimester Mapping & Reminders

## 1. Content strategy

### 1.1 Editorial principles
- **Tone:** प्रेमळ, शांत, आधार देणारा. Never उपदेशात्मक (preachy), never अति धार्मिक, never अति वैद्यकीय.
- **Reading level:** Simple Marathi, short sentences, audio-first. Avoid heavy Sanskritized jargon unless culturally expected (mantras).
- **Length:** Daily cards ≤ 60 words text; audio 2–8 minutes.
- **Inclusivity:** Culturally rooted but not exclusionary; devotional content optional/skippable; no caste/religion exclusivity.
- **Safety:** Every health-adjacent piece carries the doctor disclaimer. No diagnosis, dosage, or "cures".

### 1.2 Content types
| Type | Description | Offline |
|---|---|---|
| affirmation | 1 line positive statement + optional audio | ✓ |
| garbh_samvad | Script (text) + narrated audio to talk to baby | ✓ |
| meditation | Guided audio (breathing/meditation) | ✓ (premium packs) |
| mantra | Devotional/prayer audio + text + meaning | ✓ (premium) |
| music | Instrumental/nature soothing audio | ✓ (premium) |
| tip | Wellness/diet/lifestyle tip + disclaimer | ✓ |
| routine_task | Checklist item with optional deep link | ✓ |
| weekly_summary | Non-diagnostic baby development + mother changes | ✓ |
| partner_task | Daily action for partner | ✓ |

### 1.3 Content governance / review workflow
```mermaid
flowchart LR
  A[Content author<br/>Marathi] --> B[Editorial review<br/>tone + clarity]
  B --> C[Medical advisor sign-off<br/>non-diagnostic + safe]
  C --> D[Cultural sensitivity check]
  D --> E[Publish with review_date + version]
  E --> F[Periodic re-review every 12 months]
```
- Every published item stores: `author`, `reviewed_by`, `review_date`, `version`, `status`.
- Rejection criteria: gender claims, superstition, medical advice, unverified claims, judgmental tone.

---

## 2. Daily Routine Engine logic

### 2.1 Goal
Produce a **"आजचा दिवस"** plan for each user every day, based on stage, preferences, day index, and locale (festival/season), assembled from the content pool — deterministic, offline-capable, and non-repetitive.

### 2.2 Inputs
| Input | Source |
|---|---|
| `stage` | planning / t1 / t2 / t3 (onboarding) |
| `week` / `dayIndex` | derived from due date or week, else day-since-install |
| `prefs` | devotional on/off, music type, simple mode, language |
| `streak` / `lastCompleted` | local history |
| `locale_date` | for festival/seasonal swaps (optional) |
| `subscription` | gates premium audio |

### 2.3 Day-plan composition rule
Each day's plan = a fixed **template of slots**, each slot filled by a selection function:

| Slot | Selector |
|---|---|
| affirmation | `pick(affirmations[stage], dayIndex)` rotating, no-repeat window 30 days |
| garbh_samvad | `pick(samvad[stage], weekThemeOrDay)` |
| breathing/meditation | `pick(meditation[stage], dayIndex % poolSize)` |
| audio suggestion (mantra/music) | based on `prefs.audioType`, time of day |
| tip | `pick(tips[stage], dayIndex)` |
| routine_tasks[] | base tasks (water, light movement, rest, samvad) + stage-specific |
| partner_task | `pick(partnerTasks, dayIndex)` if partner linked |

### 2.4 Selection algorithm (pseudo)
```kotlin
fun buildTodayPlan(profile: Profile, date: LocalDate): DayPlan {
    val dayIndex = profile.dayIndexFor(date)          // stable per user/day
    val stage = profile.stage
    val seed = stableSeed(profile.userId, date)        // deterministic

    fun <T> rotate(pool: List<T>, noRepeat: Int): T {
        // avoid items shown in last `noRepeat` days using local history
        val recent = history.recentIds(noRepeat)
        val candidates = pool.filterNot { it.id in recent }.ifEmpty { pool }
        return candidates[(dayIndex) % candidates.size]
    }

    return DayPlan(
        affirmation = rotate(content.affirmations(stage), 30),
        garbhSamvad = content.samvadForWeek(stage, profile.week ?: dayIndex),
        meditation  = rotate(content.meditations(stage), 10),
        audio       = content.suggestAudio(profile.prefs, timeOfDay()),
        tip         = rotate(content.tips(stage), 20),
        routine     = baseTasks(stage) + seasonalTasks(date),
        partnerTask = if (profile.hasPartner) rotate(content.partnerTasks, 14) else null,
        disclaimer  = STANDARD_DISCLAIMER
    )
}
```

### 2.5 Determinism & offline
- Plans are deterministic given (userId, date) → same plan regenerated offline.
- Content pool is cached in Room; engine runs fully on-device.
- Server only supplies content updates; plan assembly is client-side (Phase 1). Phase 2 may move personalization server-side.

### 2.6 Non-repetition & freshness
- No-repeat windows per type (affirmation 30d, tip 20d, meditation 10d).
- "नवीन" badge for newly added content.
- Weekly theme rotation keeps garbh samvad aligned with week.

---

## 3. Trimester-wise content mapping

| Theme | Planning | Trimester 1 (1–13w) | Trimester 2 (14–27w) | Trimester 3 (28w+) |
|---|---|---|---|---|
| **Emotional focus** | तयारी, सकारात्मकता | भीती/चिंता कमी करणे, आश्वासन | जोड वाढवणे, आनंद | प्रसूतीची मानसिक तयारी, शांती |
| **Affirmations** | "मी तयार होत आहे" | "मी आणि बाळ सुरक्षित आहोत" | "माझं बाळ वाढत आहे" | "मी शांत आणि सज्ज आहे" |
| **Garbh samvad** | स्वागताचे विचार | ओळख, प्रेम | गोष्टी, गाणी, संवाद | आश्वासन, भेटीची ओढ |
| **Meditation/breathing** | सामान्य शिथिलता | सौम्य, मळमळ-सुलभ | ऊर्जा + शांती | प्रसवपूर्व शांती, झोप |
| **Music/mantra** | शांत संगीत | सौम्य मंत्र | भक्ती + बासरी | अंगाई, शांत मंत्र |
| **Tips** | पोषण-पूर्वतयारी, जीवनशैली | विश्रांती, पाणी, सौम्य आहार | हालचाल, पाठीची काळजी | झोप, सूज, हालचाल-सावधगिरी |
| **Weekly summary** | N/A (cycle/prep info, non-diagnostic) | लवकर विकासाची सर्वसाधारण माहिती | हालचाल, वाढ | स्थिती, तयारी |
| **Partner tasks** | एकत्र नियोजन | आधार, समजूत | संवाद, सोबत | व्यावहारिक तयारी, आधार |
| **Safety emphasis** | डॉक्टरांकडे पूर्वतपासणी | लवकर तपासणी | नियमित तपासणी | वारंवार तपासणी, धोक्याची चिन्हे → डॉक्टर |

**Hard rule across all:** No diagnosis, no medication, no "करा म्हणजे मुलगा/मुलगी", no fear-based claims. Each shows disclaimer.

---

## 4. Reminder system design

### 4.1 Reminder types
| Type | Default | Configurable |
|---|---|---|
| Water | every 2h, 8am–8pm | interval, window, on/off |
| Garbh samvad | 1/day (e.g., 7pm) | time |
| Meditation/breathing | 1/day (e.g., 8am) | time |
| Affirmation | morning (8am) | time |
| Mood check | evening (9pm) | time |
| Appointment | from saved appointments | per-appointment, day-before + day-of |
| Sleep | optional bedtime | time |
| Custom | user-defined | all |

### 4.2 Scheduling architecture (Android)
- **WorkManager** for periodic & exact-time reminders (survives reboot via `RECEIVE_BOOT_COMPLETED` + reschedule).
- **AlarmManager (`setExactAndAllowWhileIdle`)** for appointment exact-time reminders.
- **Notification channels** per type (water, samvad, meditation, mood, appointment, general) → user controls per channel.
- **FCM** (Firebase Cloud Messaging) only for re-engagement/announcements, not for local routine reminders (those are fully offline/local).
- Respect Doze/battery: use allow-while-idle for critical (appointments) only; others periodic.

### 4.3 Reminder logic (pseudo)
```kotlin
fun scheduleReminders(prefs: ReminderPrefs) {
  cancelAllManaged()
  if (prefs.water.enabled)
     enqueuePeriodic("water", prefs.water.interval, prefs.water.window)
  prefs.daily.forEach { r ->
     if (r.enabled) enqueueDailyAt(r.type, r.time, r.repeatDays)
  }
  appointments.forEach { a ->
     if (a.remind) {
        scheduleExact(a.id, a.dateTime.minusDays(1))   // day before
        scheduleExact(a.id, a.dateTime.minusHours(2))  // same day
     }
  }
}
```

### 4.4 Smart reminders (Phase 2)
- Adapt timing to when user typically opens app (engagement-based).
- Suppress reminders already-completed that day (e.g., water goal met → pause water nudges).
- Quiet hours (night) auto-respected.
- Frequency capping to avoid fatigue (max N notifications/day, user-set).

### 4.5 Notification UX rules
- Always Marathi, gentle, never alarming.
- Deep-link to the relevant screen.
- Easy snooze + "बंद करा" per channel.
- No health-shaming, no urgency pressure (except appointment reminders which stay factual).

---

## 5. Festival / seasonal content (optional layer)
- Optional special audio/affirmations on major Maharashtrian festivals (e.g., गणेशोत्सव, गुढीपाडवा, नवरात्र) — devotional, opt-in, skippable.
- Seasonal tips (summer hydration, monsoon care) as general wellness, with disclaimer.
- Never tie festivals to health outcomes or superstition.

---

## 6. Localization & future languages
- All content keyed with `lang` field; Marathi is source-of-truth.
- Phase 3: Hindi, then other Indian languages via same schema.
- Audio narration recorded per language; text translated + reviewed.
