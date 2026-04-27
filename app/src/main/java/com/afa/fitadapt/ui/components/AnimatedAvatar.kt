package com.afa.fitadapt.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.withTransform
import androidx.compose.ui.unit.dp
import java.util.*
import kotlin.math.PI
import kotlin.math.abs
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin

enum class MovementType {
    WALKING, SQUAT, LUNGE, ARMS, FLOOR_BRIDGE, FLOOR_PRONE, BREATHING, STRETCHING, VICTORY
}

@Composable
fun AnimatedAvatar(
    exerciseName: String,
    category: String,
    isRunning: Boolean,
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "avatar")
    
    val movementType = remember(exerciseName, category) {
        val name = exerciseName.lowercase(Locale.ROOT)
        val cat = category.uppercase(Locale.ROOT)
        
        when {
            name.contains("vittoria") || name.contains("successo") || name.contains("celebrate") || name.contains("progressi") -> MovementType.VICTORY
            name.contains("respirazione") || name.contains("body scan") || name.contains("mindfulness") -> MovementType.BREATHING
            name.contains("ponte") -> MovementType.FLOOR_BRIDGE
            name.contains("plank") || name.contains("piegamenti") || name.contains("terra") -> {
                if (name.contains("ponte")) MovementType.FLOOR_BRIDGE else MovementType.FLOOR_PRONE
            }
            name.contains("squat") || name.contains("seduto") -> MovementType.SQUAT
            name.contains("affondi") || name.contains("lunge") -> MovementType.LUNGE
            name.contains("cammino") || name.contains("corsa") || name.contains("marcia") || name.contains("nordic") || name.contains("passo") -> MovementType.WALKING
            (cat.contains("RINFORZO") || cat.contains("TONIFICAZIONE")) && (name.contains("braccia") || name.contains("alzate") || name.contains("curl") || name.contains("manubri")) -> MovementType.ARMS
            cat.contains("STRETCHING") || cat.contains("MOBILITA") || name.contains("allungamento") -> MovementType.STRETCHING
            else -> MovementType.WALKING
        }
    }

    val duration = when (movementType) {
        MovementType.VICTORY -> 1500
        MovementType.WALKING -> if (exerciseName.lowercase().contains("veloce") || exerciseName.lowercase().contains("corsa")) 800 else 1200
        MovementType.SQUAT, MovementType.LUNGE -> 3000
        MovementType.BREATHING -> 4000
        MovementType.FLOOR_BRIDGE, MovementType.ARMS -> 2500
        else -> 2000
    }

    val phase by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(duration, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "phase"
    )

    // Palette "High-Fi Soft Clay" (Fedele alla terza foto)
    val skinBase = Color(0xFFE5A880)
    val skinDark = Color(0xFFC7855C)
    val skinLight = Color(0xFFF7D2B9)
    val clothingTop = Color(0xFFBDBDBD)
    val clothingShorts = Color(0xFF424242)
    val hairColor = Color(0xFF5E3A29)
    val eyeColor = Color(0xFF212121)

    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.fillMaxSize().padding(16.dp)) {
            val anim = if (isRunning) phase else 0f
            val w = size.width
            val h = size.height
            val centerX = w / 2
            val centerY = h * 0.5f
            
            // --- DRAWING HELPERS ---

            fun drawOrganicLimb(
                start: Offset,
                end: Offset,
                widthStart: Float,
                widthEnd: Float,
                color: Color,
                isSkin: Boolean = true
            ) {
                val vector = end - start
                val length = vector.getDistance()
                if (length < 1f) return
                
                val unitVector = vector / length
                val normal = Offset(-unitVector.y, unitVector.x)
                
                val path = Path().apply {
                    val p1 = start + normal * (widthStart / 2)
                    val p2 = start - normal * (widthStart / 2)
                    val p3 = end - normal * (widthEnd / 2)
                    val p4 = end + normal * (widthEnd / 2)
                    
                    moveTo(p1.x, p1.y)
                    // Curve per dare volume "muscolare"
                    val ctrl1 = start + vector * 0.5f + normal * (widthStart * 0.6f / 2)
                    val ctrl2 = start + vector * 0.5f - normal * (widthStart * 0.6f / 2)
                    
                    quadraticBezierTo(ctrl1.x, ctrl1.y, p4.x, p4.y)
                    lineTo(p3.x, p3.y)
                    quadraticBezierTo(ctrl2.x, ctrl2.y, p2.x, p2.y)
                    close()
                }

                // Volume 3D con Gradiente
                val brush = if (isSkin) {
                    Brush.linearGradient(
                        0.0f to skinLight,
                        0.5f to skinBase,
                        1.0f to skinDark,
                        start = start + normal * (widthStart / 2),
                        end = start - normal * (widthStart / 2)
                    )
                } else {
                    Brush.linearGradient(
                        listOf(color.copy(alpha = 0.8f), color, color.copy(alpha = 1.2f).compositeOver(Color.Black)),
                        start = start + normal * (widthStart / 2),
                        end = start - normal * (widthStart / 2)
                    )
                }

                drawPath(path, brush)
                
                // Rim Light soft
                drawPath(
                    path,
                    Brush.linearGradient(
                        0.0f to Color.White.copy(alpha = 0.3f),
                        0.2f to Color.Transparent,
                        start = start + normal * (widthStart / 2.2f),
                        end = start
                    )
                )
            }

            fun draw3DHead(center: Offset, rotation: Float) {
                withTransform({
                    rotate(rotation, center)
                }) {
                    val headWidth = 38.dp.toPx()
                    val headHeight = 46.dp.toPx()
                    
                    // Collo
                    drawOrganicLimb(
                        center.copy(y = center.y + 10.dp.toPx()),
                        center.copy(y = center.y + 25.dp.toPx()),
                        18.dp.toPx(), 22.dp.toPx(), skinBase
                    )
                    
                    // Viso (Ovale morbido)
                    val faceRect = Rect(center.x - headWidth/2, center.y - headHeight/2, center.x + headWidth/2, center.y + headHeight/2)
                    drawOval(
                        brush = Brush.radialGradient(
                            0.0f to skinLight,
                            0.7f to skinBase,
                            1.0f to skinDark,
                            center = center - Offset(headWidth*0.2f, headHeight*0.2f),
                            radius = headWidth
                        ),
                        topLeft = faceRect.topLeft,
                        size = faceRect.size
                    )
                    
                    // Capelli (Stile terza foto: riga in mezzo e volumi laterali)
                    val hairPath = Path().apply {
                        moveTo(center.x - headWidth/2 - 2.dp.toPx(), center.y)
                        cubicTo(
                            center.x - headWidth/2, center.y - headHeight * 0.7f,
                            center.x + headWidth/2, center.y - headHeight * 0.7f,
                            center.x + headWidth/2 + 2.dp.toPx(), center.y
                        )
                        lineTo(center.x, center.y - headHeight * 0.1f)
                        close()
                    }
                    drawPath(hairPath, hairColor)
                    
                    // Code/Chignon bassi
                    drawCircle(hairColor, 12.dp.toPx(), Offset(center.x - headWidth/2, center.y + 15.dp.toPx()))
                    drawCircle(hairColor, 12.dp.toPx(), Offset(center.x + headWidth/2, center.y + 15.dp.toPx()))

                    // Occhi Grandi ed Espressivi
                    val eyeY = center.y + 2.dp.toPx()
                    val eyeXOffset = 10.dp.toPx()
                    val eyeSize = 7.dp.toPx()
                    
                    // Sclera
                    drawCircle(Color.White, eyeSize, Offset(center.x - eyeXOffset, eyeY))
                    drawCircle(Color.White, eyeSize, Offset(center.x + eyeXOffset, eyeY))
                    // Pupilla
                    drawCircle(eyeColor, eyeSize * 0.7f, Offset(center.x - eyeXOffset, eyeY + 1.dp.toPx()))
                    drawCircle(eyeColor, eyeSize * 0.7f, Offset(center.x + eyeXOffset, eyeY + 1.dp.toPx()))
                    // Riflesso
                    drawCircle(Color.White, 2.dp.toPx(), Offset(center.x - eyeXOffset - 2.dp.toPx(), eyeY - 2.dp.toPx()))
                    drawCircle(Color.White, 2.dp.toPx(), Offset(center.x + eyeXOffset - 2.dp.toPx(), eyeY - 2.dp.toPx()))
                    
                    // Guance (Blush)
                    drawCircle(Color(0xFFFF8A80).copy(alpha = 0.3f), 6.dp.toPx(), Offset(center.x - 14.dp.toPx(), eyeY + 10.dp.toPx()))
                    drawCircle(Color(0xFFFF8A80).copy(alpha = 0.3f), 6.dp.toPx(), Offset(center.x + 14.dp.toPx(), eyeY + 10.dp.toPx()))
                }
            }

            fun draw3DTorso(
                shoulderL: Offset, shoulderR: Offset, 
                hipL: Offset, hipR: Offset,
                animPhase: Float = 0f
            ) {
                val midShoulder = (shoulderL + shoulderR) / 2f
                val midHip = (hipL + hipR) / 2f
                val waistY = midShoulder.y + (midHip.y - midShoulder.y) * 0.5f
                val waistWidth = (shoulderR.x - shoulderL.x) * 0.75f
                
                // Path del corpo intero
                val bodyPath = Path().apply {
                    moveTo(shoulderL.x, shoulderL.y)
                    // Spalle arrotondate
                    quadraticBezierTo(midShoulder.x, midShoulder.y - 4.dp.toPx(), shoulderR.x, shoulderR.y)
                    // Fianco destro con curva a clessidra
                    cubicTo(
                        shoulderR.x + 8.dp.toPx(), waistY,
                        midHip.x + waistWidth/2 + 12.dp.toPx(), waistY,
                        hipR.x, hipR.y
                    )
                    lineTo(hipL.x, hipL.y)
                    // Fianco sinistro
                    cubicTo(
                        midHip.x - waistWidth/2 - 12.dp.toPx(), waistY,
                        shoulderL.x - 8.dp.toPx(), waistY,
                        shoulderL.x, shoulderL.y
                    )
                    close()
                }

                // Pelle
                drawPath(bodyPath, Brush.linearGradient(listOf(skinLight, skinBase, skinDark), shoulderL, hipR))
                
                // Top Sportivo (Crop Top)
                val topPath = Path().apply {
                    val topBottomY = midShoulder.y + (midHip.y - midShoulder.y) * 0.4f
                    moveTo(shoulderL.x - 1.dp.toPx(), shoulderL.y)
                    quadraticBezierTo(midShoulder.x, midShoulder.y - 4.dp.toPx(), shoulderR.x + 1.dp.toPx(), shoulderR.y)
                    lineTo(shoulderR.x + 2.dp.toPx(), topBottomY)
                    quadraticBezierTo(midShoulder.x, topBottomY + 5.dp.toPx(), shoulderL.x - 2.dp.toPx(), topBottomY)
                    close()
                }
                drawPath(topPath, Brush.verticalGradient(listOf(clothingTop, clothingTop.copy(alpha = 0.8f))))

                // Pantaloncini (High waist)
                val shortsPath = Path().apply {
                    val shortsTopY = midShoulder.y + (midHip.y - midShoulder.y) * 0.65f
                    moveTo(midHip.x - waistWidth/2 - 5.dp.toPx(), shortsTopY)
                    lineTo(midHip.x + waistWidth/2 + 5.dp.toPx(), shortsTopY)
                    lineTo(hipR.x + 4.dp.toPx(), hipR.y + 15.dp.toPx())
                    lineTo(midHip.x, hipR.y + 10.dp.toPx()) // Cavallo
                    lineTo(hipL.x - 4.dp.toPx(), hipL.y + 15.dp.toPx())
                    close()
                }
                drawPath(shortsPath, Brush.verticalGradient(listOf(clothingShorts, Color.Black)))
            }

            // --- LOGICA MOVIMENTO ---

            when (movementType) {
                MovementType.WALKING -> {
                    val cycle = anim * 2 * PI.toFloat()
                    val swing = sin(cycle)
                    val bounce = abs(sin(cycle * 2)) * 6.dp.toPx()
                    
                    val pelvisY = centerY + 40.dp.toPx() - bounce
                    val shoulderY = pelvisY - 80.dp.toPx()
                    
                    // Gambe (Anatomiche e affusolate)
                    val legWidth = 26.dp.toPx()
                    
                    // Gamba Dietro
                    val footBack = Offset(centerX + swing * 40.dp.toPx(), h * 0.92f)
                    val kneeBack = Offset(centerX + swing * 25.dp.toPx(), (pelvisY + footBack.y)/2 + 10.dp.toPx())
                    drawOrganicLimb(Offset(centerX - 12.dp.toPx(), pelvisY), kneeBack, legWidth, legWidth * 0.8f, clothingShorts, false)
                    drawOrganicLimb(kneeBack, footBack, legWidth * 0.8f, legWidth * 0.5f, skinBase, true)
                    
                    // Torso
                    draw3DTorso(
                        Offset(centerX - 26.dp.toPx(), shoulderY),
                        Offset(centerX + 26.dp.toPx(), shoulderY),
                        Offset(centerX - 20.dp.toPx(), pelvisY),
                        Offset(centerX + 20.dp.toPx(), pelvisY)
                    )
                    
                    // Gamba Avanti
                    val footFront = Offset(centerX - swing * 40.dp.toPx(), h * 0.92f)
                    val kneeFront = Offset(centerX - swing * 25.dp.toPx(), (pelvisY + footFront.y)/2 + 10.dp.toPx())
                    drawOrganicLimb(Offset(centerX + 12.dp.toPx(), pelvisY), kneeFront, legWidth, legWidth * 0.8f, clothingShorts, false)
                    drawOrganicLimb(kneeFront, footFront, legWidth * 0.8f, legWidth * 0.5f, skinBase, true)
                    
                    // Scarpe
                    drawCircle(Color.White, 10.dp.toPx(), footFront)
                    drawCircle(Color.White, 10.dp.toPx(), footBack)

                    draw3DHead(Offset(centerX, shoulderY - 30.dp.toPx()), swing * 4f)
                    
                    // Braccia
                    val armWidth = 14.dp.toPx()
                    drawOrganicLimb(Offset(centerX - 26.dp.toPx(), shoulderY + 10.dp.toPx()), Offset(centerX - 35.dp.toPx() - swing * 20.dp.toPx(), shoulderY + 50.dp.toPx()), armWidth, armWidth * 0.8f, skinBase)
                    drawOrganicLimb(Offset(centerX + 26.dp.toPx(), shoulderY + 10.dp.toPx()), Offset(centerX + 35.dp.toPx() + swing * 20.dp.toPx(), shoulderY + 50.dp.toPx()), armWidth, armWidth * 0.8f, skinBase)
                }

                MovementType.SQUAT -> {
                    val t = if (anim < 0.5f) anim * 2 else (1f - anim) * 2
                    val easeT = t * t * (3 - 2 * t)
                    val depth = easeT * 70.dp.toPx()
                    
                    val pelvisY = centerY + 30.dp.toPx() + depth
                    val shoulderY = pelvisY - 80.dp.toPx()
                    
                    // Gambe Squat
                    val footL = Offset(centerX - 50.dp.toPx(), h * 0.92f)
                    val footR = Offset(centerX + 50.dp.toPx(), h * 0.92f)
                    val kneeL = Offset(centerX - 70.dp.toPx(), (pelvisY + footL.y)/2)
                    val kneeR = Offset(centerX + 70.dp.toPx(), (pelvisY + footR.y)/2)
                    
                    drawOrganicLimb(Offset(centerX - 15.dp.toPx(), pelvisY), kneeL, 28.dp.toPx(), 24.dp.toPx(), clothingShorts, false)
                    drawOrganicLimb(kneeL, footL, 24.dp.toPx(), 18.dp.toPx(), skinBase, true)
                    
                    drawOrganicLimb(Offset(centerX + 15.dp.toPx(), pelvisY), kneeR, 28.dp.toPx(), 24.dp.toPx(), clothingShorts, false)
                    drawOrganicLimb(kneeR, footR, 24.dp.toPx(), 18.dp.toPx(), skinBase, true)
                    
                    draw3DTorso(
                        Offset(centerX - 26.dp.toPx(), shoulderY),
                        Offset(centerX + 26.dp.toPx(), shoulderY),
                        Offset(centerX - 20.dp.toPx(), pelvisY),
                        Offset(centerX + 20.dp.toPx(), pelvisY)
                    )
                    
                    draw3DHead(Offset(centerX, shoulderY - 30.dp.toPx()), 0f)
                    
                    // Braccia avanti bilanciamento
                    drawOrganicLimb(Offset(centerX - 26.dp.toPx(), shoulderY + 15.dp.toPx()), Offset(centerX - 60.dp.toPx(), shoulderY + 10.dp.toPx()), 14.dp.toPx(), 12.dp.toPx(), skinBase)
                    drawOrganicLimb(Offset(centerX + 26.dp.toPx(), shoulderY + 15.dp.toPx()), Offset(centerX + 60.dp.toPx(), shoulderY + 10.dp.toPx()), 14.dp.toPx(), 12.dp.toPx(), skinBase)
                }

                MovementType.VICTORY -> {
                    val celebration = sin(anim * 2 * PI.toFloat()) * 0.5f + 0.5f
                    val bounce = celebration * 10.dp.toPx()
                    val shoulderY = centerY - 60.dp.toPx() - bounce
                    val pelvisY = shoulderY + 80.dp.toPx()
                    
                    // Gambe aperte per stabilità
                    drawOrganicLimb(Offset(centerX - 15.dp.toPx(), pelvisY), Offset(centerX - 35.dp.toPx(), h * 0.92f), 26.dp.toPx(), 18.dp.toPx(), clothingShorts, false)
                    drawOrganicLimb(Offset(centerX + 15.dp.toPx(), pelvisY), Offset(centerX + 35.dp.toPx(), h * 0.92f), 26.dp.toPx(), 18.dp.toPx(), clothingShorts, false)
                    
                    draw3DTorso(
                        Offset(centerX - 26.dp.toPx(), shoulderY),
                        Offset(centerX + 26.dp.toPx(), shoulderY),
                        Offset(centerX - 20.dp.toPx(), pelvisY),
                        Offset(centerX + 20.dp.toPx(), pelvisY)
                    )
                    
                    draw3DHead(Offset(centerX, shoulderY - 30.dp.toPx()), sin(anim * 2 * PI.toFloat()) * 8f)
                    
                    // Braccia alzate a V
                    val armAngle = celebration * 40.dp.toPx()
                    drawOrganicLimb(Offset(centerX - 26.dp.toPx(), shoulderY + 10.dp.toPx()), Offset(centerX - 60.dp.toPx(), shoulderY - 40.dp.toPx() - armAngle), 14.dp.toPx(), 12.dp.toPx(), skinBase)
                    drawOrganicLimb(Offset(centerX + 26.dp.toPx(), shoulderY + 10.dp.toPx()), Offset(centerX + 60.dp.toPx(), shoulderY - 40.dp.toPx() - armAngle), 14.dp.toPx(), 12.dp.toPx(), skinBase)
                }

                else -> {
                    // IDLE: Posa rilassata ma elegante (simile alla foto)
                    val idle = sin(anim * 2 * PI.toFloat()) * 3.dp.toPx()
                    val shoulderY = centerY - 50.dp.toPx() + idle
                    val pelvisY = shoulderY + 80.dp.toPx()
                    
                    drawOrganicLimb(Offset(centerX - 15.dp.toPx(), pelvisY), Offset(centerX - 20.dp.toPx(), h * 0.92f), 26.dp.toPx(), 18.dp.toPx(), clothingShorts, false)
                    drawOrganicLimb(Offset(centerX + 15.dp.toPx(), pelvisY), Offset(centerX + 20.dp.toPx(), h * 0.92f), 26.dp.toPx(), 18.dp.toPx(), clothingShorts, false)

                    draw3DTorso(
                        Offset(centerX - 26.dp.toPx(), shoulderY),
                        Offset(centerX + 26.dp.toPx(), shoulderY),
                        Offset(centerX - 20.dp.toPx(), pelvisY),
                        Offset(centerX + 20.dp.toPx(), pelvisY)
                    )
                    
                    draw3DHead(Offset(centerX, shoulderY - 30.dp.toPx()), 0f)
                    
                    // Braccia lungo i fianchi o sulle anche (come in foto)
                    drawOrganicLimb(Offset(centerX - 26.dp.toPx(), shoulderY + 10.dp.toPx()), Offset(centerX - 35.dp.toPx(), shoulderY + 50.dp.toPx()), 14.dp.toPx(), 12.dp.toPx(), skinBase)
                    drawOrganicLimb(Offset(centerX + 26.dp.toPx(), shoulderY + 10.dp.toPx()), Offset(centerX + 35.dp.toPx(), shoulderY + 50.dp.toPx()), 14.dp.toPx(), 12.dp.toPx(), skinBase)
                }
            }

            // Shadow morbida
            drawOval(
                brush = Brush.radialGradient(
                    colors = listOf(Color.Black.copy(alpha = 0.2f), Color.Transparent),
                    center = Offset(centerX, h * 0.94f),
                    radius = 100.dp.toPx()
                ),
                topLeft = Offset(centerX - 70.dp.toPx(), h * 0.92f),
                size = Size(140.dp.toPx(), 20.dp.toPx())
            )
        }
    }
}
