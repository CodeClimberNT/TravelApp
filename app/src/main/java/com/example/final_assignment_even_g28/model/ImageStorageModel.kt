package com.example.final_assignment_even_g28.model

import android.content.Context
import android.net.Uri
import android.util.Log
import com.example.final_assignment_even_g28.data.Collections
import java.util.UUID

class ImageStorageModel {
    suspend fun uploadTripImage(
        userId: String,
        tripId: String,
        imageUri: Uri,
        context: Context
    ): Result<String> {
        return try {
            val fileName =
                "${System.currentTimeMillis()}_${UUID.randomUUID().toString().take(8)}.jpg"
            val filePath = "$userId/$tripId/$fileName"

            Log.d("ImageStorageModel", "Uploading image to path: $filePath")

            val inputStream = context.contentResolver.openInputStream(imageUri)
            val bytes = inputStream?.readBytes() ?: throw Exception("Failed to read image")
            inputStream.close()

            // Upload to Supabase
            Collections.travelImagesBucket.upload(filePath, bytes)

            val publicUrl = Collections.travelImagesBucket.publicUrl(filePath)

            Log.d("ImageStorageModel", "Image uploaded successfully to \"$publicUrl\"")
            Result.success(publicUrl)
        } catch (e: Exception) {
            Log.e("ImageStorageModel", "Upload failed: ${e.message}")
            Result.failure(e)
        }
    }

    suspend fun uploadMultipleTripImages(
        userId: String,
        tripId: String,
        imageUris: List<Uri>,
        context: Context
    ): Result<List<String>> {
        return try {
            val uploadResults = mutableListOf<String>()

            imageUris.forEach { uri ->
                val result = uploadTripImage(userId, tripId, uri, context)
                if (result.isSuccess) {
                    uploadResults.add(result.getOrThrow())
                } else {
                    throw result.exceptionOrNull() ?: Exception("Upload failed")
                }
            }

            Result.success(uploadResults)
        } catch (e: Exception) {
            Log.e("ImageStorageModel", "Multiple upload failed: ${e.message}")
            Result.failure(e)
        }
    }

    suspend fun uploadReviewImage(
        plannerId: String,
        tripId: String,
        reviewId: String,
        imageUri: Uri,
        context: Context
    ): Result<String> {
        return try {
            val fileName =
                "${System.currentTimeMillis()}_${UUID.randomUUID().toString().take(8)}.jpg"
            val filePath = "$plannerId/$tripId/reviews/$reviewId/$fileName"

            Log.d("ImageStorageModel", "Uploading review image to path: $filePath")

            val inputStream = context.contentResolver.openInputStream(imageUri)
            val bytes = inputStream?.readBytes() ?: throw Exception("Failed to read image")
            inputStream.close()

            // Upload to Supabase
            Collections.travelImagesBucket.upload(filePath, bytes)

            val publicUrl = Collections.travelImagesBucket.publicUrl(filePath)

            Log.d("ImageStorageModel", "Review Image uploaded successfully to \"$publicUrl\"")
            Result.success(publicUrl)
        } catch (e: Exception) {
            Log.e("ImageStorageModel", "Upload review image failed: ${e.message}")
            Result.failure(e)
        }
    }

    suspend fun uploadMultipleReviewImages(
        plannerId: String,
        tripId: String,
        reviewId: String,
        imageUris: List<Uri>,
        context: Context
    ): Result<List<String>> {
        return try {
            val uploadResults = mutableListOf<String>()

            imageUris.forEach { uri ->
                val result = uploadReviewImage(plannerId, tripId, reviewId, uri, context)
                if (result.isSuccess) {
                    uploadResults.add(result.getOrThrow())
                } else {
                    throw result.exceptionOrNull() ?: Exception("Upload failed")
                }
            }
            Log.d("ImageStorageModel", "Multiple review images uploaded successfully")
            Result.success(uploadResults)
        } catch (e: Exception) {
            Log.e("ImageStorageModel", "Multiple upload failed: ${e.message}")
            Result.failure(e)
        }
    }

    suspend fun deleteTripImage(imageUrl: String): Result<Unit> {
        return try {
            val filePath = extractTripFilePathFromUrl(imageUrl)
            Log.d("ImageStorageModel", "Deleting image at path: $filePath")

            Collections.travelImagesBucket.delete(filePath)

            Log.d("ImageStorageModel", "Image deleted successfully")
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e("ImageStorageModel", "Delete failed: ${e.message}")
            Result.failure(e)
        }
    }

    suspend fun deleteTripImages(userId: String, tripId: String): Result<Unit> {
        return try {
            val folderPath = "$userId/$tripId"
            Log.d("ImageStorageModel", "Deleting all images in trip folder: $folderPath")

            // List all files in the trip folder
            val files = Collections.travelImagesBucket.list(folderPath)

            // Delete each file
            files.forEach { file ->
                Collections.travelImagesBucket.delete("$folderPath/${file.name}")
            }

            Log.d("ImageStorageModel", "All trip images deleted successfully")
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e("ImageStorageModel", "Delete trip images failed: ${e.message}")
            Result.failure(e)
        }
    }

    suspend fun deleteReviewImages(userId: String, tripId: String, reviewId: String): Result<Unit> {
        return try {
            val folderPath = "$userId/$tripId/reviews/$reviewId"
            Log.d("ImageStorageModel", "Deleting all images in review folder: $folderPath")

            // List all files in the trip folder
            val files = Collections.travelImagesBucket.list(folderPath)

            // Delete each file
            files.forEach { file ->
                Collections.travelImagesBucket.delete("$folderPath/${file.name}")
            }

            Log.d("ImageStorageModel", "All review images deleted successfully")
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e("ImageStorageModel", "Delete review images failed: ${e.message}")
            Result.failure(e)
        }
    }

    private fun extractTripFilePathFromUrl(url: String): String {
        // Extract file path from Supabase public URL
        return url.substringAfter(Collections.TRAVEL_IMAGES_BUCKET_PREFIX)
    }

    fun validateTripFolderStructure(userId: String, tripId: String): Boolean {
        return userId.isNotBlank() && tripId.isNotBlank() &&
                !userId.contains("/") && !tripId.contains("/")
    }

    fun validateReviewFolderStructure(userId: String, tripId: String, reviewId: String): Boolean {
        return userId.isNotBlank() && tripId.isNotBlank() && reviewId.isNotBlank() &&
                !userId.contains("/") && !tripId.contains("/") && !reviewId.contains("/")
    }
}