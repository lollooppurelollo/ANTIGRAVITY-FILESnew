package com.afa.fitadapt.model

/**
 * Modello dati per l'esercizio.
 */
data class Exercise(
    val id: String,
    val title: String,
    val category: String,
    val durationMinutes: Int?,
    val intensity: String,
    val description: String,
    val modelAssetPath: String = "models/avatar_fitness.glb",
    val animationAssetPath: String?,
    val animationName: String?,
    val movementInstructions: String,
    val commonErrors: List<String>
)

/**
 * Mappa centralizzata per le animazioni degli esercizi.
 * Associa l'identificativo dell'esercizio al percorso dell'asset GLB dell'animazione.
 */
object ExerciseAnimationMap {
    private val animationMap = mapOf(
        "cammino_lento" to "animations/walk_slow.glb",
        "cammino_moderato" to "animations/walk_moderate.glb",
        "cammino_veloce" to "animations/walk_fast.glb",
        "cammino_salita" to "animations/uphill_walk.glb",
        "nordic_walking" to "animations/nordic_walking.glb",
        "corsa_leggera" to "animations/light_run.glb",
        "corsa_moderata" to "animations/moderate_run.glb",
        "corsa_intervallata" to "animations/interval_run.glb",
        "marcia_sul_posto" to "animations/march_in_place.glb",
        "passo_laterale" to "animations/side_step.glb",
        "jumping_jack_modificato" to "animations/modified_jumping_jack.glb",
        "cyclette" to "animations/stationary_bike.glb",
        "danza_aerobica_dolce" to "animations/soft_aerobic_dance.glb",
        "stair_step" to "animations/stair_step.glb",
        "circuito_aerobico_base" to "animations/basic_aerobic_circuit.glb",
        "squat_assistito" to "animations/assisted_squat.glb",
        "affondo_frontale" to "animations/forward_lunge.glb",
        "ponte_glutei" to "animations/glute_bridge.glb",
        "piegamenti_muro" to "animations/wall_push_up.glb",
        "plank_ginocchia" to "animations/knee_plank.glb",
        "alzate_laterali" to "animations/lateral_raise.glb",
        "biceps_curl" to "animations/biceps_curl.glb",
        "calf_raise" to "animations/calf_raise.glb",
        "stretching_quadricipiti" to "animations/quad_stretch.glb",
        "stretching_schiena" to "animations/back_stretch.glb",
        "stretching_polpacci_muro" to "animations/calf_stretch_wall.glb",
        "mobilita_spalle" to "animations/shoulder_mobility.glb",
        "mobilita_anca" to "animations/hip_mobility.glb",
        "mobilita_cervicale" to "animations/cervical_mobility.glb",
        "yoga_dolce" to "animations/gentle_yoga.glb",
        "stretching_catena_posteriore" to "animations/posterior_chain_stretch.glb",
        "respirazione_diaframmatica" to "animations/diaphragmatic_breathing.glb",
        "respirazione_478" to "animations/breathing_478.glb",
        "rilassamento_progressivo" to "animations/progressive_muscle_relaxation.glb",
        "body_scan" to "animations/body_scan.glb",
        "respirazione_labbra_socchiuse" to "animations/pursed_lip_breathing.glb",
        "nuoto_dolce" to "animations/gentle_swimming.glb",
        "tai_chi" to "animations/tai_chi.glb",
        "pilates_base" to "animations/basic_pilates.glb",
        "ginnastica_posturale" to "animations/postural_gymnastics.glb"
    )

    fun getAnimationPath(exerciseKey: String?): String? {
        return animationMap[exerciseKey?.lowercase()]
    }
}
