import os
import re

files_to_fix = [
    "app/src/main/java/com/example/ui/screens/StudioScreen.kt",
    "app/src/main/java/com/example/ui/screens/PrivacySettingsScreen.kt",
    "app/src/main/java/com/example/ui/components/DirectMessagesBottomSheet.kt",
    "app/src/main/java/com/example/ui/components/PulseFeedComponent.kt",
    "app/src/main/java/com/example/ui/components/DataAnalysisSheet.kt",
    "app/src/main/java/com/example/ui/components/HelpSheet.kt",
    "app/src/main/java/com/example/ui/components/InformationSheet.kt"
]

replacements = {
    "Creator Followers": "Creator Connections",
    "PEOPLE_YOU_FOLLOW": "PEOPLE_YOU_ARE_CONNECTED_WITH",
    "\"FOLLOWERS\"": "\"CONNECTIONS\"",
    "who follow you": "who are connected with you",
    "people you follow": "people you are connected with",
    "New Followers": "New Connections",
    "views and followers": "views and connections",
    "follower milestones": "connection milestones"
}

for filepath in files_to_fix:
    if os.path.exists(filepath):
        with open(filepath, 'r') as f:
            content = f.read()
        for k, v in replacements.items():
            content = content.replace(k, v)
        with open(filepath, 'w') as f:
            f.write(content)
