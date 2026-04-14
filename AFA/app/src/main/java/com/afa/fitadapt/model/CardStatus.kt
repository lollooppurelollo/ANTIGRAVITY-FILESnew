// =============================================================
// AFA - Attività Fisica Adattata
// Stato di una scheda di allenamento
// =============================================================
package com.afa.fitadapt.model

/**
 * Rappresenta lo stato di una scheda (Training Card).
 *
 * PENDING  → scheda programmata per il futuro, non ancora iniziata
 * ACTIVE   → scheda attualmente in uso dalla paziente
 * COMPLETED → scheda completata (tutte le sedute fatte o durata scaduta)
 */
enum class CardStatus {
    PENDING,
    ACTIVE,
    COMPLETED
}
