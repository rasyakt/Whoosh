package com.example.whoossh.ui.screens

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.ui.layout.ContentScale
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Fingerprint
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Train
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import com.example.whoossh.R
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.fragment.app.FragmentActivity
import com.example.whoossh.ui.components.WhooshButton
import com.example.whoossh.ui.theme.WhooshGradientDark
import com.example.whoossh.ui.theme.WhooshGradientEnd
import com.example.whoossh.ui.theme.WhooshGradientStart
import com.example.whoossh.ui.theme.WhooshRed
import com.example.whoossh.ui.theme.WhooshRedLight
import com.example.whoossh.ui.theme.WhooshWhite
import com.example.whoossh.viewmodel.BookingViewModel
import kotlinx.coroutines.launch
import com.example.whoossh.utils.tr
import com.example.whoossh.utils.trStr
import com.example.whoossh.utils.BiometricHelper

@Composable
fun LoginScreen(
    viewModel: BookingViewModel,
    onLoginSuccess: () -> Unit,
    onNavigateToRegister: () -> Unit
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    val focusManager = LocalFocusManager.current
    val context = LocalContext.current
    val activity = context as? FragmentActivity

    val fadeAnim = remember { Animatable(0f) }
    
    // ✅ FIX: Gunakan state yang reactive, bukan remember static
    val isBiometricAvailable = BiometricHelper.isBiometricAvailable(context)
    val isBiometricEnabled = viewModel.getBiometric()
    val hasBiometricCredentials = viewModel.hasBiometricCredentials()
    
    // Debug logging
    LaunchedEffect(isBiometricEnabled, hasBiometricCredentials) {
        android.util.Log.d("LoginScreen", "Biometric available: $isBiometricAvailable")
        android.util.Log.d("LoginScreen", "Biometric enabled: $isBiometricEnabled")
        android.util.Log.d("LoginScreen", "Has credentials: $hasBiometricCredentials")
        android.util.Log.d("LoginScreen", "Show button: ${isBiometricAvailable && hasBiometricCredentials && isBiometricEnabled}")
    }
    
    val showBiometricButton = isBiometricAvailable && hasBiometricCredentials && isBiometricEnabled

    LaunchedEffect(Unit) {
        fadeAnim.animateTo(1f, animationSpec = tween(600))
    }

    LaunchedEffect(viewModel.loginError) {
        viewModel.loginError?.let {
            scope.launch {
                snackbarHostState.showSnackbar(it.trStr(viewModel.currentLanguage.value))
                viewModel.clearLoginError()
            }
        }
    }
    
    // ✅ FIX: Fungsi login yang konsisten untuk semua cara login
    val performLogin: () -> Unit = {
        focusManager.clearFocus()
        android.util.Log.d("LoginScreen", "Performing login with email: ${email.take(3)}***")
        viewModel.login(email, password) { success ->
            if (success) {
                android.util.Log.d("LoginScreen", "Login successful")
                // Simpan kredensial untuk biometrik jika diaktifkan
                if (viewModel.getBiometric()) {
                    android.util.Log.d("LoginScreen", "Biometric enabled, saving credentials")
                    viewModel.saveBiometricCredentials(email, password)
                } else {
                    android.util.Log.d("LoginScreen", "Biometric not enabled, skipping credential save")
                }
                onLoginSuccess()
            } else {
                android.util.Log.e("LoginScreen", "Login failed")
            }
        }
    }
    
    // Fungsi untuk handle login biometrik
    val handleBiometricLogin: () -> Unit = {
        android.util.Log.d("LoginScreen", "Biometric login button clicked")
        if (activity != null) {
            BiometricHelper.showBiometricPromptForLogin(
                activity = activity,
                onSuccess = {
                    android.util.Log.d("LoginScreen", "Biometric auth success, logging in...")
                    viewModel.loginWithBiometric { success ->
                        if (success) {
                            android.util.Log.d("LoginScreen", "Login successful")
                            onLoginSuccess()
                        } else {
                            android.util.Log.e("LoginScreen", "Login failed")
                        }
                    }
                },
                onError = { errorMsg ->
                    android.util.Log.e("LoginScreen", "Biometric auth error: $errorMsg")
                    scope.launch {
                        snackbarHostState.showSnackbar(errorMsg)
                    }
                }
            )
        } else {
            android.util.Log.e("LoginScreen", "Activity is null, cannot show biometric prompt")
            scope.launch {
                snackbarHostState.showSnackbar("Tidak dapat menampilkan prompt biometrik")
            }
        }
    }

    Scaffold(
        snackbarHost = {
            SnackbarHost(snackbarHostState) { data ->
                Snackbar(
                    snackbarData = data,
                    containerColor = WhooshRedLight,
                    contentColor = WhooshWhite,
                    shape = RoundedCornerShape(12.dp)
                )
            }
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Top background with train image
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(320.dp)
            ) {
                // Background image
                Image(
                    painter = painterResource(id = R.drawable.whoosh_train_bg),
                    contentDescription = "Whoosh Train Background",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
                // Dark overlay for better text visibility
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Black.copy(alpha = 0.3f))
                )
            }

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .imePadding()
                    .alpha(fadeAnim.value),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.height(70.dp))

                // New Whoosh Logo
                Image(
                    painter = painterResource(id = R.drawable.logo_whoosh),
                    contentDescription = "Whoosh Logo",
                    modifier = Modifier.size(80.dp)
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "Kereta Cepat Indonesia".tr(),
                    fontSize = 12.sp,
                    color = WhooshWhite.copy(alpha = 0.6f),
                    letterSpacing = 2.sp
                )

                Spacer(modifier = Modifier.height(40.dp))

                // Login Card
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp)
                        .background(
                            color = Color.White,
                            shape = RoundedCornerShape(24.dp)
                        )
                        .padding(24.dp)
                ) {
                    Column {
                        Text(
                            text = "Selamat Datang!".tr(),
                            style = MaterialTheme.typography.headlineMedium,
                            fontWeight = FontWeight.Bold
                        )

                        Text(
                            text = "Masuk untuk melanjutkan".tr(),
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color.Gray,
                            modifier = Modifier.padding(top = 4.dp, bottom = 24.dp)
                        )

                        // Email Field
                        OutlinedTextField(
                            value = email,
                            onValueChange = { email = it },
                            label = { Text("Email".tr()) },
                            leadingIcon = {
                                Icon(
                                    Icons.Filled.Email,
                                    contentDescription = null,
                                    tint = WhooshRed
                                )
                            },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(14.dp),
                            singleLine = true,
                            enabled = !viewModel.isLoading,
                            keyboardOptions = KeyboardOptions(
                                keyboardType = KeyboardType.Email,
                                imeAction = ImeAction.Next
                            ),
                            keyboardActions = KeyboardActions(
                                onNext = { focusManager.moveFocus(FocusDirection.Down) }
                            ),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = WhooshRed,
                                unfocusedBorderColor = Color.LightGray,
                                focusedLabelColor = WhooshRed
                            )
                        )

                        Spacer(modifier = Modifier.height(14.dp))

                        // Password Field
                        OutlinedTextField(
                            value = password,
                            onValueChange = { password = it },
                            label = { Text("Password".tr()) },
                            leadingIcon = {
                                Icon(
                                    Icons.Filled.Lock,
                                    contentDescription = null,
                                    tint = WhooshRed
                                )
                            },
                            trailingIcon = {
                                IconButton(onClick = { passwordVisible = !passwordVisible }) {
                                    Icon(
                                        if (passwordVisible) Icons.Filled.Visibility
                                        else Icons.Filled.VisibilityOff,
                                        contentDescription = "Toggle password visibility",
                                        tint = Color.Gray
                                    )
                                }
                            },
                            visualTransformation = if (passwordVisible) VisualTransformation.None
                            else PasswordVisualTransformation(),
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(14.dp),
                            singleLine = true,
                            enabled = !viewModel.isLoading,
                            keyboardOptions = KeyboardOptions(
                                keyboardType = KeyboardType.Password,
                                imeAction = ImeAction.Done
                            ),
                            keyboardActions = KeyboardActions(
                                onDone = {
                                    performLogin()
                                }
                            ),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = WhooshRed,
                                unfocusedBorderColor = Color.LightGray,
                                focusedLabelColor = WhooshRed
                            )
                        )

                        Spacer(modifier = Modifier.height(24.dp))

                        // Login Button with Biometric Icon
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // Main Login Button
                            WhooshButton(
                                text = if (viewModel.isLoading) "Memuat..." else "Masuk",
                                onClick = performLogin,
                                enabled = !viewModel.isLoading,
                                modifier = Modifier.weight(1f)
                            )
                            
                            // Biometric Icon Button (Always visible if biometric available)
                            if (isBiometricAvailable && isBiometricEnabled) {
                                androidx.compose.material3.Surface(
                                    modifier = Modifier.size(54.dp),
                                    shape = RoundedCornerShape(16.dp),
                                    color = if (hasBiometricCredentials) WhooshRed else Color.LightGray,
                                    onClick = {
                                        if (hasBiometricCredentials) {
                                            handleBiometricLogin()
                                        } else {
                                            scope.launch {
                                                snackbarHostState.showSnackbar(
                                                    "Login sekali dengan email/password untuk menyimpan kredensial biometrik"
                                                )
                                            }
                                        }
                                    }
                                ) {
                                    Box(
                                        contentAlignment = Alignment.Center,
                                        modifier = Modifier.fillMaxSize()
                                    ) {
                                        Icon(
                                            Icons.Filled.Fingerprint,
                                            contentDescription = "Login dengan Biometrik",
                                            tint = WhooshWhite,
                                            modifier = Modifier.size(28.dp)
                                        )
                                    }
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        // Register link
                        TextButton(
                            onClick = onNavigateToRegister,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = "Belum punya akun? Daftar di sini".tr(),
                                color = WhooshRed,
                                fontSize = 14.sp,
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(60.dp))
            }
        }
    }
}
