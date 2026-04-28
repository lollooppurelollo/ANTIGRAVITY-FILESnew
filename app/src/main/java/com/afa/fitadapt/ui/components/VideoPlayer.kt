// =============================================================
// AFA - Attività Fisica Adattata
// Componente: Riproduttore Video (ExoPlayer)
// =============================================================
package com.afa.fitadapt.ui.components

import androidx.annotation.OptIn
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.VideocamOff
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
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
    videoUri: String?,
    modifier: Modifier = Modifier,
    isPlaying: Boolean = false
) {
    val context = LocalContext.current

    if (videoUri.isNullOrEmpty()) {
        Box(
            modifier = modifier
                .fillMaxSize()
                .background(Color.Black),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Outlined.VideocamOff,
                contentDescription = "Video non disponibile",
                tint = Color.White.copy(alpha = 0.3f),
                modifier = Modifier.size(48.dp)
            )
        }
        return
    }

    // Inizializza ExoPlayer
    val exoPlayer = remember {
        ExoPlayer.Builder(context).build().apply {
            repeatMode = Player.REPEAT_MODE_ONE
        }
    }

    // Rilascia il player quando il componente esce definitivamente dalla composizione
    DisposableEffect(Unit) {
        onDispose {
            exoPlayer.release()
        }
    }

    // Gestione del cambio URI: prepara il nuovo video senza distruggere l'istanza del player
    LaunchedEffect(videoUri) {
        if (!videoUri.isNullOrEmpty()) {
            val mediaItem = MediaItem.fromUri(videoUri)
            exoPlayer.setMediaItem(mediaItem)
            exoPlayer.prepare()
        }
    }

    // Sincronizza lo stato di riproduzione
    LaunchedEffect(isPlaying) {
        exoPlayer.playWhenReady = isPlaying
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
        modifier = modifier.fillMaxSize(),
        update = { view ->
            view.player = exoPlayer
        }
    )
}
