import re

with open('app/src/main/java/com/example/ui/screens/ClipsScreen.kt', 'r') as f:
    content = f.read()

content = content.replace('            import androidx.compose.foundation.pager.PagerDefaults', '')
content = content.replace('import androidx.compose.ui.hapticfeedback.HapticFeedbackType', 'import androidx.compose.ui.hapticfeedback.HapticFeedbackType\nimport androidx.compose.foundation.pager.PagerDefaults')

with open('app/src/main/java/com/example/ui/screens/ClipsScreen.kt', 'w') as f:
    f.write(content)
