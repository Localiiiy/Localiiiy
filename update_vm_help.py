with open("app/src/main/java/com/example/ui/LocaliiiyViewModel.kt", "r") as f:
    text = f.read()

target_state = "private val _showDataAnalysis = MutableStateFlow(false)"
replacement_state = """private val _showHelpSheet = MutableStateFlow(false)
    val showHelpSheet: kotlinx.coroutines.flow.StateFlow<Boolean> = _showHelpSheet.asStateFlow()

    private val _showInformationSheet = MutableStateFlow(false)
    val showInformationSheet: kotlinx.coroutines.flow.StateFlow<Boolean> = _showInformationSheet.asStateFlow()

    private val _showDataAnalysis = MutableStateFlow(false)"""

text = text.replace(target_state, replacement_state)

target_funcs = "fun openDataAnalysis() {"
replacement_funcs = """fun openHelp() { _showHelpSheet.value = true }
    fun closeHelp() { _showHelpSheet.value = false }

    fun openInformation() { _showInformationSheet.value = true }
    fun closeInformation() { _showInformationSheet.value = false }

    fun openDataAnalysis() {"""

text = text.replace(target_funcs, replacement_funcs)

with open("app/src/main/java/com/example/ui/LocaliiiyViewModel.kt", "w") as f:
    f.write(text)
