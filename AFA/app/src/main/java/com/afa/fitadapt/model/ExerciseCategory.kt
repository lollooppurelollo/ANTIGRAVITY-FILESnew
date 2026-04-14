// =============================================================
// AFA - Attività Fisica Adattata
// Categorie degli esercizi disponibili nella libreria
// =============================================================
package com.afa.fitadapt.model

/**
 * Categorie di esercizi precaricati nell'app.
 * Ogni esercizio appartiene a una di queste categorie.
 *
 * @property displayName il nome mostrato nell'interfaccia all'utente
 */
enum class ExerciseCategory(val displayName: String) {
    CAMMINO_STANDARD("Cammino standard"),
    CAMMINO_VELOCE("Cammino veloce"),
    CORSA("Corsa"),
    ESERCIZI_AEROBICI("Esercizi aerobici"),
    RINFORZO_MUSCOLARE("Rinforzo muscolare"),
    STRETCHING_MOBILITA("Stretching e mobilità"),
    RESPIRAZIONE_RILASSAMENTO("Respirazione e rilassamento"),
    ALTRO("Altro")
}
