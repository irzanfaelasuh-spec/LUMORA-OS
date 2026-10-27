package com.lumora.os

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.*
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.*
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.math.roundToInt

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.statusBarColor = android.graphics.Color.rgb(16, 21, 42)
        window.navigationBarColor = android.graphics.Color.rgb(16, 21, 42)
        setContent { LumoraApp() }
    }
}

private val Ink = Color(0xFF10152A)
private val Panel = Color(0xE8222944)
private val Edge = Color(0x44FFFFFF)
private val Periwinkle = Color(0xFF9AA8FF)
private val Mint = Color(0xFF67E2D2)
private val Muted = Color(0xFFB6BED8)

private data class AppInfo(val name: String, val icon: ImageVector, val subtitle: String)
private val appList = listOf(
    AppInfo("Finder", Icons.Default.Folder, "Files and folders"),
    AppInfo("Terminal", Icons.Default.Terminal, "Command console"),
    AppInfo("Settings", Icons.Default.Settings, "Personalize Lumora"),
    AppInfo("System Info", Icons.Default.Memory, "About this system"),
    AppInfo("Notes", Icons.Default.Description, "Write and save notes"),
    AppInfo("Calculator", Icons.Default.Calculate, "Quick calculations"),
    AppInfo("Lumora Arcade", Icons.Default.SportsEsports, "Mini games"),
    AppInfo("App Studio", Icons.Default.Code, "Create a local app card")
)

@Composable
private fun LumoraApp() {
    var loggedIn by remember { mutableStateOf(false) }
    var user by remember { mutableStateOf("Creator") }
    var dark by remember { mutableStateOf(true) }
    var accent by remember { mutableStateOf(0) }
    var openApp by remember { mutableStateOf<String?>(null) }
    var minimized by remember { mutableStateOf(false) }
    var wallpaper by remember { mutableStateOf(0) }
    var desktopName by remember { mutableStateOf("Lumora Desktop") }
    var showAppMenu by remember { mutableStateOf(false) }
    var notes by remember { mutableStateOf("") }
    var customApps by remember { mutableStateOf(listOf<String>()) }
    val accentColor = when (accent) { 1 -> Mint; 2 -> Color(0xFFFFA6D7); else -> Periwinkle }
    val bg = if (dark) Ink else Color(0xFFE9EDFA)
    val fg = if (dark) Color.White else Color(0xFF18203B)

    MaterialTheme(colorScheme = if (dark) darkColorScheme(primary = accentColor, background = bg, surface = Panel) else lightColorScheme(primary = accentColor, background = bg, surface = Color(0xFFF7F8FF))) {
        Box(Modifier.fillMaxSize().background(Brush.linearGradient(when (wallpaper) {
            1 -> listOf(Color(0xFF173D55), Color(0xFF15152F), Color(0xFF50336F))
            2 -> listOf(Color(0xFF5A2B59), Color(0xFF242C5D), Color(0xFF10152A))
            else -> listOf(Color(0xFF273B79), Color(0xFF171D3C), Color(0xFF0A1023))
        }))) {
            if (!loggedIn) LoginScreen(onLogin = { name -> user = name.ifBlank { "Creator" }; loggedIn = true })
            else {
                Column(Modifier.fillMaxSize()) {
                    Row(Modifier.fillMaxWidth().height(38.dp).background(Color(0xB9101427)).padding(horizontal = 14.dp), verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Layers, null, tint = accentColor, modifier = Modifier.size(17.dp))
                        Spacer(Modifier.width(8.dp)); Text("Lumora", color = fg, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                        Spacer(Modifier.width(18.dp))
                        Box {
                            Text("Desktop  ▾", color = Muted, fontSize = 12.sp, modifier = Modifier.clickable { showAppMenu = true })
                            DropdownMenu(expanded = showAppMenu, onDismissRequest = { showAppMenu = false }) {
                                DropdownMenuItem(text = { Text("About Lumora OS") }, onClick = { openApp = "System Info"; showAppMenu = false })
                                DropdownMenuItem(text = { Text("Lock session") }, onClick = { loggedIn = false; openApp = null; showAppMenu = false })
                            }
                        }
                        Spacer(Modifier.weight(1f))
                        Text("${user}  ·  ${timeNow()}", color = Muted, fontSize = 11.sp)
                    }
                    Box(Modifier.weight(1f).fillMaxWidth()) {
                        Column(Modifier.align(Alignment.TopStart).padding(start = 18.dp, top = 22.dp), verticalArrangement = Arrangement.spacedBy(18.dp)) {
                            DesktopShortcut("Finder", Icons.Default.Folder) { openApp = "Finder"; minimized = false }
                            DesktopShortcut("Terminal", Icons.Default.Terminal) { openApp = "Terminal"; minimized = false }
                            DesktopShortcut("Lumora Arcade", Icons.Default.SportsEsports) { openApp = "Lumora Arcade"; minimized = false }
                            DesktopShortcut("System Info", Icons.Default.Memory) { openApp = "System Info"; minimized = false }
                        }
                        if (openApp != null && !minimized) {
                            AnimatedVisibility(visible = true, enter = fadeIn(), exit = fadeOut(), modifier = Modifier.align(Alignment.Center)) {
                                AppWindow(
                                    title = openApp!!,
                                    dark = dark,
                                    accent = accentColor,
                                    onClose = { openApp = null },
                                    onMinimize = { minimized = true },
                                    onMaximize = {},
                                    content = {
                                        when (openApp) {
                                            "Finder" -> FinderScreen(customApps, onOpen = { openApp = it }, onDelete = { customApps = customApps - it })
                                            "Terminal" -> TerminalScreen(user)
                                            "Settings" -> SettingsScreen(dark, { dark = it }, wallpaper, { wallpaper = it }, accent, { accent = it }, desktopName, { desktopName = it }, user, { user = it })
                                            "System Info" -> SystemInfoScreen()
                                            "Notes" -> NotesScreen(notes, { notes = it })
                                            "Calculator" -> CalculatorScreen()
                                            "Lumora Arcade" -> ArcadeScreen()
                                            "App Studio" -> AppStudioScreen(onCreate = { if (it.isNotBlank()) customApps = customApps + it.trim() })
                                            else -> if (customApps.contains(openApp)) CustomAppScreen(openApp!!) else Text("This app is not available yet.", color = fg)
                                        }
                                    }
                                )
                            }
                        }
                        if (minimized && openApp != null) Text("${openApp} minimized", color = Muted, modifier = Modifier.align(Alignment.Center).background(Panel, RoundedCornerShape(14.dp)).clickable { minimized = false }.padding(16.dp))
                    }
                    Row(Modifier.align(Alignment.CenterHorizontally).padding(bottom = 12.dp).clip(RoundedCornerShape(22.dp)).background(Color(0xCC171D35)).border(1.dp, Edge, RoundedCornerShape(22.dp)).padding(horizontal = 9.dp, vertical = 8.dp), horizontalArrangement = Arrangement.spacedBy(9.dp), verticalAlignment = Alignment.CenterVertically) {
                        listOf(appList[0], appList[1], appList[2], appList[3], appList[4], appList[5], appList[6], appList[7]).forEach { app ->
                            DockIcon(app, accentColor) { openApp = app.name; minimized = false }
                        }
                    }
                }
            }
        }
    }
}

@Composable private fun LoginScreen(onLogin: (String) -> Unit) {
    var username by remember { mutableStateOf("Creator") }; var password by remember { mutableStateOf("") }; var error by remember { mutableStateOf(false) }
    Box(Modifier.fillMaxSize().background(Brush.linearGradient(listOf(Color(0xFF263D7D), Color(0xFF171B39), Color(0xFF0B1022)))), contentAlignment = Alignment.Center) {
        Column(Modifier.padding(24.dp).widthIn(max = 360.dp).fillMaxWidth().clip(RoundedCornerShape(28.dp)).background(Color(0xD91A203A)).border(1.dp, Edge, RoundedCornerShape(28.dp)).padding(26.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            Box(Modifier.size(66.dp).clip(RoundedCornerShape(20.dp)).background(Brush.linearGradient(listOf(Periwinkle, Mint))), contentAlignment = Alignment.Center) { Icon(Icons.Default.Layers, null, tint = Ink, modifier = Modifier.size(34.dp)) }
            Spacer(Modifier.height(18.dp)); Text("LUMORA OS", color = Color.White, fontSize = 25.sp, fontWeight = FontWeight.Bold, letterSpacing = 2.sp)
            Text("A workspace of your own", color = Muted, fontSize = 13.sp); Spacer(Modifier.height(26.dp))
            OutlinedTextField(username, { username = it }, label = { Text("Username") }, singleLine = true, modifier = Modifier.fillMaxWidth())
            Spacer(Modifier.height(10.dp)); OutlinedTextField(password, { password = it; error = false }, label = { Text("Password (optional for demo)") }, singleLine = true, visualTransformation = PasswordVisualTransformation(), modifier = Modifier.fillMaxWidth())
            if (error) Text("Please enter a username.", color = Color(0xFFFF9DAD), fontSize = 12.sp)
            Spacer(Modifier.height(18.dp)); Button(onClick = { if (username.isNotBlank()) onLogin(username.trim()) else error = true }, modifier = Modifier.fillMaxWidth().height(50.dp), shape = RoundedCornerShape(15.dp)) { Text("Sign in", fontWeight = FontWeight.SemiBold) }
            Spacer(Modifier.height(12.dp)); Text("Local demo profile · no online account", color = Muted, fontSize = 11.sp)
        }
    }
}

@Composable private fun DesktopShortcut(name: String, icon: ImageVector, onClick: () -> Unit) {
    Column(Modifier.width(84.dp).clip(RoundedCornerShape(13.dp)).clickable { onClick() }.padding(6.dp), horizontalAlignment = Alignment.CenterHorizontally) {
        Box(Modifier.size(52.dp).clip(RoundedCornerShape(16.dp)).background(Brush.linearGradient(listOf(Color(0xFF667BE0), Color(0xFF54CFC6)))), contentAlignment = Alignment.Center) { Icon(icon, null, tint = Color.White, modifier = Modifier.size(28.dp)) }
        Spacer(Modifier.height(5.dp)); Text(name, color = Color.White, fontSize = 11.sp, maxLines = 2)
    }
}

@Composable private fun DockIcon(app: AppInfo, accent: Color, onClick: () -> Unit) {
    var pressed by remember { mutableStateOf(false) }; val scale by animateFloatAsState(if (pressed) 1.12f else 1f, label = "dockScale")
    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.scale(scale).clickable { pressed = !pressed; onClick() }) {
        Box(Modifier.size(38.dp).clip(RoundedCornerShape(12.dp)).background(Brush.linearGradient(listOf(accent, Color(0xFF4359A7)))), contentAlignment = Alignment.Center) { Icon(app.icon, null, tint = Color.White, modifier = Modifier.size(22.dp)) }
        Spacer(Modifier.height(3.dp)); Text(app.name, color = Color.White, fontSize = 8.sp, maxLines = 1)
    }
}

@Composable private fun AppWindow(title: String, dark: Boolean, accent: Color, onClose: () -> Unit, onMinimize: () -> Unit, onMaximize: () -> Unit, content: @Composable () -> Unit) {
    var offset by remember { mutableStateOf(Offset.Zero) }; var maximized by remember { mutableStateOf(false) }
    val foreground = if (dark) Color.White else Color(0xFF17203A)
    Column(Modifier.fillMaxWidth(if (maximized) 0.98f else 0.91f).heightIn(min = 300.dp, max = if (maximized) 620.dp else 510.dp).offset { IntOffset(offset.x.roundToInt(), offset.y.roundToInt()) }.clip(RoundedCornerShape(18.dp)).background(if (dark) Color(0xF21A2037) else Color(0xF7F7F9FF)).border(1.dp, Edge, RoundedCornerShape(18.dp)).shadow(22.dp, RoundedCornerShape(18.dp))) {
        Row(Modifier.fillMaxWidth().background(if (dark) Color(0xFF222A45) else Color(0xFFE1E6F5)).pointerInput(Unit) { detectDragGestures { change, drag -> change.consume(); offset = Offset((offset.x + drag.x).coerceIn(-180f, 180f), (offset.y + drag.y).coerceIn(-260f, 260f)) } }.padding(horizontal = 12.dp, vertical = 10.dp), verticalAlignment = Alignment.CenterVertically) {
            Row(horizontalArrangement = Arrangement.spacedBy(7.dp)) {
                Box(Modifier.size(11.dp).clip(CircleShape).background(Color(0xFFFF6B6B)).clickable { onClose() })
                Box(Modifier.size(11.dp).clip(CircleShape).background(Color(0xFFFFC45C)).clickable { onMinimize() })
                Box(Modifier.size(11.dp).clip(CircleShape).background(Color(0xFF55D99A)).clickable { maximized = !maximized; onMaximize() })
            }
            Spacer(Modifier.weight(1f)); Text(title, color = foreground, fontSize = 13.sp, fontWeight = FontWeight.SemiBold); Spacer(Modifier.weight(1f)); Icon(Icons.Default.MoreHoriz, null, tint = foreground.copy(alpha = .65f), modifier = Modifier.size(18.dp))
        }
        Box(Modifier.fillMaxWidth().weight(1f).padding(14.dp)) { content() }
    }
}

@Composable private fun FinderScreen(customApps: List<String>, onOpen: (String) -> Unit, onDelete: (String) -> Unit) {
    var selected by remember { mutableStateOf<String?>(null) }; var showDelete by remember { mutableStateOf(false) }
    Column(Modifier.fillMaxSize()) {
        Text("Favorites", color = Muted, fontSize = 12.sp); Spacer(Modifier.height(10.dp))
        listOf("Applications", "Documents", "Downloads", "Desktop", "Lumora Arcade").forEach { name -> Row(Modifier.fillMaxWidth().clip(RoundedCornerShape(10.dp)).clickable { selected = name }.background(if (selected == name) Periwinkle.copy(alpha = .15f) else Color.Transparent).padding(11.dp), verticalAlignment = Alignment.CenterVertically) { Icon(Icons.Default.Folder, null, tint = Periwinkle); Spacer(Modifier.width(10.dp)); Text(name, color = Color.White); Spacer(Modifier.weight(1f)); if (name == "Applications") Text("8", color = Muted, fontSize = 11.sp) } }
        if (customApps.isNotEmpty()) { Spacer(Modifier.height(8.dp)); Text("Created in App Studio", color = Muted, fontSize = 12.sp); customApps.forEach { app -> Row(Modifier.fillMaxWidth().clickable { selected = app }.padding(8.dp), verticalAlignment = Alignment.CenterVertically) { Icon(Icons.Default.Apps, null, tint = Mint); Spacer(Modifier.width(8.dp)); Text(app, color = Color.White, modifier = Modifier.weight(1f)); TextButton(onClick = { onOpen(app) }) { Text("Open") }; TextButton(onClick = { selected = app; showDelete = true }) { Text("Delete") } } } }
    }
    if (showDelete && selected != null) AlertDialog(onDismissRequest = { showDelete = false }, title = { Text("Delete ${selected}?") }, text = { Text("This removes the app card from this demo desktop.") }, confirmButton = { TextButton(onClick = { onDelete(selected!!); showDelete = false }) { Text("Delete") } }, dismissButton = { TextButton(onClick = { showDelete = false }) { Text("Cancel") } })
}

@Composable private fun TerminalScreen(user: String) {
    var input by remember { mutableStateOf("") }; var history by remember { mutableStateOf(listOf("Lumora Terminal 0.1.0", "Type 'help' to see supported demo commands.")) }
    Column(Modifier.fillMaxSize()) {
        LazyColumn(Modifier.weight(1f).fillMaxWidth(), reverseLayout = false) { items(history) { line -> Text(line, color = if (line.startsWith("lumora:")) Mint else Color(0xFFD5DCF5), fontSize = 12.sp, lineHeight = 18.sp) } }
        Row(Modifier.fillMaxWidth().background(Color(0xFF101525), RoundedCornerShape(10.dp)).padding(10.dp), verticalAlignment = Alignment.CenterVertically) {
            Text("$ ", color = Mint, fontWeight = FontWeight.Bold); BasicTextField(value = input, onValueChange = { input = it }, modifier = Modifier.weight(1f), singleLine = true, textStyle = androidx.compose.ui.text.TextStyle(color = Color.White, fontSize = 13.sp), keyboardOptions = KeyboardOptions(imeAction = ImeAction.Send), keyboardActions = KeyboardActions(onSend = { history = history + "$ $input" + terminalResponse(input, user); input = "" }))
            IconButton(onClick = { history = history + "$ $input" + terminalResponse(input, user); input = "" }) { Icon(Icons.Default.Send, null, tint = Mint) }
        }
    }
}
private fun terminalResponse(command: String, user: String): List<String> = when (command.trim().lowercase()) {
    "help" -> listOf("Supported commands:", "  help      show this menu", "  clear     clear terminal", "  ls        list demo directories", "  pwd       show virtual path", "  whoami    show local profile", "  uname     show Lumora system info", "  neofetch  system summary", "  date      current device date", "  apps      list built-in apps", "Note: this is a sandboxed demo terminal, not an Android/Linux shell.")
    "clear" -> listOf("\u0000CLEAR")
    "ls" -> listOf("Desktop/  Documents/  Downloads/  Applications/")
    "pwd" -> listOf("/home/$user")
    "whoami" -> listOf(user)
    "uname" -> listOf("Lumora OS 0.1.0 · Android host · Kotlin/Compose UI")
    "neofetch" -> listOf("LUMORA OS", "──────────────", "OS: Lumora Desktop 0.1.0", "Host: Android", "Shell: Lumora Demo Shell", "UI: Compose", "Session: $user", "Status: prototype")
    "date" -> listOf(SimpleDateFormat("EEE MMM dd HH:mm:ss z yyyy", Locale.getDefault()).format(Date()))
    "apps" -> listOf("Finder  Terminal  Settings  System Info  Notes  Calculator  Lumora Arcade  App Studio")
    "" -> emptyList()
    else -> listOf("Command not found: ${command.trim()}", "Type 'help' for available commands.")
}

@Composable private fun SettingsScreen(dark: Boolean, onDark: (Boolean) -> Unit, wallpaper: Int, onWallpaper: (Int) -> Unit, accent: Int, onAccent: (Int) -> Unit, desktopName: String, onDesktopName: (String) -> Unit, user: String, onUser: (String) -> Unit) {
    Column(Modifier.fillMaxSize().verticalScroll(rememberScrollState()), verticalArrangement = Arrangement.spacedBy(14.dp)) {
        Text("System Settings", color = if (dark) Color.White else Ink, fontSize = 21.sp, fontWeight = FontWeight.Bold)
        SettingCard("Appearance", "Choose a light or dark interface") { Row(verticalAlignment = Alignment.CenterVertically) { Text("Dark mode", color = if (dark) Color.White else Ink, modifier = Modifier.weight(1f)); Switch(dark, onDark) } }
        SettingCard("Accent color", "Personalize interactive controls") { Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) { listOf(0,1,2).forEach { idx -> val c = listOf(Periwinkle, Mint, Color(0xFFFFA6D7))[idx]; Box(Modifier.size(35.dp).clip(CircleShape).background(c).border(if (accent == idx) 3.dp else 0.dp, Color.White, CircleShape).clickable { onAccent(idx) }) } } }
        SettingCard("Wallpaper", "Choose a desktop gradient") { Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) { listOf(0,1,2).forEach { idx -> Box(Modifier.weight(1f).height(54.dp).clip(RoundedCornerShape(12.dp)).background(Brush.linearGradient(when (idx) { 1 -> listOf(Color(0xFF173D55), Color(0xFF50336F)); 2 -> listOf(Color(0xFF5A2B59), Color(0xFF242C5D)); else -> listOf(Color(0xFF273B79), Ink) })).border(if (wallpaper == idx) 2.dp else 0.dp, Color.White, RoundedCornerShape(12.dp)).clickable { onWallpaper(idx) }) } } }
        SettingCard("Desktop label", "Change the local desktop name") { var draft by remember(desktopName) { mutableStateOf(desktopName) }; OutlinedTextField(draft, { draft = it }, modifier = Modifier.fillMaxWidth(), singleLine = true); TextButton(onClick = { onDesktopName(draft) }) { Text("Apply name") }; Text("Current: $desktopName", color = Muted, fontSize = 11.sp) }
        SettingCard("Profile", "Local demo profile") { var draft by remember(user) { mutableStateOf(user) }; OutlinedTextField(draft, { draft = it }, label = { Text("Display name") }, singleLine = true, modifier = Modifier.fillMaxWidth()); TextButton(onClick = { if (draft.isNotBlank()) onUser(draft.trim()) }) { Text("Update profile") } }
    }
}
@Composable private fun SettingCard(title: String, desc: String, content: @Composable ColumnScope.() -> Unit) { Column(Modifier.fillMaxWidth().clip(RoundedCornerShape(15.dp)).background(Panel).border(1.dp, Edge, RoundedCornerShape(15.dp)).padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) { Text(title, color = Color.White, fontWeight = FontWeight.SemiBold); Text(desc, color = Muted, fontSize = 11.sp); content() } }

@Composable private fun SystemInfoScreen() {
    Column(Modifier.fillMaxSize().verticalScroll(rememberScrollState()), verticalArrangement = Arrangement.spacedBy(10.dp)) {
        Text("About This System", color = Color.White, fontSize = 22.sp, fontWeight = FontWeight.Bold)
        Text("LUMORA OS", color = Periwinkle, fontSize = 18.sp, fontWeight = FontWeight.ExtraBold, letterSpacing = 2.sp)
        Text("Version 0.1.0 · Prototype", color = Muted)
        listOf("Host platform" to "Android", "Interface" to "Kotlin + Jetpack Compose", "Kernel" to "Android kernel (host system)", "Session" to "Local demo profile", "Terminal" to "Sandboxed command simulator", "Storage" to "Demo virtual files", "License" to "Project license to be selected").forEach { (a,b) -> Row(Modifier.fillMaxWidth().padding(vertical = 7.dp), verticalAlignment = Alignment.Top) { Text(a, color = Muted, modifier = Modifier.weight(1f), fontSize = 12.sp); Text(b, color = Color.White, modifier = Modifier.weight(1.3f), fontSize = 12.sp) }; HorizontalDivider(color = Edge) }
        Text("Lumora is a desktop-style environment running as an Android app. It is not a standalone bootable operating system yet.", color = Muted, fontSize = 12.sp, lineHeight = 18.sp)
    }
}
@Composable private fun NotesScreen(notes: String, onNotes: (String) -> Unit) { Column(Modifier.fillMaxSize()) { Text("Notes", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 20.sp); Spacer(Modifier.height(12.dp)); OutlinedTextField(notes, onNotes, modifier = Modifier.fillMaxSize(), placeholder = { Text("Write something...") }) } }
@Composable private fun CalculatorScreen() {
    var expr by remember { mutableStateOf("") }; var result by remember { mutableStateOf("Ready") }
    Column(Modifier.fillMaxSize(), verticalArrangement = Arrangement.spacedBy(12.dp)) { Text("Calculator", color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.Bold); Text(result, color = Mint, fontSize = 22.sp); OutlinedTextField(expr, { expr = it }, modifier = Modifier.fillMaxWidth(), label = { Text("Expression (e.g. 12+5)") }); Button(onClick = { result = try { val m = Regex("^\\s*(-?\\d+(?:\\.\\d+)?)\\s*([+*/-])\\s*(-?\\d+(?:\\.\\d+)?)\\s*$").matchEntire(expr) ?: error("Use two numbers and + - * /"); val a = m.groupValues[1].toDouble(); val b = m.groupValues[3].toDouble(); when(m.groupValues[2]) { "+" -> (a+b).toString(); "-" -> (a-b).toString(); "*" -> (a*b).toString(); "/" -> if (b == 0.0) "Cannot divide by zero" else (a/b).toString(); else -> "Error" } } catch (_: Exception) { "Invalid expression" } }, modifier = Modifier.fillMaxWidth()) { Text("Calculate") } }
}
@Composable private fun ArcadeScreen() {
    var score by remember { mutableIntStateOf(0) }; var taps by remember { mutableIntStateOf(0) }; var phase by remember { mutableIntStateOf(0) }
    Column(Modifier.fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Text("LUMORA ARCADE", color = Color.White, fontSize = 19.sp, fontWeight = FontWeight.ExtraBold, letterSpacing = 1.sp); Text("Nebula Collector · 2D mini game", color = Muted, fontSize = 12.sp)
        Box(Modifier.fillMaxWidth().weight(1f).clip(RoundedCornerShape(18.dp)).background(Brush.radialGradient(listOf(Color(0xFF343E82), Color(0xFF11162E)))), contentAlignment = Alignment.Center) {
            Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(18.dp)) {
                Icon(Icons.Default.AutoAwesome, null, tint = listOf(Mint, Periwinkle, Color(0xFFFFC875))[phase % 3], modifier = Modifier.size(76.dp).clickable { score += 1; taps += 1; phase += 1 })
                Text("Tap the crystal to collect stardust", color = Color.White, fontSize = 12.sp)
                Text("STARDUST  $score", color = Mint, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                Button(onClick = { score = 0; taps = 0; phase = 0 }) { Text("Restart") }
            }
        }
        Text("Prototype mini-game. Full 2D/3D open-world RPG is not included in this first build.", color = Muted, fontSize = 11.sp, lineHeight = 16.sp)
    }
}
@Composable private fun AppStudioScreen(onCreate: (String) -> Unit) { var name by remember { mutableStateOf("") }; var made by remember { mutableStateOf(false) }; Column(Modifier.fillMaxSize(), verticalArrangement = Arrangement.spacedBy(12.dp)) { Text("App Studio", color = Color.White, fontSize = 21.sp, fontWeight = FontWeight.Bold); Text("Create a launchable app card inside this prototype desktop.", color = Muted, fontSize = 12.sp); OutlinedTextField(name, { name = it; made = false }, label = { Text("App name") }, modifier = Modifier.fillMaxWidth(), singleLine = true); Button(onClick = { if (name.isNotBlank()) { onCreate(name.trim()); made = true } }, modifier = Modifier.fillMaxWidth()) { Icon(Icons.Default.Add, null); Spacer(Modifier.width(8.dp)); Text("Create app") }; if (made) Text("App card created. Open Finder to launch or delete it.", color = Mint, fontSize = 12.sp) } }
@Composable private fun CustomAppScreen(name: String) { Column(Modifier.fillMaxSize(), verticalArrangement = Arrangement.spacedBy(10.dp)) { Icon(Icons.Default.Apps, null, tint = Mint, modifier = Modifier.size(42.dp)); Text(name, color = Color.White, fontSize = 22.sp, fontWeight = FontWeight.Bold); Text("This is a user-created local app card. Add a dedicated app screen and logic to turn it into a full application.", color = Muted, fontSize = 13.sp, lineHeight = 18.sp) } }
private fun timeNow(): String = SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date())
