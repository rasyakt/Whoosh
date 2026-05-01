package com.example.whoossh.ui.screens

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Train
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.whoossh.ui.theme.WhooshRed
import com.example.whoossh.ui.theme.WhooshWhite

@Composable
fun SchedulesScreen() {
    var selectedTab by remember { mutableStateOf(0) }
    val tabs = listOf("Jadwal Utama", "Integrasi KA Feeder")

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(WhooshWhite)
    ) {
        // Simple Header
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 40.dp, bottom = 16.dp, start = 20.dp, end = 20.dp)
        ) {
            Text(
                text = "Jadwal Perjalanan",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF1A1A1A)
            )
        }

        // Custom Tabs
        ScrollableTabRow(
            selectedTabIndex = selectedTab,
            containerColor = WhooshWhite,
            contentColor = WhooshRed,
            edgePadding = 20.dp,
            indicator = { tabPositions ->
                TabRowDefaults.Indicator(
                    modifier = Modifier.tabIndicatorOffset(tabPositions[selectedTab]),
                    color = WhooshRed,
                    height = 3.dp
                )
            },
            divider = { HorizontalDivider(color = Color(0xFFF0F0F0)) }
        ) {
            tabs.forEachIndexed { index, title ->
                Tab(
                    selected = selectedTab == index,
                    onClick = { selectedTab = index },
                    text = {
                        Text(
                            text = title,
                            fontSize = 14.sp,
                            fontWeight = if (selectedTab == index) FontWeight.Bold else FontWeight.Medium,
                            color = if (selectedTab == index) WhooshRed else Color.Gray
                        )
                    }
                )
            }
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {
            if (selectedTab == 0) {
                WhooshMainSchedule()
            } else {
                FeederIntegrationSchedule()
            }
            
            Spacer(modifier = Modifier.height(32.dp))
            RouteMapSection()
            Spacer(modifier = Modifier.height(40.dp))
        }
    }
}

@Composable
private fun WhooshMainSchedule() {
    Column {
        ScheduleTableHeader("Halim \u2192 Tegalluar Summarecon")
        ScheduleTable(
            headers = listOf("TRAIN", "Halim", "Karawang", "Padalarang", "Tegalluar"),
            rows = listOf(
                listOf("G1003", "06:25", "06:42", "07:05", "07:19"),
                listOf("G1005", "07:00", "", "07:33", "07:47"),
                listOf("G1007", "07:25", "06:42", "08:05", "08:19"),
                listOf("G1009", "08:00", "", "08:33", "08:47"),
                listOf("G1011", "08:25", "08:42", "09:05", "09:19"),
                listOf("G1013", "09:00", "", "09:33", "09:47"),
                listOf("G1015", "09:25", "09:42", "10:05", "10:19"),
                listOf("G1017", "10:00", "", "10:33", "10:47"),
                listOf("G1019", "10:25", "10:42", "11:05", "11:19"),
                listOf("G1021", "11:00", "", "11:33", "11:47"),
                listOf("G1023", "11:25", "11:42", "12:05", "12:19"),
                listOf("G1025", "12:00", "", "12:33", "12:47"),
                listOf("G1027", "12:25", "12:42", "13:05", "13:19"),
                listOf("G1029", "13:00", "", "13:33", "13:47"),
                listOf("G1031", "13:25", "13:42", "14:05", "14:19"),
                listOf("G1033", "14:00", "", "14:33", "14:47"),
                listOf("G1035", "14:25", "14:42", "15:05", "15:19"),
                listOf("G1037", "15:00", "", "15:33", "15:47"),
                listOf("G1039", "15:25", "15:42", "16:05", "16:19"),
                listOf("G1041", "16:00", "", "16:33", "16:47"),
                listOf("G1043", "16:25", "16:42", "17:05", "17:19"),
                listOf("G1045", "17:00", "", "17:33", "17:47"),
                listOf("G1047", "17:25", "17:42", "18:05", "18:19"),
                listOf("G1049", "18:00", "", "18:33", "18:47"),
                listOf("G1051", "18:25", "18:42", "19:05", "19:19"),
                listOf("G1053", "19:00", "", "19:33", "19:47"),
                listOf("G1055", "19:25", "19:42", "20:05", "20:19"),
                listOf("G1057", "20:00", "", "20:33", "20:47"),
                listOf("G1059", "20:25", "20:42", "21:05", "21:19"),
                listOf("G1061", "21:00", "", "21:33", "21:47"),
                listOf("G1063", "21:25", "21:42", "22:05", "22:19")
            )
        )

        Spacer(modifier = Modifier.height(24.dp))

        ScheduleTableHeader("Tegalluar Summarecon \u2192 Halim")
        ScheduleTable(
            headers = listOf("TRAIN", "Tegalluar", "Padalarang", "Karawang", "Halim"),
            rows = listOf(
                listOf("G1004", "06:05", "06:23", "", "06:52"),
                listOf("G1006", "06:35", "06:53", "07:14", "07:29"),
                listOf("G1008", "07:05", "07:23", "", "07:52"),
                listOf("G1010", "07:35", "07:53", "08:14", "08:29"),
                listOf("G1012", "08:05", "08:23", "", "08:52"),
                listOf("G1014", "08:35", "08:53", "09:14", "09:29"),
                listOf("G1016", "09:05", "09:23", "", "09:52"),
                listOf("G1018", "09:35", "09:53", "10:14", "10:29"),
                listOf("G1020", "10:05", "10:23", "", "10:52"),
                listOf("G1022", "10:35", "10:53", "11:14", "11:29"),
                listOf("G1024", "11:05", "11:23", "", "11:52"),
                listOf("G1026", "11:35", "11:53", "12:14", "12:29"),
                listOf("G1028", "12:05", "12:23", "", "12:52"),
                listOf("G1030", "12:35", "12:53", "13:14", "13:29"),
                listOf("G1032", "13:05", "13:23", "", "13:52"),
                listOf("G1034", "13:35", "13:53", "14:14", "14:29"),
                listOf("G1036", "14:05", "14:23", "", "14:52"),
                listOf("G1038", "14:35", "14:53", "15:14", "15:29"),
                listOf("G1040", "15:05", "15:23", "", "15:52"),
                listOf("G1042", "15:35", "15:53", "16:14", "16:29"),
                listOf("G1044", "16:05", "16:23", "", "16:52"),
                listOf("G1046", "16:35", "16:53", "17:14", "17:29"),
                listOf("G1048", "17:05", "17:23", "", "17:52"),
                listOf("G1050", "17:35", "17:53", "18:14", "18:29"),
                listOf("G1052", "18:05", "18:23", "", "18:52"),
                listOf("G1054", "18:35", "18:53", "19:14", "19:29"),
                listOf("G1056", "19:05", "19:23", "", "19:52"),
                listOf("G1058", "19:35", "19:53", "20:14", "20:29"),
                listOf("G1060", "20:05", "20:23", "", "20:52"),
                listOf("G1062", "20:35", "20:53", "21:14", "21:29"),
                listOf("G1064", "21:05", "21:23", "", "21:52")
            )
        )
        
        Spacer(modifier = Modifier.height(12.dp))
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(modifier = Modifier.size(8.dp).clip(CircleShape).background(Color(0xFFFFA000)))
            Spacer(modifier = Modifier.width(8.dp))
            Text("Beroperasi pada hari Senin-Sabtu", fontSize = 11.sp, color = Color.Gray)
        }
    }
}

@Composable
private fun FeederIntegrationSchedule() {
    Column {
        ScheduleTableHeader("Integrasi Whoosh & KA Feeder (Halim \u2192 Bandung)")
        ScheduleTable(
            headers = listOf("Whoosh", "Halim", "Padalarang", "Feeder", "Cimahi", "Bandung"),
            rows = listOf(
                listOf("G1003", "06:25", "07:05", "07:11", "07:18", "07:29"),
                listOf("G1005", "07:00", "07:30", "07:41", "07:48", "07:59"),
                listOf("G1007", "07:25", "08:05", "08:11", "08:18", "08:29"),
                listOf("G1009", "08:00", "08:30", "08:41", "08:48", "08:59"),
                listOf("G1011", "08:25", "09:05", "09:11", "09:18", "09:29"),
                listOf("G1013", "09:00", "09:30", "09:41", "09:48", "09:59"),
                listOf("G1015", "09:25", "10:05", "10:11", "10:18", "10:29"),
                listOf("G1017", "10:00", "10:30", "10:41", "10:48", "10:59"),
                listOf("G1019", "10:25", "11:05", "11:11", "11:18", "11:29"),
                listOf("G1021", "11:00", "11:30", "11:41", "11:48", "11:59"),
                listOf("G1023", "11:25", "12:05", "12:11", "12:18", "12:29"),
                listOf("G1025", "12:00", "12:30", "12:41", "12:48", "12:59"),
                listOf("G1027", "12:25", "13:05", "13:11", "13:18", "13:29"),
                listOf("G1029", "13:00", "13:30", "13:41", "13:48", "13:59"),
                listOf("G1031", "13:25", "14:05", "14:11", "14:18", "14:29"),
                listOf("G1033", "14:00", "14:30", "14:41", "14:48", "14:59"),
                listOf("G1035", "14:25", "15:05", "15:11", "15:18", "15:29"),
                listOf("G1037", "15:00", "15:30", "15:41", "15:48", "15:59"),
                listOf("G1039", "15:25", "16:05", "16:11", "16:18", "16:29"),
                listOf("G1041", "16:00", "16:30", "16:41", "16:48", "16:59"),
                listOf("G1043", "16:25", "17:05", "17:11", "17:18", "17:29"),
                listOf("G1045", "17:00", "17:30", "17:41", "17:48", "17:59"),
                listOf("G1047", "17:25", "18:05", "18:11", "18:18", "18:29"),
                listOf("G1049", "18:00", "18:30", "18:41", "18:48", "18:59"),
                listOf("G1051", "18:25", "19:05", "19:11", "19:18", "19:29"),
                listOf("G1053", "19:00", "19:30", "19:41", "19:48", "19:59"),
                listOf("G1055", "19:25", "20:05", "20:11", "20:18", "20:29"),
                listOf("G1057", "20:00", "20:30", "20:41", "20:48", "20:59"),
                listOf("G1059", "20:25", "21:05", "21:11", "21:18", "21:29"),
                listOf("G1061", "21:00", "21:30", "21:41", "21:48", "21:59"),
                listOf("G1063", "21:25", "22:02", "22:11", "22:18", "22:29")
            )
        )

        Spacer(modifier = Modifier.height(24.dp))

        ScheduleTableHeader("Integrasi KA Feeder & Whoosh (Bandung \u2192 Halim)")
        ScheduleTable(
            headers = listOf("Feeder", "Bandung", "Cimahi", "Padalarang", "Whoosh", "Halim"),
            rows = listOf(
                listOf("F-", "05:46", "05:58", "06:04", "06:23", "06:52"),
                listOf("F-", "06:16", "06:28", "06:34", "06:53", "07:29"),
                listOf("F-", "06:46", "06:58", "07:04", "07:23", "07:52"),
                listOf("F-", "07:16", "07:28", "07:34", "07:53", "08:29"),
                listOf("F-", "07:46", "07:58", "08:04", "08:23", "08:52"),
                listOf("F-", "08:16", "08:28", "08:34", "08:53", "09:29"),
                listOf("F-", "08:46", "08:58", "09:04", "09:23", "09:52"),
                listOf("F-", "09:16", "09:28", "09:34", "09:53", "10:29"),
                listOf("F-", "09:46", "09:58", "10:04", "10:23", "10:52"),
                listOf("F-", "10:16", "10:28", "10:34", "10:53", "11:29"),
                listOf("F-", "10:46", "10:58", "11:04", "11:23", "11:52"),
                listOf("F-", "11:16", "11:28", "11:34", "11:53", "12:29"),
                listOf("F-", "11:46", "11:58", "12:04", "12:23", "12:52"),
                listOf("F-", "12:16", "12:28", "12:34", "12:53", "13:29"),
                listOf("F-", "12:46", "12:58", "13:04", "13:23", "13:52"),
                listOf("F-", "13:16", "13:28", "13:34", "13:53", "14:29"),
                listOf("F-", "13:46", "13:58", "14:04", "14:23", "14:52"),
                listOf("F-", "14:16", "14:28", "14:34", "14:53", "15:29"),
                listOf("F-", "14:46", "14:58", "15:04", "15:23", "15:52"),
                listOf("F-", "15:16", "15:28", "15:34", "15:53", "16:29"),
                listOf("F-", "15:46", "15:58", "16:04", "16:23", "16:52"),
                listOf("F-", "16:16", "16:28", "16:34", "16:53", "17:29"),
                listOf("F-", "16:46", "16:58", "17:04", "17:23", "17:52"),
                listOf("F-", "17:16", "17:28", "17:34", "17:53", "18:29"),
                listOf("F-", "17:46", "17:58", "18:04", "18:23", "18:52"),
                listOf("F-", "18:16", "18:28", "18:34", "18:53", "19:29"),
                listOf("F-", "18:46", "18:58", "19:04", "19:23", "19:52"),
                listOf("F-", "19:16", "19:28", "19:34", "19:53", "20:29"),
                listOf("F-", "19:46", "19:58", "20:04", "20:23", "20:52"),
                listOf("F-", "20:16", "20:28", "20:34", "20:53", "21:29"),
                listOf("F-", "20:46", "20:58", "21:04", "21:23", "21:52")
            )
        )
    }
}

@Composable
private fun ScheduleTableHeader(title: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(Icons.Filled.Train, null, tint = Color(0xFF1A1A1A), modifier = Modifier.size(20.dp))
        Spacer(modifier = Modifier.width(8.dp))
        Text(title, fontSize = 15.sp, fontWeight = FontWeight.Bold, color = Color(0xFF1A1A1A))
    }
}

@Composable
private fun ScheduleTable(headers: List<String>, rows: List<List<String>>) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, Color(0xFFEEEEEE), RoundedCornerShape(8.dp))
            .clip(RoundedCornerShape(8.dp))
    ) {
        // Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(WhooshRed)
                .padding(vertical = 10.dp, horizontal = 4.dp)
        ) {
            headers.forEach { header ->
                Text(
                    text = header,
                    modifier = Modifier.weight(1f),
                    color = WhooshWhite,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center
                )
            }
        }

        // Rows
        rows.forEachIndexed { index, row ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(if (index % 2 == 0) WhooshWhite else Color(0xFFF9F9F9))
                    .padding(vertical = 10.dp, horizontal = 4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                row.forEachIndexed { colIndex, cell ->
                    Text(
                        text = cell.ifEmpty { "-" },
                        modifier = Modifier.weight(1f),
                        color = if (colIndex == 0) Color(0xFF1A1A1A) else Color(0xFF444444),
                        fontSize = 11.sp,
                        fontWeight = if (colIndex == 0) FontWeight.Bold else FontWeight.Medium,
                        textAlign = TextAlign.Center
                    )
                }
            }
            if (index < rows.size - 1) {
                HorizontalDivider(color = Color(0xFFEEEEEE))
            }
        }
    }
}

@Composable
private fun RouteMapSection() {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text("Peta Rute Whoosh", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color(0xFF1A1A1A))
        Spacer(modifier = Modifier.height(20.dp))
        
        Box(modifier = Modifier.fillMaxWidth().height(80.dp), contentAlignment = Alignment.Center) {
            val stations = listOf("Halim", "Karawang", "Padalarang", "Tegalluar")
            
            Canvas(modifier = Modifier.fillMaxWidth().height(4.dp).padding(horizontal = 40.dp)) {
                drawLine(
                    color = WhooshRed,
                    start = Offset(0f, size.height / 2),
                    end = Offset(size.width, size.height / 2),
                    strokeWidth = 8f
                )
            }
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                stations.forEach { station ->
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Box(
                            modifier = Modifier
                                .size(12.dp)
                                .clip(CircleShape)
                                .background(WhooshWhite)
                                .border(2.dp, WhooshRed, CircleShape)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(station, fontSize = 10.sp, fontWeight = FontWeight.Medium, color = Color.Gray)
                    }
                }
            }
        }
        
        Text(
            "Update: 12.03.2026",
            fontSize = 10.sp,
            color = Color.LightGray,
            modifier = Modifier.align(Alignment.End).padding(top = 8.dp)
        )
    }
}
