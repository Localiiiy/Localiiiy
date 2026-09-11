import re

with open("app/src/main/java/com/example/ui/components/LiveRadarComponent.kt", "r") as f:
    text = f.read()

# Remove the Nearby Pulse Items section from LiveRadarComponent
start_str = "Spacer(modifier = Modifier.height(16.dp))"
end_str = "}\n}\n\n@Composable\nfun NearbyCard"

if start_str in text and end_str in text:
    start_idx = text.index(start_str)
    end_idx = text.index(end_str)
    # also remove NearbyCard function completely
    text = text[:start_idx] + text[end_idx:]
    text = re.sub(r'@Composable\nfun NearbyCard.*?}\n}\n', '', text, flags=re.DOTALL)

with open("app/src/main/java/com/example/ui/components/LiveRadarComponent.kt", "w") as f:
    f.write(text)
