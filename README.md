# Lumora OS

Lumora OS is an Android app prototype for a desktop-style environment inspired by desktop operating systems. It is written in Kotlin with Jetpack Compose; it does not use an HTML/CSS/JavaScript frontend.

## Prototype features
- Local demo login screen (not secure account authentication)
- Desktop shortcuts and bottom dock with drawn/vector app icons
- App windows with close, minimize and draggable title bars
- Finder-style virtual app list with delete action for app cards created in App Studio
- Sandboxed demo terminal (`help`, `ls`, `pwd`, `whoami`, `uname`, `neofetch`, `date`, `apps`, `clear`)
- Theme, accent color, wallpaper and local display-name settings
- System information page
- Notes, basic calculator, App Studio app cards
- Tiny 2D tap-to-collect mini-game prototype

## Important limitations
This is an Android-hosted desktop simulation, not a bootable OS or Linux distribution. The terminal is a safe command simulator, not a real shell; the Finder is a virtual demo view, not a full filesystem browser. Login is local-only and must not be treated as secure authentication. Notes and preferences are currently in-memory and reset when the process is recreated. The full 2D/3D open-world RPG, true app packaging/installing, persistent file system, real terminal, multi-window resizing and production account security are not implemented in this first prototype.

## Build APK with GitHub Actions
1. Create a GitHub repository and upload the contents of this folder (including `.github/workflows/build-apk.yml`).
2. Open **Actions** in the repository and select **Build Lumora OS APK**.
3. Press **Run workflow** (or push to `main` / `master`).
4. When the workflow succeeds, open its run and download the `LumoraOS-debug-apk` artifact.
5. Extract the artifact ZIP to get `app-debug.apk`.

## Build locally
Install Android Studio / JDK 17 and Gradle, then run:

```bash
gradle assembleDebug
```

The APK will be at `app/build/outputs/apk/debug/app-debug.apk`.
