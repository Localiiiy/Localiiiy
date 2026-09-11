import re

with open("app/src/main/java/com/example/ui/components/CreatorMonetizationHubSheet.kt", "r") as f:
    text = f.read()

start_str = "// Additional Monetization stats"
end_str = "// 5. Global Advertiser"

if start_str in text and end_str in text:
    start_idx = text.index(start_str)
    end_idx = text.index(end_str)
    text = text[:start_idx] + text[end_idx:]

with open("app/src/main/java/com/example/ui/components/CreatorMonetizationHubSheet.kt", "w") as f:
    f.write(text)
