package com.example.whoossh.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.whoossh.model.Passenger
import com.example.whoossh.ui.components.WhooshButton
import com.example.whoossh.ui.components.WhooshTopBar
import com.example.whoossh.ui.theme.WhooshRed
import com.example.whoossh.ui.theme.WhooshTextSecondary
import com.example.whoossh.viewmodel.BookingViewModel
import com.example.whoossh.utils.tr

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEditPassengerScreen(
    viewModel: BookingViewModel,
    passenger: Passenger? = null,
    onSave: () -> Unit,
    onSelectCountry: () -> Unit,
    onBack: () -> Unit,
    navController: androidx.navigation.NavController? = null
) {
    val isLoggedIn = viewModel.isLoggedIn
    val isFirstPassenger = viewModel.selectedPassengers.collectAsState().value.isEmpty()
    
    var name by remember { mutableStateOf(passenger?.name ?: "") }
    var identityNo by remember { mutableStateOf(passenger?.identityNo ?: "") }
    var gender by remember { mutableStateOf(passenger?.gender ?: "Male") }
    var dateOfBirth by remember { mutableStateOf(passenger?.dateOfBirth ?: "") }
    var passengerType by remember { mutableStateOf(passenger?.passengerType ?: "Adult") }
    var discountType by remember { mutableStateOf(passenger?.discountType ?: "none") }
    var country by remember { mutableStateOf(passenger?.country ?: "Indonesia") }
    var documentType by remember { mutableStateOf(passenger?.documentType ?: "No. identitas") }
    var expiryDate by remember { mutableStateOf(passenger?.expiryDate ?: "31 Dec 2099") }
    var whatsapp by remember { mutableStateOf(passenger?.whatsapp ?: "") }
    var email by remember { mutableStateOf(passenger?.email ?: "") }
    var savePassenger by remember { mutableStateOf(passenger?.isSaved ?: false) }
    
    // Account registration fields (only for first passenger if not logged in)
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    
    // Dialog states
    var showDatePicker by remember { mutableStateOf(false) }
    var showExpiryPicker by remember { mutableStateOf(false) }
    var showPassengerTypeDialog by remember { mutableStateOf(false) }
    var showDiscountTypeDialog by remember { mutableStateOf(false) }
    var showDocumentTypeDialog by remember { mutableStateOf(false) }

    // Listen for country selection result
    LaunchedEffect(navController?.currentBackStackEntry) {
        navController?.currentBackStackEntry?.savedStateHandle?.get<String>("selected_country")?.let { 
            country = it
            navController.currentBackStackEntry?.savedStateHandle?.remove<String>("selected_country")
        }
    }

    Scaffold(
        topBar = {
            WhooshTopBar(
                title = if (passenger == null) "Add Passenger" else "Edit Passenger",
                onBack = onBack
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(Color.White)
                .verticalScroll(rememberScrollState())
        ) {
            // Info banner for first passenger
            if (!isLoggedIn && isFirstPassenger) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    shape = RoundedCornerShape(8.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFFFF5F5)),
                    elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.Top
                    ) {
                        Icon(
                            imageVector = Icons.Default.Info,
                            contentDescription = null,
                            tint = WhooshRed,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Text(
                                text = "Buat Akun Otomatis".tr(),
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 12.sp,
                                color = Color.Black
                            )
                            Text(
                                text = "Data penumpang pertama akan digunakan untuk membuat akun Anda".tr(),
                                fontSize = 10.sp,
                                color = WhooshTextSecondary,
                                lineHeight = 14.sp
                            )
                        }
                    }
                }
            }

            // Personal Information Section
            SectionHeader("Personal Information".tr())
            
            Spacer(modifier = Modifier.height(12.dp))

            // Gender
            Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "*".tr(),
                        color = WhooshRed,
                        fontSize = 12.sp,
                        modifier = Modifier.padding(end = 4.dp)
                    )
                    Text(
                        text = "Gender".tr(),
                        fontSize = 12.sp,
                        color = Color(0xFF666666)
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    GenderOption(
                        text = "Male".tr(),
                        isSelected = gender == "Male",
                        onClick = { gender = "Male" },
                        modifier = Modifier.weight(1f)
                    )
                    GenderOption(
                        text = "Female".tr(),
                        isSelected = gender == "Female",
                        onClick = { gender = "Female" },
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))
            HorizontalDivider(color = Color(0xFFF0F0F0), thickness = 1.dp)
            Spacer(modifier = Modifier.height(12.dp))

            // Date of Birth
            SelectableFieldRow(
                label = "Date of birth",
                value = dateOfBirth.ifEmpty { "Please select a date of birth".tr() },
                onClick = { showDatePicker = true },
                isRequired = true,
                isEmpty = dateOfBirth.isEmpty()
            )

            Spacer(modifier = Modifier.height(12.dp))
            HorizontalDivider(color = Color(0xFFF0F0F0), thickness = 1.dp)
            Spacer(modifier = Modifier.height(12.dp))

            // Passenger Type
            SelectableFieldRow(
                label = "Passenger Type",
                value = passengerType,
                onClick = { showPassengerTypeDialog = true },
                isRequired = true,
                isEmpty = false
            )

            Spacer(modifier = Modifier.height(12.dp))
            HorizontalDivider(color = Color(0xFFF0F0F0), thickness = 1.dp)
            Spacer(modifier = Modifier.height(12.dp))

            // Discount Type
            SelectableFieldRow(
                label = "Discount type",
                value = discountType,
                onClick = { showDiscountTypeDialog = true },
                isRequired = true,
                isEmpty = false
            )

            Spacer(modifier = Modifier.height(12.dp))
            HorizontalDivider(color = Color(0xFFF0F0F0), thickness = 1.dp)
            Spacer(modifier = Modifier.height(12.dp))

            // Country/Region
            SelectableFieldRow(
                label = "Country/Region",
                value = country,
                onClick = onSelectCountry,
                isRequired = true,
                isEmpty = false
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Certificate Information Section
            SectionHeader("Certificate Information".tr())
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Text(
                text = "The input information must be consistent with the certificate information.".tr(),
                fontSize = 11.sp,
                color = WhooshRed,
                lineHeight = 15.sp,
                modifier = Modifier.padding(horizontal = 16.dp)
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Document Type
            SelectableFieldRow(
                label = "Document Type",
                value = documentType,
                onClick = { showDocumentTypeDialog = true },
                isRequired = false,
                isEmpty = false
            )

            Spacer(modifier = Modifier.height(12.dp))
            HorizontalDivider(color = Color(0xFFF0F0F0), thickness = 1.dp)
            Spacer(modifier = Modifier.height(12.dp))

            // ID Card / Identity Number
            Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                OutlinedTextField(
                    value = identityNo,
                    onValueChange = { identityNo = it },
                    label = { 
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.CreditCard,
                                contentDescription = null,
                                tint = WhooshRed,
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(documentType, fontSize = 11.sp)
                        }
                    },
                    placeholder = { 
                        Text(
                            "Please enter your ".tr() + documentType.tr() + " number".tr(),
                            fontSize = 11.sp,
                            color = Color(0xFFCCCCCC)
                        ) 
                    },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = WhooshRed,
                        focusedLabelColor = WhooshRed,
                        unfocusedBorderColor = Color(0xFFE0E0E0),
                        cursorColor = WhooshRed
                    ),
                    shape = RoundedCornerShape(8.dp),
                    textStyle = LocalTextStyle.current.copy(fontSize = 14.sp)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Name
            Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Full Name".tr(), fontSize = 11.sp) },
                    placeholder = { 
                        Text(
                            "Enter your name on your ID Card".tr(),
                            fontSize = 11.sp,
                            color = Color(0xFFCCCCCC)
                        ) 
                    },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = WhooshRed,
                        focusedLabelColor = WhooshRed,
                        unfocusedBorderColor = Color(0xFFE0E0E0),
                        cursorColor = WhooshRed
                    ),
                    shape = RoundedCornerShape(8.dp),
                    textStyle = LocalTextStyle.current.copy(fontSize = 14.sp)
                )
            }

            Spacer(modifier = Modifier.height(12.dp))
            HorizontalDivider(color = Color(0xFFF0F0F0), thickness = 1.dp)
            Spacer(modifier = Modifier.height(12.dp))

            // Expiry Date
            SelectableFieldRow(
                label = "Expiry Date",
                value = expiryDate,
                onClick = { showExpiryPicker = true },
                isRequired = false,
                isEmpty = false
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Contact Information Section
            SectionHeader("Contact Information".tr())
            
            Spacer(modifier = Modifier.height(12.dp))

            // WhatsApp
            Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                OutlinedTextField(
                    value = whatsapp,
                    onValueChange = { whatsapp = it },
                    label = { Text("WhatsApp Number".tr(), fontSize = 11.sp) },
                    placeholder = { Text("8123456789".tr(), fontSize = 11.sp) },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    leadingIcon = {
                        Box(
                            modifier = Modifier
                                .padding(start = 12.dp, end = 8.dp)
                                .background(WhooshRed, RoundedCornerShape(4.dp))
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = "+62".tr(),
                                color = Color.White,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = WhooshRed,
                        focusedLabelColor = WhooshRed,
                        unfocusedBorderColor = Color(0xFFE0E0E0),
                        cursorColor = WhooshRed
                    ),
                    shape = RoundedCornerShape(8.dp),
                    textStyle = LocalTextStyle.current.copy(fontSize = 14.sp)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Email
            Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it },
                    label = { Text("Email Address".tr(), fontSize = 11.sp) },
                    placeholder = { Text("example@email.com".tr(), fontSize = 11.sp) },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = WhooshRed,
                        focusedLabelColor = WhooshRed,
                        unfocusedBorderColor = Color(0xFFE0E0E0),
                        cursorColor = WhooshRed
                    ),
                    shape = RoundedCornerShape(8.dp),
                    textStyle = LocalTextStyle.current.copy(fontSize = 14.sp)
                )
            }

            // Account Creation Section (only for first passenger if not logged in)
            if (!isLoggedIn && isFirstPassenger) {
                Spacer(modifier = Modifier.height(16.dp))
                
                SectionHeader("Account Password".tr())
                
                Spacer(modifier = Modifier.height(12.dp))

                // Password
                Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                    OutlinedTextField(
                        value = password,
                        onValueChange = { password = it },
                        label = { Text("Password (min. 6 characters)".tr(), fontSize = 11.sp) },
                        placeholder = { Text("Enter password".tr(), fontSize = 11.sp) },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                        visualTransformation = androidx.compose.ui.text.input.PasswordVisualTransformation(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = WhooshRed,
                            focusedLabelColor = WhooshRed,
                            unfocusedBorderColor = Color(0xFFE0E0E0),
                            cursorColor = WhooshRed
                        ),
                        shape = RoundedCornerShape(6.dp),
                        textStyle = LocalTextStyle.current.copy(fontSize = 12.sp)
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Confirm Password
                Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                    OutlinedTextField(
                        value = confirmPassword,
                        onValueChange = { confirmPassword = it },
                        label = { Text("Confirm Password".tr(), fontSize = 11.sp) },
                        placeholder = { Text("Re-enter password".tr(), fontSize = 11.sp) },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                        visualTransformation = androidx.compose.ui.text.input.PasswordVisualTransformation(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = WhooshRed,
                            focusedLabelColor = WhooshRed,
                            unfocusedBorderColor = Color(0xFFE0E0E0),
                            cursorColor = WhooshRed
                        ),
                        shape = RoundedCornerShape(6.dp),
                        textStyle = LocalTextStyle.current.copy(fontSize = 12.sp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Save Passenger Checkbox (only if logged in or not first passenger)
            if (isLoggedIn || !isFirstPassenger) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                        .clickable { savePassenger = !savePassenger },
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Checkbox(
                        checked = savePassenger,
                        onCheckedChange = { savePassenger = it },
                        colors = CheckboxDefaults.colors(
                            checkedColor = WhooshRed,
                            checkmarkColor = Color.White
                        )
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "Save this passenger for future bookings".tr(),
                        fontSize = 12.sp,
                        color = Color(0xFF666666)
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Submit Button
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 16.dp)
            ) {
                WhooshButton(
                    text = if (!isLoggedIn && isFirstPassenger) "Create Account & Add" else "Submit",
                    onClick = {
                        // Validation
                        if (!isLoggedIn && isFirstPassenger) {
                            // Register user first
                            if (password.length >= 6 && password == confirmPassword) {
                                viewModel.register(
                                    name = name,
                                    email = email,
                                    phone = whatsapp,
                                    password = password,
                                    confirmPassword = confirmPassword
                                ) { success ->
                                    if (success) {
                                        // Add passenger after successful registration
                                        val newPassenger = Passenger(
                                            id = passenger?.id ?: java.util.UUID.randomUUID().toString(),
                                            name = name,
                                            identityNo = identityNo,
                                            gender = gender,
                                            dateOfBirth = dateOfBirth,
                                            passengerType = passengerType,
                                            discountType = discountType,
                                            country = country,
                                            documentType = documentType,
                                            expiryDate = expiryDate,
                                            whatsapp = whatsapp,
                                            email = email,
                                            isSaved = true
                                        )
                                        
                                        viewModel.addPassenger(newPassenger)
                                        viewModel.savePassengerForFuture(newPassenger)
                                        onSave()
                                    }
                                }
                            }
                        } else {
                            // Just add passenger
                            val newPassenger = Passenger(
                                id = passenger?.id ?: java.util.UUID.randomUUID().toString(),
                                name = name,
                                identityNo = identityNo,
                                gender = gender,
                                dateOfBirth = dateOfBirth,
                                passengerType = passengerType,
                                discountType = discountType,
                                country = country,
                                documentType = documentType,
                                expiryDate = expiryDate,
                                whatsapp = whatsapp,
                                email = email,
                                isSaved = savePassenger
                            )
                            
                            if (passenger == null) {
                                viewModel.addPassenger(newPassenger)
                            } else {
                                viewModel.updatePassenger(newPassenger)
                            }
                            
                            if (savePassenger) {
                                viewModel.savePassengerForFuture(newPassenger)
                            }
                            
                            onSave()
                        }
                    },
                    enabled = name.isNotEmpty() && identityNo.isNotEmpty() && 
                             dateOfBirth.isNotEmpty() && whatsapp.isNotEmpty() && email.isNotEmpty() &&
                             (isLoggedIn || !isFirstPassenger || (password.length >= 6 && password == confirmPassword))
                )
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
        
        // Date Picker Dialog for Date of Birth
        if (showDatePicker) {
            CustomDatePickerDialog(
                onDateSelected = { selectedDate ->
                    dateOfBirth = selectedDate
                    showDatePicker = false
                },
                onDismiss = { showDatePicker = false }
            )
        }
        
        // Date Picker Dialog for Expiry Date
        if (showExpiryPicker) {
            CustomDatePickerDialog(
                onDateSelected = { selectedDate ->
                    expiryDate = selectedDate
                    showExpiryPicker = false
                },
                onDismiss = { showExpiryPicker = false }
            )
        }
        
        // Passenger Type Dialog
        if (showPassengerTypeDialog) {
            SelectionDialog(
                title = "Select Passenger Type".tr(),
                options = listOf("Adult", "Bayi"),
                selectedOption = passengerType,
                onOptionSelected = { selected ->
                    passengerType = selected
                    showPassengerTypeDialog = false
                },
                onDismiss = { showPassengerTypeDialog = false }
            )
        }
        
        // Discount Type Dialog
        if (showDiscountTypeDialog) {
            SelectionDialog(
                title = "Select Discount Type".tr(),
                options = listOf("none", "veteran", "old", "group"),
                selectedOption = discountType,
                onOptionSelected = { selected ->
                    discountType = selected
                    showDiscountTypeDialog = false
                },
                onDismiss = { showDiscountTypeDialog = false }
            )
        }
        
        
        // Document Type Dialog
        if (showDocumentTypeDialog) {
            SelectionDialog(
                title = "Select Document Type".tr(),
                options = listOf("No. identitas", "Paspor"),
                selectedOption = documentType,
                onOptionSelected = { selected ->
                    documentType = selected
                    showDocumentTypeDialog = false
                },
                onDismiss = { showDocumentTypeDialog = false }
            )
        }
    }
}

@Composable
private fun SectionHeader(text: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFFF5F5F5))
            .padding(horizontal = 16.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .width(3.dp)
                .height(14.dp)
                .background(WhooshRed, RoundedCornerShape(2.dp))
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = text,
            fontSize = 13.sp,
            fontWeight = FontWeight.SemiBold,
            color = Color.Black
        )
    }
}

@Composable
private fun GenderOption(
    text: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .clip(RoundedCornerShape(6.dp))
            .clickable(onClick = onClick)
            .padding(vertical = 8.dp, horizontal = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        RadioButton(
            selected = isSelected,
            onClick = onClick,
            colors = RadioButtonDefaults.colors(
                selectedColor = WhooshRed,
                unselectedColor = Color(0xFFCCCCCC)
            ),
            modifier = Modifier.size(20.dp)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = text,
            fontSize = 12.sp,
            color = if (isSelected) Color.Black else Color(0xFF666666)
        )
    }
}

@Composable
private fun SelectableFieldRow(
    label: String,
    value: String,
    onClick: () -> Unit,
    isRequired: Boolean,
    isEmpty: Boolean
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            if (isRequired) {
                Text(
                    text = "*".tr(),
                    color = WhooshRed,
                    fontSize = 12.sp,
                    modifier = Modifier.padding(end = 4.dp)
                )
            }
            Text(text = label.tr(),
                fontSize = 12.sp,
                color = Color(0xFF666666)
            )
        }
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = value,
                fontSize = 12.sp,
                color = if (isEmpty) Color(0xFFCCCCCC) else Color(0xFF333333)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Icon(
                imageVector = Icons.Default.KeyboardArrowRight,
                contentDescription = null,
                tint = Color(0xFFCCCCCC),
                modifier = Modifier.size(16.dp)
            )
        }
    }
}


// Custom Date Picker Dialog
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CustomDatePickerDialog(
    onDateSelected: (String) -> Unit,
    onDismiss: () -> Unit
) {
    val datePickerState = rememberDatePickerState()
    
    DatePickerDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(onClick = {
                datePickerState.selectedDateMillis?.let { millis ->
                    val calendar = java.util.Calendar.getInstance()
                    calendar.timeInMillis = millis
                    val dateFormat = java.text.SimpleDateFormat("dd MMM yyyy", java.util.Locale.ENGLISH)
                    val formattedDate = dateFormat.format(calendar.time)
                    onDateSelected(formattedDate)
                }
            }) {
                Text("Select".tr(), fontWeight = FontWeight.Bold, color = WhooshRed)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel".tr(), color = Color.Gray)
            }
        },
        colors = DatePickerDefaults.colors(
            containerColor = Color.White
        ),
        shape = RoundedCornerShape(24.dp)
    ) {
        DatePicker(
            state = datePickerState,
            title = null,
            headline = null,
            showModeToggle = false,
            colors = DatePickerDefaults.colors(
                selectedDayContainerColor = WhooshRed,
                selectedDayContentColor = Color.White,
                todayContentColor = WhooshRed,
                todayDateBorderColor = WhooshRed,
                containerColor = Color.White,
                weekdayContentColor = Color.Gray,
                subheadContentColor = Color.Black
            )
        )
    }
}

// Selection Dialog for dropdown options
@Composable
private fun SelectionDialog(
    title: String,
    options: List<String>,
    selectedOption: String,
    onOptionSelected: (String) -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(text = title.tr(),
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color.Black
            )
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth()
            ) {
                options.forEach { option ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onOptionSelected(option) }
                            .padding(vertical = 12.dp, horizontal = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = option == selectedOption,
                            onClick = { onOptionSelected(option) },
                            colors = RadioButtonDefaults.colors(
                                selectedColor = WhooshRed,
                                unselectedColor = Color(0xFFCCCCCC)
                            )
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(text = option.tr(),
                            fontSize = 14.sp,
                            color = if (option == selectedOption) Color.Black else Color(0xFF666666)
                        )
                    }
                }
            }
        },
        confirmButton = {},
        containerColor = Color.White,
        shape = RoundedCornerShape(12.dp)
    )
}
