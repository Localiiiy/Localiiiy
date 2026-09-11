with open("app/src/main/java/com/example/ui/components/LiveRadarComponent.kt", "r") as f:
    text = f.read()

text = text.replace("MarketItemEntity", "MarketplaceItemEntity")

with open("app/src/main/java/com/example/ui/components/LiveRadarComponent.kt", "w") as f:
    f.write(text)
