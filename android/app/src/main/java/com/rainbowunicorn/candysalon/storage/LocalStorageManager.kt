package com.rainbowunicorn.candysalon.storage

import android.content.ContentValues
import android.content.Context
import android.graphics.Bitmap
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import com.rainbowunicorn.core.models.SaveData
import com.rainbowunicorn.core.services.SaveSerializer
import java.io.File
import java.io.FileOutputStream

class LocalStorageManager(private val context: Context) {

    companion object {
        const val MAX_GALLERY_PHOTOS = 50
        const val SAVE_FILE_NAME = "save_game.json"
        const val BACKUP_FILE_NAME = "save_game.bak"
        const val GALLERY_DIR_NAME = "candy_gallery"
    }

    private val saveFile: File get() = File(context.filesDir, SAVE_FILE_NAME)
    private val backupFile: File get() = File(context.filesDir, BACKUP_FILE_NAME)
    private val galleryDir: File get() = File(context.filesDir, GALLERY_DIR_NAME).apply { mkdirs() }

    fun loadSaveData(): SaveData {
        if (!saveFile.exists()) {
            return if (backupFile.exists() && backupFile.length() > 0) {
                try {
                    val backupContent = backupFile.readText(Charsets.UTF_8)
                    val recovered = SaveSerializer.fromJsonStrict(backupContent)
                    saveFile.writeText(SaveSerializer.toJson(recovered), Charsets.UTF_8)
                    recovered
                } catch (_: Exception) {
                    SaveData()
                }
            } else {
                SaveData()
            }
        }

        return try {
            val content = saveFile.readText(Charsets.UTF_8)
            val parsed = SaveSerializer.fromJsonStrict(content)
            if (content.isNotBlank() && (!backupFile.exists() || backupFile.length() == 0L)) {
                backupFile.writeText(content, Charsets.UTF_8)
            }
            parsed
        } catch (_: Exception) {
            // Corrupt primary file: fallback to backup file if available (RULE-091)
            try {
                if (backupFile.exists() && backupFile.length() > 0) {
                    val backupContent = backupFile.readText(Charsets.UTF_8)
                    val recovered = SaveSerializer.fromJsonStrict(backupContent)
                    saveFile.writeText(SaveSerializer.toJson(recovered), Charsets.UTF_8)
                    recovered
                } else {
                    SaveData()
                }
            } catch (_: Exception) {
                SaveData()
            }
        }
    }

    fun saveSaveData(data: SaveData) {
        try {
            val json = SaveSerializer.toJson(data)
            if (saveFile.exists() && saveFile.length() > 0) {
                try {
                    val existing = saveFile.readText(Charsets.UTF_8)
                    SaveSerializer.fromJsonStrict(existing)
                    backupFile.writeText(existing, Charsets.UTF_8)
                } catch (_: Exception) {
                    // Existing primary file was corrupt, overwrite backup with new valid state instead
                    backupFile.writeText(json, Charsets.UTF_8)
                }
            } else {
                backupFile.writeText(json, Charsets.UTF_8)
            }
            saveFile.writeText(json, Charsets.UTF_8)
        } catch (_: Exception) {}
    }

    fun getGalleryPhotos(): List<File> {
        val files = galleryDir.listFiles { file -> file.extension == "png" } ?: emptyArray()
        return files.sortedByDescending { it.lastModified() }
    }

    fun canAddPhoto(): Boolean {
        return getGalleryPhotos().size < MAX_GALLERY_PHOTOS
    }

    fun savePhotoToInternalGallery(bitmap: Bitmap): File? {
        if (!canAddPhoto()) return null
        val filename = "photo_${System.currentTimeMillis()}.png"
        val destFile = File(galleryDir, filename)
        return try {
            FileOutputStream(destFile).use { out ->
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, out)
            }
            destFile
        } catch (_: Exception) {
            null
        }
    }

    fun deletePhoto(file: File): Boolean {
        return try {
            if (file.exists() && file.parentFile == galleryDir) {
                file.delete()
            } else false
        } catch (_: Exception) {
            false
        }
    }

    fun exportPhotoToMediaStore(bitmap: Bitmap, title: String = "RainbowUnicorn"): Boolean {
        return try {
            val resolver = context.contentResolver
            val contentValues = ContentValues().apply {
                put(MediaStore.Images.Media.DISPLAY_NAME, "$title-${System.currentTimeMillis()}.png")
                put(MediaStore.Images.Media.MIME_TYPE, "image/png")
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    put(MediaStore.Images.Media.RELATIVE_PATH, Environment.DIRECTORY_PICTURES + "/RainbowUnicorn")
                    put(MediaStore.Images.Media.IS_PENDING, 1)
                }
            }

            val uri = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues) ?: return false
            resolver.openOutputStream(uri)?.use { out ->
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, out)
            }

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                contentValues.clear()
                contentValues.put(MediaStore.Images.Media.IS_PENDING, 0)
                resolver.update(uri, contentValues, null, null)
            }
            true
        } catch (_: Exception) {
            false
        }
    }
}
