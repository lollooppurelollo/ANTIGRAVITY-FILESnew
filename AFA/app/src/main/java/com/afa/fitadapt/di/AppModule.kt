// =============================================================
// AFA - Attività Fisica Adattata
// Modulo Hilt: Dipendenze generali dell'app
// =============================================================
package com.afa.fitadapt.di

import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

/**
 * Modulo Hilt per le dipendenze generali dell'app.
 *
 * Attualmente vuoto perché:
 * - Il database è gestito da DatabaseModule
 * - La sicurezza è gestita da SecurityModule
 * - I repository usano @Inject constructor()
 *
 * Si usa questo modulo per dipendenze future che non rientrano
 * nelle altre categorie (es. serializer, utility condivise, ecc.).
 */
@Module
@InstallIn(SingletonComponent::class)
object AppModule {
    // Le dipendenze verranno aggiunte qui quando necessario
}
