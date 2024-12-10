package com.example.projectakhirdashboard.User

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.provider.MediaStore
import android.util.Base64
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.result.launch
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.navigation.NavHostController
import coil.compose.rememberImagePainter
import com.example.projectakhirdashboard.BottomNavigationBar
import com.example.projectakhirdashboard.R
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.auth.auth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.util.Date

@Composable
fun ProfilePage(navController: NavHostController, userId: String, displayName: String?) {
    var selectedItem = "Profile"
    val auth = FirebaseAuth.getInstance()
    val currentUser = auth.currentUser
    val userEmail = currentUser?.email ?: ""
    var userName = displayName?.ifEmpty { auth.currentUser?.displayName ?: "" }
    var showDialog by remember { mutableStateOf(false) }
    val mContext = LocalContext.current
    var showImagePickerDialog by remember { mutableStateOf(false) }
    var showDeleteConfirmationDialog by remember { mutableStateOf(false) }

    var profileImageUri by rememberSaveable { mutableStateOf<Uri?>(null) }
    var isLoading by rememberSaveable { mutableStateOf(true) }

    val imageFilename = "profile_image_$userId.jpg"

    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent(),
        onResult = { uri ->
            profileImageUri = uri
            uploadProfileImageToFirebase(uri, mContext) { success ->
                if (success) {
                    Toast.makeText(mContext, "Successfully uploaded profile picture", Toast.LENGTH_LONG)
                }
            }

        }
    )

    LaunchedEffect(key1 = Unit) {
        val userId = auth.currentUser?.uid ?: return@LaunchedEffect
        val databaseRef = FirebaseDatabase.getInstance().getReference("users/$userId/profileImage")

        val existingImageUri = getImageUriFromMediaStore(mContext, imageFilename)
        if (existingImageUri != null) {
            profileImageUri = existingImageUri
            isLoading = false
            return@LaunchedEffect
        }

        databaseRef.get().addOnSuccessListener { snapshot ->
            val base64Image = snapshot.getValue(String::class.java)
            if (!base64Image.isNullOrEmpty()) {
                try {
                    val imageBytes = Base64.decode(base64Image, Base64.DEFAULT)
                    val bitmap = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)

                    val path = MediaStore.Images.Media.insertImage(mContext.contentResolver, bitmap, imageFilename, null)
                    profileImageUri = Uri.parse(path)

                } catch (e: Exception) {
                    Log.e("ProfilePage", "Error decoding image: ${e.message}")
                }
            }
            isLoading = false
        }.addOnFailureListener {
            // ... error handling ...
            isLoading = false
        }
    }

    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicturePreview(),
        onResult = { bitmap ->
            val uri = bitmap?.let { image ->
                val tmpFile = File.createTempFile("tmp_image_file", ".jpg", mContext.cacheDir).apply {
                    deleteOnExit()
                }


                FileOutputStream(tmpFile).use {
                    image.compress(Bitmap.CompressFormat.JPEG, 100, it)
                }
                Uri.fromFile(tmpFile)

            }
            profileImageUri = uri
            uploadProfileImageToFirebase(uri, mContext) { success ->
                if (success) {
                    Toast.makeText(mContext, "Successfully update profile image", Toast.LENGTH_LONG)
                }
            }
        }
    )

    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            cameraLauncher.launch()
        } else {
            Toast.makeText(mContext, "Camera permission denied", Toast.LENGTH_SHORT).show()
        }
    }



    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFEFEFEF))
            .verticalScroll(rememberScrollState())
            .padding(bottom = 100.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(220.dp)
                .background(
                    brush = androidx.compose.ui.graphics.Brush.horizontalGradient(
                        colors = listOf(Color(0xff002f49), Color(0xff5374a1))
                    )
                ),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box {
                    if (isLoading) {
                        CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                    } else if (profileImageUri != null) {
                        Image(
                            painter = rememberImagePainter(profileImageUri),
                            contentDescription = "Profile Picture",
                            modifier = Modifier
                                .size(90.dp)
                                .clip(CircleShape),
                            contentScale = ContentScale.Crop
                        )
                    } else {
                        Image(
                            painter = painterResource(id = R.drawable.ic_profilepict),
                            contentDescription = "Profile Picture",
                            modifier = Modifier
                                .size(90.dp)
                                .clip(CircleShape),
                            contentScale = ContentScale.Crop
                        )
                    }

                    Icon(
                        painter = painterResource(id = R.drawable.ic_cam),
                        contentDescription = "Edit Photo",
                        tint = Color.White,
                        modifier = Modifier
                            .align(Alignment.BottomEnd)
                            .clip(CircleShape)
                            .background(Color(0xFFD4AF37))
                            .padding(4.dp)
                            .clickable { showImagePickerDialog = true }
                    )

                    if (showImagePickerDialog) {
                        Dialog(onDismissRequest = { showImagePickerDialog = false }) {
                            ImagePickerDialog(
                                onTakePhoto = {
                                    showImagePickerDialog = false
                                    permissionLauncher.launch(android.Manifest.permission.CAMERA)
                                },
                                onChooseFromGallery = {
                                    showImagePickerDialog = false
                                    imagePickerLauncher.launch("image/*")
                                },
                                onDeletePhoto = {
                                    showImagePickerDialog = false
                                    showDeleteConfirmationDialog = true
                                },
                                showDeleteOption = profileImageUri != null,
                                showDialog = showImagePickerDialog,
                                setShowDialog = { showImagePickerDialog = it }
                            )
                        }
                    }
                }

                if (showDeleteConfirmationDialog) {
                    AlertDialog(
                        onDismissRequest = { showDeleteConfirmationDialog = false },
                        containerColor = Color.White,
                        title = { Text("Delete Profile Picture", color = Color.Black) },
                        text = {
                            Text("Are you sure you want to delete your profile picture?", color = Color.Gray) },
                        confirmButton = {
                            Button(onClick = {
                                showDeleteConfirmationDialog = false
                                deleteProfileImageFromFirebase(mContext) { success ->
                                    if (success) {
                                        profileImageUri = null // Update UI
                                        Toast.makeText(mContext, "Profile picture deleted!", Toast.LENGTH_SHORT).show()
                                    } else {
                                        Toast.makeText(mContext, "Failed to delete profile picture.", Toast.LENGTH_SHORT).show()
                                    }
                                }
                            }) {
                                Text("Delete")
                            }
                        },
                        dismissButton = {
                            Button(onClick = { showDeleteConfirmationDialog = false }) {
                                Text("Cancel")
                            }
                        },
                        modifier = Modifier
                            .background(Color.White, shape = RoundedCornerShape(8.dp)),
                        shape = RoundedCornerShape(8.dp)
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(horizontal = 16.dp)
                ) {
                    Text(
                        text = "$userName",
                        color = Color.White,
                        style = androidx.compose.ui.text.TextStyle(
                            fontSize = 35.sp,
                            fontWeight = FontWeight.Bold
                        )
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Icon(
                        painter = painterResource(id = R.drawable.ic_pencil),
                        contentDescription = "Edit Username",
                        tint = Color.White,
                        modifier = Modifier
                            .size(24.dp)
                            .clickable {
                                showDialog = true
                            }
                    )
                }

                if (showDialog) {
                    userName?.let {
                        EditUsernameDialog(
                            currentUsername = it,
                            onDismiss = { showDialog = false },
                            onConfirm = { newUsername ->
                                showDialog = false

                                val user = auth.currentUser
                                val profileUpdates = UserProfileChangeRequest.Builder()
                                    .setDisplayName(newUsername)
                                    .build()

                                user?.updateProfile(profileUpdates)
                                    ?.addOnSuccessListener {
                                        userName = newUsername
                                        Toast.makeText(mContext, "Display name updated!", Toast.LENGTH_SHORT).show()
                                    }
                                    ?.addOnFailureListener { exception ->
                                        Log.e("ProfilePage", "Error updating display name: ${exception.message}")
                                        Toast.makeText(mContext, "Error updating display name!", Toast.LENGTH_SHORT).show()
                                    }
                            }
                        )
                    }
                }

                Text(
                    text = userEmail,
                    color = Color.White.copy(alpha = 0.7f),
                    style = androidx.compose.ui.text.TextStyle(
                        fontSize = 14.sp
                    )
                )
            }
        }
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .offset(y = (-20).dp)
                .wrapContentSize(Alignment.Center)
        ) {
            Box(
                modifier = Modifier
                    .width(200.dp)
                    .clip(RoundedCornerShape(20.dp))
                    .background(
                        brush = androidx.compose.ui.graphics.Brush.horizontalGradient(
                            colors = listOf(Color(0xff5374a1), Color(0xfff7bd73))
                        )
                    )
                    .height(40.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Info Akun",
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp
                )
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp)
        ) {
            ProfileOptionItem(
                icon = R.drawable.ic_lock, // Replace with your icon resources
                title = "Password dan Autentikasi",
                onClick = { navController.navigate("passauth") }
            )
            ProfileOptionItem(
                icon = R.drawable.ic_bell,
                title = "Notifikasi",
                onClick = { navController.navigate("notif") }
            )
            ProfileOptionItem(
                icon = R.drawable.ic_like,
                title = "Sarapan Ingin Dicoba",
                onClick = { navController.navigate("like/${userId}/${displayName}") }
            )
            ProfileOptionItem(
                icon = R.drawable.ic_docs,
                title = "Kebijakan Privasi",
                onClick = {
                    navController.navigate("policy")
                }
            )

            Spacer(modifier = Modifier.height(50.dp))

            Button(
                onClick = {
                    Firebase.auth.signOut()
                    navController.navigate("login")
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 5.dp)
                    .height(45.dp),
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.buttonColors(Color(0xffd04446))
            ) {
                Text(
                    text = "LOGOUT",
                    fontWeight = FontWeight.Medium,
                    fontSize = 18.sp,
                    color = Color.White
                )
            }
        }
    }

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.BottomCenter
    ) {
        BottomNavigationBar(
            navController = navController,
            selectedItem = selectedItem,
            onItemSelected = { selectedItem = it },
            userId = userId,
            displayName = displayName
        )
    }
}

@Composable
fun ImagePickerDialog(
    onTakePhoto: () -> Unit,
    onChooseFromGallery: () -> Unit,
    onDeletePhoto: () -> Unit,
    showDeleteOption: Boolean,
    showDialog: Boolean,
    setShowDialog: (Boolean) -> Unit,

) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(Color.White)
            .padding(16.dp)
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = "Take a Photo",
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium,
                color = Color.Black,
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.CenterHorizontally)
                    .clickable {
                        setShowDialog(false)
                        onTakePhoto()
                    }
                    .padding(vertical = 12.dp)
            )

            Divider(color = Color.Gray.copy(alpha = 0.5f), thickness = 1.dp)

            Text(
                text = "Choose Image from Gallery",
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium,
                color = Color.Black,
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.CenterHorizontally)
                    .clickable {
                        setShowDialog(false)
                        onChooseFromGallery()
                    }
                    .padding(vertical = 12.dp)
            )
            if (showDeleteOption) {
                Divider(color = Color.Gray.copy(alpha = 0.5f), thickness = 1.dp)
                Text(
                    text = "Delete Photo",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color.Red,
                    modifier = Modifier
                        .fillMaxWidth()
                        .align(Alignment.CenterHorizontally)
                        .clickable(onClick = onDeletePhoto)
                        .padding(vertical = 12.dp)
                )
            }
        }
    }
}

@Composable
fun ProfileOptionItem(icon: Int, title: String, onClick: () -> Unit) {
    val gradientColors = listOf(Color(0xff5374a1), Color(0xffc68e64))

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .shadow(
                elevation = 10.dp,
                shape = RoundedCornerShape(10.dp),
                ambientColor = Color.Black.copy(alpha = 0.25f),
                spotColor = Color.Black.copy(alpha = 0.5f)
            )
            .clip(RoundedCornerShape(10.dp))
            .background(Color.White)
            .clickable(onClick = onClick)
            .padding(16.dp),

    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                painter = painterResource(id = icon),
                contentDescription = title,
                tint = Color(0xFF5A5A5A),
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.width(16.dp))
            Text(
                text = title,
                style = TextStyle(
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium,
                    brush = Brush.horizontalGradient(gradientColors)
                )
            )


        }
    }
}

@Composable
fun EditUsernameDialog(currentUsername: String, onDismiss: () -> Unit, onConfirm: (String) -> Unit) {
    var newUsername by remember { mutableStateOf(currentUsername) }

    androidx.compose.material.AlertDialog(
        onDismissRequest = { onDismiss() },
        title = {
            Text(text = "Edit Username", fontWeight = FontWeight.Bold, color = Color(0xff5374a1))
        },
        text = {
            androidx.compose.material.OutlinedTextField(
                value = newUsername,
                onValueChange = { newUsername = it },
                label = { Text(text = "New Username", color = Color(0xff5374a1), fontWeight = FontWeight.Bold) }
            )
        },
        confirmButton = {
            androidx.compose.material.TextButton(
                onClick = {
                    onConfirm(newUsername)
                }
            ) {
                Text(text = "OK", fontWeight = FontWeight.Bold, color = Color(0xff5374a1))
            }
        },
        dismissButton = {
            androidx.compose.material.TextButton(
                onClick = { onDismiss() }
            ) {
                Text(text = "Cancel", fontWeight = FontWeight.Bold, color = Color.Red)
            }
        }
    )
}

private fun uploadProfileImageToFirebase(uri: Uri?, context: Context, onResult: (Boolean) -> Unit) {
    if (uri == null) {
        onResult(false)
        return
    }

    val user = FirebaseAuth.getInstance().currentUser
    val userId = user?.uid ?: run {
        onResult(false)
        return
    }

    val databaseRef = FirebaseDatabase.getInstance().getReference("users/$userId/profileImage")
    val contentResolver = context.contentResolver

    try {
        val inputStream = contentResolver.openInputStream(uri)
        val bitmap = BitmapFactory.decodeStream(inputStream)
        val outputStream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)
        val byteArray = outputStream.toByteArray()
        val base64Image = Base64.encodeToString(byteArray, Base64.DEFAULT)

        databaseRef.setValue(base64Image)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Toast.makeText(context, "Profile image updated!", Toast.LENGTH_SHORT).show()
                    onResult(true)
                } else {
                    Toast.makeText(context, "Failed to update profile image.", Toast.LENGTH_SHORT).show()
                    onResult(false)
                }
            }
    } catch (e: Exception) {
        Toast.makeText(context, "Error processing image: ${e.message}", Toast.LENGTH_SHORT).show()
        onResult(false)
    }
}

private fun deleteProfileImageFromFirebase(context: Context, onResult: (Boolean) -> Unit) {
    val userId = FirebaseAuth.getInstance().currentUser?.uid ?: run {
        onResult(false)
        return
    }
    val databaseRef = FirebaseDatabase.getInstance().getReference("users/$userId/profileImage")

    databaseRef.removeValue()
        .addOnSuccessListener {
            onResult(true)
        }
        .addOnFailureListener {
            onResult(false)
        }
}

fun getImageUriFromMediaStore(context: Context, imageFilename: String): Uri? {
    val projection = arrayOf(MediaStore.Images.Media._ID)
    val selection = "${MediaStore.Images.Media.DISPLAY_NAME} = ?"
    val selectionArgs = arrayOf(imageFilename)

    val cursor = context.contentResolver.query(
        MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
        projection,
        selection,
        selectionArgs,
        null
    )

    return cursor?.use {
        if (it.moveToFirst()) {
            Uri.withAppendedPath(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, it.getString(0))
        } else {
            null
        }
    }
}

