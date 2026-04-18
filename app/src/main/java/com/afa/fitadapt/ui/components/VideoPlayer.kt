// =============================================================
// AFA - Attività Fisica Adattata
// Componente: Riproduttore Video (ExoPlayer)
// =============================================================
package com.afa.fitadapt.ui.components

import androidx.annotation.OptIn
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.PlayerView

/**
 * Componente Compose per riprodurre video locali (assets o file interni).
 * Incapsula ExoPlayer e PlayerView.
 *
 * @param videoUri Percorso del video (es. "asset:///videos/cammino.mp4")
 * @param modifier Modificatore per il layout
 */
@OptIn(UnstableApi::class)
@Composable
fun VideoPlayer(
    videoUri: String,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    // Inizializza ExoPlayer
    val exoPlayer = remember {
        ExoPlayer.Builder(context).build().apply {
            repeatMode = Player.REPEAT_MODE_ONE
            playWhenReady = false // Non avviare automaticamente per risparmiare dati/batteria
        }
    }

    // Gestione del ciclo di vita: rilascia il player quando il composable viene rimosso
    DisposableEffect(videoUri) {
        val mediaItem = MediaItem.fromUri(videoUri)
        exoPlayer.setMediaItem(mediaItem)
        exoPlayer.prepare()

        onDispose {
            exoPlayer.release()
        }
    }

    // Integrazione della vista Android (PlayerView) in Compose
    AndroidView(
        factory = { ctx ->
            PlayerView(ctx).apply {
                player = exoPlayer
                useController = true // Mostra i controlli (play/pause/barra)
                setShowNextButton(false)
                setShowPreviousButton(false)
            }
        },
        modifier = modifier.fillMaxSize()
    )
}
