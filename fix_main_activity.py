with open("app/src/main/java/com/example/MainActivity.kt", "r") as f:
    text = f.read()

# Only the ProfileScreen invocation should have onOpenDataAnalysis.
# I will find all instances of "onOpenDataAnalysis = { viewModel.openDataAnalysis() },"
# and remove them. Then I will add it back only to ProfileScreen.
text = text.replace("                        onOpenDataAnalysis = { viewModel.openDataAnalysis() },\n", "")
text = text.replace("                        onOpenDataAnalysis = { viewModel.openDataAnalysis() }", "")

target = """                        creatorEarnings = creatorEarnings,
                        onOpenMonetizationHub = { viewModel.openMonetizationHub() },"""
replacement = """                        creatorEarnings = creatorEarnings,
                        onOpenDataAnalysis = { viewModel.openDataAnalysis() },
                        onOpenMonetizationHub = { viewModel.openMonetizationHub() },"""
text = text.replace(target, replacement)

with open("app/src/main/java/com/example/MainActivity.kt", "w") as f:
    f.write(text)
