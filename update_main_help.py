with open("app/src/main/java/com/example/MainActivity.kt", "r") as f:
    text = f.read()

target_state = "val showDataAnalysis by viewModel.showDataAnalysis.collectAsStateWithLifecycle()"
replacement_state = """val showHelpSheet by viewModel.showHelpSheet.collectAsStateWithLifecycle()
    val showInformationSheet by viewModel.showInformationSheet.collectAsStateWithLifecycle()
    val showDataAnalysis by viewModel.showDataAnalysis.collectAsStateWithLifecycle()"""
text = text.replace(target_state, replacement_state)

target_sheet = "if (showDataAnalysis) {"
replacement_sheet = """if (showHelpSheet) {
            com.example.ui.components.HelpSheet(onDismiss = { viewModel.closeHelp() })
        }
        if (showInformationSheet) {
            com.example.ui.components.InformationSheet(onDismiss = { viewModel.closeInformation() })
        }
        if (showDataAnalysis) {"""
text = text.replace(target_sheet, replacement_sheet)

target_args = "onOpenDataAnalysis = { viewModel.openDataAnalysis() },"
replacement_args = """onOpenHelp = { viewModel.openHelp() },
                        onOpenInformation = { viewModel.openInformation() },
                        onOpenDataAnalysis = { viewModel.openDataAnalysis() },"""
text = text.replace(target_args, replacement_args)

with open("app/src/main/java/com/example/MainActivity.kt", "w") as f:
    f.write(text)
