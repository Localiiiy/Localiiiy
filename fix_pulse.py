import re

with open('app/src/main/java/com/example/ui/components/PulseFeedComponent.kt', 'r') as f:
    content = f.read()

# Remove the incorrectly inserted blocks
bad_block = """                item {
                    var selectedLens by remember { mutableStateOf("NEIGHBOR") }
                    TactileTriDialFeedLens(
                        selectedDial = selectedLens,
                        onDialSelected = { selectedLens = it }
                    )
                }\n"""
content = content.replace(bad_block, "")

# Insert it in the correct place, right before 'item {\n                    StoriesTray('
correct_insertion = """                item {
                    var selectedLens by remember { mutableStateOf("NEIGHBOR") }
                    TactileTriDialFeedLens(
                        selectedDial = selectedLens,
                        onDialSelected = { selectedLens = it },
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                }
                item {
                    StoriesTray("""

content = content.replace("                item {\n                    StoriesTray(", correct_insertion)

with open('app/src/main/java/com/example/ui/components/PulseFeedComponent.kt', 'w') as f:
    f.write(content)

