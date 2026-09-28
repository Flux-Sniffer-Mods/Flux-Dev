# Dictionary Pre-processing Script

This script converts JSON dictionary files into a serialized format for faster loading at runtime.

## Performance Improvement

- **Before (JSON)**: ~800-1300ms to load 50k words
- **After (Serialized)**: ~130-250ms to load 50k words
- **Improvement**: ~75-85% faster (5-8x speedup)

## How to Run

### Option 1: Using Kotlin Scripting (Recommended)

If you have Kotlin installed:

```bash
kotlinc -script scripts/preprocess-dictionaries.main.kts
```

### Option 2: Manual Execution

1. Navigate to the project root
2. Run the script with Kotlin compiler:

```bash
kotlinc -script scripts/preprocess-dictionaries.main.kts .
```

### Option 3: Android Studio (Easiest)

1. Open `scripts/preprocess-dictionaries.main.kts` in Android Studio
2. Right-click on the file → "Run" (or press `Shift+F10`)
3. Android Studio has Kotlin built-in, no installation needed!

The script will:
- Read all `*_base.json` files from `app/src/main/assets/common/dictionaries/`
- Build normalized index and prefix cache
- Serialize to `.dict` files in `app/src/main/assets/common/dictionaries_serialized/`

## Output

The script generates `.dict` files (JSON serialized format) that are:
- **20-30% smaller** than original JSON
- **Pre-indexed** (no indexing overhead at runtime)
- **5-8x faster** to load

## Fallback Behaviour

The app automatically falls back to JSON format if `.dict` files are not found, so the system remains backward compatible.

## When to Re-run

Re-run the script when:
- Dictionary JSON files are updated
- New languages are added
- Dictionary structure changes

## Notes

- The serialized format uses Kotlinx Serialization JSON (compact mode)
- Original JSON files are kept as fallback
- User dictionary entries are always loaded dynamically (not pre-processed)

## Pastiera signing-key rotation test

Build the official Stable variant without a Gradle signing key:

```bash
./scripts/build-stable-release-unsigned.sh 0.86 86
```

The default successor endpoint is `pkb-rocks/plektra`. A third argument can
select a test fixture for an end-to-end build:

```bash
./scripts/build-stable-release-unsigned.sh \
  0.86 86 pkb-rocks/plektra-updater-e2e-test
```

Connect YK1 and sign the APK with Stable A in PIV slot 9C:

```bash
./scripts/sign-pastiera-stable-apk-piv.sh \
  app/build/outputs/apk/stable/release/app-stable-release-unsigned.apk
```

The signing script requests the PIV PIN in the terminal and requires a touch.
It verifies the application ID, version, Stable A certificate, v3 signature,
and the embedded Legacy Stable to Stable A lineage.
Pass `--yes` as the last argument only when an enclosing ceremony has already
confirmed the exact input and output files.

Connect one Android device that has the published Legacy Stable version. Test
the update with:

```bash
./scripts/test-pastiera-stable-rotation-update.sh \
  app/build/outputs/apk/stable/release/app-stable-release-piv-signed.apk
```

For an already approved unattended device test, append the device serial and
`--yes`.

The device script rejects a non-Legacy installation and a version downgrade.
It also verifies the installed APK, package user ID, first install time, and
default input method after the update. Confirm the preserved settings and user
data in the app after the script succeeds.

## Pastiera Nightly A release signing

Build the GitHub and F-Droid Nightly APKs unsigned with the same timestamp:

```bash
scripts/build-nightly-release-unsigned.sh 0.86 \
  .release/0.86/artifacts/Pastiera-0.86-nightly.TIMESTAMP-github-unsigned.apk \
  --timestamp TIMESTAMP
scripts/build-nightly-release-unsigned.sh 0.86 \
  .release/0.86/artifacts/Pastiera-0.86-nightly.TIMESTAMP-fdroid-unsigned.apk \
  --timestamp TIMESTAMP --fdroid
```

Connect YK1 and sign both APKs with Nightly A in PIV slot 82:

```bash
scripts/sign-pastiera-nightly-apk-piv.sh \
  .release/0.86/artifacts/Pastiera-0.86-nightly.TIMESTAMP-github-unsigned.apk \
  .release/0.86/artifacts/Pastiera-0.86-nightly.TIMESTAMP-github.apk
scripts/sign-pastiera-nightly-apk-piv.sh \
  .release/0.86/artifacts/Pastiera-0.86-nightly.TIMESTAMP-fdroid-unsigned.apk \
  .release/0.86/artifacts/Pastiera-0.86-nightly.TIMESTAMP-fdroid.apk
```

The signing script embeds `signing/lineages/pastiera-nightly-v1.lineage`, checks the
Nightly A certificate, requests the PIV PIN in the terminal and requires a touch.

### Verify signing key hardware attestations

`python3 scripts/verify-signing-key-attestations.py` verifies the Stable and Nightly
Markdown evidence, including QR/PEM equality, Android certificate self-signatures,
public-key matches and the complete manufacturer chain. Requires OpenSSL 3;
select it with `--openssl /path/to/openssl` when needed.

The Yubico root is fetched from its official HTTPS URL and pinned by DER SHA-256.
Repeated runs reuse the verified cache at `~/.cache/pkb-signing-attestations`.
An invalid cached root fails closed. The script needs no token or private files.
