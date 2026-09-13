import re

with open('app/src/main/java/com/example/ui/screens/MarketScreen.kt', 'r') as f:
    content = f.read()

new_badge = """
                // Price / Barter / Service Badge (Bottom Left)
                val isBarter = item.price == 0.0 || item.category.contains("Barter", ignoreCase = true)
                val isService = item.category.contains("Service", ignoreCase = true)
                
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = if (isBarter) Color(0xFF0284C7) else if (isService) Color(0xFF9C27B0) else MaterialTheme.colorScheme.primary,
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .padding(6.dp)
                ) {
                    val priceText = when {
                        isBarter -> "🔄 Barter"
                        isService -> CurrencyHelper.format(item.price, currentCurrency) + "/hr"
                        else -> CurrencyHelper.format(item.price, currentCurrency)
                    }
                    Text(
                        text = priceText,
                        fontSize = if (isBarter) 10.5.sp else 13.sp,
                        fontWeight = FontWeight.Black,
                        color = Color.White,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
"""

content = re.sub(
    r'// Price / Barter Badge \(Bottom Left\)[\s\S]*?Modifier\.padding\(horizontal = 6\.dp, vertical = 2\.dp\)\s*\)\s*\}\s*',
    new_badge,
    content
)

with open('app/src/main/java/com/example/ui/screens/MarketScreen.kt', 'w') as f:
    f.write(content)
