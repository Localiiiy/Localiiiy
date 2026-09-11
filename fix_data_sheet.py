import re

with open("app/src/main/java/com/example/ui/components/DataAnalysisSheet.kt", "r") as f:
    text = f.read()

# I want to add Vico imports
imports = """
import com.patrykandpatrick.vico.compose.axis.horizontal.rememberBottomAxis
import com.patrykandpatrick.vico.compose.axis.vertical.rememberStartAxis
import com.patrykandpatrick.vico.compose.chart.Chart
import com.patrykandpatrick.vico.compose.chart.column.columnChart
import com.patrykandpatrick.vico.compose.chart.line.lineChart
import com.patrykandpatrick.vico.core.entry.entryModelOf
"""

text = text.replace("import androidx.compose.ui.unit.sp", "import androidx.compose.ui.unit.sp\n" + imports)

# We need to add charts to Account Reach, Studio Performance, Market Connections
reach_replacement = """AnalyticsSection(
                        title = "Account Reach Data",
                        icon = Icons.Default.Public,
                        color = Color(0xFF00BFA5)
                    ) {
                        val reachData = when (selectedFilter) {
                            "Today" -> entryModelOf(10f, 20f, 15f, 40f, 35f, 50f)
                            "Last 7 Days" -> entryModelOf(50f, 80f, 120f, 90f, 150f, 200f, 180f)
                            "Last 30 Days" -> entryModelOf(400f, 450f, 500f, 600f, 550f, 700f)
                            "Calendar Year" -> entryModelOf(10f, 15f, 12f, 20f, 25f, 30f, 28f, 35f, 40f, 42f, 48f, 50f)
                            else -> entryModelOf(50f, 60f, 70f, 85f, 100f, 120f, 140f)
                        }
                        Box(modifier = Modifier.fillMaxWidth().height(150.dp)) {
                            Chart(
                                chart = lineChart(),
                                model = reachData,
                                startAxis = rememberStartAxis(),
                                bottomAxis = rememberBottomAxis()
                            )
                        }
                        Spacer(modifier = Modifier.height(16.dp))
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            StatTile(title = "Total Profile Visits", value = "124,532", modifier = Modifier.weight(1f))
                            StatTile(title = "Global Reach", value = "14.2M", modifier = Modifier.weight(1f))
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            StatTile(title = "New Connections", value = "+4,120", modifier = Modifier.weight(1f))
                            StatTile(title = "Engagement Rate", value = "18.4%", modifier = Modifier.weight(1f))
                        }
                    }"""

text = re.sub(r'AnalyticsSection\(\s*title = "Account Reach Data".*?Engagement Rate.*?\}\s*\}', reach_replacement, text, flags=re.DOTALL)


market_replacement = """AnalyticsSection(
                        title = "Marketplace Connections",
                        icon = Icons.Default.Storefront,
                        color = Color(0xFFFF7043)
                    ) {
                        val marketData = when (selectedFilter) {
                            "Today" -> entryModelOf(2f, 4f, 3f, 6f, 5f)
                            "Last 7 Days" -> entryModelOf(10f, 15f, 12f, 20f, 25f, 30f, 28f)
                            "Last 30 Days" -> entryModelOf(50f, 60f, 55f, 80f, 90f, 100f)
                            "Calendar Year" -> entryModelOf(200f, 250f, 300f, 400f, 500f, 600f, 700f, 800f, 900f, 100f, 110f, 120f)
                            else -> entryModelOf(100f, 150f, 200f, 250f, 300f, 350f, 400f)
                        }
                        Box(modifier = Modifier.fillMaxWidth().height(150.dp)) {
                            Chart(
                                chart = columnChart(),
                                model = marketData,
                                startAxis = rememberStartAxis(),
                                bottomAxis = rememberBottomAxis()
                            )
                        }
                        Spacer(modifier = Modifier.height(16.dp))
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            StatTile(title = "Items Viewed", value = "8,430", modifier = Modifier.weight(1f))
                            StatTile(title = "Approaches/Chats", value = "342", modifier = Modifier.weight(1f))
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            StatTile(title = "Items Saved", value = "1,204", modifier = Modifier.weight(1f))
                            StatTile(title = "Successful Meets", value = "48", modifier = Modifier.weight(1f))
                        }
                    }"""

text = re.sub(r'AnalyticsSection\(\s*title = "Marketplace Connections".*?Successful Meets.*?\}\s*\}', market_replacement, text, flags=re.DOTALL)

studio_replacement = """AnalyticsSection(
                        title = "Studio Video Performance",
                        icon = Icons.Default.VideoLibrary,
                        color = Color(0xFF7E57C2)
                    ) {
                        val studioData = when (selectedFilter) {
                            "Today" -> entryModelOf(5f, 10f, 8f, 15f)
                            "Last 7 Days" -> entryModelOf(20f, 40f, 35f, 60f, 80f, 90f, 100f)
                            "Last 30 Days" -> entryModelOf(100f, 150f, 200f, 250f, 220f, 300f)
                            "Calendar Year" -> entryModelOf(50f, 70f, 90f, 120f, 150f, 180f, 200f, 250f, 280f, 300f, 320f, 350f)
                            else -> entryModelOf(200f, 300f, 400f, 500f, 600f, 750f, 800f)
                        }
                        Box(modifier = Modifier.fillMaxWidth().height(150.dp)) {
                            Chart(
                                chart = lineChart(),
                                model = studioData,
                                startAxis = rememberStartAxis(),
                                bottomAxis = rememberBottomAxis()
                            )
                        }
                        Spacer(modifier = Modifier.height(16.dp))
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            StatTile(title = "Total Views", value = "2.4M", modifier = Modifier.weight(1f))
                            StatTile(title = "Total Likes", value = "112K", modifier = Modifier.weight(1f))
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            StatTile(title = "Hours Watched", value = "45,200", modifier = Modifier.weight(1f))
                            StatTile(title = "Avg View Duration", value = "4m 12s", modifier = Modifier.weight(1f))
                        }
                    }"""
text = re.sub(r'AnalyticsSection\(\s*title = "Studio Video Performance".*?Avg View Duration.*?\}\s*\}', studio_replacement, text, flags=re.DOTALL)

with open("app/src/main/java/com/example/ui/components/DataAnalysisSheet.kt", "w") as f:
    f.write(text)
