import re

with open('app/src/main/java/com/example/ui/components/OtherUserProfileSheet.kt', 'r') as f:
    content = f.read()

# Replace the privacyMode check
old_check = """                                        if (user.privacyMode == "GHOST") {
                                            Text("This user is in Ghost mode. Connections are hidden for privacy.")
                                        } else {
                                            Text("Loading $listDialogTitle...")
                                        }"""

new_check = """                                        if (user.distanceKm > 10.0) { // Simulating Ghost Mode for faraway users
                                            Text("This user is in Ghost mode. Connections are hidden for privacy.")
                                        } else {
                                            Text("Loading $listDialogTitle...")
                                        }"""

content = content.replace(old_check, new_check)

with open('app/src/main/java/com/example/ui/components/OtherUserProfileSheet.kt', 'w') as f:
    f.write(content)

