with open("app/src/main/java/com/example/ui/screens/ProfileScreen.kt", "r") as f:
    text = f.read()

# 1. ProfileScreen Signature
target_ps = "onOpenDataAnalysis: () -> Unit = {},"
replacement_ps = "onOpenDataAnalysis: () -> Unit = {},\n    onOpenHelp: () -> Unit = {},\n    onOpenInformation: () -> Unit = {},"
text = text.replace(target_ps, replacement_ps)

# 2. ProfileScreen content pass-through
target_content = "onOpenDataAnalysis = {\n                    showMoreSettingsSheet = false\n                    onOpenDataAnalysis()\n                },"
replacement_content = "onOpenDataAnalysis = {\n                    showMoreSettingsSheet = false\n                    onOpenDataAnalysis()\n                },\n                onOpenHelp = {\n                    showMoreSettingsSheet = false\n                    onOpenHelp()\n                },\n                onOpenInformation = {\n                    showMoreSettingsSheet = false\n                    onOpenInformation()\n                },"
text = text.replace(target_content, replacement_content)

# 3. ProfileSettingsSheetContent Signature
target_ssc = "onOpenDataAnalysis: () -> Unit = {},"
replacement_ssc = "onOpenDataAnalysis: () -> Unit = {},\n    onOpenHelp: () -> Unit = {},\n    onOpenInformation: () -> Unit = {},"
text = text.replace(target_ssc, replacement_ssc)

# 4. Add UI to ProfileSettingsSheetContent
target_ui = """        SettingsRowItem(
            icon = Icons.Default.Storage,
            title = "Storage & Cache",
            subtitle = "Clear cached clips & media (0.8 MB cached)",
            onClick = { /* Cache clear */ }
        )"""

replacement_ui = """        SettingsRowItem(
            icon = Icons.Default.Storage,
            title = "Storage & Cache",
            subtitle = "Clear cached clips & media (0.8 MB cached)",
            onClick = { /* Cache clear */ }
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "SUPPORT & INFO",
            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, letterSpacing = 1.sp),
            color = MaterialTheme.colorScheme.primary
        )
        Spacer(modifier = Modifier.height(6.dp))

        SettingsRowItem(
            icon = Icons.Default.HelpOutline,
            title = "Help & Support",
            subtitle = "5W & 1H FAQ, advance search, app tasks",
            onClick = onOpenHelp
        )

        SettingsRowItem(
            icon = Icons.Default.Info,
            title = "Information & Badges",
            subtitle = "Creator tiers, terminology, app pages",
            onClick = onOpenInformation
        )"""

text = text.replace(target_ui, replacement_ui)

with open("app/src/main/java/com/example/ui/screens/ProfileScreen.kt", "w") as f:
    f.write(text)
