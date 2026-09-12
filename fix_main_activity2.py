with open('app/src/main/java/com/example/MainActivity.kt', 'r') as f:
    lines = f.readlines()

new_lines = []
for i, line in enumerate(lines):
    # Remove duplicate imports around line 71
    if 70 <= i <= 78 and line.strip().startswith('import'):
        continue
    
    # Fix the androidx.compose fully qualified names
    if 'androidx.compose.foundation.layout.Box' in line:
        line = line.replace('androidx.compose.foundation.layout.Box', 'Box')
    if 'androidx.compose.ui.Modifier' in line:
        line = line.replace('androidx.compose.ui.Modifier', 'Modifier')
    if 'androidx.compose.foundation.layout.fillMaxSize()' in line:
        line = line.replace('androidx.compose.foundation.layout.fillMaxSize()', 'fillMaxSize()')
    if 'androidx.compose.foundation.background(androidx.compose.ui.graphics.Color.Black)' in line:
        line = line.replace('androidx.compose.foundation.background(androidx.compose.ui.graphics.Color.Black)', 'background(Color.Black)')
    if 'androidx.compose.ui.Alignment.Center' in line:
        line = line.replace('androidx.compose.ui.Alignment.Center', 'Alignment.Center')
    if 'androidx.compose.material3.Text' in line:
        line = line.replace('androidx.compose.material3.Text', 'Text')
    if 'androidx.compose.ui.graphics.Color.White' in line:
        line = line.replace('androidx.compose.ui.graphics.Color.White', 'Color.White')
    if 'androidx.compose.material3.MaterialTheme.typography.titleLarge' in line:
        line = line.replace('androidx.compose.material3.MaterialTheme.typography.titleLarge', 'MaterialTheme.typography.titleLarge')

    new_lines.append(line)

with open('app/src/main/java/com/example/MainActivity.kt', 'w') as f:
    f.writelines(new_lines)
