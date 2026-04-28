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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEditPassengerScreen(
    viewModel: BookingViewModel,
    passenger: Passenger? = null,
    onSave: () -> Unit,
    onBack: () -> Unit
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
    var documentType by remember { mutableStateOf(passenger?.documentType ?: "ID Card") }
    var expiryDate by remember { mutableStateOf(passenger?.expiryDate ?: "31 Dec 2099") }
    var whatsapp by remember { mutableStateOf(passenger?.whatsapp ?: "") }
    var email by remember { mutableStateOf(passenger?.email ?: "") }
    var savePassenger by remember { mutableStateOf(passenger?.isSaved ?: false) }
    
    // Account registration fields (only for first passenger if not logged in)
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }

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
                                text = "Buat Akun Otomatis",
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 12.sp,
                                color = Color.Black
                            )
                            Text(
                                text = "Data penumpang pertama akan digunakan untuk membuat akun Anda",
                                fontSize = 10.sp,
                                color = WhooshTextSecondary,
                                lineHeight = 14.sp
                            )
                        }
                    }
                }
            }

            // Personal Information Section
            SectionHeader("Personal Information")
            
            Spacer(modifier = Modifier.height(12.dp))

            // Gender
            Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "*",
                        color = WhooshRed,
                        fontSize = 12.sp,
                        modifier = Modifier.padding(end = 4.dp)
                    )
                    Text(
                        text = "Gender",
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
                        text = "Male",
                        isSelected = gender == "Male",
                        onClick = { gender = "Male" },
                        modifier = Modifier.weight(1f)
                    )
                    GenderOption(
                        text = "Female",
                        isSelected = gender == "Female",
                        onClick = { gender = "Female" },
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))
            Divider(color = Color(0xFFF0F0F0), thickness = 1.dp)
            Spacer(modifier = Modifier.height(12.dp))

            // Date of Birth
            SelectableFieldRow(
                label = "Date of birth",
                value = dateOfBirth.ifEmpty { "Please select a date of birth" },
                onClick = { /* Show date picker */ },
                isRequired = true,
                isEmpty = dateOfBirth.isEmpty()
            )

            Spacer(modifier = Modifier.height(12.dp))
            Divider(color = Color(0xFFF0F0F0), thickness = 1.dp)
            Spacer(modifier = Modifier.height(12.dp))

            // Passenger Type
            SelectableFieldRow(
                label = "Passenger Type",
                value = passengerType,
                onClick = { /* Show passenger type picker */ },
                isRequired = true,
                isEmpty = false
            )

            Spacer(modifier = Modifier.height(12.dp))
            Divider(color = Color(0xFFF0F0F0), thickness = 1.dp)
            Spacer(modifier = Modifier.height(12.dp))

            // Discount Type
            SelectableFieldRow(
                label = "Discount type",
                value = discountType,
                onClick = { /* Show discount type picker */ },
                isRequired = true,
                isEmpty = false
            )

            Spacer(modifier = Modifier.height(12.dp))
            Divider(color = Color(0xFFF0F0F0), thickness = 1.dp)
            Spacer(modifier = Modifier.height(12.dp))

            // Country/Region
            SelectableFieldRow(
                label = "Country/Region",
                value = country,
                onClick = { /* Show country picker */ },
                isRequired = true,
                isEmpty = false
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Certificate Information Section
            SectionHeader("Certificate Information")
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Text(
                text = "The input information must be consistent with the certificate information.",
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
                onClick = { /* Show document type picker */ },
                isRequired = false,
                isEmpty = false
            )

            Spacer(modifier = Modifier.height(12.dp))
            Divider(color = Color(0xFFF0F0F0), thickness = 1.dp)
            Spacer(modifier = Modifier.height(12.dp))

            // ID Card
            Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(
                            imageVector = Icons.Default.CreditCard,
                            contentDescription = null,
                            tint = WhooshRed,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "ID Card",
                            fontSize = 12.sp,
                            color = Color(0xFF666666)
                        )
                    }
                    OutlinedTextField(
                        value = identityNo,
                        onValueChange = { identityNo = it },
                        placeholder = { 
                            Text(
                                "Please enter your Indonesian ID card",
                                fontSize = 11.sp,
                                color = Color(0xFFCCCCCC)
                            ) 
                        },
                        modifier = Modifier.weight(2f),
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = WhooshRed,
                            unfocusedBorderColor = Color(0xFFE0E0E0),
                            cursorColor = WhooshRed
                        ),
                        shape = RoundedCornerShape(6.dp),
                        textStyle = LocalTextStyle.current.copy(fontSize = 12.sp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))
            Divider(color = Color(0xFFF0F0F0), thickness = 1.dp)
            Spacer(modifier = Modifier.height(12.dp))

            // Name
            Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Name",
                        fontSize = 12.sp,
                        color = Color(0xFF666666),
                        modifier = Modifier.weight(1f)
                    )
                    OutlinedTextField(
                        value = name,
                        onValueChange = { name = it },
                        placeholder = { 
                            Text(
                                "Enter your name on your ID Card",
                                fontSize = 11.sp,
                                color = Color(0xFFCCCCCC)
                            ) 
                        },
                        modifier = Modifier.weight(2f),
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = WhooshRed,
                            unfocusedBorderColor = Color(0xFFE0E0E0),
                            cursorColor = WhooshRed
                        ),
                        shape = RoundedCornerShape(6.dp),
                        textStyle = LocalTextStyle.current.copy(fontSize = 12.sp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))
            Divider(color = Color(0xFFF0F0F0), thickness = 1.dp)
            Spacer(modifier = Modifier.height(12.dp))

            // Expiry Date
            SelectableFieldRow(
                label = "Expiry Date",
                value = expiryDate,
                onClick = { /* Show expiry date picker */ },
                isRequired = false,
                isEmpty = false
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Contact Information Section
            SectionHeader("Contact Information")
            
            Spacer(modifier = Modifier.height(12.dp))

            // WhatsApp
            Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                OutlinedTextField(
                    value = whatsapp,
                    onValueChange = { whatsapp = it },
                    label = { Text("Please enter WhatsApp", fontSize = 11.sp) },
                    placeholder = { Text("8123456789", fontSize = 11.sp) },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    leadingIcon = {
                        Box(
                            modifier = Modifier
                                .background(WhooshRed, RoundedCornerShape(4.dp))
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = "+62",
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
                    shape = RoundedCornerShape(6.dp),
                    textStyle = LocalTextStyle.current.copy(fontSize = 12.sp)
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Email
            Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it },
                    label = { Text("Please enter your email address", fontSize = 11.sp) },
                    placeholder = { Text("example@email.com", fontSize = 11.sp) },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
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

            // Account Creation Section (only for first passenger if not logged in)
            if (!isLoggedIn && isFirstPassenger) {
                Spacer(modifier = Modifier.height(16.dp))
                
                SectionHeader("Account Password")
                
                Spacer(modifier = Modifier.height(12.dp))

                // Password
                Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                    OutlinedTextField(
                        value = password,
                        onValueChange = { password = it },
                        label = { Text("Password (min. 6 characters)", fontSize = 11.sp) },
                        placeholder = { Text("Enter password", fontSize = 11.sp) },
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
                        label = { Text("Confirm Password", fontSize = 11.sp) },
                        placeholder = { Text("Re-enter password", fontSize = 11.sp) },
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
                        text = "Save this passenger for future bookings",
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
                    text = "*",
                    color = WhooshRed,
                    fontSize = 12.sp,
                    modifier = Modifier.padding(end = 4.dp)
                )
            }
            Text(
                text = label,
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
