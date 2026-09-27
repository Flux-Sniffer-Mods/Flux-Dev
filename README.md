# Flux Keyboard

> **Flux Keyboard** is an unofficial fork of [Pastiera](https://github.com/palsoftware/pastiera),
> created by Andrea Palumbo (PalSoftware) and developed by Andrea Palumbo, Patrick Zauner and the
> Pastiera contributors. Nearly everything described below is their work; this fork adds the
> changes listed in [FORK_CHANGES.md](FORK_CHANGES.md). It is not affiliated with or endorsed by the
> Pastiera team, so please report problems with Flux Keyboard to
> [this fork](https://github.com/Flux-Sniffer-Mods/Flux-Keyboard), not upstream.

Flux Keyboard is up to date with **Pastiera 0.86**, Pastiera's final planned feature release.
Pastiera keeps receiving security fixes, and its development continues as
[Plektra](https://github.com/pkb-rocks/plektra). Flux Keyboard merges upstream changes as they land,
so it never falls behind Pastiera.

### Support the original Pastiera project

Support Pastiera on [OpenCollective](https://pastiera.eu/donate)

<details>
<summary>Alternative direct support options</summary>

> **Notice:** The following payments are made directly to individual maintainers and are not administered through OpenCollective. Depending on the terms of Pastiera's future fiscal host, these options may be discontinued and all project contributions may subsequently be processed exclusively through OpenCollective.

### Current maintainer

| | |
|---|---|
| Account holder | Patrick Alexander Zauner |
| IBAN | DE25660702130058075300 |
| BIC | DEUTDESMP12 |

For everyone who sees an IBAN and quietly gives up:  
[Support via PayPal](https://www.paypal.me/zaunerpa)

### Original developer

[![Support the original developer on Ko-fi](https://ko-fi.com/img/githubbutton_sm.svg)](https://ko-fi.com/C0C31OHWF2)

</details>

Input method for physical keyboards android devices (e.g. Unihertz Titan 2), designed to make typing faster through shortcuts, gestures, and customization.

## Flux Keyboard (this fork)

**Flux Keyboard** builds on Pastiera for the Unihertz Titan 2 Elite and works on any phone with a hardware keyboard. It installs alongside Pastiera (app ID `io.github.fluxsniffermods.fluxkeyboard`), so you can keep both. The biggest differences:

1. **GIFs, emoji and symbols**: GIF search with favourites, a dedicated **emoji picker key** (Right Shift by default), emoji layer **profiles** that follow the app, and search across every Unicode symbol. Tap SYM or the emoji key to apply it to the next key without opening a screen; their LEDs (purple and pink) show it.
2. **Spell checking, autofill and one-time codes in every app**: Flux Keyboard becomes Android's spell checker, password managers show their chips in the suggestion bar, codes from your notifications are offered as a chip, and a password you just copied is offered in password fields as ⎘ •••••••• (never shown, never kept in the clipboard history).
3. **Edit layouts in the app**: change what any key types, save, restore, copy or export a layout, with no web editor.
4. **App shortcuts everywhere**: the same shortcuts in every app, suggested per app category, and apps' own shortcuts in the quick launcher, which can also hand over to Niagara's search.
5. **Made for the Titan 2 Elite**: a status bar fitted to its rounded display, **per-LED colours** with a fifth LED for the emoji key, trackpad swipes for suggestions and deleting words (tuned to its touch layer, with a shortcut straight to the phone's Keyboard gesture page), a **customisable menu bar**, and **recommended settings** with an Apply button that then walks you through your own choices.
6. **Settings and tutorial rebuilt**: grouped by task, searchable, hiding what your phone's hardware can't use, and a tutorial that sets up the extras needing a permission, including Android's restricted settings.
7. **Terminal mode and hidden-keyboard apps**: Termux gets a real Ctrl and the keyboard's Alt and SYM with the keyboard out of the way; Termux:X11 and launchers get the keys without the keyboard.
8. **Faster typing**: pick a suggestion with Ctrl+Shift+Q, W or E or a trackpad swipe, undo an auto-replace with Backspace, delete forwards with Shift + Backspace, and paste what you just copied from a chip, with tracking stripped from links.
9. **Offline mode**, and **updates** from this fork's own releases that download and install from the app: full releases, or dev builds too (Developer options).

Everything else, including per-app exact typing and languages, automatic Shift by field type, snippets and voice input that keeps listening, is in the [changelog](FORK_CHANGES.md). It lists what Flux Keyboard adds over Pastiera 0.86, Pastiera's final feature release, and what the Pastiera team built for 0.86, which Flux Keyboard includes.

*The rest of this README is Pastiera's own documentation, updated where the fork differs.*

## Quick overview
- Compact status bar with LED indicators for Shift/SYM/Ctrl/Alt, variants/suggestions bar, and swipe-pad gestures to move the cursor.
- Multiple layouts (QWERTY/AZERTY/QWERTZ, Greek, Cyrillic, Arabic, translit, etc.) fully configurable; JSON import/export directly from the app, and an editor in the app for what each key types.
- SYM pages usable via touch or physical keys (emoji, symbols, clipboard and the full emoji picker), reorderable/disableable, with an integrated layout editor.
- Clipboard support with multiple entries and pinnable items.
- Support for dictionary based suggestions/Autocorrections + trackpad swipes to accept a suggestion (read directly, or through Shizuku)
- Full backup/restore (settings, layouts, variations, dictionaries), UI translated into multiple languages, and update notices (see [Backup, updates, and data](#backup-updates-and-data)).

## Typing and modifiers
- Long press on a key can input Alt+key or Shift+Key (uppercase) timing configurable.
- Shift/Ctrl/Alt in one-shot or lock mode (double tap), option to clear Alt on space.
- Current behaviour note: `Ctrl` used as a physically held shortcut modifier (e.g. hold `Ctrl` + `A`) intentionally follows the app shortcut path and is not the same flow as Nav Mode (`Ctrl` double-tap latch outside text fields). Nav Mode remains a separate implementation/state.
- Multi-tap support for keys with layout-defined variants (e.g. Cyrillic)
- Standard shortcuts: Ctrl+C/X/V, Ctrl+A, Ctrl+Backspace, Ctrl+E/D/S/F or I/J/K/L for arrows, Ctrl+W/R for selection, Ctrl+T for Tab, Ctrl+Y/H for Page Up/Down, Ctrl+Q for Esc (all customizable in the Customize Nav screen).

## QOL features
- **Nav Mode**: double tap Ctrl outside text fields to use ESDF or IJKL as arrows, and many more useful mappings (everything is customizable in Customize Nav Mode settings)
- **Variations bar as swipe pad**: drag to move the cursor, with adjustable threshold.
- **Launcher shortcuts**: in the launcher, press a letter to open/assign an app.
- **Power shortcuts**: press SYM (5s timeout) then a letter to use the same shortcuts anywhere, even outside the launcher.
- Change language with a tap on language code in the status bar, longpress to enter pastiera settings

## Keyboard layouts
- Included layouts: qwerty, azerty, qwertz, greek, arabic, russian/armenian phonetic translit, plus dedicated Alt maps for Titan 2.
- Layout switching: select from the enabled layouts list (configurable).
- Multi-tap support and mapping for complex characters.
- JSON import/export directly from the app, with visual preview and list management (enable/disable, delete).
- Layout maps are stored in `files/keyboard_layouts` and can also be edited manually, in the app (Keyboard layout > a layout's pencil), or with the web editor at https://pastierakeyedit.vercel.app/
- Device/firmware behaviour snapshots for physical keyboards are archived under [docs/device-archives](docs/device-archives/).

## Symbols, emoji, and variations
- Touch-based SYM pages (emoji, symbols, clipboard and the full emoji picker with search): reorderable/enableable, auto-close after input, customizable keycaps. Flux Keyboard adds a Device SYM layer editor and GIF and symbol search.
- In-app SYM editor with emoji grid and Unicode picker.
- Variations bar above the keyboard: shows accents/variants of the last typed letter or static sets (utility/email) when needed.
- Dedicated variations editor to replace/add variants via JSON or Unicode picker; optional static bar.

## Suggestions and autocorrection

- Experimental support for dictionary based autocorrection/suggestions
- User dictionary with search and edit abilities.
- Per-language auto substituion editor, quick search, and a global “Pastiera Recipes” set shared across all languages.
- Change language/keymap with a tap on the language code button or ctrl+space



## Comfort and extra input
- Double space → period + space + uppercase; 
- Swipe left or down on the keyboard to delete a word (Titan 2; one choice in Trackpad & gestures).
- Optional Alt+Ctrl shortcut to start speech input; microphone always available on the variants bar.
- Compact status bar to minimize vertical space. With on-screen keyboard disabled from the IME selector, it uses even less space (Solderina mode, called Pastierina in Pastiera)
- Translated UI (en/it/de/el/es/fr/hy/pl/ru/uk/vi) and onboarding tutorial. Some settings added by Flux Keyboard are English only.

## Backup, updates, and data
- UI-based backup/restore in ZIP format: includes preferences, custom layouts, variations, SYM/Ctrl maps, and user dictionaries.
- Restore merges saved variations with defaults to avoid losing newly added keys.
- Update notices when opening settings and once a day (with option to ignore a release). Flux Keyboard checks [this fork's releases](https://github.com/Flux-Sniffer-Mods/Flux-Keyboard/releases) and offers the newest one's APK; offline mode turns the checks off.
- Customizable files in `files/`: `variations.json`, `ctrl_key_mappings.json`, `sym_key_mappings*.json`, `keyboard_layouts/*.json`, user dictionaries.
- Android autobackup function 

## Installation
1. Download the APK from the [latest Flux Keyboard release](https://github.com/Flux-Sniffer-Mods/Flux-Keyboard/releases/latest), or a dev build (marked Pre-release) from [all releases](https://github.com/Flux-Sniffer-Mods/Flux-Keyboard/releases). Or build it yourself.
2. Android Settings → System → Languages & input → Virtual keyboard → Manage keyboards.
3. Enable “Flux Keyboard” and select it from the input selector when typing.

Flux Keyboard installs next to official Pastiera; it doesn't replace or update it.

**Coming from Pastiera Flux?** Flux Keyboard was called Pastiera Flux until September 2026 and
had the app ID `it.palsoftware.pastiera.flux`. It's now a separate app, so install Flux Keyboard,
move your settings over ("Backup now" in Pastiera Flux, then restore that ZIP in Flux
Keyboard), then uninstall Pastiera Flux.

## Requirements
- Android 10 (API 29) or higher.
- Device with a physical keyboard (profiled on Unihertz Titan 2 and Titan 2 Elite, adaptable via JSON).

## Contributing

Pastiera now accepts security, compatibility, and maintenance changes. Active feature development continues in [Plektra](https://github.com/pkb-rocks/plektra).
Flux Keyboard issues and suggestions go to [this fork](https://github.com/Flux-Sniffer-Mods/Flux-Keyboard/issues), never to Pastiera.

### Pastiera's forking policy

Pastiera is free software under the GPLv3. You can fork, modify, and redistribute the code under the terms of that licence.

A distributed fork must use its own distinct identity. Its project, repository, application, and release names must not contain “Pastiera” as a standalone word, prefix, suffix, or other name component.

Forks must retain the required copyright and licence notices. They must not present themselves as an official Pastiera release. Before distribution, a fork must use its own application ID, update endpoints, and branding.

## Development / Tests
- Run core + routing + service modifier regression tests:
  - `./gradlew :app:testStableDebugUnitTest --tests it.palsoftware.pastiera.core.ModifierStateControllerTest --tests it.palsoftware.pastiera.inputmethod.InputEventRouterModifierE2ETest --tests it.palsoftware.pastiera.inputmethod.PhysicalKeyboardInputMethodServiceDeviceBehaviorTest`
- Run release/update flavor coverage tests:
  - `./gradlew :app:testStableDebugUnitTest --tests it.palsoftware.pastiera.FlavorBuildConfigTest --tests it.palsoftware.pastiera.update.UpdateCheckerFlavorLogicTest`
  - `./gradlew :app:testNightlyDebugUnitTest --tests it.palsoftware.pastiera.FlavorBuildConfigTest --tests it.palsoftware.pastiera.update.UpdateCheckerFlavorLogicTest`
- Run the stable F-Droid-path tests:
  - `./gradlew :app:testStableDebugUnitTest -PPASTIERA_FDROID_BUILD=true`
- Service-level (device-near) modifier behaviour regressions:
  - `./gradlew :app:testStableDebugUnitTest --tests it.palsoftware.pastiera.inputmethod.PhysicalKeyboardInputMethodServiceDeviceBehaviorTest`
- Router-level input pipeline modifier/SYM tests:
  - `./gradlew :app:testStableDebugUnitTest --tests it.palsoftware.pastiera.inputmethod.InputEventRouterModifierE2ETest`
- Core modifier state machine tests:
  - `./gradlew :app:testStableDebugUnitTest --tests it.palsoftware.pastiera.core.ModifierStateControllerTest`
- Build nightly debug APK with dynamic nightly version code:
  - `./scripts/build-nightly-debug.sh 0.86`
  - `./scripts/build-nightly-debug.sh 0.86 --install`
  - `./scripts/build-nightly-debug.sh 0.86 --install --device <adb-serial>`

## Flux Keyboard builds
- There are two kinds of build, picked by the branch `.github/workflows/fork-build.yml` is run on:
  - **`flux-release` (the default branch): full releases** such as `0.92`, tagged `flux/v0.92`. The version is the newest one in the `"releases"` list of `app/src/main/assets/fork/whats_new.json`, which also records when it was built.
  - **`flux-dev`: dev builds** such as `0.93-flux.202609262100`, the next version after the latest release plus the build time, published as pre-releases with "dev" in the title.
- The version and version code are worked out from the branch; a version given by hand has to be of the branch's kind.
- Each release lists only what changed since the build before it: a full release since the previous full release, a dev build since the previous build of either kind. The list comes from the What's new entries (`"after"` is the build each entry is new since).
- The repository's "Latest" release is always the latest full release; dev builds are marked Pre-release.
- The app's update check reads these releases: Stable offers full releases only, Dev offers both; both read the release tags, so no release is missed however many there are.
- Dev work goes on `flux-dev` as individual commits, one per change.
- To make a full release: add it to `"releases"` on `flux-dev`, fold the commits since the last full release into category commits, move `flux-release` up to the result, and run the workflow on `flux-release`. Commits at or below `flux-release` are never rewritten.
- Builds never delete earlier ones: every run, artifact and release (full or dev) stays. They run one at a time, so each works out its version and "the build before" after the previous one has published.
- Dev builds turn Developer options on by default, where the app's Dev builds update switch lives; full releases leave them off.
- `tools/find-keyboard-gesture-page.sh` (run as root from Termux, with the page open) prints which screen a phone's keyboard gesture settings are and whether other apps may open it; that's how the Titan 2 Elite's `com.agui.settings/.touchpad.KeyboardGestureActivity` was found.

The sections below describe upstream Pastiera's workflows and release channels. They need upstream's signing secrets and don't apply to Flux Keyboard builds.

## Continuous Integration
- Pushes to `main` and pull requests run `.github/workflows/ci.yml`.
- The CI job runs, in order:
  - `:app:testStableDebugUnitTest`
  - `:app:testStableDebugUnitTest -PPASTIERA_FDROID_BUILD=true`
  - `:app:testNightlyDebugUnitTest`

## Manual release CI
- The repository includes a manually triggered GitHub Actions workflow at `.github/workflows/release.yml`.
- Required GitHub Actions secrets:
  - `PASTIERA_KEYSTORE_B64`
  - `PASTIERA_KEYSTORE_PASSWORD`
  - `PASTIERA_KEY_ALIAS`
  - `PASTIERA_KEY_PASSWORD`
- The workflow:
  - runs stable flavor unit tests
  - optionally runs the stable F-Droid-path unit tests
  - builds a signed stable release APK
  - optionally builds an unsigned stable APK for the official F-Droid path
  - verifies APK signing
  - uploads the signed APK and its SHA256 checksum as artifacts
  - uploads the unsigned F-Droid APK and its SHA256 checksum as artifacts
  - optionally creates a GitHub Release
- Release versioning is injected via Gradle properties:
  - `-PPASTIERA_VERSION_CODE=...`
  - `-PPASTIERA_VERSION_NAME=...`
- Local release builds can use the same mechanism:
  - `./gradlew :app:assembleStableRelease -PPASTIERA_VERSION_CODE=86 -PPASTIERA_VERSION_NAME=0.86`
  - `./scripts/build-release.sh 0.86 86`
  - `./scripts/build-fdroid.sh 0.86 86`

### Local signing config (`release/keystore.properties`)
- Local wrapper scripts read signing config from `release/keystore.properties` (gitignored).
- You can provide file paths, embedded Base64, or both (path + B64 for parity with CI secrets storage).
- CI-style variable names are supported directly:
  - Stable:
    - `PASTIERA_KEYSTORE_FILE`, `PASTIERA_KEYSTORE_PASSWORD`, `PASTIERA_KEY_ALIAS`, `PASTIERA_KEY_PASSWORD`, optional `PASTIERA_KEYSTORE_B64`
  - Nightly:
    - `NIGHTLY_KEYSTORE_FILE`, `PASTIERA_NIGHTLY_KEYSTORE_PASSWORD`, `PASTIERA_NIGHTLY_KEY_ALIAS`, `PASTIERA_NIGHTLY_KEY_PASSWORD`, optional `PASTIERA_NIGHTLY_KEYSTORE_B64`
- Legacy Gradle property names are still supported (`storeFile`, `storePassword`, `keyAlias`, `keyPassword`, `nightlyStoreFile`, `nightlyStorePassword`, `nightlyKeyAlias`, `nightlyKeyPassword`).
- When `PASTIERA_KEYSTORE_B64` or `PASTIERA_NIGHTLY_KEYSTORE_B64` is present, local scripts materialize the corresponding `.jks` only if the target file is missing.

## Manual nightly CI
- The repository includes a manually triggered nightly workflow at `.github/workflows/debug.yml`.
- Required GitHub Actions secrets:
  - `PASTIERA_NIGHTLY_KEYSTORE_B64`
  - `PASTIERA_NIGHTLY_KEYSTORE_PASSWORD`
  - `PASTIERA_NIGHTLY_KEY_ALIAS`
  - `PASTIERA_NIGHTLY_KEY_PASSWORD`
- The workflow:
  - runs nightly flavor debug-unit tests
  - builds a nightly release APK signed with the shared nightly key
  - computes a SHA256 checksum
  - uploads the APK and checksum as workflow artifacts
  - automatically turns a base version like `0.86` into a unique nightly version like `0.86-nightly.20260306.195412`
  - optionally publishes a GitHub pre-release under the `nightly/v*` tag scheme using that full nightly version
- The nightly flavor uses a separate application ID so it installs alongside the stable release.
- The nightly flavor is signed with a shared nightly key so local and CI nightly builds remain upgrade-compatible.
- Nightly version names follow the pattern `BASE-nightly.YYYYMMDD.HHMMSS`, for example `0.86-nightly.20260307.005731`.
- GitHub Nightly builds and private F-Droid Nightly builds share the same application ID and signing key, but F-Droid Nightly builds disable GitHub update checks so updates come from the F-Droid repo.
- Nightly pre-release disclaimer text is maintained in `.github/release-templates/debug-prerelease.md`.
- The same versioning can be generated locally:
  - `./scripts/nightly-version.sh 0.86`
  - `./gradlew :app:assembleNightlyRelease -PPASTIERA_VERSION_NAME=0.86 -PPASTIERA_NIGHTLY_VERSION_SUFFIX=-nightly.$(./scripts/nightly-version.sh 0.86 | awk -F= '/^timestamp=/{print $2}')`
- Local wrappers are available:
  - `./scripts/build-nightly.sh 0.86`
  - `./scripts/build-nightly.sh 0.86 --publish`
  - `./scripts/build-nightly-debug.sh 0.86`
  - `./scripts/build-nightly-debug.sh 0.86 --install`
  - `./scripts/build-nightly-debug.sh 0.86 --install --device <adb-serial>`
  - `./scripts/publish-private-fdroid-nightly.sh 0.86`
  - `./scripts/publish-private-fdroid-nightly.sh 0.86 ../palsoftware-web/apps/docs/public https://pastiera.eu/fdroid/nightly/repo`
  - `./scripts/publish-private-fdroid-nightly.sh 0.86 --timestamp 20260307.005731`
  - `./scripts/publish-private-fdroid-nightly.sh 0.86 ../palsoftware-web/apps/docs/public https://pastiera.eu/fdroid/nightly/repo --no-push-pages`
  - `./scripts/build-release.sh 0.86 86`
  - `./scripts/build-release.sh 0.86 86 --publish`

## Private F-Droid Nightly Repo
- Docs landing page:
  - `https://pastiera.eu/`
- Local Pages target:
  - `../palsoftware-web/apps/docs/public/fdroid/nightly/repo`
- Public repo URL:
  - `https://pastiera.eu/fdroid/nightly/repo`
- GitHub Nightly releases:
  - `https://github.com/palsoftware/pastiera/releases?q=nightly%2F`
- Local publish flow:
  - install `fdroidserver`
  - make sure nightly signing is configured
  - run `./scripts/publish-private-fdroid-nightly.sh 0.86`
  - pass `--timestamp YYYYMMDD.HHMMSS` when mirroring a GitHub Nightly pre-release so the F-Droid build uses the same version name and version code
  - optional: add `--no-push-pages` if you explicitly do not want the generated Pages repo changes committed and pushed
- The script:
  - builds the signed nightly APK
  - initializes or reuses a local F-Droid repo under `.fdroid/nightly`
  - stores each APK under a versioned filename so older Nightly builds can remain in the repo
  - updates the repo metadata with `fdroid update`
  - syncs the generated `repo/` contents into the Pages public directory
  - by default commits and pushes only `apps/docs/public/fdroid/nightly/repo` in `palsoftware-web`, which triggers the GitHub Pages deployment

## Signing Attestations
*Upstream only: these cover official Pastiera builds. Flux Keyboard is signed with a different key.*

These attestations document the public signing certificates and Android proof-of-rotation lineages used for stable and Nightly builds.
The current PKB.rocks attestations include complete YubiKey hardware attestations and manufacturer certificates on additional pages, with QR codes and PEM text. Android lineages are provided under signing/lineages and referenced by hash.
The Markdown files are the browser-friendly references. The PDFs are the archival artifacts prepared for qualified electronic signatures.
The legacy signed PDFs remain available under explicit legacy names.

| Channel | Current source | Prepared PDF | Signed PDF | Legacy signed PDF |
| --- | --- | --- | --- | --- |
| Stable | [docs/stable-signing-key-attestation.md](docs/stable-signing-key-attestation.md) | [docs/stable-signing-key-attestation.pdf](docs/stable-signing-key-attestation.pdf) | [docs/stable-signing-key-attestation_signed_signed.pdf](docs/stable-signing-key-attestation_signed_signed.pdf) | [docs/pastiera-legacy-release-signing-certificate-attestation_signed.pdf](docs/pastiera-legacy-release-signing-certificate-attestation_signed.pdf) |
| Nightly | [docs/nightly-signing-key-attestation.md](docs/nightly-signing-key-attestation.md) | [docs/nightly-signing-key-attestation.pdf](docs/nightly-signing-key-attestation.pdf) | [docs/nightly-signing-key-attestation_signed_signed.pdf](docs/nightly-signing-key-attestation_signed_signed.pdf) | [docs/pastiera-legacy-nightly-signing-certificate-attestation_signed.pdf](docs/pastiera-legacy-nightly-signing-certificate-attestation_signed.pdf) |

The signed PDF variants do not turn APK signing certificates into identity certificates. They authenticate the signer's statement about the documented Android signing keys and evidence.
Where a qualified electronic signature is present, validate it with the EU DSS validator and interpret it in the context of the eIDAS trust-services framework.

External verification references:

| Reference | Link | Purpose |
| --- | --- | --- |
| EU DSS Validator Demo | [ec.europa.eu/digital-building-blocks/DSS/webapp-demo/validation](https://ec.europa.eu/digital-building-blocks/DSS/webapp-demo/validation) | Validate the signed PDF attestations with the European Commission DSS demo service. |
| eIDAS overview | [digital-strategy.ec.europa.eu/en/policies/eidas-regulation](https://digital-strategy.ec.europa.eu/en/policies/eidas-regulation) | Background on the EU trust-services framework under which qualified electronic signatures are defined. |

## License and credits
Pastiera is licensed under the [GNU General Public License v3](LICENSE), and Flux Keyboard is
distributed under the same license. Pastiera was created by Andrea Palumbo (PalSoftware) and is
developed by Andrea Palumbo, Patrick Zauner and the contributors credited in the app's About screen
and in the [upstream repository](https://github.com/palsoftware/pastiera). Third-party components
are listed in [THIRD_PARTY_NOTICES.md](THIRD_PARTY_NOTICES.md). The fork's modifications are
described in [FORK_CHANGES.md](FORK_CHANGES.md).
