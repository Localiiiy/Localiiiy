import re

with open("app/src/main/java/com/example/ui/LocaliiiyViewModel.kt", "r") as f:
    text = f.read()

state_target = "private val _showMonetizationHub = MutableStateFlow(false)"
state_replacement = "private val _showDataAnalysis = MutableStateFlow(false)\n    val showDataAnalysis: StateFlow<Boolean> = _showDataAnalysis.asStateFlow()\n\n    private val _showMonetizationHub = MutableStateFlow(false)"

if state_target in text:
    text = text.replace(state_target, state_replacement)

func_target = "fun openMonetizationHub() {"
func_replacement = """fun openDataAnalysis() {
        _showDataAnalysis.value = true
    }
    
    fun closeDataAnalysis() {
        _showDataAnalysis.value = false
    }

    fun openMonetizationHub() {"""

if func_target in text:
    text = text.replace(func_target, func_replacement)

with open("app/src/main/java/com/example/ui/LocaliiiyViewModel.kt", "w") as f:
    f.write(text)
