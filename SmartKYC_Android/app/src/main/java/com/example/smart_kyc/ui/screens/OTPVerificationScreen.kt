package com.example.smart_kyc.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.smart_kyc.ui.theme.SmartkycTheme
import kotlinx.coroutines.delay

@Composable
fun OTPVerificationScreen(
    modifier: Modifier = Modifier,
    contactInfo: String = "+91 XXXXXXX123",
    isMobile: Boolean = true,
    onVerifyClick: (String) -> Unit = {},
    onResendClick: () -> Unit = {}
) {
    var otp by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var timeRemaining by remember { mutableStateOf(30) } // 30 seconds
    var canResend by remember { mutableStateOf(false) }
    
    // Timer countdown
    LaunchedEffect(timeRemaining) {
        if (timeRemaining > 0 && !canResend) {
            delay(1000)
            timeRemaining--
            if (timeRemaining == 0) {
                canResend = true
            }
        }
    }
    
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp)
            .padding(top = 48.dp, bottom = 32.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Title
        Text(
            text = "Enter OTP sent to $contactInfo",
            style = MaterialTheme.typography.headlineMedium.copy(
                fontWeight = FontWeight.Bold,
                fontSize = 24.sp
            ),
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.padding(bottom = 32.dp)
        )
        
        // OTP Input Boxes (6 digits)
        OTPInputField(
            otp = otp,
            onOtpChange = { newOtp ->
                otp = newOtp
                errorMessage = null
            },
            modifier = Modifier.padding(bottom = 24.dp)
        )
        
        // Error message
        if (errorMessage != null) {
            Text(
                text = errorMessage!!,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(bottom = 16.dp)
            )
        }
        
        // Timer / Resend
        Row(
            modifier = Modifier.padding(bottom = 32.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (!canResend) {
                val minutes = timeRemaining / 60
                val seconds = timeRemaining % 60
                Text(
                    text = "Resend OTP in ${String.format("%02d:%02d", minutes, seconds)}",
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontSize = 14.sp
                    ),
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            } else {
                TextButton(onClick = {
                    onResendClick()
                    timeRemaining = 30
                    canResend = false
                    otp = ""
                    errorMessage = null
                }) {
                    Text(
                        text = "Resend OTP",
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium
                        ),
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }
        
        Spacer(modifier = Modifier.weight(1f))
        
        // Verify & Continue Button
        Button(
            onClick = {
                if (otp.length == 6) {
                    // Validate OTP (in real app, this would call an API)
                    if (otp == "123456") { // Demo: accept 123456 as valid
                        onVerifyClick(otp)
                    } else {
                        errorMessage = "Incorrect OTP. Please try again."
                    }
                } else {
                    errorMessage = "Please enter 6-digit OTP"
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            shape = RoundedCornerShape(12.dp),
            enabled = otp.length == 6,
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primary,
                disabledContainerColor = MaterialTheme.colorScheme.surfaceVariant
            )
        ) {
            Text(
                text = "Verify & Continue",
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 18.sp
                ),
                color = if (otp.length == 6) {
                    MaterialTheme.colorScheme.onPrimary
                } else {
                    MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                }
            )
        }
    }
}

@Composable
fun OTPInputField(
    otp: String,
    onOtpChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val focusRequesterList = remember { List(6) { FocusRequester() } }
    val focusManager = LocalFocusManager.current
    
    // Track previous OTP length to detect if we're going backwards
    var previousLength by remember { mutableStateOf(otp.length) }
    
    // Handle focus based on OTP length changes - this is the primary mechanism
    LaunchedEffect(otp.length) {
        if (otp.length > previousLength) {
            // OTP length increased - move forward
            if (otp.length in 1..5) {
                delay(100) // Delay to ensure state is updated
                focusRequesterList[otp.length].requestFocus()
            } else if (otp.length == 6) {
                // All digits entered, clear focus
                delay(100)
                focusManager.clearFocus()
            }
        } else if (otp.length < previousLength) {
            // OTP length decreased - move backward
            if (otp.length > 0) {
                delay(100)
                focusRequesterList[otp.length].requestFocus()
            } else {
                delay(100)
                focusRequesterList[0].requestFocus()
            }
        }
        previousLength = otp.length
    }
    
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        repeat(6) { index ->
            val char = if (index < otp.length) otp[index].toString() else ""
            OutlinedTextField(
                value = char,
                onValueChange = { newValue ->
                    // Only allow single digit
                    if (newValue.isEmpty()) {
                        // Backspace - remove current digit and move to previous
                        if (otp.isNotEmpty()) {
                            val newOtp = if (index < otp.length) {
                                otp.substring(0, index) + otp.substring(index + 1)
                            } else {
                                otp.dropLast(1)
                            }
                            onOtpChange(newOtp)
                            // Focus will be handled by LaunchedEffect based on OTP length
                        }
                    } else if (newValue.length == 1 && newValue[0].isDigit()) {
                        // Add new digit - always append to the end if typing sequentially
                        val newOtp = if (index == otp.length) {
                            // Typing in the next empty box
                            otp + newValue
                        } else if (index < otp.length) {
                            // Replacing existing digit
                            otp.substring(0, index) + newValue + otp.substring(index + 1)
                        } else {
                            otp + newValue
                        }
                        // Limit to 6 digits
                        if (newOtp.length <= 6) {
                            onOtpChange(newOtp)
                            // Focus will be handled by LaunchedEffect based on OTP length
                        }
                    }
                },
                modifier = Modifier
                    .weight(1f)
                    .height(64.dp)
                    .focusRequester(focusRequesterList[index]),
                textStyle = MaterialTheme.typography.headlineMedium.copy(
                    fontSize = 24.sp,
                    textAlign = TextAlign.Center
                ),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                singleLine = true,
                maxLines = 1,
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                    unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f)
                )
            )
        }
    }
    
    // Auto-focus first box when OTP is empty or cleared
    LaunchedEffect(otp) {
        if (otp.isEmpty()) {
            delay(100)
            focusRequesterList[0].requestFocus()
        }
    }
}

@Preview(showBackground = true)
@Composable
fun OTPVerificationScreenPreview() {
    SmartkycTheme {
        OTPVerificationScreen()
    }
}

