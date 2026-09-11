with open("app/src/main/java/com/example/ui/components/PulseFeedComponent.kt", "r") as f:
    text = f.read()

# Fix ClipCard text
text = text.replace('Text(text = "Clip by ${clip.username}",', 'Text(text = clip.username,')

# Fix MarketCard text
text = text.replace('Text(text = "from ${item.sellerUsername}",', 'Text(text = "@${item.sellerUsername}",')

with open("app/src/main/java/com/example/ui/components/PulseFeedComponent.kt", "w") as f:
    f.write(text)
