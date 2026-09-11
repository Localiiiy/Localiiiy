with open("app/src/main/java/com/example/ui/screens/ProfileScreen.kt", "r") as f:
    text = f.read()

text = text.replace("onOpenDataAnalysis: () -> Unit = {},\n    onOpenDataAnalysis: () -> Unit = {},", "onOpenDataAnalysis: () -> Unit = {},")

with open("app/src/main/java/com/example/ui/screens/ProfileScreen.kt", "w") as f:
    f.write(text)
