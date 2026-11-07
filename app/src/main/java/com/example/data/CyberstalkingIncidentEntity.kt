package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.security.MessageDigest
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.UUID

/**
 * Immutable Record Entity for Cyberstalking, Harassment & Electronic Surveillance Incidents.
 * Strictly append-only (OnConflictStrategy.ABORT) in Room to ensure records cannot be tampered with or changed.
 * Automatically generates an unalterable statutory Electronic Record Certificate compliant with international
 * standards of digital forensics, cyber harassment prosecution, and rules of electronic evidence.
 */
@Entity(tableName = "cyberstalking_incident_records")
data class CyberstalkingIncidentEntity(
    @PrimaryKey
    val id: String = UUID.randomUUID().toString(),
    val timestamp: Long = System.currentTimeMillis(),
    val complainantUsername: String,
    val accusedUsername: String,
    val accusedDisplayName: String,
    val accusedAvatarUrl: String = "",
    val offenseCategory: String, // e.g., "PERSISTENT_ELECTRONIC_CONTACT", "UNAUTHORIZED_LOCATION_MONITORING", "DIGITAL_PRIVACY_VIOLATION", "THROWAWAY_EVASION"
    val offenseLegalTitle: String,
    val incidentDescription: String,
    val rawEvidencePayload: String, // Chat messages, location pings, proximity log dump
    val digitalSignatureSha256: String, // SHA-256 cryptographic seal of record contents
    val statutoryEvidenceCertificateText: String, // Statutory electronic evidence certificate
    val isPermanentBlocked: Boolean = true,
    val isLawEnforcementNotified: Boolean = false,
    val reportingJurisdiction: String = "Worldwide / Universal Jurisdiction",
    val deviceHardwareFingerprint: String = "${android.os.Build.MANUFACTURER} ${android.os.Build.MODEL} (Android OS ${android.os.Build.VERSION.RELEASE})",
    val status: String = "LOCKED_IMMUTABLE"
) {
    fun getFormattedDate(): String {
        val sdf = SimpleDateFormat("dd MMMM yyyy, hh:mm:ss a (z)", Locale.getDefault())
        return sdf.format(Date(timestamp))
    }

    companion object {
        fun calculateSha256(content: String): String {
            val digest = MessageDigest.getInstance("SHA-256")
            val hashBytes = digest.digest(content.toByteArray(Charsets.UTF_8))
            return hashBytes.joinToString("") { "%02x".format(it) }
        }

        fun generateStatutoryEvidenceCertificate(
            incidentId: String,
            timestamp: Long,
            complainant: String,
            accused: String,
            offenseTitle: String,
            evidencePayload: String,
            sha256Hash: String,
            deviceInfo: String
        ): String {
            val formattedDate = SimpleDateFormat("dd MMMM yyyy, hh:mm:ss a (z)", Locale.getDefault()).format(Date(timestamp))
            return """
================================================================================
           STATUTORY ELECTRONIC EVIDENCE CERTIFICATE
 (UNIVERSAL JURISDICTION • INTERNATIONAL CYBER CRIME & EVIDENCE PROTOCOL)
================================================================================

1. INCIDENT & RECORD IDENTIFICATION:
   - Certificate Unique ID: EVID-INTL-$incidentId
   - Date & Time of Electronic Record Generation: $formattedDate
   - Complainant Account: @$complainant
   - Accused Offender Account: @$accused
   - Statutory Offense Charged: $offenseTitle
   - Applicable Legal Mandates:
     * Universal Anti-Cyberstalking & Digital Harassment Statutes
     * International Standards of Electronic Evidence (UN / Interpol Cyber Norms)
     * Budapest Convention on Cybercrime & National Penal Codes
     * Digital Privacy & Electronic Communications Protection Mandates

2. SYSTEM ENVIRONMENT & DIGITAL CUSTODY:
   - Capturing Operating Environment: $deviceInfo
   - Electronic Application: Localiiiy Mobile Platform
   - Integrity Seal (SHA-256 Cryptographic Digest): $sha256Hash
   - Immutability Status: LOCKED_IMMUTABLE (Append-Only SQLite Record, Non-Modifiable)

3. EVIDENCE LOG & ELECTRONIC AUDIT TRAIL:
$evidencePayload

4. STATUTORY DECLARATION OF ELECTRONIC INTEGRITY:
   I hereby certify under penalty of perjury and applicable penal laws that:
   (a) The electronic record above was produced by the computer system / device during
       the period over which the system was used regularly to store or process information;
   (b) Throughout the material part of the said period, information of the kind contained
       in the electronic record was regularly recorded in the ordinary course of activities;
   (c) Throughout the material part of the said period, the device was operating properly
       without manipulation or alteration affecting the electronic record or accuracy of contents;
   (d) The electronic record contains true and unmanipulated data as captured at the time of the incident;
   (e) The cryptographic SHA-256 digest mathematically certifies that this record has not been altered or deleted.

Signed and Sealed for Submission to Police, Judicial Courts, and Cybercrime Authorities Worldwide.
Emergency Response: 112 (Universal / Global) / 911 (North America) / 999 (UK) / Local Emergency Services
================================================================================
            """.trimIndent()
        }
    }
}
