package com.example.smart_kyc

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.smart_kyc.ui.screens.ConsentDetailsScreen
import com.example.smart_kyc.ui.screens.ConsentPrivacyScreen
import com.example.smart_kyc.ui.screens.ContactCaptureScreen
import com.example.smart_kyc.ui.screens.DocumentCaptureScreen
import com.example.smart_kyc.ui.screens.OTPVerificationScreen
import com.example.smart_kyc.ui.screens.WelcomeScreen
import com.example.smart_kyc.ui.theme.SmartkycTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            SmartkycTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    KYCApp()
                }
            }
        }
    }
}

@Composable
fun KYCApp() {
    var currentScreen by remember { mutableStateOf<Screen>(Screen.Welcome) }
    var contactInfo by remember { mutableStateOf("") }
    var isMobileContact by remember { mutableStateOf(true) }
    
    when (currentScreen) {
        Screen.Welcome -> {
            WelcomeScreen(
                onStartKycClick = {
                    currentScreen = Screen.ContactCapture
                }
            )
        }
        Screen.ContactCapture -> {
            ContactCaptureScreen(
                onSendOtpClick = { contactValue, isMobile ->
                    contactInfo = contactValue
                    isMobileContact = isMobile
                    currentScreen = Screen.OTPVerification
                },
                onPrivacyClick = {
                    currentScreen = Screen.ConsentDetails
                }
            )
        }
        Screen.ConsentDetails -> {
            ConsentDetailsScreen(
                onUnderstandClick = {
                    currentScreen = Screen.ContactCapture
                }
            )
        }
        Screen.OTPVerification -> {
            val maskedContact = if (isMobileContact && contactInfo.length == 10) {
                "+91 XXXXXX${contactInfo.takeLast(3)}"
            } else if (!isMobileContact && contactInfo.contains("@")) {
                val emailParts = contactInfo.split("@")
                val username = emailParts[0]
                val domain = emailParts[1]
                val maskedUsername = if (username.length > 2) {
                    "${username.take(2)}${"X".repeat(username.length - 2)}"
                } else {
                    "XX"
                }
                "$maskedUsername@$domain"
            } else {
                contactInfo
            }
            
            OTPVerificationScreen(
                contactInfo = maskedContact,
                isMobile = isMobileContact,
                onVerifyClick = { otp ->
                    // Navigate to document capture screen
                    currentScreen = Screen.DocumentCapture
                },
                onResendClick = {
                    // Resend OTP logic
                }
            )
        }
        Screen.DocumentCapture -> {
            DocumentCaptureScreen(
                onCaptureSuccess = { bitmap ->
                    // Navigate to next screen (document review/confirmation)
                    // For now, just stay on document capture
                    currentScreen = Screen.DocumentCapture
                },
                onBackClick = {
                    currentScreen = Screen.OTPVerification
                }
            )
        }
        Screen.ConsentPrivacy -> {
            ConsentPrivacyScreen(
                onBackClick = {
                    currentScreen = Screen.ContactCapture
                }
            )
        }
    }
}

enum class Screen {
    Welcome,
    ContactCapture,
    ConsentDetails,
    OTPVerification,
    DocumentCapture,
    ConsentPrivacy
}

@Preview(showBackground = true)
@Composable
fun KYCAppPreview() {
    SmartkycTheme {
        KYCApp()
    }
}