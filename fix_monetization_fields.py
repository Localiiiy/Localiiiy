with open("app/src/main/java/com/example/ui/components/CreatorMonetizationHubSheet.kt", "r") as f:
    text = f.read()

text = text.replace("earnings.inStreamAdUSD", "earnings.inStreamVideoAdUSD")
text = text.replace("earnings.fanTipsUSD", "earnings.superThanksTipsUSD")

with open("app/src/main/java/com/example/ui/components/CreatorMonetizationHubSheet.kt", "w") as f:
    f.write(text)
