package com.example.whoossh.ui.screens

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.detectVerticalDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.whoossh.api.ApiClient
import com.example.whoossh.api.Country
import com.example.whoossh.api.CountryResponse
import com.example.whoossh.ui.components.WhooshTopBar
import com.example.whoossh.ui.theme.WhooshRed
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SelectCountryScreen(
    onCountrySelected: (String) -> Unit,
    onBack: () -> Unit
) {
    var searchQuery by remember { mutableStateOf("") }
    var allCountries by remember { mutableStateOf<List<Country>>(emptyList()) }
    var priorityCountries by remember { mutableStateOf(fallbackPriorityCountries) }
    var isLoading by remember { mutableStateOf(true) }
    val listState = rememberLazyListState()
    val scope = rememberCoroutineScope()

    // Fetch countries from API
    LaunchedEffect(Unit) {
        try {
            val response = withContext(Dispatchers.IO) {
                ApiClient.apiService.getCountries()
            }
            if (response.isSuccessful && response.body()?.status == "success") {
                val data: List<CountryResponse> = response.body()?.data ?: emptyList()
                allCountries = data.filter { it.isPriority.not() }.map { c ->
                    Country(c.name, c.flag, c.code, false)
                }
                priorityCountries = data.filter { it.isPriority }.map { c ->
                    Country(c.name, c.flag, c.code, true)
                }
            }
        } catch (e: Exception) {
            Log.e("SelectCountry", "API error: ${e.message}")
        }
        isLoading = false
    }

    val filteredAll = remember(searchQuery, allCountries) {
        if (searchQuery.isEmpty()) allCountries
        else allCountries.filter { it.name.contains(searchQuery, ignoreCase = true) }
    }

    val filteredPriority = remember(searchQuery, priorityCountries) {
        if (searchQuery.isEmpty()) priorityCountries
        else priorityCountries.filter { it.name.contains(searchQuery, ignoreCase = true) }
    }

    val grouped = remember(filteredAll) {
        filteredAll.groupBy { it.name.first().uppercaseChar() }.toSortedMap()
    }

    // Build flat list for index mapping
    data class ListItem(val type: String, val label: String = "", val country: Country? = null)

    val flatItems = remember(filteredPriority, grouped) {
        buildList {
            if (filteredPriority.isNotEmpty()) {
                add(ListItem("header", "Popular"))
                filteredPriority.forEach { add(ListItem("country", country = it)) }
            }
            grouped.forEach { (letter, countries) ->
                add(ListItem("header", letter.toString()))
                countries.forEach { add(ListItem("country", country = it)) }
            }
        }
    }

    // Letter index mapping: letter -> position in flatItems
    val letterPositions = remember(flatItems) {
        val map = mutableMapOf<String, Int>()
        flatItems.forEachIndexed { index, item ->
            if (item.type == "header" && item.label != "Popular") {
                map[item.label] = index
            }
        }
        map
    }

    val availableLetters = remember(letterPositions) {
        ('A'..'Z').map { it.toString() }.filter { letterPositions.containsKey(it) }
    }

    Scaffold(
        topBar = {
            Column {
                WhooshTopBar(title = "Select Country/Region", onBack = onBack)
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color.White)
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                ) {
                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = { searchQuery = it },
                        modifier = Modifier.fillMaxWidth(),
                        placeholder = { Text("Search country...", fontSize = 14.sp) },
                        leadingIcon = { Icon(Icons.Default.Search, null, tint = Color.Gray) },
                        trailingIcon = {
                            if (searchQuery.isNotEmpty()) {
                                IconButton(onClick = { searchQuery = "" }) {
                                    Icon(Icons.Default.Close, null, tint = Color.Gray)
                                }
                            }
                        },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = WhooshRed,
                            unfocusedBorderColor = Color(0xFFE0E0E0),
                            cursorColor = WhooshRed
                        ),
                        shape = RoundedCornerShape(8.dp),
                        singleLine = true,
                        textStyle = LocalTextStyle.current.copy(fontSize = 14.sp)
                    )
                }
            }
        }
    ) { paddingValues ->
        if (isLoading) {
            Box(Modifier.fillMaxSize().padding(paddingValues), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = WhooshRed)
            }
        } else {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .background(Color.White)
            ) {
                // Main list
                LazyColumn(
                    state = listState,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(end = 24.dp)
                ) {
                    items(flatItems.size) { index ->
                        val item = flatItems[index]
                        when (item.type) {
                            "header" -> SectionLabel(item.label)
                            "country" -> item.country?.let { c ->
                                CountryRow(c) { onCountrySelected(c.name) }
                            }
                        }
                    }
                }

                // Alphabet sidebar
                if (searchQuery.isEmpty() && availableLetters.isNotEmpty()) {
                    var sidebarHeight by remember { mutableIntStateOf(0) }

                    Column(
                        modifier = Modifier
                            .align(Alignment.CenterEnd)
                            .width(24.dp)
                            .onGloballyPositioned { sidebarHeight = it.size.height }
                            .pointerInput(availableLetters, letterPositions) {
                                detectTapGestures { offset ->
                                    val idx = (offset.y / sidebarHeight * availableLetters.size)
                                        .toInt()
                                        .coerceIn(0, availableLetters.lastIndex)
                                    val letter = availableLetters[idx]
                                    letterPositions[letter]?.let { pos ->
                                        scope.launch { listState.scrollToItem(pos) }
                                    }
                                }
                            }
                            .pointerInput(availableLetters, letterPositions) {
                                detectVerticalDragGestures { change, _ ->
                                    change.consume()
                                    val idx = (change.position.y / sidebarHeight * availableLetters.size)
                                        .toInt()
                                        .coerceIn(0, availableLetters.lastIndex)
                                    val letter = availableLetters[idx]
                                    letterPositions[letter]?.let { pos ->
                                        scope.launch { listState.scrollToItem(pos) }
                                    }
                                }
                            },
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.SpaceEvenly
                    ) {
                        availableLetters.forEach { letter ->
                            Text(
                                text = letter,
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold,
                                color = WhooshRed,
                                textAlign = TextAlign.Center,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 1.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun SectionLabel(text: String) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFFF5F5F5))
            .padding(horizontal = 16.dp, vertical = 6.dp)
    ) {
        Text(text, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Color(0xFF888888))
    }
}

@Composable
private fun CountryRow(country: Country, onClick: () -> Unit) {
    Column {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable(onClick = onClick)
                .padding(horizontal = 16.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = country.flag, fontSize = 22.sp)
            Spacer(modifier = Modifier.width(14.dp))
            Text(country.name, fontSize = 15.sp, color = Color.Black, fontWeight = FontWeight.Medium)
        }
        HorizontalDivider(color = Color(0xFFF0F0F0), thickness = 1.dp, modifier = Modifier.padding(horizontal = 16.dp))
    }
}

private val fallbackPriorityCountries = listOf(
    Country("Brunei Darussalam", "\uD83C\uDDE7\uD83C\uDDF3", "BN", true),
    Country("Cambodia", "\uD83C\uDDF0\uD83C\uDDED", "KH", true),
    Country("China", "\uD83C\uDDE8\uD83C\uDDF3", "CN", true),
    Country("Indonesia", "\uD83C\uDDEE\uD83C\uDDE9", "ID", true),
    Country("Laos", "\uD83C\uDDF1\uD83C\uDDE6", "LA", true),
    Country("Malaysia", "\uD83C\uDDF2\uD83C\uDDFE", "MY", true),
    Country("Myanmar", "\uD83C\uDDF2\uD83C\uDDF2", "MM", true),
    Country("Philippines", "\uD83C\uDDF5\uD83C\uDDED", "PH", true),
    Country("Singapore", "\uD83C\uDDF8\uD83C\uDDEC", "SG", true),
    Country("Thailand", "\uD83C\uDDF9\uD83C\uDDED", "TH", true),
    Country("Timor-Leste", "\uD83C\uDDF9\uD83C\uDDF1", "TL", true),
    Country("Vietnam", "\uD83C\uDDFB\uD83C\uDDF3", "VN", true),
)
