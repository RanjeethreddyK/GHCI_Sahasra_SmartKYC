package com.example.smart_kyc.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.smart_kyc.ui.theme.SmartkycTheme

@Composable
fun ContactCaptureScreen(
    onSendOtpClick: (String, Boolean) -> Unit = { _, _ -> },
    onPrivacyClick: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    var selectedOption by remember { mutableStateOf<ContactOption>(ContactOption.Mobile) }
    var mobileNumber by remember { mutableStateOf("") }
    var emailAddress by remember { mutableStateOf("") }
    var mobileError by remember { mutableStateOf<String?>(null) }
    var emailError by remember { mutableStateOf<String?>(null) }
    var isConsentChecked by remember { mutableStateOf(false) }

    val isMobileValid = remember(mobileNumber) {
        mobileNumber.isNotEmpty() && isValidMobileNumber(mobileNumber)
    }
    
    val isEmailValid = remember(emailAddress) {
        emailAddress.isNotEmpty() && isValidEmail(emailAddress)
    }

    val isFormValid = remember(selectedOption, isMobileValid, isEmailValid, isConsentChecked) {
        when (selectedOption) {
            ContactOption.Mobile -> isMobileValid && isConsentChecked
            ContactOption.Email -> isEmailValid && isConsentChecked
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp)
            .padding(top = 32.dp, bottom = 32.dp)
            .verticalScroll(rememberScrollState())
    ) {
        // Title & Subtitle
        Text(
            text = "Verify Your Contact Details",
            style = MaterialTheme.typography.headlineLarge.copy(
                fontWeight = FontWeight.Bold,
                fontSize = 24.sp
            ),
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.fillMaxWidth()
        )
        
        Spacer(modifier = Modifier.height(8.dp))
        
        Text(
            text = "Enter your mobile number or email to continue",
            style = MaterialTheme.typography.bodyLarge.copy(
                fontSize = 14.sp,
                lineHeight = 20.sp
            ),
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.fillMaxWidth()
        )
        
        Spacer(modifier = Modifier.height(32.dp))
        
        // Option Selection (Button Style)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Mobile Button
            Surface(
                modifier = Modifier
                    .weight(1f)
                    .clickable {
                        selectedOption = ContactOption.Mobile
                        mobileError = null
                    },
                shape = RoundedCornerShape(8.dp),
                color = if (selectedOption == ContactOption.Mobile) {
                    MaterialTheme.colorScheme.surfaceVariant
                } else {
                    MaterialTheme.colorScheme.surface
                },
                border = if (selectedOption != ContactOption.Mobile) {
                    BorderStroke(
                        1.dp,
                        MaterialTheme.colorScheme.outline.copy(alpha = 0.5f)
                    )
                } else {
                    null
                }
            ) {
                Text(
                    text = "Mobile",
                    style = MaterialTheme.typography.bodyLarge.copy(
                        fontWeight = FontWeight.Medium
                    ),
                    color = if (selectedOption == ContactOption.Mobile) {
                        MaterialTheme.colorScheme.onSurfaceVariant
                    } else {
                        MaterialTheme.colorScheme.onSurface
                    },
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 14.dp)
                )
            }
            
            // Email Button
            Surface(
                modifier = Modifier
                    .weight(1f)
                    .clickable {
                        selectedOption = ContactOption.Email
                        emailError = null
                    },
                shape = RoundedCornerShape(8.dp),
                color = if (selectedOption == ContactOption.Email) {
                    MaterialTheme.colorScheme.surfaceVariant
                } else {
                    MaterialTheme.colorScheme.surface
                },
                border = if (selectedOption != ContactOption.Email) {
                    BorderStroke(
                        1.dp,
                        MaterialTheme.colorScheme.outline.copy(alpha = 0.5f)
                    )
                } else {
                    null
                }
            ) {
                Text(
                    text = "Email",
                    style = MaterialTheme.typography.bodyLarge.copy(
                        fontWeight = FontWeight.Medium
                    ),
                    color = if (selectedOption == ContactOption.Email) {
                        MaterialTheme.colorScheme.onSurfaceVariant
                    } else {
                        MaterialTheme.colorScheme.onSurface
                    },
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 14.dp)
                )
            }
        }
        
        Spacer(modifier = Modifier.height(24.dp))
        
        // Input Fields
        when (selectedOption) {
            ContactOption.Mobile -> {
                MobileNumberInput(
                    value = mobileNumber,
                    onValueChange = { newValue ->
                        mobileNumber = newValue
                        mobileError = null
                        if (newValue.isNotEmpty() && !isValidMobileNumber(newValue)) {
                            mobileError = "Please enter a valid mobile number."
                        }
                    },
                    error = mobileError
                )
            }
            ContactOption.Email -> {
                EmailInput(
                    value = emailAddress,
                    onValueChange = { newValue ->
                        emailAddress = newValue
                        emailError = null
                        if (newValue.isNotEmpty() && !isValidEmail(newValue)) {
                            emailError = "Invalid email format."
                        }
                    },
                    error = emailError
                )
            }
        }
        
        Spacer(modifier = Modifier.height(24.dp))
        
        // Consent Checkbox
        ConsentCheckbox(
            isChecked = isConsentChecked,
            onCheckedChange = { isConsentChecked = it },
            onPrivacyClick = onPrivacyClick
        )
        
        Spacer(modifier = Modifier.height(32.dp))
        
        // Send OTP Button
        Button(
            onClick = {
                val contactValue = when (selectedOption) {
                    ContactOption.Mobile -> mobileNumber
                    ContactOption.Email -> emailAddress
                }
                onSendOtpClick(contactValue, selectedOption == ContactOption.Mobile)
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            shape = RoundedCornerShape(12.dp),
            enabled = isFormValid,
            colors = ButtonDefaults.buttonColors(
                containerColor = if (isFormValid) {
                    MaterialTheme.colorScheme.primary
                } else {
                    MaterialTheme.colorScheme.surfaceVariant
                },
                disabledContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                contentColor = if (isFormValid) {
                    MaterialTheme.colorScheme.onPrimary
                } else {
                    MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                },
                disabledContentColor = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
            )
        ) {
            Text(
                text = "Send OTP",
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 18.sp
                )
            )
        }
        
        Spacer(modifier = Modifier.height(24.dp))
        
        // Privacy Note Footer
        Text(
            text = "We use secure AI-based systems to verify your identity. Your information will never be shared without consent.",
            style = MaterialTheme.typography.bodySmall.copy(
                fontSize = 12.sp,
                lineHeight = 16.sp
            ),
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Composable
fun MobileNumberInput(
    value: String,
    onValueChange: (String) -> Unit,
    error: String?,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.fillMaxWidth()) {
        OutlinedTextField(
            value = value,
            onValueChange = { newValue ->
                // Only allow digits
                if (newValue.all { it.isDigit() } && newValue.length <= 10) {
                    onValueChange(newValue)
                }
            },
            label = { Text("Mobile Number") },
            placeholder = { 
                Text(
                    text = "Mobile Number",
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                )
            },
            leadingIcon = {
                Text(
                    text = "+91",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.padding(start = 4.dp)
                )
            },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            singleLine = true,
            isError = error != null,
            shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = MaterialTheme.colorScheme.primary,
                unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f),
                errorBorderColor = MaterialTheme.colorScheme.error
            ),
            modifier = Modifier.fillMaxWidth()
        )
        if (error != null) {
            Text(
                text = error,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.padding(start = 16.dp, top = 4.dp)
            )
        }
    }
}

@Composable
fun EmailInput(
    value: String,
    onValueChange: (String) -> Unit,
    error: String?,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.fillMaxWidth()) {
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            label = { Text("Email Address") },
            placeholder = { Text("Enter your email address") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
            singleLine = true,
            isError = error != null,
            shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = MaterialTheme.colorScheme.primary,
                unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f),
                errorBorderColor = MaterialTheme.colorScheme.error
            ),
            modifier = Modifier.fillMaxWidth()
        )
        if (error != null) {
            Text(
                text = error,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.padding(start = 16.dp, top = 4.dp)
            )
        }
    }
}

@Composable
fun ConsentCheckbox(
    isChecked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    onPrivacyClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.Top
    ) {
        Checkbox(
            checked = isChecked,
            onCheckedChange = onCheckedChange,
            modifier = Modifier.padding(end = 12.dp, top = 4.dp)
        )
        Box(
            modifier = Modifier
                .weight(1f)
        ) {
            // Base text for display and checkbox toggle
            Text(
                text = buildAnnotatedString {
                    append("I consent to the ")
                    withStyle(
                        style = SpanStyle(
                            color = MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.Medium,
                            textDecoration = TextDecoration.Underline
                        )
                    ) {
                        append("processing of my personal data")
                    }
                    append(" for KYC verification as per regulatory guidelines.")
                },
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontSize = 14.sp,
                    lineHeight = 20.sp
                ),
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier
                    .padding(top = 4.dp)
            )
            
            // Invisible clickable overlay - checkbox toggle for most of the text
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .matchParentSize()
                    .clickable { onCheckedChange(!isChecked) }
            )
            
            // Clickable overlay specifically for privacy text
            // Positioned over "processing of my personal data" (first line only)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 4.dp)
            ) {
                Spacer(modifier = Modifier.weight(0.35f))
                Box(
                    modifier = Modifier
                        .weight(0.4f)
                        .height(20.dp) // Single line height
                        .clickable { onPrivacyClick() }
                )
                Spacer(modifier = Modifier.weight(0.25f))
            }
        }
    }
}

private fun isValidMobileNumber(mobile: String): Boolean {
    return mobile.length == 10 && mobile.all { it.isDigit() }
}

private fun isValidEmail(email: String): Boolean {
    val emailRegex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}\$".toRegex()
    return emailRegex.matches(email) && email.contains("@") && email.contains(".")
}

private enum class ContactOption {
    Mobile,
    Email
}

@Preview(showBackground = true)
@Composable
fun ContactCaptureScreenPreview() {
    SmartkycTheme {
        ContactCaptureScreen()
    }
}
