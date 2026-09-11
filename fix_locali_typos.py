import re
import os

files_to_fix = [
    "app/src/main/java/com/example/ui/components/StoriesTray.kt",
    "app/src/main/java/com/example/ui/components/StudioAnalyticsComponent.kt",
    "app/src/main/java/com/example/ui/components/StoryViewerDialog.kt",
    "app/src/main/java/com/example/ui/components/SponsoredAdCard.kt"
]

replacements = {
    "LocaliSeenStoryGradient": "LocaliiiySeenStoryGradient",
    "LocaliStoryGradient": "LocaliiiyStoryGradient",
    "LocaliAccentMint": "LocaliiiyAccentMint",
    "LocaliDeepNavy": "LocaliiiyDeepNavy",
    "LocaliHeart": "LocaliiiyHeart",
    "LocaliPrimaryTeal": "LocaliiiyPrimaryTeal",
    "LocaliCurrency": "LocaliiiyCurrency",
    "LocaliLanguage": "LocaliiiyLanguage",
    "LocaliStringKey": "LocaliiiyStringKey"
}

for filepath in files_to_fix:
    if os.path.exists(filepath):
        with open(filepath, 'r') as f:
            content = f.read()
        for k, v in replacements.items():
            content = content.replace(k, v)
        with open(filepath, 'w') as f:
            f.write(content)

