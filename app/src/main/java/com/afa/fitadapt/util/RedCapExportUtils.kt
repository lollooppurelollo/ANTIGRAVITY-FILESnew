package com.afa.fitadapt.util

import com.afa.fitadapt.model.KinAptoCRF
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.zip.ZipEntry
import java.util.zip.ZipOutputStream
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

/**
 * Utility per generare l'esportazione compatibile con REDCap.
 */
object RedCapExportUtils {

    private val json = Json { prettyPrint = true }

    fun generateRedCapZip(crf: KinAptoCRF, outputDir: File): File {
        val timestamp = SimpleDateFormat("yyyy-MM-dd_HH-mm", Locale.getDefault()).format(Date())
        val fileName = "KINAPTO_REDCAP_EXPORT_${crf.metadata.patientStudyCode}_$timestamp.zip"
        val zipFile = File(outputDir, fileName)
        
        if (!outputDir.exists()) outputDir.mkdirs()

        ZipOutputStream(FileOutputStream(zipFile)).use { zos ->
            // 1. crf_complete.json
            addToZip(zos, "crf_complete.json", json.encodeToString(crf).toByteArray())

            // 2. redcap_import.csv
            val csvContent = generateCsv(crf)
            addToZip(zos, "redcap_import.csv", csvContent.toByteArray())

            // 3. data_dictionary.csv
            val dictionaryContent = generateDataDictionary()
            addToZip(zos, "data_dictionary.csv", dictionaryContent.toByteArray())

            // 4. README.txt
            val readme = "Export KinApto per REDCap\nPaziente: ${crf.metadata.patientStudyCode}\nData: ${crf.metadata.exportTimestamp}\n"
            addToZip(zos, "README.txt", readme.toByteArray())

            // 5. checksum.txt
            val checksum = CrfCryptoUtils.checksum(csvContent)
            addToZip(zos, "checksum.txt", "redcap_import.csv SHA-256 (part): $checksum".toByteArray())
        }
        
        return zipFile
    }

    private fun addToZip(zos: ZipOutputStream, fileName: String, content: ByteArray) {
        val entry = ZipEntry(fileName)
        zos.putNextEntry(entry)
        zos.write(content)
        zos.closeEntry()
    }

    private fun generateCsv(crf: KinAptoCRF): String {
        val sb = StringBuilder()
        // Header (semplificato per l'esempio, dovrebbe contenere tutti i campi necessari)
        sb.append("record_id,redcap_event_name,redcap_repeat_instrument,redcap_repeat_instance,session_date,session_time,actual_duration_min,perceived_intensity_rpe,completed,notes\n")
        
        val patientId = crf.metadata.patientStudyCode
        
        // Evento Baseline
        sb.append("$patientId,baseline,,, , , , , , \n")

        // Sessioni di allenamento (strumento ripetibile)
        crf.performedSessions.forEachIndexed { index, session ->
            val date = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date(session.date))
            val time = SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date(session.date))
            val completed = if (session.completed) "1" else "0"
            val duration = session.actualDurationMin ?: ""
            val rpe = session.perceivedEffort ?: ""
            val notes = (session.notes ?: "").replace("\n", " ").replace(",", ";")
            
            sb.append("$patientId,exercise_session,exercise_session,${index + 1},$date,$time,$duration,$rpe,$completed,$notes\n")
        }
        
        return sb.toString()
    }

    private fun generateDataDictionary(): String {
        val sb = StringBuilder()
        sb.append("Variable / Field Name,Form Name,Section Header,Field Type,Field Label,Choices, Unit, List enumerations,Field Note,Text Validation Type,Text Validation Min,Text Validation Max,Identifier,Branching Logic,Required Field,Custom Alignment,Question Number,Matrix Group Name,Matrix Ranking,Field Annotation\n")
        sb.append("record_id,profile,,text,Record ID,,,,,,,,,,,,,,,,\n")
        sb.append("session_date,exercise_session,,text,Data sessione,,,,date_ymd,,,,,,,,,,,,\n")
        sb.append("actual_duration_min,exercise_session,,text,Durata (min),,,,integer,,,,,,,,,,,,\n")
        sb.append("perceived_intensity_rpe,exercise_session,,text,RPE (0-10),,,,integer,0,10,,,,,,,,,,\n")
        sb.append("completed,exercise_session,,yesno,Completato,,,,,,,,,,,,,,,,\n")
        return sb.toString()
    }
}
