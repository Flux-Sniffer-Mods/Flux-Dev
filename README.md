# Flux Keyboard

An input method for Android phones with a physical keyboard, tuned for the Unihertz Titan 2 Elite and
at home on any phone with a hardware keyboard.

> **Flux Keyboard** is an unofficial fork of [Pastiera](https://github.com/palsoftware/pastiera),
> created by Andrea Palumbo (PalSoftware) and developed by Andrea Palumbo, Patrick Zauner and the
> Pastiera contributors. Most of what makes it a keyboard is their work, credited
> [below](#built-on-pastiera). Flux Keyboard is not affiliated with or endorsed by the Pastiera
> team, so please report problems to [this repository](https://github.com/Flux-Sniffer-Mods/Flux-Keyboard/issues),
> not upstream.

Flux Keyboard is up to date with **Pastiera 0.86**, Pastiera's final planned feature release, and
merges Pastiera's later changes as they land. Pastiera keeps receiving security fixes, and its
development continues as [Plektra](https://github.com/pkb-rocks/plektra).

## What Flux Keyboard adds

1. **GIFs, emoji and symbols**: GIF search with favourites, a dedicated **emoji picker key** (Right Shift by default), emoji layer **profiles** that follow the app, and search across every Unicode symbol. Tap SYM or the emoji key to apply it to the next key without opening a screen; their LEDs (purple and pink) show it.
2. **Spell checking, autofill and one-time codes in every app**: Flux Keyboard becomes Android's spell checker, password managers show their chips in the suggestion bar, codes from your notifications are offered as a chip, and a password you just copied is offered in password fields as ⎘ •••••••• (never shown, never kept in the clipboard history).
3. **Edit layouts in the app**: change what any key types, save, restore, copy or export a layout.
4. **App shortcuts everywhere**: the same shortcuts in every app, suggested per app category, and apps' own shortcuts ("New message") as results in Pastiera's quick launcher, plus any shortcut an app offers for the home screen (a contact's direct dial, a bookmark) with **Add a shortcut**.
5. **More for the Titan 2 Elite**: Pastiera's contour LEDs reworked to draw over everything on every page, with the corner buttons following the same curve, or straight buttons reaching into the corners (the default); **per-LED colours** with a fifth LED for the emoji key; left, right and down **trackpad swipes** to pick suggestions or delete a word, with a shortcut to the phone's Scroll assistant; and **recommended settings** with an Apply button that walks you through your own choices, offered again after updates as a "from → to" list.
6. **Colours from your wallpaper or a picture**: the keys take your wallpaper's colours, or sit over a **background picture** you drag, zoom and rotate on a preview of the bar, shaded to match with adjustable opacity.
7. **A tutorial and settings layout of its own**: settings grouped by task, hiding what your phone's hardware can't use, and a tutorial that sets up the extras needing a permission, with a step-by-step guide to Android's restricted settings.
8. **Terminal mode and hidden-keyboard apps**: Termux gets a real Ctrl and the keyboard's Alt and SYM with the keyboard out of the way and no microphone button; Termux:X11 and Niagara Launcher get the keys without the keyboard by default, and search bars that open focused keep the bar hidden until you type, so it no longer pops up on the home screen ([pastiera#319](https://github.com/palsoftware/pastiera/issues/319)).
9. **Faster typing**: pick a suggestion with Ctrl+Shift+Q, W or E, add the last word to the dictionary with Ctrl+Shift+D, undo an auto-replace with Backspace, delete forwards with Shift + Backspace, no automatic spaces after punctuation in email, sign-in or web address fields or inside numbers, emoticons that keep their shape, and paste what you just copied from a chip. Links you copy lose their tracking (YouTube's too) wherever you paste them.
10. **A keyboard that learns, and forgets when asked**: words you use often join the dictionary, and emails and phone numbers you type are offered again in email and phone fields; incognito typing (private tabs, or always) learns nothing at all.
11. **Offline mode**, and **updates** from this fork's own releases that download and install from the app: full releases, or dev builds too (Developer options).

Everything else, including per-app exact typing and languages, automatic Shift by field type,
snippet placeholders and voice input that keeps listening, is in the [changelog](FORK_CHANGES.md).

## Built on Pastiera

These are the Pastiera team's work, which Flux Keyboard builds on and keeps. Thank you to Andrea
Palumbo, Patrick Zauner and every Pastiera contributor.

**Typing and modifiers**
- Long press for Alt or Shift characters, with configurable timing.
- Shift, Ctrl and Alt as one-shot or locked (double tap), configurable latching, and clearing Alt on space.
- Multi-tap for keys with several characters (for example Cyrillic), and bounce keys.
- Standard shortcuts: Ctrl+C/X/V/A, Ctrl+Backspace, arrows on Ctrl+E/S/D/F or I/J/K/L, selection, Tab, Page Up/Down and Esc, all customisable.
- **Nav Mode**: double tap Ctrl outside text fields for arrows and many more mappings, with word navigation and media controls.
- Double space for a full stop and a capital, configurable punctuation spacing (French spacing, brackets, commas) and smart quotes.

**Layouts and languages**
- QWERTY, AZERTY, QWERTZ, Greek, Arabic, Russian and Armenian phonetic transliteration and more, with Alt maps for the Titan 2, Titan 2 Elite and original Titan.
- Layout switching with a tap on the language code, Ctrl+Space or Alt+Enter; JSON import and export with a preview.
- The layout web editor at [pastierakeyedit.vercel.app](https://pastierakeyedit.vercel.app/).
- A translated interface (English, Italian, German, Greek, Spanish, French, Armenian, Polish, Russian, Ukrainian, Vietnamese) and the onboarding tutorial.

**Status bar, symbols and variations**
- The compact status bar with modifier LEDs, the variations and suggestions bar, and Solderina, the smallest bar (Pastierina in Pastiera).
- The Titan 2 Elite's rounded-display geometry, its display contour calibration and Pastiera's contour LEDs, which Flux Keyboard's contoured LEDs grew from.
- SYM pages for emoji, symbols and the clipboard, usable by touch or keys, with an in-app SYM editor, emoji grid and Unicode picker.
- The variations bar: accents of the last letter or static sets, its editor, and dragging it as a swipe pad to move the cursor.
- Clipboard history with pinned items, hidden while the phone is locked.

**Suggestions and corrections**
- Dictionary suggestions and auto-correction, suggestions from several dictionaries, learned next words, and adding words from a swipe or a substitution.
- The user dictionary with search and editing, per-language substitutions and the shared "Pastiera Recipes".
- Snippet expansion, and emoji and symbol shortcodes.
- Native trackpad gestures on the Titan 2 and Titan 2 Elite (directly or through Shizuku), with separate sensitivities.

**Apps and extras**
- Launcher shortcuts (press a letter to open an app), power shortcuts with SYM anywhere, and the QuickLauncher with Niagara search.
- Enter behaviour per app.
- Speech input from Alt+Ctrl or the microphone on the bar.
- The on-screen keyboard mode with themes, a theme editor, per-app themes, presets, a number row and long-press layers.
- Clicks Power Keyboard support: controls, firmware status and SYM profiles.
- Backup and restore in a ZIP (settings, layouts, variations, SYM and Ctrl maps, dictionaries, themes and typing sounds), Android auto-backup, and the searchable settings with shareable links.

The full list of what Pastiera 0.86 added over 0.85 is in the [changelog](FORK_CHANGES.md#from-the-pastiera-team-085-to-086).

## Installation

1. Download the APK from the [latest release](https://github.com/Flux-Sniffer-Mods/Flux-Keyboard/releases/latest), or a dev build (marked Pre-release) from [all releases](https://github.com/Flux-Sniffer-Mods/Flux-Keyboard/releases).
2. Android Settings → System → Languages & input → Virtual keyboard → Manage keyboards.
3. Enable "Flux Keyboard" and pick it when typing.

Flux Keyboard (app ID `io.github.fluxsniffermods.fluxkeyboard`) installs next to Pastiera and
doesn't replace or update it.

**Coming from Pastiera Flux?** Flux Keyboard was called Pastiera Flux until September 2026, with the
app ID `it.palsoftware.pastiera.flux`. Tap "Backup now" in Pastiera Flux, restore that ZIP in Flux
Keyboard, then uninstall Pastiera Flux.

## Requirements

- Android 10 (API 29) or newer.
- A phone with a physical keyboard (profiled on the Unihertz Titan 2 and Titan 2 Elite, adaptable with JSON layouts).

## Building

- Build a debug APK with `./gradlew :app:assembleStableDebug` and run the tests with `./gradlew :app:testStableDebugUnitTest`.
- Signed builds come from `.github/workflows/fork-build.yml`, picked by the branch it runs on:
  - **`flux-release` (the default branch): full releases** such as `0.94`, tagged `flux/v0.94`. The version is the newest one in the `"releases"` list of `app/src/main/assets/fork/whats_new.json`, which also records when it was built.
  - **`flux-dev`: dev builds** such as `0.94.2-flux.202610012100`, named after the next patch release (so the next full release, patch or minor, supersedes and installs over them) plus the build time, published as pre-releases.
- Each release lists only what changed since the build before it (a full release since the previous full release, a dev build since the previous build of either kind), from the What's new entries' `"after"` times.
- The repository's "Latest" release is always the latest full release. The app's update check reads the release tags: Stable offers full releases, Dev offers both.
- Dev work goes on `flux-dev` as individual commits, one per change, and Pastiera's changes are merged into it as they land. For a full release, add it to `"releases"`, fold the commits since the last full release into category commits, move `flux-release` up to the result and run the workflow on `flux-release`. Commits at or below `flux-release` are never rewritten.
- A full release deletes the dev builds before it (their releases and tags) when it publishes; builds run one at a time.
- `tools/find-keyboard-gesture-page.sh` (as root from Termux, with the page open) prints which screen a phone's keyboard gesture settings are.

Pastiera's release, nightly and CI workflows aren't kept: Flux Keyboard builds only with
`fork-build.yml`. When Pastiera changes them, a merge keeps them deleted.

## Contributing

Issues and suggestions for Flux Keyboard go to [this repository](https://github.com/Flux-Sniffer-Mods/Flux-Keyboard/issues).
Pastiera itself now accepts security, compatibility and maintenance changes, with feature work in
Plektra.

Flux Keyboard follows [Pastiera's forking policy](https://github.com/palsoftware/pastiera#forking-policy):
its own name, application ID, update feed and branding, the copyright and licence notices kept, and
no claim to be an official Pastiera release.

## Licence and credits

Pastiera is licensed under the [GNU General Public License v3](LICENSE), and Flux Keyboard is
distributed under the same licence. Pastiera was created by Andrea Palumbo (PalSoftware) and is
developed by Andrea Palumbo, Patrick Zauner and the contributors credited in the app's About screen
and in the [upstream repository](https://github.com/palsoftware/pastiera). Third-party components
are listed in [THIRD_PARTY_NOTICES.md](THIRD_PARTY_NOTICES.md), and the fork's changes in
[FORK_CHANGES.md](FORK_CHANGES.md).

If you enjoy Flux Keyboard, consider supporting the people it's built on:
[Pastiera on Open Collective](https://pastiera.eu/donate) and
[Andrea Palumbo on Ko-fi](https://ko-fi.com/palsoftware).
