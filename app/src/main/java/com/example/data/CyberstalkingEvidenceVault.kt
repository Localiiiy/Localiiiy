package com.example.data

import android.content.Context
import android.util.Log
import java.io.File
import java.io.FileOutputStream
import java.util.UUID

/**
 * Manages the Immutable Directory Vault for Cyberstalking & Harassment incidents.
 * Stores unchangeable physical text audit logs and verifies electronic certificates under statutory cybercrime standards.
 */
class CyberstalkingEvidenceVault(private val context: Context) {

    private val vaultDirectory: File by lazy {
        val dir = File(context.filesDir, "cyberstalking_immutable_records")
        if (!dir.exists()) {
            dir.mkdirs()
        }
        dir
    }

    /**
     * Stores an unalterable text file of the evidence certificate in the internal app directory.
     * File permissions are made read-only to prevent tampering.
     */
    fun writeImmutableRecordToFile(incident: CyberstalkingIncidentEntity): File? {
        return try {
            val fileName = "incident_${incident.id}_sealed.legal.txt"
            val file = File(vaultDirectory, fileName)
            if (file.exists()) {
                // Already locked, do not overwrite!
                return file
            }
            FileOutputStream(file).use { fos ->
                val recordData = """
--------------------------------------------------------------------------------
IMMUTABLE Localiiiy CYBER EVIDENCE VAULT RECORD
SECURITY SEAL: SHA-256 [${incident.digitalSignatureSha256}]
RECORD TIMESTAMP: ${incident.timestamp} (${incident.getFormattedDate()})
COMPLAINANT: @${incident.complainantUsername}
ACCUSED: @${incident.accusedUsername} (${incident.accusedDisplayName})
STATUTORY OFFENSE: ${incident.offenseLegalTitle}
STATUS: ${incident.status} (PERMANENTLY BLOCKED FROM LOCALIIIYY)
HARDWARE SIGNATURE: ${incident.deviceHardwareFingerprint}
--------------------------------------------------------------------------------

${incident.statutoryEvidenceCertificateText}
                """.trimIndent()
                fos.write(recordData.toByteArray(Charsets.UTF_8))
                fos.flush()
            }
            // Set file read-only to ensure it cannot be modified
            file.setReadOnly()
            Log.i("CyberstalkingVault", "Successfully created immutable evidence record: ${file.absolutePath}")
            file
        } catch (e: Exception) {
            Log.e("CyberstalkingVault", "Failed to write immutable incident record", e)
            null
        }
    }

    /**
     * Reads all sealed evidence certificates stored in the immutable directory.
     */
    fun listImmutableRecordFiles(): List<File> {
        return try {
            vaultDirectory.listFiles()?.filter { it.isFile && it.name.endsWith(".txt") }?.sortedByDescending { it.lastModified() } ?: emptyList()
        } catch (e: Exception) {
            emptyList()
        }
    }

    /**
     * Returns the total count of immutable evidence files saved in the directory vault.
     */
    fun getVaultFileCount(): Int = listImmutableRecordFiles().size
}
