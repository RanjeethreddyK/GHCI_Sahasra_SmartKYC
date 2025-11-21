package com.example.smart_kyc.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.smart_kyc.ui.theme.SmartkycTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ConsentPrivacyScreen(
    onBackClick: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Consent & Privacy Details") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 24.dp)
                .padding(top = 16.dp, bottom = 32.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Text(
                text = "Data Processing Consent",
                style = MaterialTheme.typography.headlineMedium.copy(
                    fontWeight = FontWeight.Bold,
                    fontSize = 24.sp
                ),
                modifier = Modifier.padding(bottom = 16.dp)
            )
            
            Text(
                text = "By providing your consent, you agree to the processing of your personal data for KYC verification purposes in accordance with applicable regulatory guidelines.",
                style = MaterialTheme.typography.bodyLarge.copy(
                    fontSize = 16.sp,
                    lineHeight = 24.sp
                ),
                modifier = Modifier.padding(bottom = 24.dp)
            )
            
            Text(
                text = "What data we collect:",
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 18.sp
                ),
                modifier = Modifier.padding(bottom = 8.dp)
            )
            
            Text(
                text = "• Contact information (mobile number or email)\n" +
                        "• Identity documents (as required for verification)\n" +
                        "• Biometric data (if applicable)\n" +
                        "• Device information for security purposes",
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontSize = 15.sp,
                    lineHeight = 22.sp
                ),
                modifier = Modifier.padding(bottom = 24.dp)
            )
            
            Text(
                text = "How we use your data:",
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 18.sp
                ),
                modifier = Modifier.padding(bottom = 8.dp)
            )
            
            Text(
                text = "• To verify your identity and complete KYC procedures\n" +
                        "• To comply with regulatory requirements\n" +
                        "• To prevent fraud and ensure security\n" +
                        "• To communicate with you regarding your verification status",
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontSize = 15.sp,
                    lineHeight = 22.sp
                ),
                modifier = Modifier.padding(bottom = 24.dp)
            )
            
            Text(
                text = "Your rights:",
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 18.sp
                ),
                modifier = Modifier.padding(bottom = 8.dp)
            )
            
            Text(
                text = "• Right to access your personal data\n" +
                        "• Right to rectification of inaccurate data\n" +
                        "• Right to erasure (subject to regulatory requirements)\n" +
                        "• Right to data portability\n" +
                        "• Right to withdraw consent (where applicable)",
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontSize = 15.sp,
                    lineHeight = 22.sp
                ),
                modifier = Modifier.padding(bottom = 24.dp)
            )
            
            Text(
                text = "Data Security:",
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 18.sp
                ),
                modifier = Modifier.padding(bottom = 8.dp)
            )
            
            Text(
                text = "We use secure AI-based systems and encryption to protect your personal data. Your information is stored securely and will never be shared without your explicit consent, except as required by law or regulatory authorities.",
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontSize = 15.sp,
                    lineHeight = 22.sp
                ),
                modifier = Modifier.padding(bottom = 24.dp)
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ConsentPrivacyScreenPreview() {
    SmartkycTheme {
        ConsentPrivacyScreen()
    }
}

