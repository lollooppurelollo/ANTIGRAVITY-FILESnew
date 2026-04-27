package com.afa.fitadapt.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import io.github.sceneview.SceneView
import io.github.sceneview.math.Position
import io.github.sceneview.node.ModelNode

/**
 * Composable per la visualizzazione di un avatar 3D animato (GLB).
 * Utilizza la libreria SceneView 2.2.1.
 */
@Composable
fun Avatar3DViewer(
    modelPath: String,
    animationPath: String?,
    animationName: String?,
    isPlaying: Boolean,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color.White),
        contentAlignment = Alignment.Center
    ) {
        if (modelPath.isEmpty()) {
            Text(text = "Modello non disponibile", color = Color.Gray)
        } else {
            AndroidView(
                modifier = Modifier.fillMaxSize(),
                factory = { ctx ->
                    SceneView(ctx).apply {
                        // Creazione del nodo del modello
                        // Se animationPath è presente, carichiamo quello per le animazioni, 
                        // altrimenti carichiamo il modello base.
                        val glbToLoad = animationPath ?: modelPath
                        
                        try {
                            val node = ModelNode(
                                modelInstance = modelLoader.createModelInstance(glbToLoad)
                            ).apply {
                                // Posizionamento: x=centrato, y=altezza suolo, z=distanza camera
                                position = Position(x = 0.0f, y = -0.8f, z = -3.5f)
                            }
                            
                            addChildNode(node)
                            
                            if (isPlaying && animationPath != null) {
                                if (animationName != null) {
                                    node.playAnimation(animationName, loop = true)
                                } else {
                                    node.playAnimation(0, loop = true)
                                }
                            }
                        } catch (e: Exception) {
                            // Gestito nel blocco update o tramite visualizzazione fallback
                        }
                    }
                },
                update = { sceneView ->
                    val node = sceneView.childNodes.filterIsInstance<ModelNode>().firstOrNull()
                    node?.let {
                        if (isPlaying && animationPath != null) {
                            if (animationName != null) {
                                it.playAnimation(animationName, loop = true)
                            } else {
                                it.playAnimation(0, loop = true)
                            }
                        } else {
                            it.stopAnimation(0)
                        }
                    }
                }
            )
        }
        
        // Badge 3D per feedback visivo
        Box(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(12.dp)
                .background(Color.Black.copy(alpha = 0.05f), MaterialTheme.shapes.small)
                .padding(horizontal = 8.dp, vertical = 4.dp)
        ) {
            Text(
                text = "3D",
                style = MaterialTheme.typography.labelSmall,
                color = Color.Gray
            )
        }

        if (animationPath == null) {
            Box(
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .padding(12.dp)
                    .background(Color.Yellow.copy(alpha = 0.2f), MaterialTheme.shapes.small)
                    .padding(horizontal = 8.dp, vertical = 4.dp)
            ) {
                Text(
                    text = "Animazione non disponibile",
                    style = MaterialTheme.typography.labelSmall,
                    color = Color.DarkGray
                )
            }
        }
    }
}
