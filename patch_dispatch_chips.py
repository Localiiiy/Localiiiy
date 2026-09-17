import os

fpath = "app/src/main/java/com/example/ui/components/radar/RadarSpatialDiscoveryComponents.kt"
with open(fpath, "r") as f:
    content = f.read()

old_chips = """                listOf(
                    "Wave 👋",
                    "Coffee nearby? ☕",
                    "What's happening? 📍"
                ).forEach { preset ->"""

new_chips = """                listOf(
                    "Wave 👋",
                    "Coffee? ☕",
                    "What's happening? 📍",
                    "Connect Request 🤝"
                ).forEach { preset ->"""

content = content.replace(old_chips, new_chips)

with open(fpath, "w") as f:
    f.write(content)
