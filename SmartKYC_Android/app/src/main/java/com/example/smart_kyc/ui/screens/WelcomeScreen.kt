package com.example.smart_kyc.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.smart_kyc.ui.theme.SmartkycTheme

@Composable
fun WelcomeScreen(
    onStartKycClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp)
            .padding(top = 48.dp, bottom = 32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        // Header Section
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // App Logo
            Surface(
                modifier = Modifier
                    .size(100.dp)
                    .clip(CircleShape),
                color = MaterialTheme.colorScheme.primaryContainer,
                shape = CircleShape
            ) {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier.fillMaxSize()
                ) {
                    SmartKYCLogo(
                        modifier = Modifier.size(56.dp),
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(24.dp))
            
            // Title
            Text(
                text = "Start Your KYC Verification",
                style = MaterialTheme.typography.headlineLarge.copy(
                    fontWeight = FontWeight.Bold,
                    fontSize = 28.sp
                ),
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onSurface
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Description
            Text(
                text = "Complete your KYC in a few easy steps. It will take less than 5 minutes.",
                style = MaterialTheme.typography.bodyLarge.copy(
                    fontSize = 16.sp,
                    lineHeight = 24.sp
                ),
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            
            Spacer(modifier = Modifier.height(32.dp))
            
            // Bullet Highlights
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                HighlightItem(text = "Auto-scan documents")
                HighlightItem(text = "Instant verification")
                HighlightItem(text = "Secure & compliant")
            }
        }
        
        // Bottom Section
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Primary Button
            Button(
                onClick = onStartKycClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary
                )
            ) {
                Text(
                    text = "Start KYC",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 18.sp
                    ),
                    color = MaterialTheme.colorScheme.onPrimary
                )
            }
            
            Spacer(modifier = Modifier.height(24.dp))
            
            // Footer
            Text(
                text = "Your data is securely encrypted and used only for verification.",
                style = MaterialTheme.typography.bodySmall.copy(
                    fontSize = 12.sp,
                    lineHeight = 16.sp
                ),
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
            )
        }
    }
}

@Composable
fun SmartKYCLogo(
    modifier: Modifier = Modifier,
    tint: Color = MaterialTheme.colorScheme.primary
) {
    androidx.compose.foundation.Canvas(
        modifier = modifier
    ) {
        val width = size.width
        val height = size.height
        val centerX = width / 2
        val centerY = height / 2
        
        // Draw shield shape (simplified)
        val shieldPath = Path().apply {
            // Top curve
            moveTo(centerX, height * 0.15f)
            quadraticBezierTo(
                width * 0.2f, height * 0.15f,
                width * 0.15f, height * 0.3f
            )
            // Left side
            lineTo(width * 0.15f, height * 0.7f)
            // Bottom left curve
            quadraticBezierTo(
                width * 0.15f, height * 0.85f,
                centerX, height * 0.9f
            )
            // Bottom right curve
            quadraticBezierTo(
                width * 0.85f, height * 0.85f,
                width * 0.85f, height * 0.7f
            )
            // Right side
            lineTo(width * 0.85f, height * 0.3f)
            // Top right curve
            quadraticBezierTo(
                width * 0.8f, height * 0.15f,
                centerX, height * 0.15f
            )
            close()
        }
        
        // Draw shield
        drawPath(
            path = shieldPath,
            color = tint,
            style = Stroke(width = 4.dp.toPx(), cap = androidx.compose.ui.graphics.StrokeCap.Round)
        )
        
        // Draw checkmark inside shield
        val checkPath = Path().apply {
            moveTo(centerX - width * 0.15f, centerY)
            lineTo(centerX - width * 0.05f, centerY + height * 0.15f)
            lineTo(centerX + width * 0.2f, centerY - height * 0.1f)
        }
        
        drawPath(
            path = checkPath,
            color = tint,
            style = Stroke(
                width = 4.dp.toPx(),
                cap = androidx.compose.ui.graphics.StrokeCap.Round,
                join = androidx.compose.ui.graphics.StrokeJoin.Round
            )
        )
    }
}

@Composable
fun HighlightItem(
    text: String,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Start
    ) {
        Icon(
            imageVector = Icons.Default.CheckCircle,
            contentDescription = null,
            modifier = Modifier.size(24.dp),
            tint = MaterialTheme.colorScheme.primary
        )
        Spacer(modifier = Modifier.width(12.dp))
        Text(
            text = text,
            style = MaterialTheme.typography.bodyLarge.copy(
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium
            ),
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}

@Preview(showBackground = true)
@Composable
fun WelcomeScreenPreview() {
    SmartkycTheme {
        WelcomeScreen(
            onStartKycClick = {}
        )
    }
}

