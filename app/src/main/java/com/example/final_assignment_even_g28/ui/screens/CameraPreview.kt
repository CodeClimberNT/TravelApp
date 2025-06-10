package com.example.final_assignment_even_g28.ui.screens

import android.Manifest
import android.graphics.Color
import android.net.Uri
import android.os.Environment
import android.widget.Toast
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Cameraswitch
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import java.io.File

// from the documentation: https://developer.android.com/media/camera/camerax
// CameraX do not immediately allow to launch the default camera application.
// For this reason this composable was used to make a mini-version of that,
// that allows to preview the camera output in a profile-style rounded box
// and allows to take a picture and saving it in a file, overriding the default

// after the email with the professor, he suggested to look at the example at:
// https://github.com/android/camera-samples/blob/main/CameraXBasic/app/src/androidTest/java/com/android/example/cameraxbasic/CameraPreviewTest.kt
// also here the camera app is a recreation of the default camera application.
// it does not invoke the default camera application.

@OptIn(ExperimentalPermissionsApi::class, ExperimentalMaterial3Api::class)
@Composable
fun CameraPreview(
    onDismissCameraPreview: () -> Unit,
    isLandScape: Boolean,
    onPhotoTaken: (uri: Uri) -> Unit,
) {
    val context = LocalContext.current

    val lifecycleOwner = LocalLifecycleOwner.current
    val preview = Preview.Builder().build()
    val cameraProviderFuture = remember{ ProcessCameraProvider.getInstance(context) }
    val permissionState = rememberPermissionState(Manifest.permission.CAMERA)
    val cameraSelector = remember { mutableStateOf(CameraSelector.DEFAULT_FRONT_CAMERA) }
    val previewView = remember {
        PreviewView(context).apply {
            scaleType = PreviewView.ScaleType.FILL_CENTER
            setBackgroundColor(Color.TRANSPARENT)

        }
    }


    val cameraProvider = cameraProviderFuture.get()
    val imageCapture = ImageCapture.Builder()
        .setTargetRotation(context.display.rotation)
        .build()

    val outputDirectory = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
    val photoFile = File(outputDirectory, "profile_picture.jpg")

    LaunchedEffect(cameraProviderFuture, lifecycleOwner, cameraSelector.value) {
        val cameraProvider = cameraProviderFuture.get()
        cameraProvider.unbindAll()
        preview.surfaceProvider = previewView.surfaceProvider
        cameraProvider.bindToLifecycle(lifecycleOwner, cameraSelector.value, preview, imageCapture)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "",
                        style = MaterialTheme.typography.headlineLarge
                    )
                },
                navigationIcon = {
                    IconButton(onClick = {
                        cameraProvider.unbindAll()
                        onDismissCameraPreview()
                    }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                        )
                    }
                },
                colors =
                    TopAppBarDefaults.topAppBarColors(
                        MaterialTheme.colorScheme.inversePrimary,
                    ),
                modifier = Modifier.shadow(16.dp)
            )
        },
        modifier = Modifier.fillMaxSize()
    ) { innerPadding ->
        if (permissionState.status.isGranted) {
            if (!isLandScape) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.SpaceEvenly,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding),
                ) {
                    Box(
                        modifier = Modifier
                            .size(300.dp)
                            .border(
                                5.dp,
                                color = androidx.compose.ui.graphics.Color.Red,
                                shape = CircleShape
                            ),
                        contentAlignment = Alignment.BottomEnd,
                    ) {
                        AndroidView(
                            factory = { previewView },
                            modifier = Modifier
                                .matchParentSize()
                                .clip(CircleShape)
                        )
                        IconButton(
                            onClick = {
                                cameraSelector.value =
                                    if (cameraSelector.value == CameraSelector.DEFAULT_BACK_CAMERA) {
                                        CameraSelector.DEFAULT_FRONT_CAMERA
                                    } else {
                                        CameraSelector.DEFAULT_BACK_CAMERA
                                    }
                                cameraProvider.unbindAll()

                                preview.surfaceProvider = previewView.surfaceProvider
                                cameraProvider.bindToLifecycle(
                                    lifecycleOwner,
                                    cameraSelector.value,
                                    preview
                                )
                            },
                            colors = IconButtonDefaults.iconButtonColors(MaterialTheme.colorScheme.primary)
                        ) {
                            Icon(
                                Icons.Default.Cameraswitch, contentDescription = "Camera Icon",
                                tint =
                                    MaterialTheme.colorScheme
                                        .onPrimary,
                            )
                        }
                    }

                    Button(
                        onClick = {
                            val outputOptions =
                                ImageCapture.OutputFileOptions.Builder(photoFile).build()
                            imageCapture.takePicture(
                                outputOptions,
                                ContextCompat.getMainExecutor(context),
                                object : ImageCapture.OnImageSavedCallback {
                                    override fun onImageSaved(outputFileResults: ImageCapture.OutputFileResults) {
                                        Toast.makeText(context, "Photo saved!", Toast.LENGTH_SHORT)
                                            .show()
                                        onDismissCameraPreview()
                                        val updatedUri = Uri.fromFile(photoFile).buildUpon()
                                            .appendQueryParameter(
                                                "timestamp",
                                                System.currentTimeMillis().toString()
                                            )
                                            .build()
                                        onPhotoTaken(updatedUri)
                                        cameraProvider.unbindAll()
                                    }

                                    override fun onError(exception: ImageCaptureException) {
                                        Toast.makeText(
                                            context,
                                            "Error saving photo: ${exception.message}",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    }
                                }
                            )
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primary,
                            contentColor = MaterialTheme.colorScheme.onPrimary
                        ),
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Text("Take Photo")
                    }

                }
            } else {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding),
                ) {
                    Box(
                        modifier = Modifier
                            .size(300.dp)
                            .border(
                                5.dp,
                                color = androidx.compose.ui.graphics.Color.Red,
                                shape = CircleShape
                            ),
                        contentAlignment = Alignment.BottomEnd,
                    ) {
                        AndroidView(
                            factory = { previewView },
                            modifier = Modifier
                                .matchParentSize()
                                .clip(CircleShape)
                        )
                        IconButton(
                            onClick = {
                                cameraSelector.value =
                                    if (cameraSelector.value == CameraSelector.DEFAULT_BACK_CAMERA) {
                                        CameraSelector.DEFAULT_FRONT_CAMERA
                                    } else {
                                        CameraSelector.DEFAULT_BACK_CAMERA
                                    }
                            },
                            colors = IconButtonDefaults.iconButtonColors(MaterialTheme.colorScheme.primary)
                        ) {
                            Icon(
                                Icons.Default.Cameraswitch, contentDescription = "Camera Icon",
                                tint =
                                    MaterialTheme.colorScheme
                                        .onPrimary,
                            )
                        }
                    }

                    Button(
                        onClick = {
                            val outputOptions =
                                ImageCapture.OutputFileOptions.Builder(photoFile).build()
                            imageCapture.takePicture(
                                outputOptions,
                                ContextCompat.getMainExecutor(context),
                                object : ImageCapture.OnImageSavedCallback {
                                    override fun onImageSaved(outputFileResults: ImageCapture.OutputFileResults) {
                                        Toast.makeText(context, "Photo saved!", Toast.LENGTH_SHORT)
                                            .show()
                                        onDismissCameraPreview()
                                        val updatedUri = Uri.fromFile(photoFile).buildUpon()
                                            .appendQueryParameter(
                                                "timestamp",
                                                System.currentTimeMillis().toString()
                                            )
                                            .build()
                                        onPhotoTaken(updatedUri)
                                    }

                                    override fun onError(exception: ImageCaptureException) {
                                        Toast.makeText(
                                            context,
                                            "Error saving photo: ${exception.message}",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    }
                                }
                            )
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primary,
                            contentColor = MaterialTheme.colorScheme.onPrimary
                        ),
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Text("Take Photo")
                    }

                }
            }


        } else {
            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text("Camera permission is required.")
                Button(onClick = { permissionState.launchPermissionRequest() }) {
                    Text("Grant Permission")
                }
            }
        }
    }
}