package com.kinapto.fitadapt.util

import com.kinapto.fitadapt.model.KinAptoCRF
import java.io.File
import java.io.FileOutputStream
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale
import java.util.zip.ZipEntry
import java.util.zip.ZipOutputStream
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.security.MessageDigest

/**
 * Utility per generare l'esportazione compatibile con REDCap.
 * SECURITY & COMPLIANCE: v1.1 - DateTimeFormatter thread-safe, audit trail completo, dizionario espanso.
 */
object RedCapExportUtils {

    private val json = Json { prettyPrint = true }
    private val romeZone = ZoneId.of("Europe/Rome")
    private val dfDate = DateTimeFormatter.ofPattern("yyyy-MM-dd", Locale.US)
    private val dfTime = DateTimeFormatter.ofPattern("HH:mm", Locale.US)
    private val dfFile = DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm", Locale.US)

    fun generateRedCapZip(crf: KinAptoCRF, outputDir: File, cryptoUtils: CrfCryptoUtils): File {
        val now = Instant.now()
        val timestampStr = now.atZone(romeZone).format(dfFile)
        val fileName = "KINAPTO_REDCAP_EXPORT_${crf.metadata.patientStudyCode}_$timestampStr.zip"
        val zipFile = File(outputDir, fileName)
        
        if (!outputDir.exists()) outputDir.mkdirs()

        ZipOutputStream(FileOutputStream(zipFile)).use { zos ->
            // 1. crf_complete.json
            val jsonContent = json.encodeToString(crf)
            addToZip(zos, "crf_complete.json", jsonContent.toByteArray())

            // 2. redcap_import.csv
            val csvContent = generateCsv(crf)
            addToZip(zos, "redcap_import.csv", csvContent.toByteArray())

            // 3. data_dictionary.csv
            val dictionaryContent = generateDataDictionary()
            addToZip(zos, "data_dictionary.csv", dictionaryContent.toByteArray())

            // 4. README.txt
            val readme = """
                Export KinApto per REDCap
                Paziente: ${crf.metadata.patientStudyCode}
                Export ID: ${crf.metadata.exportId}
                Data Export: ${Instant.ofEpochMilli(crf.metadata.exportTimestamp).atZone(romeZone).format(dfDate)} ${Instant.ofEpochMilli(crf.metadata.exportTimestamp).atZone(romeZone).format(dfTime)}
                App version: ${crf.metadata.appVersion}
                CRF schema version: 1.1
            """.trimIndent()
            addToZip(zos, "README.txt", readme.toByteArray())

            // 5. checksum.txt - SECURITY FIX: SHA-256 completo per Audit Trail
            val csvHash = sha256(csvContent)
            val jsonHash = sha256(jsonContent)
            val checksumContent = """
                redcap_import.csv SHA-256: $csvHash
                crf_complete.json SHA-256: $jsonHash
            """.trimIndent()
            addToZip(zos, "checksum.txt", checksumContent.toByteArray())
        }
        
        return zipFile
    }

    private fun sha256(input: String): String {
        val digest = MessageDigest.getInstance("SHA-256")
        val hashBytes = digest.digest(input.toByteArray(Charsets.UTF_8))
        return hashBytes.joinToString("") { "%02x".format(it) }
    }

    private fun addToZip(zos: ZipOutputStream, fileName: String, content: ByteArray) {
        val entry = ZipEntry(fileName)
        zos.putNextEntry(entry)
        zos.write(content)
        zos.closeEntry()
    }

    private fun generateCsv(crf: KinAptoCRF): String {
        val sb = StringBuilder()
        // Header allineato con Data Dictionary e Entity
        sb.append("record_id,redcap_event_name,redcap_repeat_instrument,redcap_repeat_instance,")
        sb.append("date,time,actual_duration,perceived_effort,asthenia,osteoarticular_pain,rest_dyspnea,exertion_dyspnea,")
        sb.append("mood,sleep_quality,nausea,appetite,anxiety,lymphoedema,quality_of_life,well_being,spo2,heart_rate,")
        sb.append("text_content,completed_flag,notes\n")
        
        val patientId = crf.metadata.patientStudyCode

        // 1. Evento Baseline
        val baselineDate = Instant.ofEpochMilli(crf.metadata.exportTimestamp).atZone(romeZone).format(dfDate)
        val baselineTime = Instant.ofEpochMilli(crf.metadata.exportTimestamp).atZone(romeZone).format(dfTime)
        sb.append("$patientId,baseline,,,")
        sb.append("$baselineDate,$baselineTime,,,,,,,,,,,,,,,,,,KinApto Export ${crf.metadata.appVersion},1,\n")

        // 2. Sessioni di allenamento (Instrument: exercise_session)
        crf.performedSessions.forEachIndexed { index, session ->
            val date = Instant.ofEpochMilli(session.date).atZone(romeZone).format(dfDate)
            val time = Instant.ofEpochMilli(session.date).atZone(romeZone).format(dfTime)
            val notes = (session.notes ?: "").replace("\n", " ").replace(",", ";")
            sb.append("$patientId,exercise_session,exercise_session,${index + 1},")
            sb.append("$date,$time,${session.actualDurationMin ?: ""},${session.perceivedEffort ?: ""},")
            sb.append("${session.asthenia ?: ""},${session.osteoarticularPain ?: ""},${session.restDyspnea ?: ""},${session.exertionDyspnea ?: ""},")
            sb.append("${session.mood ?: ""},${session.sleepQuality ?: ""},${session.nausea ?: ""},${session.appetite ?: ""},")
            sb.append("${session.anxiety ?: ""},${session.lymphoedema ?: ""},${session.qualityOfLife ?: ""},${session.wellBeing ?: ""},")
            sb.append("${session.spo2 ?: ""},${session.heartRate ?: ""},,${if (session.completed) 1 else 0},$notes\n")
        }

        // 3. Scale Rapide (Instrument: daily_scales)
        crf.scaleEntries.forEachIndexed { index, scale ->
            val date = Instant.ofEpochMilli(scale.date).atZone(romeZone).format(dfDate)
            sb.append("$patientId,daily_scales,daily_scales,${index + 1},")
            sb.append("$date,,${""},${scale.perceivedEffort ?: ""},")
            sb.append("${scale.asthenia ?: ""},${scale.osteoarticularPain ?: ""},${scale.restDyspnea ?: ""},${scale.exertionDyspnea ?: ""},")
            sb.append("${scale.mood ?: ""},${scale.sleepQuality ?: ""},${scale.nausea ?: ""},${scale.appetite ?: ""},")
            sb.append("${scale.anxiety ?: ""},${scale.lymphoedema ?: ""},${scale.qualityOfLife ?: ""},${scale.wellBeing ?: ""},")
            sb.append("${scale.spo2 ?: ""},${scale.heartRate ?: ""},,, \n")
        }

        // 4. Diario Libero (Instrument: diary_entry)
        crf.diaryEntries.forEachIndexed { index, entry ->
            val date = Instant.ofEpochMilli(entry.date).atZone(romeZone).format(dfDate)
            val text = entry.text.replace("\n", " ").replace(",", ";")
            sb.append("$patientId,diary_entry,diary_entry,${index + 1},")
            sb.append("$date,,,,,,,,,,,,,,,,,,$text,, \n")
        }

        // 5. Audit Log (Instrument: audit_trail)
        crf.auditLog.forEachIndexed { index, log ->
            val date = Instant.ofEpochMilli(log.timestamp).atZone(romeZone).format(dfDate)
            val time = Instant.ofEpochMilli(log.timestamp).atZone(romeZone).format(dfTime)
            val details = (log.details ?: "").replace("\n", " ").replace(",", ";")
            sb.append("$patientId,audit_trail,audit_trail,${index + 1},")
            sb.append("$date,$time,,,,,,${log.action},$details\n")
        }
        
        return sb.toString()
    }

    private fun generateDataDictionary(): String {
        val sb = StringBuilder()
        sb.append("Variable / Field Name,Form Name,Section Header,Field Type,Field Label,Choices, Unit, List enumerations,Field Note,Text Validation Type,Text Validation Min,Text Validation Max,Identifier,Branching Logic,Required Field,Custom Alignment,Question Number,Matrix Group Name,Matrix Ranking,Field Annotation\n")
        
        // Baseline / Profile
        sb.append("record_id,baseline,,text,Codice Paziente,,,,,,,,,,,,,,,,\n")
        
        // Form: exercise_session
        val forms = listOf(
            "date,exercise_session,,text,Data Sessione,,,,date_ymd,,,,,,,,,,,,",
            "time,exercise_session,,text,Ora Sessione,,,,time_hm,,,,,,,,,,,,",
            "actual_duration,exercise_session,,integer,Durata effettiva (min),,,,integer,0,300,,,,,,,,,,",
            "perceived_effort,exercise_session,,integer,Sforzo percepito (RPE 0-10),,,,integer,0,10,,,,,,,,,,",
            "asthenia,exercise_session,,integer,Astenia (0-10),,,,integer,0,10,,,,,,,,,,",
            "osteoarticular_pain,exercise_session,,integer,Dolore osteoarticolare (0-10),,,,integer,0,10,,,,,,,,,,",
            "rest_dyspnea,exercise_session,,integer,Dispnea a riposo (0-10),,,,integer,0,10,,,,,,,,,,",
            "exertion_dyspnea,exercise_session,,integer,Dispnea da sforzo (0-10),,,,integer,0,10,,,,,,,,,,",
            "mood,exercise_session,,integer,Umore (0-10),,,,integer,0,10,,,,,,,,,,",
            "sleep_quality,exercise_session,,integer,Qualità del sonno (0-10),,,,integer,0,10,,,,,,,,,,",
            "nausea,exercise_session,,integer,Nausea (0-10),,,,integer,0,10,,,,,,,,,,",
            "appetite,exercise_session,,integer,Appetito (0-10),,,,integer,0,10,,,,,,,,,,",
            "anxiety,exercise_session,,integer,Ansia (0-10),,,,integer,0,10,,,,,,,,,,",
            "lymphoedema,exercise_session,,integer,Linfedema (0-10),,,,integer,0,10,,,,,,,,,,",
            "quality_of_life,exercise_session,,integer,Qualità della vita (0-10),,,,integer,0,10,,,,,,,,,,",
            "well_being,exercise_session,,integer,Benessere generale (0-10),,,,integer,0,10,,,,,,,,,,",
            "spo2,exercise_session,,integer,Saturazione O2 (%),,,,integer,50,100,,,,,,,,,,",
            "heart_rate,exercise_session,,integer,Frequenza cardiaca (bpm),,,,integer,30,220,,,,,,,,,,",
            "completed_flag,exercise_session,,yesno,Sessione completata,,,,,,,,,,,,,,,,\n",
            "notes,exercise_session,,notes,Note cliniche,,,,,,,,,,,,,,,,",
            
            // Daily Scales (reuse names but different form)
            "date,daily_scales,,text,Data Scala,,,,date_ymd,,,,,,,,,,,,",
            "text_content,diary_entry,,notes,Testo diario,,,,,,,,,,,,,,,,",
            
            // Audit Trail
            "action,audit_trail,,text,Azione Audit,,,,,,,,,,,,,,,,",
            "details,audit_trail,,notes,Dettagli Audit,,,,,,,,,,,,,,,,"
        )
        
        forms.forEach { sb.append(it).append("\n") }

        return sb.toString()
    }
}
