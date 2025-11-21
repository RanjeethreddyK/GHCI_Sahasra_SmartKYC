package com.example.smart_kyc.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.smart_kyc.ui.theme.SmartkycTheme

@Composable
fun ConsentDetailsScreen(
    modifier: Modifier = Modifier,
    onUnderstandClick: () -> Unit = {}
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(24.dp)
            .verticalScroll(rememberScrollState())
    ) {
        Spacer(modifier = Modifier.height(32.dp))
        
        // Title
        Text(
            text = "How We Use Your Data",
            style = MaterialTheme.typography.headlineLarge.copy(
                fontWeight = FontWeight.Bold,
                fontSize = 28.sp
            ),
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.padding(bottom = 32.dp)
        )
        
        // Why we need your data
        DataSection(
            title = "Why we need your data",
            content = "To verify your identity as per KYC/AML regulations."
        )
        
        Spacer(modifier = Modifier.height(24.dp))
        
        // How AI is used
        DataSection(
            title = "How AI is used",
            content = "AI helps extract and validate information from your documents."
        )
        
        Spacer(modifier = Modifier.height(24.dp))
        
        // What we store
        DataSection(
            title = "What we store",
            content = "Only necessary information for regulatory compliance."
        )
        
        Spacer(modifier = Modifier.height(24.dp))
        
        // Your rights
        DataSection(
            title = "Your rights",
            content = "You can request deletion or correction anytime."
        )
        
        Spacer(modifier = Modifier.height(40.dp))
        
        // I Understand Button
        Button(
            onClick = onUnderstandClick,
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primary
            )
        ) {
            Text(
                text = "I Understand",
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 18.sp
                ),
                color = MaterialTheme.colorScheme.onPrimary
            )
        }
        
        Spacer(modifier = Modifier.height(16.dp))
    }
}

@Composable
fun DataSection(
    title: String,
    content: String,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.fillMaxWidth()) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium.copy(
                fontWeight = FontWeight.SemiBold,
                fontSize = 18.sp
            ),
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        Text(
            text = content,
            style = MaterialTheme.typography.bodyLarge.copy(
                fontSize = 16.sp,
                lineHeight = 24.sp
            ),
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Preview(showBackground = true)
@Composable
fun ConsentDetailsScreenPreview() {
    SmartkycTheme {
        ConsentDetailsScreen()
    }
}

