package com.kinapto.fitadapt.util

import com.kinapto.fitadapt.model.KinAptoCRF
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
        // Header esteso per coprire diversi strumenti
        sb.append("record_id,redcap_event_name,redcap_repeat_instrument,redcap_repeat_instance,")
        sb.append("date,time,value_1,value_2,value_3,value_4,text_content,completed_flag,notes\n")
        
        val patientId = crf.metadata.patientStudyCode
        val dfDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val dfTime = SimpleDateFormat("HH:mm", Locale.getDefault())

        // 1. Evento Baseline (Metadata e Profilo)
        sb.append("$patientId,baseline,,,")
        sb.append("${dfDate.format(Date(crf.metadata.exportTimestamp))},${dfTime.format(Date(crf.metadata.exportTimestamp))},")
        sb.append(",,,,KinApto Export ${crf.metadata.appVersion},1,\n")

        // 2. Sessioni di allenamento (Instrument: exercise_session)
        crf.performedSessions.forEachIndexed { index, session ->
            val date = dfDate.format(Date(session.date))
            val time = dfTime.format(Date(session.date))
            val notes = (session.notes ?: "").replace("\n", " ").replace(",", ";")
            sb.append("$patientId,exercise_session,exercise_session,${index + 1},")
            sb.append("$date,$time,${session.actualDurationMin ?: ""},${session.perceivedEffort ?: ""},${session.mood ?: ""},${session.sleepQuality ?: ""},,${if (session.completed) 1 else 0},$notes\n")
        }

        // 3. Scale Rapide (Instrument: daily_scales)
        crf.scaleEntries.forEachIndexed { index, scale ->
            val date = dfDate.format(Date(scale.date))
            sb.append("$patientId,daily_scales,daily_scales,${index + 1},")
            sb.append("$date,,${scale.asthenia ?: ""},${scale.osteoarticularPain ?: ""},${scale.restDyspnea ?: ""},${scale.exertionDyspnea ?: ""},,, \n")
        }

        // 4. Diario Libero (Instrument: diary_entry)
        crf.diaryEntries.forEachIndexed { index, entry ->
            val date = dfDate.format(Date(entry.date))
            val text = entry.text.replace("\n", " ").replace(",", ";")
            sb.append("$patientId,diary_entry,diary_entry,${index + 1},")
            sb.append("$date,,,,,,$text,, \n")
        }

        // 5. Audit Log (Instrument: audit_trail)
        crf.auditLog.forEachIndexed { index, log ->
            val date = dfDate.format(Date(log.timestamp))
            val time = dfTime.format(Date(log.timestamp))
            val details = (log.details ?: "").replace("\n", " ").replace(",", ";")
            sb.append("$patientId,audit_trail,audit_trail,${index + 1},")
            sb.append("$date,$time,,,,,,${log.action},$details\n")
        }
        
        return sb.toString()
    }

    private fun generateDataDictionary(): String {
        val sb = StringBuilder()
        sb.append("Variable / Field Name,Form Name,Section Header,Field Type,Field Label,Choices, Unit, List enumerations,Field Note,Text Validation Type,Text Validation Min,Text Validation Max,Identifier,Branching Logic,Required Field,Custom Alignment,Question Number,Matrix Group Name,Matrix Ranking,Field Annotation\n")
        sb.append("record_id,profile,,text,Record ID,,,,,,,,,,,,,,,,\n")
        sb.append("date,exercise_session,,text,Data,,,,date_ymd,,,,,,,,,,,,\n")
        sb.append("value_1,exercise_session,,text,Valore 1 (Durata/Astenia),,,,,,,,,,,,,,,,\n")
        sb.append("value_2,exercise_session,,text,Valore 2 (RPE/Dolore),,,,,,,,,,,,,,,,\n")
        sb.append("text_content,diary_entry,,notes,Contenuto Testuale,,,,,,,,,,,,,,,,\n")
        sb.append("completed_flag,exercise_session,,yesno,Completato,,,,,,,,,,,,,,,,\n")
        return sb.toString()
    }
}
