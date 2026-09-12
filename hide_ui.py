import re

with open('app/src/main/java/com/example/ui/screens/ClipsScreen.kt', 'r') as f:
    content = f.read()

# Right Side
content = content.replace(
'''        // Right Side Floating Action Buttons
        Column(
            modifier = Modifier
                .align(Alignment.BottomEnd)''',
'''        // Right Side Floating Action Buttons
        Column(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .graphicsLayer { alpha = if (isCinemaMode) 0f else 1f }'''
)

# Bottom Left
content = content.replace(
'''        // Bottom Left Creator Info & Caption
        Column(
            modifier = Modifier
                .align(Alignment.BottomStart)''',
'''        // Bottom Left Creator Info & Caption
        Column(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .graphicsLayer { alpha = if (isCinemaMode) 0f else 1f }'''
)

with open('app/src/main/java/com/example/ui/screens/ClipsScreen.kt', 'w') as f:
    f.write(content)
