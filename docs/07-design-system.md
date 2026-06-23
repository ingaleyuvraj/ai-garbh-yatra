# Design System, Theme, App Icon, Components & Accessibility

## 1. Brand & visual direction

- **Mood:** शांत, सकारात्मक, विश्वासार्ह, सांस्कृतिकदृष्ट्या आपुलकीचा.
- **Style:** Soft pastel, rounded, airy, gentle illustrations. Modern + Indian cultural touch.
- **Motifs:** आई व बाळ, चंद्र, कमळ (lotus), दिवा (diya), पानं (leaves), सूर लहरी (sound waves).

---

## 2. Color palette

| Token | Hex | Use |
|---|---|---|
| `saffron` (primary) | `#F4A261` | Primary actions, accents, affirmation cards |
| `saffronDeep` | `#E8843C` | Pressed/active primary |
| `blush` (secondary) | `#F6C9CE` | Garbh samvad cards, highlights |
| `blushDeep` | `#E79AA2` | Secondary emphasis |
| `cream` (background) | `#FFF7EE` | App background |
| `surface` | `#FFFFFF` | Cards |
| `leaf` (accent) | `#88B04B` | Success, water, nature, progress |
| `leafSoft` | `#D8E8C2` | Subtle accent fills |
| `nightLotus` | `#6D597A` | Meditation/night sections |
| `textPrimary` | `#3A332E` | Body text (warm near-black) |
| `textSecondary` | `#7A726B` | Secondary text |
| `disclaimer` | `#B4533B` | Disclaimer/safety emphasis (not alarming red) |
| `error` | `#C0584A` | Errors (soft) |

**Contrast:** All text/background pairs target **WCAG AA (≥4.5:1 body, ≥3:1 large)**. Provide a **high-contrast theme** variant that deepens text and borders.

### 2.1 Dark / सौम्य (evening) theme
- Background `#241F2B` (deep lotus), surface `#2E2838`, text `#F3EDE6`, primary saffron retained, accents softened. Used for meditation/bedtime and user-selectable.

---

## 3. Typography (Marathi-first)

| Role | Font | Size (sp) | Weight |
|---|---|---|---|
| Display / screen title | **Mukta** / Noto Sans Devanagari | 24–28 | SemiBold |
| Section header | Mukta | 20 | Medium |
| Body | **Noto Sans Devanagari** | 16 (scales to 20/24) | Regular |
| Affirmation (feature) | Mukta / Baloo 2 Devanagari | 22 | Medium |
| Caption / meta | Noto Sans Devanagari | 13–14 | Regular |
| Button | Mukta | 16 | Medium |

- Use fonts with **full Devanagari + Marathi conjunct support** (test ज्ञ, श्र, क्ष, द्य, र्‍य, half-letters, matras).
- **Line height** generous (1.4–1.6) for readability.
- Respect system font scale; cap at a tested max to avoid layout breakage. Provide in-app font size (सामान्य/मोठा/खूप मोठा).

---

## 4. App icon & theme guidelines

### 4.1 App icon
- **Concept:** A lotus cradling a crescent moon with a small glowing center (life/seed); warm saffron-to-blush gradient on cream; subtle diya glow. Calm, feminine, auspicious, not overtly religious.
- **Alt concept:** Mother silhouette with hands forming a heart over belly + sound wave + leaf.
- **Specs:** Adaptive icon (foreground + background layers), 108dp canvas / 72dp safe zone; provide mdpi→xxxhdpi + Play 512×512; monochrome layer for themed icons (Android 13+).
- **Don'ts:** No fetus imagery, no medical symbols, no deity faces, no gender cues.

### 4.2 Illustration guidelines
- Soft vector, rounded forms, pastel fills, minimal line work.
- Reusable set: mother+baby, lotus, moon, diya, leaves, sound waves, water drop, calm landscape.
- Inclusive skin tones; avoid stereotyping.
- Use for empty states, onboarding, section headers, audio art.

### 4.3 Elevation, shape, spacing
- **Corner radius:** cards 20dp, buttons 16dp, chips full.
- **Elevation:** subtle (1–3dp), soft shadows only.
- **Spacing scale:** 4/8/12/16/24/32dp. Card padding 16–20dp.
- **Tap targets:** min **48×48dp**; primary CTAs full-width, 56dp tall.

---

## 5. Prompt-ready Android UI component specs (Jetpack Compose)

> Each spec is implementation-ready for Compose + Material 3. Use `MaterialTheme` tokens mapped to the palette above.

### 5.1 `AffirmationCard`
- **Props:** `text: String`, `audioUrl: String?`, `onPlay`, `onShare`.
- **Visual:** saffron gradient surface, 20dp radius, ✨ icon, large centered Marathi text (22sp), row with ▶ ऐका + ↗ शेअर करा.
- **A11y:** card has contentDescription "आजची प्रेरणा: {text}"; buttons labeled.

### 5.2 `RoutineChecklist`
- **Props:** `tasks: List<RoutineTask>`, `onToggle(taskKey)`.
- **Visual:** header "✅ आजची दिनचर्या" + progress "{done}/{total}"; rows with large checkbox + label; completed rows show soft leaf check, struck text optional.
- **A11y:** each row toggleable, state announced (निवडले/निवड रद्द).

### 5.3 `AudioPlayerBar` (mini) + `AudioPlayerScreen` (full)
- **Mini:** persistent above bottom nav; title, ⏸/▶, ✕; tap expands.
- **Full:** large art, title, scrubber, ▶/⏸, ⟲ repeat, ⏱ झोपेचा टायमर, ⬇ ऑफलाइनसाठी जतन करा; background playback note.
- **Tech:** Media3 `MediaSessionService`; works offline if downloaded; lock-screen controls.

### 5.4 `TrackerQuickChip`
- **Props:** `icon`, `label`, `value?`, `onClick`.
- **Visual:** pill, leaf accent for water, 48dp tall.

### 5.5 `MoodSelector`
- **Props:** `selected: Mood?`, `onSelect`.
- **Visual:** horizontal row of large emoji + Marathi label (आनंदी, शांत, थकलेली, चिंताग्रस्त, दु:खी, उत्साही); selected ring.
- **A11y:** radio-group semantics.

### 5.6 `WaterTracker`
- Glass counter with + button, animated fill, goal text; gentle encouragement; disclaimer line.

### 5.7 `DisclaimerBanner`
- **Props:** `text` (default standard), `prominent: Boolean`.
- **Visual:** soft `disclaimer` color, ⓘ icon, rounded; non-alarming. Pinned on health screens.

### 5.8 `SectionCard` / `ContentTile`
- Generic card for samvad/mantra/music/meditation: thumbnail, title, meta (मिनिटे), premium "प्लस" badge, ▶ / ⬇.

### 5.9 `PrimaryButton` / `SecondaryButton`
- 56dp tall, 16dp radius, saffron primary / outlined secondary; loading + disabled states; Marathi labels.

### 5.10 `StageSelector`
- 4 large selectable cards (नियोजन / पहिली / दुसरी / तिसरी तिमाही) with helper subtext and illustration.

### 5.11 `BreathingAnimation`
- Expanding/contracting lotus circle synced to "श्वास घ्या / थांबा / श्वास सोडा" captions + optional audio + haptics.

### 5.12 `BottomNavBar`
- 5 items, always-labeled, 48dp targets, selected tint saffron. Optional raised center (आराम).

### 5.13 `SimpleModeWrapper`
- When simple mode on: larger fonts, fewer cards/screen, bigger buttons, more whitespace.

---

## 6. Motion & feedback
- Gentle transitions (200–300ms ease), subtle fades; no jarring motion.
- Haptic on key completions (routine done, water added) — light.
- Respect "reduce motion" system setting.

---

## 7. Accessibility checklist

- [ ] All text in Marathi; no untranslated English in UI.
- [ ] Min font scale support up to 200%; in-app सामान्य/मोठा/खूप मोठा.
- [ ] All interactive elements ≥ 48×48dp.
- [ ] Color contrast WCAG AA (AAA where feasible for body).
- [ ] High-contrast theme available.
- [ ] All images/icons have Marathi `contentDescription`.
- [ ] TalkBack: logical focus order, labeled controls, state changes announced.
- [ ] Audio-first: key content has audio; player has large controls + background play.
- [ ] No information by color alone (use icon + text).
- [ ] Forms: clear labels, error text in Marathi, no time-pressure.
- [ ] Simple mode reduces cognitive load (big targets, less text).
- [ ] Touch alternatives to gestures; no essential long-press-only actions.
- [ ] Captions/transcripts for guided audio where feasible.
- [ ] Tested with TalkBack + large font + low-end device.
- [ ] Disclaimer readable and not skippable on health screens.
- [ ] Works one-handed; reachable primary actions near bottom.
