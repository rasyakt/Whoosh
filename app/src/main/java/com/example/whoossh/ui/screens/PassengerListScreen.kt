package com.example.whoossh.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.whoossh.model.Passenger
import com.example.whoossh.ui.components.WhooshButton
import com.example.whoossh.ui.components.WhooshTopBar
import com.example.whoossh.ui.theme.WhooshRed
import com.example.whoossh.ui.theme.WhooshTextSecondary
import com.example.whoossh.viewmodel.BookingViewModel

@Composable
fun PassengerListScreen(
    viewModel: BookingViewModel,
    onDone: () -> Unit,
    onAddPassenger: () -> Unit,
    onEditPassenger: (Passenger) -> Unit,
    onBack: () -> Unit
) {
    val selectedPassengers by viewModel.selectedPassengers.collectAsState()
    val savedPassengers by viewModel.savedPassengers.collectAsState()
    val maxPassengers = 15

    // Auto-refresh data when entering screen
    LaunchedEffect(Unit) {
        viewModel.refreshSavedPassengers()
    }

    Scaffold(
        topBar = {
            WhooshTopBar(
                title = "Passenger",
                onBack = onBack
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(MaterialTheme.colorScheme.background)
        ) {
            // Header info with Done button
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFFFFF0F0))
                    .padding(horizontal = 16.dp, vertical = 10.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "${selectedPassengers.size}/$maxPassengers penumpang",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = WhooshRed,
                    modifier = Modifier.weight(1f)
                )
                Button(
                    onClick = onDone,
                    modifier = Modifier
                        .height(32.dp)
                        .wrapContentWidth(),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = WhooshRed
                    ),
                    shape = RoundedCornerShape(6.dp),
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 0.dp)
                ) {
                    Text(
                        text = "Done",
                        color = Color.White,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 12.sp
                    )
                }
            }

            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 20.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                contentPadding = PaddingValues(vertical = 16.dp)
            ) {
                // Add Passenger Button
                item {
                    Card(
                        onClick = {
                            if (selectedPassengers.size < maxPassengers) {
                                onAddPassenger()
                            }
                        },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = Color.White
                        ),
                        border = BorderStroke(1.dp, Color(0xFFEEEEEE)),
                        enabled = selectedPassengers.size < maxPassengers
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.AddCircleOutline,
                                contentDescription = null,
                                tint = WhooshRed,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Add Passenger",
                                color = WhooshRed,
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 15.sp
                            )
                        }
                    }
                }

                // Selected Passengers
                items(selectedPassengers) { passenger ->
                    PassengerCard(
                        passenger = passenger,
                        isSelected = true,
                        onToggleSelect = { viewModel.removePassenger(passenger) },
                        onEdit = { onEditPassenger(passenger) },
                        onDelete = { viewModel.removePassenger(passenger) }
                    )
                }

                // Saved Passengers (not yet selected)
                items(savedPassengers.filter { saved ->
                    selectedPassengers.none { it.id == saved.id }
                }) { passenger ->
                    PassengerCard(
                        passenger = passenger,
                        isSelected = false,
                        onToggleSelect = {
                            if (selectedPassengers.size < maxPassengers) {
                                viewModel.addPassenger(passenger)
                            }
                        },
                        onEdit = { onEditPassenger(passenger) },
                        onDelete = { viewModel.deleteSavedPassenger(passenger) }
                    )
                }
            }
        }
    }
}

@Composable
private fun PassengerCard(
    passenger: Passenger,
    isSelected: Boolean,
    onToggleSelect: () -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    var showMenu by remember { mutableStateOf(false) }

    Card(
        onClick = onToggleSelect,
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) Color(0xFFFFF5F5) else Color.White
        ),
        border = BorderStroke(
            width = if (isSelected) 1.5.dp else 1.dp,
            color = if (isSelected) WhooshRed else Color(0xFFEEEEEE)
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Checkbox
            Box(
                modifier = Modifier
                    .size(24.dp)
                    .clip(RoundedCornerShape(4.dp))
                    .border(
                        width = 2.dp,
                        color = if (isSelected) WhooshRed else Color(0xFFCCCCCC),
                        shape = RoundedCornerShape(4.dp)
                    )
                    .background(if (isSelected) WhooshRed else Color.Transparent),
                contentAlignment = Alignment.Center
            ) {
                if (isSelected) {
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.width(12.dp))

            // Passenger Info
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = passenger.name,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 15.sp,
                        color = Color.Black
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Box(
                        modifier = Modifier
                            .background(
                                color = Color(0xFFF0F0F0),
                                shape = RoundedCornerShape(4.dp)
                            )
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = passenger.passengerType,
                            fontSize = 10.sp,
                            color = WhooshTextSecondary
                        )
                    }
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Identity No.  ${passenger.identityNo.take(4)}****${passenger.identityNo.takeLast(2)}",
                    fontSize = 13.sp,
                    color = WhooshTextSecondary
                )
            }

            // Edit Icon
            Box {
                IconButton(onClick = { showMenu = true }) {
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = "Edit",
                        tint = WhooshTextSecondary,
                        modifier = Modifier.size(20.dp)
                    )
                }

                DropdownMenu(
                    expanded = showMenu,
                    onDismissRequest = { showMenu = false }
                ) {
                    DropdownMenuItem(
                        text = { Text("Edit") },
                        onClick = {
                            showMenu = false
                            onEdit()
                        },
                        leadingIcon = {
                            Icon(Icons.Default.Edit, contentDescription = null)
                        }
                    )
                    DropdownMenuItem(
                        text = { Text("Delete", color = Color.Red) },
                        onClick = {
                            showMenu = false
                            onDelete()
                        },
                        leadingIcon = {
                            Icon(
                                Icons.Default.Delete,
                                contentDescription = null,
                                tint = Color.Red
                            )
                        }
                    )
                }
            }
        }
    }
}
