package com.example.smart_kyc.ui.screens

import android.graphics.Bitmap
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.Preview as CameraPreview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import com.example.smart_kyc.ui.theme.SmartkycTheme
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import kotlin.random.Random

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun DocumentCaptureScreen(
    modifier: Modifier = Modifier,
    onCaptureSuccess: (Bitmap) -> Unit = {},
    onBackClick: () -> Unit = {}
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val cameraProviderFuture = remember { ProcessCameraProvider.getInstance(context) }
    var hasPermission by remember { mutableStateOf(false) }
    
    val permissionsState = rememberMultiplePermissionsState(
        permissions = listOf(android.Manifest.permission.CAMERA)
    )
    
    var flashEnabled by remember { mutableStateOf(false) }
    var imageCapture: ImageCapture? by remember { mutableStateOf(null) }
    var preview: CameraPreview? by remember { mutableStateOf(null) }
    var qualityIssue by remember { mutableStateOf<String?>(null) }
    var isCapturing by remember { mutableStateOf(false) }
    
    LaunchedEffect(permissionsState.allPermissionsGranted) {
        hasPermission = permissionsState.allPermissionsGranted
    }
    
    if (!hasPermission) {
        // Request permissions
        LaunchedEffect(Unit) {
            permissionsState.launchMultiplePermissionRequest()
        }
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(
                    text = "Camera Permission Required",
                    style = MaterialTheme.typography.headlineMedium
                )
                Text(
                    text = "Please grant camera permission to capture documents",
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
        return
    }
    
    Box(modifier = modifier.fillMaxSize()) {
        // Camera Preview
        AndroidView(
            factory = { ctx ->
                val previewView = PreviewView(ctx)
                val executor = ContextCompat.getMainExecutor(ctx)
                
                cameraProviderFuture.addListener({
                    val cameraProvider = cameraProviderFuture.get()
                    
                    // Preview
                    val previewUseCase = CameraPreview.Builder()
                        .build()
                        .also {
                            it.setSurfaceProvider(previewView.surfaceProvider)
                        }
                    
                    // Image Capture
                    val imageCaptureUseCase = ImageCapture.Builder()
                        .setCaptureMode(ImageCapture.CAPTURE_MODE_MINIMIZE_LATENCY)
                        .setFlashMode(if (flashEnabled) ImageCapture.FLASH_MODE_ON else ImageCapture.FLASH_MODE_OFF)
                        .build()
                    
                    // Camera Selector
                    val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA
                    
                    try {
                        cameraProvider.unbindAll()
                        cameraProvider.bindToLifecycle(
                            lifecycleOwner,
                            cameraSelector,
                            previewUseCase,
                            imageCaptureUseCase
                        )
                        preview = previewUseCase
                        imageCapture = imageCaptureUseCase
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }, executor)
                
                previewView
            },
            modifier = Modifier.fillMaxSize()
        )
        
        // Overlay with edge detection guide
        DocumentOverlay(
            modifier = Modifier.fillMaxSize()
        )
        
        // Top bar with flash control
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Top
        ) {
            IconButton(onClick = onBackClick) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "Back",
                    tint = Color.White
                )
            }
            
            IconButton(
                onClick = { 
                    flashEnabled = !flashEnabled
                    val currentImageCapture = imageCapture
                    if (currentImageCapture != null) {
                        currentImageCapture.flashMode = if (flashEnabled) ImageCapture.FLASH_MODE_ON else ImageCapture.FLASH_MODE_OFF
                    }
                }
            ) {
                FlashIcon(
                    enabled = flashEnabled,
                    modifier = Modifier.size(24.dp)
                )
            }
        }
        
        // Quality issue prompt
        if (qualityIssue != null) {
            Card(
                modifier = Modifier
                    .align(Alignment.Center)
                    .padding(24.dp)
                    .fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.errorContainer
                )
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        text = qualityIssue!!,
                        style = MaterialTheme.typography.bodyLarge.copy(
                            fontWeight = FontWeight.Medium,
                            fontSize = 16.sp
                        ),
                        color = MaterialTheme.colorScheme.onErrorContainer,
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                    )
                    Button(
                        onClick = { qualityIssue = null },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.error
                        )
                    ) {
                        Text("OK")
                    }
                }
            }
        }
        
        // Bottom controls
        Column(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .background(
                    Color.Black.copy(alpha = 0.5f),
                    shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
                )
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = "Align your document within the frame",
                style = MaterialTheme.typography.bodyLarge.copy(
                    color = Color.White,
                    fontSize = 16.sp
                ),
                textAlign = androidx.compose.ui.text.style.TextAlign.Center
            )
            
            // Capture button
            val context = LocalContext.current
            
            Button(
                onClick = {
                    val currentImageCapture = imageCapture
                    if (!isCapturing && currentImageCapture != null) {
                        isCapturing = true
                        // Perform capture
                        val outputFileOptions = ImageCapture.OutputFileOptions.Builder(
                            java.io.File.createTempFile("doc_", ".jpg", context.cacheDir)
                        ).build()
                        
                        currentImageCapture.takePicture(
                            outputFileOptions,
                            ContextCompat.getMainExecutor(context),
                            object : ImageCapture.OnImageSavedCallback {
                                override fun onImageSaved(output: ImageCapture.OutputFileResults) {
                                    // In a real implementation, you would load the bitmap from the file
                                    // For now, we'll create a placeholder and perform quality checks
                                    val bitmap = android.graphics.Bitmap.createBitmap(100, 100, android.graphics.Bitmap.Config.ARGB_8888)
                                    val qualityResult = performQualityChecks(bitmap)
                                    if (qualityResult.isValid) {
                                        onCaptureSuccess(bitmap)
                                    } else {
                                        qualityIssue = qualityResult.errorMessage
                                        isCapturing = false
                                    }
                                }
                                
                                override fun onError(exception: ImageCaptureException) {
                                    qualityIssue = "Failed to capture image. Please try again."
                                    isCapturing = false
                                }
                            }
                        )
                    }
                },
                modifier = Modifier
                    .size(72.dp)
                    .border(4.dp, Color.White, CircleShape),
                shape = CircleShape,
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (isCapturing) {
                        MaterialTheme.colorScheme.surfaceVariant
                    } else {
                        MaterialTheme.colorScheme.primary
                    }
                ),
                enabled = !isCapturing
            ) {
                if (isCapturing) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(32.dp),
                        color = Color.White,
                        strokeWidth = 3.dp
                    )
                } else {
                    Box(
                        modifier = Modifier
                            .size(56.dp)
                            .background(Color.White, CircleShape)
                    )
                }
            }
        }
    }
}

@Composable
fun FlashIcon(
    enabled: Boolean,
    modifier: Modifier = Modifier
) {
    androidx.compose.foundation.Canvas(
        modifier = modifier
    ) {
        val width = size.width
        val height = size.height
        val centerX = width / 2
        val centerY = height / 2
        
        // Draw lightning bolt shape
        val flashPath = Path().apply {
            // Top part
            moveTo(centerX, height * 0.1f)
            lineTo(centerX - width * 0.15f, centerY - height * 0.1f)
            lineTo(centerX, centerY - height * 0.05f)
            lineTo(centerX + width * 0.15f, centerY)
            // Bottom part
            lineTo(centerX, centerY + height * 0.05f)
            lineTo(centerX + width * 0.1f, centerY + height * 0.2f)
            lineTo(centerX, centerY + height * 0.15f)
            lineTo(centerX - width * 0.15f, height * 0.9f)
            close()
        }
        
        drawPath(
            path = flashPath,
            color = if (enabled) Color.Yellow else Color.White,
            style = androidx.compose.ui.graphics.drawscope.Fill
        )
    }
}

@Composable
fun DocumentOverlay(modifier: Modifier = Modifier) {
    Canvas(modifier = modifier) {
        val centerX = size.width / 2
        val centerY = size.height / 2
        val cardWidth = size.width * 0.85f
        val cardHeight = cardWidth * 0.6f // ID card aspect ratio
        
        val cardRect = Rect(
            left = centerX - cardWidth / 2,
            top = centerY - cardHeight / 2,
            right = centerX + cardWidth / 2,
            bottom = centerY + cardHeight / 2
        )
        
        // Draw semi-transparent overlay
        drawRect(
            color = Color.Black.copy(alpha = 0.5f),
            size = size
        )
        
        // Clear the document area
        val path = Path().apply {
            addRect(cardRect)
        }
        drawPath(
            path = path,
            color = Color.Transparent,
            blendMode = androidx.compose.ui.graphics.BlendMode.Clear
        )
        
        // Draw border with corner indicators
        val cornerLength = 40.dp.toPx()
        val strokeWidth = 4.dp.toPx()
        
        // Top-left corner
        drawLine(
            color = Color.White,
            start = Offset(cardRect.left, cardRect.top + cornerLength),
            end = Offset(cardRect.left, cardRect.top),
            strokeWidth = strokeWidth
        )
        drawLine(
            color = Color.White,
            start = Offset(cardRect.left, cardRect.top),
            end = Offset(cardRect.left + cornerLength, cardRect.top),
            strokeWidth = strokeWidth
        )
        
        // Top-right corner
        drawLine(
            color = Color.White,
            start = Offset(cardRect.right - cornerLength, cardRect.top),
            end = Offset(cardRect.right, cardRect.top),
            strokeWidth = strokeWidth
        )
        drawLine(
            color = Color.White,
            start = Offset(cardRect.right, cardRect.top),
            end = Offset(cardRect.right, cardRect.top + cornerLength),
            strokeWidth = strokeWidth
        )
        
        // Bottom-left corner
        drawLine(
            color = Color.White,
            start = Offset(cardRect.left, cardRect.bottom - cornerLength),
            end = Offset(cardRect.left, cardRect.bottom),
            strokeWidth = strokeWidth
        )
        drawLine(
            color = Color.White,
            start = Offset(cardRect.left, cardRect.bottom),
            end = Offset(cardRect.left + cornerLength, cardRect.bottom),
            strokeWidth = strokeWidth
        )
        
        // Bottom-right corner
        drawLine(
            color = Color.White,
            start = Offset(cardRect.right - cornerLength, cardRect.bottom),
            end = Offset(cardRect.right, cardRect.bottom),
            strokeWidth = strokeWidth
        )
        drawLine(
            color = Color.White,
            start = Offset(cardRect.right, cardRect.bottom),
            end = Offset(cardRect.right, cardRect.bottom - cornerLength),
            strokeWidth = strokeWidth
        )
    }
}


data class QualityCheckResult(
    val isValid: Boolean,
    val errorMessage: String? = null
)

private fun performQualityChecks(bitmap: Bitmap): QualityCheckResult {
    // Simulated quality checks
    // In a real implementation, these would use image processing libraries
    
    // Blur detection (simplified)
    val blurScore = checkBlur(bitmap)
    if (blurScore > 0.7) {
        return QualityCheckResult(false, "Image is blurry, please hold steady and retake.")
    }
    
    // Glare detection (simplified)
    val glareScore = checkGlare(bitmap)
    if (glareScore > 0.6) {
        return QualityCheckResult(false, "Too much glare detected. Please adjust lighting and retake.")
    }
    
    // Shadow detection (simplified)
    val shadowScore = checkShadow(bitmap)
    if (shadowScore > 0.5) {
        return QualityCheckResult(false, "Shadow detected. Please ensure even lighting and retake.")
    }
    
    // Document coverage (simplified)
    val coverageScore = checkDocumentCoverage(bitmap)
    if (coverageScore < 0.8) {
        return QualityCheckResult(false, "Document not fully visible. Please align it within the frame.")
    }
    
    return QualityCheckResult(true)
}

// Simplified quality check functions
// In production, these would use actual image processing algorithms
private fun checkBlur(bitmap: Bitmap): Float {
    // Placeholder: returns random value for demo
    // Real implementation would use Laplacian variance or similar
    return 0.3f + Random.nextFloat() * 0.6f // Range: 0.3 to 0.9
}

private fun checkGlare(bitmap: Bitmap): Float {
    // Placeholder: returns random value for demo
    // Real implementation would detect bright spots/reflections
    return 0.2f + Random.nextFloat() * 0.6f // Range: 0.2 to 0.8
}

private fun checkShadow(bitmap: Bitmap): Float {
    // Placeholder: returns random value for demo
    // Real implementation would detect dark regions
    return 0.1f + Random.nextFloat() * 0.6f // Range: 0.1 to 0.7
}

private fun checkDocumentCoverage(bitmap: Bitmap): Float {
    // Placeholder: returns random value for demo
    // Real implementation would use edge detection to verify document is in frame
    return 0.7f + Random.nextFloat() * 0.3f // Range: 0.7 to 1.0
}

@Preview(showBackground = true)
@Composable
fun DocumentCaptureScreenPreview() {
    SmartkycTheme {
        DocumentCaptureScreen()
    }
}

