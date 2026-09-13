import re

with open('app/src/main/java/com/example/ui/screens/onboarding/ZeroKnowledgeOnboardingComponents.kt', 'r') as f:
    content = f.read()

content = re.sub(
    r'val defaultCoreInterests = listOf\([\s\S]*?\)',
    r'''val defaultCoreInterests = listOf(
    "Local Food & Coffee", "Indie Film & Clips", "Live Music & Gigs",
    "Tech & Open Source", "Urban Photography", "Maker Crafts",
    "Community News", "Cycling & Trails", "Visual Art & Murals", "Night Markets",
    "Funny Clips", "Education", "Fitness", "Fashion", "Gaming", "DIY & Home"
)''',
    content
)

with open('app/src/main/java/com/example/ui/screens/onboarding/ZeroKnowledgeOnboardingComponents.kt', 'w') as f:
    f.write(content)
