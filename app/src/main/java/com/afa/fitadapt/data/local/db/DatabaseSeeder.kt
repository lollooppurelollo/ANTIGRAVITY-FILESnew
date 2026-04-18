// =============================================================
// AFA - Attività Fisica Adattata
// Seeding iniziale del database: esercizi e articoli precaricati
// =============================================================
package com.afa.fitadapt.data.local.db

import com.afa.fitadapt.data.local.dao.ArticleDao
import com.afa.fitadapt.data.local.dao.ExerciseDao
import com.afa.fitadapt.data.local.entity.ArticleEntity
import com.afa.fitadapt.data.local.entity.ExerciseEntity
import com.afa.fitadapt.model.ExerciseCategory
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Popola il database al primo avvio con:
 * - ~64 esercizi (8 per categoria × 8 categorie)
 * - ~35 articoli (5 per categoria × 7 categorie)
 *
 * Il seeding avviene solo se il database è vuoto.
 * Nuovi esercizi e articoli possono essere aggiunti
 * in un secondo momento dalla sezione protetta.
 */
@Singleton
class DatabaseSeeder @Inject constructor(
    private val exerciseDao: ExerciseDao,
    private val articleDao: ArticleDao
) {

    /**
     * Esegue il seeding completo se il database è vuoto.
     * Viene chiamata all'avvio dell'app (dalla SplashScreen o dall'init).
     */
    suspend fun seedIfEmpty() {
        if (exerciseDao.count() == 0) {
            exerciseDao.insertAll(createExercises())
        }
        if (articleDao.count() == 0) {
            articleDao.insertAll(createArticles())
        }
    }

    // ══════════════════════════════════════════════════════════
    // ESERCIZI PRECARICATI (~64 totali)
    // ══════════════════════════════════════════════════════════

    private fun createExercises(): List<ExerciseEntity> {
        return camminoStandard() +
                camminoVeloce() +
                corsa() +
                eserciziAerobici() +
                rinforzoMuscolare() +
                stretchingMobilita() +
                respirazioneRilassamento() +
                altro()
    }

    // ── Cammino Standard ──
    private fun camminoStandard() = listOf(
        exercise("Cammino lento 10 min", ExerciseCategory.CAMMINO_STANDARD,
            "Cammino a passo tranquillo su terreno pianeggiante. Ideale per iniziare.",
            durationSec = 600, intensity = "bassa", videoUri = "asset:///videos/cammino_lento.mp4"),
        exercise("Cammino lento 15 min", ExerciseCategory.CAMMINO_STANDARD,
            "Cammino a passo lento e costante. Ottimo per riscaldamento o recupero.",
            durationSec = 900, intensity = "bassa", videoUri = "asset:///videos/cammino_lento.mp4"),
        exercise("Cammino lento 20 min", ExerciseCategory.CAMMINO_STANDARD,
            "Cammino di durata media a ritmo leggero. Buona attività quotidiana di base.",
            durationSec = 1200, intensity = "bassa", videoUri = "asset:///videos/cammino_lento.mp4"),
        exercise("Cammino moderato 15 min", ExerciseCategory.CAMMINO_STANDARD,
            "Cammino a passo sostenuto. Si dovrebbe riuscire a parlare ma non cantare.",
            durationSec = 900, intensity = "moderata", videoUri = "asset:///videos/cammino_moderato.mp4"),
        exercise("Cammino moderato 20 min", ExerciseCategory.CAMMINO_STANDARD,
            "Cammino a ritmo costante e vivace. Attività aerobica di base.",
            durationSec = 1200, intensity = "moderata", videoUri = "asset:///videos/cammino_moderato.mp4"),
        exercise("Cammino moderato 30 min", ExerciseCategory.CAMMINO_STANDARD,
            "Sessione di cammino completa a ritmo moderato. Obiettivo quotidiano ideale.",
            durationSec = 1800, intensity = "moderata", videoUri = "asset:///videos/cammino_moderato.mp4"),
        exercise("Cammino in salita leggera 10 min", ExerciseCategory.CAMMINO_STANDARD,
            "Cammino su pendenza leggera o scale con ritmo lento. Rafforza gambe e cuore.",
            durationSec = 600, intensity = "moderata"),
        exercise("Cammino con bastoncini 20 min", ExerciseCategory.CAMMINO_STANDARD,
            "Cammino con bastoncini da trekking. Coinvolge braccia e migliora la stabilità.",
            durationSec = 1200, intensity = "moderata"),
    )

    // ── Cammino Veloce ──
    private fun camminoVeloce() = listOf(
        exercise("Cammino veloce 10 min", ExerciseCategory.CAMMINO_VELOCE,
            "Cammino a passo rapido. Il respiro accelera ma resta gestibile.",
            durationSec = 600, intensity = "moderata", videoUri = "asset:///videos/cammino_veloce.mp4"),
        exercise("Cammino veloce 15 min", ExerciseCategory.CAMMINO_VELOCE,
            "Cammino veloce sostenuto. Buon esercizio cardiovascolare.",
            durationSec = 900, intensity = "moderata", videoUri = "asset:///videos/cammino_veloce.mp4"),
        exercise("Cammino veloce 20 min", ExerciseCategory.CAMMINO_VELOCE,
            "Sessione di cammino rapido. Aumenta la frequenza cardiaca e la resistenza.",
            durationSec = 1200, intensity = "moderata", videoUri = "asset:///videos/cammino_veloce.mp4"),
        exercise("Cammino veloce 25 min", ExerciseCategory.CAMMINO_VELOCE,
            "Cammino veloce prolungato per chi ha già una buona base aerobica.",
            durationSec = 1500, intensity = "moderata", videoUri = "asset:///videos/cammino_veloce.mp4"),
        exercise("Cammino veloce 30 min", ExerciseCategory.CAMMINO_VELOCE,
            "Sessione completa di cammino veloce. Ottimo per la salute cardiovascolare.",
            durationSec = 1800, intensity = "alta", videoUri = "asset:///videos/cammino_veloce.mp4"),
        exercise("Cammino veloce intervallato 15 min", ExerciseCategory.CAMMINO_VELOCE,
            "Alterna 2 min di cammino veloce a 1 min di cammino lento. Migliora la resistenza.",
            durationSec = 900, intensity = "moderata"),
        exercise("Cammino veloce intervallato 20 min", ExerciseCategory.CAMMINO_VELOCE,
            "Sessione intervallata: 3 min veloce, 1 min lento. Per chi vuole progredire.",
            durationSec = 1200, intensity = "alta"),
        exercise("Cammino nordico 20 min", ExerciseCategory.CAMMINO_VELOCE,
            "Cammino con bastoncini da nordic walking. Coinvolge tutto il corpo.",
            durationSec = 1200, intensity = "moderata"),
    )

    // ── Corsa ──
    private fun corsa() = listOf(
        exercise("Corsa leggera 5 min", ExerciseCategory.CORSA,
            "Breve corsa leggera (jogging). Per chi inizia a correre. Ascolta il tuo corpo.",
            durationSec = 300, intensity = "moderata"),
        exercise("Corsa leggera 10 min", ExerciseCategory.CORSA,
            "Jogging a ritmo gentile per 10 minuti. Mantieni un passo che ti permetta di parlare.",
            durationSec = 600, intensity = "moderata"),
        exercise("Corsa leggera 15 min", ExerciseCategory.CORSA,
            "Corsa leggera continuata. Buona sessione per costruire resistenza.",
            durationSec = 900, intensity = "moderata"),
        exercise("Corsa moderata 10 min", ExerciseCategory.CORSA,
            "Corsa a ritmo moderato. Il respiro è accelerato ma gestibile.",
            durationSec = 600, intensity = "moderata"),
        exercise("Corsa moderata 15 min", ExerciseCategory.CORSA,
            "Sessione di corsa a ritmo costante. Per chi ha già esperienza.",
            durationSec = 900, intensity = "alta"),
        exercise("Corsa moderata 20 min", ExerciseCategory.CORSA,
            "Corsa continuata a ritmo moderato-alto. Obiettivo avanzato.",
            durationSec = 1200, intensity = "alta"),
        exercise("Corsa intervallata 15 min", ExerciseCategory.CORSA,
            "Alterna 1 min di corsa a 2 min di cammino. Perfetta per iniziare gradualmente.",
            durationSec = 900, intensity = "moderata"),
        exercise("Corsa intervallata 20 min", ExerciseCategory.CORSA,
            "Alterna 2 min di corsa a 1 min di cammino. Per migliorare la resistenza.",
            durationSec = 1200, intensity = "alta"),
    )

    // ── Esercizi Aerobici ──
    private fun eserciziAerobici() = listOf(
        exercise("Marcia sul posto 5 min", ExerciseCategory.ESERCIZI_AEROBICI,
            "Marcia sul posto alzando bene le ginocchia. Riscaldamento ideale.",
            durationSec = 300, intensity = "bassa", videoUri = "asset:///videos/marcia_sul_posto.mp4"),
        exercise("Step laterale 10 min", ExerciseCategory.ESERCIZI_AEROBICI,
            "Passi laterali alternati con movimento delle braccia. Lavoro cardiovascolare dolce.",
            durationSec = 600, intensity = "moderata", videoUri = "asset:///videos/step_laterale.mp4"),
        exercise("Jumping jack modificato", ExerciseCategory.ESERCIZI_AEROBICI,
            "Versione facilitata: solo gambe OPPURE solo braccia. Senza saltare se necessario.",
            durationSec = 300, reps = 20, intensity = "moderata", videoUri = "asset:///videos/jumping_jack.mp4"),
        exercise("Cyclette leggera 15 min", ExerciseCategory.ESERCIZI_AEROBICI,
            "Pedalata a resistenza minima. Lavoro cardiovascolare a basso impatto articolare.",
            durationSec = 900, intensity = "bassa"),
        exercise("Cyclette moderata 20 min", ExerciseCategory.ESERCIZI_AEROBICI,
            "Pedalata a resistenza media. Buon esercizio per cuore e gambe.",
            durationSec = 1200, intensity = "moderata"),
        exercise("Danza aerobica dolce 15 min", ExerciseCategory.ESERCIZI_AEROBICI,
            "Movimenti ritmici e passi semplici a tempo di musica. Divertente e motivante.",
            durationSec = 900, intensity = "moderata"),
        exercise("Salita gradini 10 min", ExerciseCategory.ESERCIZI_AEROBICI,
            "Salire e scendere gradini a ritmo controllato. Rafforza gambe e cuore.",
            durationSec = 600, intensity = "moderata"),
        exercise("Circuito aerobico base 20 min", ExerciseCategory.ESERCIZI_AEROBICI,
            "Sequenza: marcia, step laterale, ginocchia alte, talloni al gluteo. 30 sec ciascuno.",
            durationSec = 1200, intensity = "moderata"),
    )

    // ── Rinforzo Muscolare ──
    private fun rinforzoMuscolare() = listOf(
        exercise("Squat assistito", ExerciseCategory.RINFORZO_MUSCOLARE,
            "Piegamenti sulle gambe tenendosi a una sedia. Rinforza cosce e glutei.",
            reps = 10, intensity = "moderata", videoUri = "asset:///videos/squat_assistito.mp4"),
        exercise("Affondi in avanti", ExerciseCategory.RINFORZO_MUSCOLARE,
            "Passo lungo in avanti con piegamento del ginocchio. Alternare le gambe.",
            reps = 8, intensity = "moderata", videoUri = "asset:///videos/affondi.mp4",
            notes = "8 per gamba. Usare un appoggio se necessario."),
        exercise("Ponte glutei", ExerciseCategory.RINFORZO_MUSCOLARE,
            "Sdraiati a pancia in su, piedi a terra, solleva il bacino. Rinforza glutei e schiena.",
            reps = 12, intensity = "bassa", videoUri = "asset:///videos/ponte_glutei.mp4"),
        exercise("Push-up facilitato al muro", ExerciseCategory.RINFORZO_MUSCOLARE,
            "Piegamenti sulle braccia appoggiando le mani al muro. Distanza variabile.",
            reps = 10, intensity = "bassa", videoUri = "asset:///videos/pushup_muro.mp4"),
        exercise("Plank sulle ginocchia", ExerciseCategory.RINFORZO_MUSCOLARE,
            "Posizione di plank con ginocchia a terra. Mantieni la schiena dritta.",
            durationSec = 20, intensity = "moderata", videoUri = "asset:///videos/plank_ginocchia.mp4",
            notes = "Inizia con 20 secondi, aumenta gradualmente."),
        exercise("Alzate laterali con elastico", ExerciseCategory.RINFORZO_MUSCOLARE,
            "In piedi, solleva le braccia lateralmente usando un elastico. Rinforza le spalle.",
            reps = 12, intensity = "moderata"),
        exercise("Curl bicipiti con elastico", ExerciseCategory.RINFORZO_MUSCOLARE,
            "Piedi sull'elastico, piega le braccia portando le mani verso le spalle.",
            reps = 12, intensity = "moderata"),
        exercise("Sollevamento polpacci", ExerciseCategory.RINFORZO_MUSCOLARE,
            "In piedi, alzati sulle punte dei piedi e scendi lentamente. Appoggio per equilibrio.",
            reps = 15, intensity = "bassa"),
    )

    // ── Stretching e Mobilità ──
    private fun stretchingMobilita() = listOf(
        exercise("Stretching quadricipiti", ExerciseCategory.STRETCHING_MOBILITA,
            "In piedi, porta il tallone verso il gluteo. Tieni 30 secondi per gamba.",
            durationSec = 60, intensity = "bassa"),
        exercise("Stretching dorsale", ExerciseCategory.STRETCHING_MOBILITA,
            "Seduta, fletti il busto in avanti verso le ginocchia. Tieni 30 secondi.",
            durationSec = 60, intensity = "bassa"),
        exercise("Stretching polpacci", ExerciseCategory.STRETCHING_MOBILITA,
            "In piedi davanti a un muro, gamba indietro, tallone a terra. 30 secondi per gamba.",
            durationSec = 60, intensity = "bassa"),
        exercise("Mobilità spalle", ExerciseCategory.STRETCHING_MOBILITA,
            "Rotazioni delle spalle avanti e indietro. Ampi cerchi con le braccia.",
            durationSec = 120, reps = 10, intensity = "bassa"),
        exercise("Mobilità anche", ExerciseCategory.STRETCHING_MOBILITA,
            "Rotazioni delle anche in piedi. Movimenti circolari del bacino.",
            durationSec = 120, intensity = "bassa"),
        exercise("Yoga dolce 15 min", ExerciseCategory.STRETCHING_MOBILITA,
            "Sequenza di posizioni yoga semplici: gatto-mucca, cobra, bambino, albero.",
            durationSec = 900, intensity = "bassa"),
        exercise("Stretching catena posteriore", ExerciseCategory.STRETCHING_MOBILITA,
            "Seduta a terra, gambe distese, fletti il busto verso le punte dei piedi.",
            durationSec = 60, intensity = "bassa"),
        exercise("Mobilità cervicale", ExerciseCategory.STRETCHING_MOBILITA,
            "Movimenti lenti del collo: rotazioni, inclinazioni laterali, flessione-estensione.",
            durationSec = 120, intensity = "bassa",
            notes = "Movimenti lenti e controllati. Fermarsi se c'è dolore."),
    )

    // ── Respirazione e Rilassamento ──
    private fun respirazioneRilassamento() = listOf(
        exercise("Respirazione diaframmatica 5 min", ExerciseCategory.RESPIRAZIONE_RILASSAMENTO,
            "Inspira dal naso gonfiando la pancia, espira dalla bocca. Ritmo lento e regolare.",
            durationSec = 300, intensity = "bassa", videoUri = "asset:///videos/respirazione.mp4"),
        exercise("Respirazione 4-7-8", ExerciseCategory.RESPIRAZIONE_RILASSAMENTO,
            "Inspira per 4 secondi, trattieni per 7, espira per 8. Riduce ansia e stress.",
            durationSec = 300, reps = 8, intensity = "bassa", videoUri = "asset:///videos/respirazione.mp4"),
        exercise("Rilassamento muscolare progressivo 10 min", ExerciseCategory.RESPIRAZIONE_RILASSAMENTO,
            "Contrai e rilassa ogni gruppo muscolare, partendo dai piedi fino alla testa.",
            durationSec = 600, intensity = "bassa"),
        exercise("Rilassamento muscolare progressivo 15 min", ExerciseCategory.RESPIRAZIONE_RILASSAMENTO,
            "Versione estesa: contrazione e rilassamento di tutti i distretti muscolari.",
            durationSec = 900, intensity = "bassa"),
        exercise("Body scan guidato 10 min", ExerciseCategory.RESPIRAZIONE_RILASSAMENTO,
            "Porta l'attenzione su ogni parte del corpo, notando sensazioni senza giudicare.",
            durationSec = 600, intensity = "bassa"),
        exercise("Meditazione mindfulness 5 min", ExerciseCategory.RESPIRAZIONE_RILASSAMENTO,
            "Siediti comodamente e concentrati sul respiro. Lascia passare i pensieri.",
            durationSec = 300, intensity = "bassa"),
        exercise("Meditazione mindfulness 10 min", ExerciseCategory.RESPIRAZIONE_RILASSAMENTO,
            "Sessione di meditazione guidata. Focus sul presente, respiro, sensazioni corporee.",
            durationSec = 600, intensity = "bassa"),
        exercise("Respirazione con labbra socchiuse", ExerciseCategory.RESPIRAZIONE_RILASSAMENTO,
            "Inspira dal naso per 2 sec, espira dalle labbra socchiuse per 4 sec. Per la dispnea.",
            durationSec = 300, reps = 10, intensity = "bassa",
            notes = "Particolarmente utile in caso di dispnea da sforzo."),
    )

    // ── Altro ──
    private fun altro() = listOf(
        exercise("Esercizio personalizzato", ExerciseCategory.ALTRO,
            "Esercizio libero da personalizzare nella scheda secondo le esigenze.",
            intensity = "moderata"),
        exercise("Attività libera", ExerciseCategory.ALTRO,
            "Attività fisica a scelta: giardinaggio, ballo, gioco, passeggiate.",
            durationSec = 1800, intensity = "moderata"),
        exercise("Nuoto dolce 20 min", ExerciseCategory.ALTRO,
            "Nuoto a stile libero o dorso a ritmo lento. Basso impatto articolare.",
            durationSec = 1200, intensity = "moderata"),
        exercise("Acquagym 30 min", ExerciseCategory.ALTRO,
            "Ginnastica in acqua con esercizi vari. L'acqua sostiene e riduce il carico.",
            durationSec = 1800, intensity = "moderata"),
        exercise("Tai Chi 15 min", ExerciseCategory.ALTRO,
            "Sequenza di movimenti lenti e fluidi. Migliora equilibrio e serenità.",
            durationSec = 900, intensity = "bassa"),
        exercise("Pilates base 20 min", ExerciseCategory.ALTRO,
            "Esercizi di Pilates per principianti. Core, postura, controllo del corpo.",
            durationSec = 1200, intensity = "moderata"),
        exercise("Ginnastica posturale 20 min", ExerciseCategory.ALTRO,
            "Esercizi per migliorare la postura. Focus su schiena, spalle e core.",
            durationSec = 1200, intensity = "bassa"),
        exercise("Attività domestiche attive 30 min", ExerciseCategory.ALTRO,
            "Pulizie, giardinaggio, lavori domestici svolti in modo attivo e dinamico.",
            durationSec = 1800, intensity = "bassa"),
    )

    // ── Helper per creare un ExerciseEntity in modo compatto ──
    private fun exercise(
        name: String,
        category: ExerciseCategory,
        description: String,
        durationSec: Int? = null,
        reps: Int? = null,
        intensity: String = "moderata",
        notes: String? = null,
        videoUri: String? = null
    ) = ExerciseEntity(
        name = name,
        category = category.name,
        description = description,
        defaultDurationSec = durationSec,
        defaultRepetitions = reps,
        defaultIntensity = intensity,
        notes = notes,
        videoUri = videoUri
    )

    // ══════════════════════════════════════════════════════════
    // ARTICOLI PRECARICATI (~35 totali, 5 per categoria)
    // ══════════════════════════════════════════════════════════

    private fun createArticles(): List<ArticleEntity> {
        return articoliAttivitaFisica() +
                articoliAlimentazione() +
                articoliSonno() +
                articoliFatigue() +
                articoliDolore() +
                articoliBenessere() +
                articoliLinfedema()
    }

    // ── Attività Fisica ──
    private fun articoliAttivitaFisica() = listOf(
        article("Perché muoversi fa bene durante il percorso di cura", "Attività fisica",
            "L'attività fisica regolare porta benefici importanti anche durante un percorso di cura.",
            """L'attività fisica durante il percorso di cura non è solo sicura, ma è raccomandata da tutte le principali linee guida internazionali. Muoversi regolarmente aiuta a ridurre la fatica, migliorare l'umore, mantenere la forza muscolare e favorire il recupero.

Non serve fare sport intenso: anche una passeggiata quotidiana di 20-30 minuti può fare la differenza. L'importante è scegliere attività che ti piacciono e adattare l'intensità al tuo stato di forma attuale.

Inizia gradualmente e aumenta poco alla volta. Ascolta il tuo corpo: nei giorni in cui ti senti più stanca, puoi ridurre l'intensità senza rinunciare del tutto al movimento.""",
            isFeatured = true),
        article("Quanto movimento fare ogni settimana", "Attività fisica",
            "Le raccomandazioni suggeriscono almeno 150 minuti di attività moderata a settimana.",
            """Le linee guida dell'OMS raccomandano almeno 150 minuti di attività fisica moderata a settimana, oppure 75 minuti di attività intensa. Questo si traduce in circa 30 minuti al giorno per 5 giorni.

Per chi è in un percorso di cura, anche quantità inferiori portano benefici. L'obiettivo non è raggiungere subito il target, ma costruire gradualmente un'abitudine.

Puoi suddividere l'attività in sessioni più brevi: 3 blocchi da 10 minuti equivalgono a 30 minuti continuativi in termini di benefici per la salute."""),
        article("Come iniziare se non ti muovevi prima", "Attività fisica",
            "Consigli pratici per chi parte da zero o riprende dopo un periodo di stop.",
            """Se non hai mai fatto attività fisica regolare, o se hai interrotto per un periodo, è normale sentirsi incerti. Il primo passo è il più importante.

Inizia con camminate brevi (10 minuti) e aumenta di 5 minuti ogni settimana. Scegli un orario fisso per creare un'abitudine. Vestiti comodamente e scegli scarpe adatte.

Non confrontarti con gli altri: il tuo percorso è unico. Ogni passo conta. Registra i tuoi progressi nell'app per vedere i miglioramenti nel tempo."""),
        article("L'importanza del riscaldamento e del defaticamento", "Attività fisica",
            "Perché è fondamentale preparare il corpo prima e dopo l'esercizio.",
            """Il riscaldamento prepara muscoli, articolazioni e sistema cardiovascolare all'esercizio. Bastano 5 minuti di movimenti dolci: marcia sul posto, rotazioni delle braccia, mobilità articolare.

Il defaticamento è altrettanto importante: dopo l'esercizio, dedica 5 minuti allo stretching e a una camminata lenta. Questo aiuta il recupero muscolare e riduce i dolori post-esercizio.

Saltare riscaldamento e defaticamento aumenta il rischio di fastidi muscolari e articolari."""),
        article("Movimento e sistema immunitario", "Attività fisica",
            "Come l'attività fisica moderata può supportare le difese del corpo.",
            """L'attività fisica moderata e regolare ha un effetto positivo sul sistema immunitario. Favorisce la circolazione delle cellule immunitarie e riduce l'infiammazione cronica.

Attenzione: l'esercizio molto intenso e prolungato può avere l'effetto opposto, indebolendo temporaneamente le difese. Per questo è importante trovare il giusto equilibrio.

La chiave è la costanza: meglio muoversi un po' ogni giorno che fare sessioni intense saltuarie."""),
    )

    // ── Alimentazione ──
    private fun articoliAlimentazione() = listOf(
        article("Alimentazione e attività fisica: i consigli base", "Alimentazione",
            "Come nutrirti per sostenere il tuo percorso di movimento.",
            """Un'alimentazione equilibrata è il complemento naturale dell'attività fisica. Non servono diete speciali: la base è variare gli alimenti e mantenere pasti regolari.

Prima dell'esercizio, preferisci cibi leggeri e digeribili: frutta, yogurt, fette biscottate. Evita pasti abbondanti nelle 2 ore prima dell'attività.

Dopo l'esercizio, reintegra con acqua e un piccolo spuntino che contenga proteine e carboidrati: un frutto con qualche noce, oppure pane e formaggio fresco."""),
        article("L'importanza dell'idratazione", "Alimentazione",
            "Bere acqua a sufficienza è fondamentale, soprattutto quando ci si muove.",
            """L'acqua è essenziale per il funzionamento di ogni cellula del corpo. Durante l'attività fisica perdiamo liquidi con il sudore, anche quando non ce ne accorgiamo.

Bevi regolarmente durante la giornata, senza aspettare di avere sete. L'obiettivo è circa 1.5-2 litri al giorno, di più se fai attività fisica o fa caldo.

Evita bevande zuccherate e alcoliche prima e dopo l'esercizio. L'acqua resta la scelta migliore."""),
        article("Proteine per mantenere la massa muscolare", "Alimentazione",
            "Perché le proteine sono importanti durante il percorso di cura.",
            """Le proteine sono i mattoni dei muscoli. Durante un percorso di cura, mantenere la massa muscolare è molto importante per la forza, l'equilibrio e il recupero.

Distribuisci l'assunzione di proteine nell'arco della giornata: colazione, pranzo, cena e spuntini. Fonti buone: legumi, pesce, uova, latticini, carni magre.

Abbina le proteine all'attività di rinforzo muscolare per massimizzare i benefici."""),
        article("Frutta e verdura: i tuoi alleati", "Alimentazione",
            "Almeno 5 porzioni al giorno per vitamine, minerali e antiossidanti.",
            """Frutta e verdura forniscono vitamine, minerali, fibre e antiossidanti essenziali. Aiutano a combattere lo stress ossidativo e supportano il sistema immunitario.

Varia i colori: ogni colore corrisponde a nutrienti diversi. Rosso (pomodori, fragole), verde (spinaci, broccoli), arancione (carote, arance), viola (melanzane, mirtilli).

Punta a 5 porzioni al giorno tra frutta e verdura. Anche piccole quantità fanno la differenza."""),
        article("Gestire le variazioni di appetito", "Alimentazione",
            "Cosa fare quando l'appetito cambia durante il percorso di cura.",
            """È normale che l'appetito vari durante il percorso di cura. In alcuni periodi potresti avere meno fame, in altri di più. Non forzarti, ma cerca di mantenere una routine alimentare.

Se hai poco appetito, fai pasti piccoli e frequenti (5-6 al giorno). Scegli alimenti nutrienti e densi: frutta secca, avocado, uova, formaggi.

Se hai troppa fame, scegli alimenti sazianti e ricchi di fibre: cereali integrali, legumi, verdure."""),
    )

    // ── Sonno ──
    private fun articoliSonno() = listOf(
        article("Migliorare il sonno con semplici abitudini", "Sonno",
            "Consigli pratici per dormire meglio durante il percorso di cura.",
            """Un buon sonno è essenziale per il recupero fisico e mentale. Ecco alcune abitudini che possono aiutarti:

Vai a letto e svegliati alla stessa ora ogni giorno, anche nel weekend. Crea una routine serale rilassante: lettura, bagno caldo, tisana.

Evita schermi luminosi nell'ora prima di dormire. La luce blu di smartphone e tablet interferisce con la produzione di melatonina, l'ormone del sonno."""),
        article("L'attività fisica migliora il sonno", "Sonno",
            "Come il movimento durante il giorno favorisce un riposo migliore.",
            """Chi fa attività fisica regolare dorme meglio. Il movimento aiuta a regolare il ritmo circadiano e riduce lo stress, facilitando l'addormentamento.

L'ideale è muoversi al mattino o nel primo pomeriggio. Evita esercizi intensi nelle 3 ore prima di dormire, perché possono rendere più difficile prendere sonno.

Anche una passeggiata di 20 minuti durante il giorno può migliorare significativamente la qualità del sonno."""),
        article("Gestire i risvegli notturni", "Sonno",
            "Cosa fare quando ti svegli durante la notte e non riesci a riaddormentarti.",
            """I risvegli notturni sono comuni e spesso non sono un problema. Se ti svegli, non guardare l'orologio: questo aumenta l'ansia da prestazione del sonno.

Se dopo 20 minuti non ti sei riaddormentata, alzati e fai un'attività tranquilla in un'altra stanza (leggere, ascoltare musica soft). Torna a letto quando torni ad avere sonno.

La respirazione diaframmatica e il rilassamento muscolare progressivo possono aiutare molto."""),
        article("L'ambiente ideale per dormire", "Sonno",
            "Come preparare la tua camera da letto per un sonno di qualità.",
            """La camera da letto dovrebbe essere fresca (18-20°C), buia e silenziosa. Investi in tende oscuranti se c'è luce esterna. Usa tappi per le orecchie se ci sono rumori.

Il materasso e il cuscino devono essere confortevoli e adatti alla tua postura. Sostituisci il materasso se ha più di 8-10 anni.

Usa la camera solo per dormire e per il riposo. Evita di lavorare, mangiare o guardare la TV a letto."""),
        article("Tisane e rituali serali", "Sonno",
            "Piccoli rituali che possono favorire il rilassamento prima di dormire.",
            """Una tisana calda alla camomilla, valeriana o tiglio può segnalare al corpo che è ora di rilassarsi. Non è solo l'erba — è il rituale che conta.

Crea la tua routine serale: 30 minuti prima di dormire, fai qualcosa di piacevole e tranquillo. Lettura, disegno, scrittura nel diario, meditazione.

Evita caffeina dopo le 14:00. Anche il tè verde contiene caffeina e può interferire con il sonno."""),
    )

    // ── Fatigue ──
    private fun articoliFatigue() = listOf(
        article("Gestire la stanchezza: consigli pratici", "Fatigue",
            "La fatigue è diversa dalla normale stanchezza. Ecco come affrontarla.",
            """La fatigue legata al percorso di cura è diversa dalla stanchezza normale: non migliora con il riposo e può compromettere le attività quotidiane.

Paradossalmente, l'attività fisica moderata è uno dei migliori rimedi. Muoversi regolarmente aiuta a ridurre la fatigue nel medio-lungo periodo.

Pianifica le attività nei momenti della giornata in cui ti senti meglio. Alterna momenti di attività a momenti di riposo. Non sentirti in colpa se hai bisogno di fermarti."""),
        article("Risparmio energetico nella vita quotidiana", "Fatigue",
            "Come distribuire le energie durante la giornata per sentirti meno stanca.",
            """Pensa alle tue energie come a un budget giornaliero: pianifica come spenderle. Fai le attività più impegnative nelle ore in cui ti senti meglio.

Semplifica dove possibile: chiedi aiuto per i lavori pesanti, usa sgabelli in cucina, prepara i pasti in anticipo. Ogni energia risparmiata è energia che puoi usare per cose importanti.

Tieni un diario dell'energia: annota quando ti senti meglio e quando peggio. Questo ti aiuterà a pianificare le giornate."""),
        article("Movimento e fatigue: un alleato insospettabile", "Fatigue",
            "Perché muoversi aiuta a ridurre la fatica anche quando sembra controintuitivo.",
            """Quando sei stanca, l'ultima cosa che vorresti fare è muoverti. Ma la ricerca dimostra chiaramente che l'attività fisica moderata riduce la fatigue.

Non serve fare tanto: anche 10 minuti di cammino possono fare la differenza. L'importante è essere costanti: un poco ogni giorno è meglio di tanto una volta sola.

Inizia con obiettivi minimi e aumenta gradualmente. Se oggi riesci a fare 5 minuti, domani potresti riuscirne a fare 6. Ogni passo conta."""),
        article("Fatigue e umore: una relazione stretta", "Fatigue",
            "Come la stanchezza cronica può influenzare l'umore e cosa fare.",
            """Fatigue e umore sono strettamente collegati: la stanchezza cronica può portare a irritabilità, tristezza e perdita di motivazione. E l'umore basso a sua volta peggiora la fatica.

Riconoscere questo circolo vizioso è il primo passo per spezzarlo. Attività fisica, socializzazione e piccoli piaceri quotidiani possono aiutare.

Non esitare a parlare di come ti senti con chi ti sta vicino o con i professionisti del tuo percorso di cura."""),
        article("Quando la stanchezza deve preoccupare", "Fatigue",
            "Segnali a cui prestare attenzione e quando parlare con il medico.",
            """Una certa stanchezza durante il percorso di cura è normale. Ma ci sono segnali che meritano attenzione: stanchezza che peggiora improvvisamente, difficoltà a svolgere attività di base, sonnolenza eccessiva.

Se la fatigue è molto intensa, continua o si accompagna ad altri sintomi (febbre, dolore, mancanza di respiro), parlane con il tuo medico.

Registra i tuoi livelli di stanchezza nell'app: questo dato nel tempo è molto utile per i professionisti che ti seguono."""),
    )

    // ── Dolore ──
    private fun articoliDolore() = listOf(
        article("Attività fisica e gestione del dolore", "Dolore",
            "Come il movimento può aiutare nella gestione del dolore cronico.",
            """Il movimento regolare può ridurre il dolore cronico attraverso diversi meccanismi: rilascio di endorfine (antidolorifici naturali), riduzione della rigidità, miglioramento della circolazione.

L'esercizio non deve causare dolore. Se un movimento fa male, modificalo o scegli un'alternativa. Lo stretching dolce e gli esercizi in acqua sono spesso ben tollerati.

Inizia con intensità bassa e aumenta gradualmente. La costanza è più importante dell'intensità."""),
        article("Stretching per ridurre le tensioni muscolari", "Dolore",
            "Esercizi di stretching che possono aiutare a ridurre rigidità e tensione.",
            """Lo stretching regolare può ridurre la rigidità muscolare e le tensioni che spesso accompagnano il dolore cronico. Non forzare mai: lo stretching deve dare una sensazione di allungamento piacevole.

Mantieni ogni posizione per 30 secondi, respirando profondamente. Ripeti 2-3 volte per lato. I momenti migliori sono al mattino e alla sera.

Lo stretching dopo l'esercizio è particolarmente importante per prevenire dolori muscolari nei giorni successivi."""),
        article("Dolore osteoarticolare e movimento", "Dolore",
            "Come muoversi in sicurezza quando le articolazioni fanno male.",
            """Il dolore articolare non significa che si debba stare fermi. Al contrario, il movimento mantiene le articolazioni lubrificate e i muscoli intorno alle articolazioni forti.

Scegli attività a basso impatto: cammino, nuoto, cyclette, yoga dolce. Evita salti, corsa su asfalto e movimenti bruschi.

Applica la regola del 2-ore: se 2 ore dopo l'esercizio il dolore è aumentato rispetto a prima, la prossima volta riduci l'intensità."""),
        article("Il calore e il freddo per il dolore", "Dolore",
            "Quando usare il caldo e quando il freddo per gestire il dolore.",
            """Il calore (borsa dell'acqua calda, impacchi caldi) rilassa i muscoli tesi e migliora la circolazione. È utile per rigidità cronica e tensioni muscolari. Applicare per 15-20 minuti.

Il freddo (impacco di ghiaccio avvolto in un panno) riduce il gonfiore e intorpidisce il dolore acuto. Utile dopo un'attività intensa o in caso di infiammazione.

Non applicare mai ghiaccio direttamente sulla pelle e non usare il calore su aree infiammate o gonfie."""),
        article("Tecniche di rilassamento per il dolore", "Dolore",
            "Come la respirazione e il rilassamento possono aiutare nella gestione del dolore.",
            """Il dolore e la tensione muscolare sono collegati: il dolore causa tensione, la tensione peggiora il dolore. Le tecniche di rilassamento possono spezzare questo circolo.

La respirazione diaframmatica lenta (4 secondi inspira, 6 secondi espira) attiva il sistema parasimpatico e riduce la percezione del dolore.

Il rilassamento muscolare progressivo, dove contrai e rilasci ogni gruppo muscolare, è particolarmente efficace per le tensioni muscolari croniche."""),
    )

    // ── Benessere Psicologico ──
    private fun articoliBenessere() = listOf(
        article("Il movimento come alleato del benessere mentale", "Benessere psicologico",
            "Come l'attività fisica influenza positivamente l'umore e la mente.",
            """L'attività fisica stimola il rilascio di endorfine, serotonina e dopamina — sostanze chimiche del cervello che migliorano l'umore e riducono stress e ansia.

Anche una breve passeggiata di 10 minuti può avere un effetto immediato sull'umore. L'esercizio all'aperto ha benefici aggiuntivi: la luce naturale e il contatto con la natura amplificano l'effetto positivo.

Non serve fare sport: giardinaggio, ballo, gioco con animali domestici — tutto ciò che ti fa muovere e ti piace conta."""),
        article("Affrontare i momenti difficili", "Benessere psicologico",
            "Strategie per gestire emozioni complesse durante il percorso.",
            """È normale provare paura, tristezza, rabbia o frustrazione durante un percorso di cura. Queste emozioni non sono debolezza — sono risposte umane normali.

Parlare con qualcuno di fiducia può aiutare molto: un familiare, un amico, un professionista. Non isolarti. I gruppi di supporto possono essere preziosi.

Scrivi nel diario dell'app come ti senti: mettere le emozioni in parole le rende più gestibili."""),
        article("Piccoli piaceri quotidiani", "Benessere psicologico",
            "L'importanza di ritagliare momenti di gioia nella quotidianità.",
            """Anche nei periodi difficili, è importante coltivare piccoli momenti di piacere: una tazza di tè al sole, una telefonata con un'amica, un libro, la musica preferita.

Questi momenti non sono frivolezze — sono nutrimento per la mente. Pianifica almeno un'attività piacevole al giorno, anche piccola.

La gratitudine aiuta: prova a pensare ogni sera a 3 cose positive della giornata. Anche le più piccole contano."""),
        article("Mindfulness: essere presente nel momento", "Benessere psicologico",
            "Come la pratica della consapevolezza può ridurre ansia e stress.",
            """La mindfulness è la capacità di essere presenti nel momento, senza giudicare. Non è svuotare la mente — è osservare pensieri e sensazioni con curiosità gentile.

Puoi praticare ovunque: mentre cammini, mentre mangi, mentre respiri. Porta l'attenzione a ciò che stai facendo in questo momento, usando i 5 sensi.

Inizia con 5 minuti al giorno. L'app include esercizi di respirazione e meditazione che possono guidarti."""),
        article("Il valore del supporto sociale", "Benessere psicologico",
            "Perché mantenere i rapporti sociali è importante per la salute.",
            """Le relazioni sociali sono un fattore protettivo fondamentale per la salute, sia fisica che mentale. Le persone con una rete sociale solida affrontano meglio le sfide.

Non devi affrontare tutto da sola. Chiedi aiuto quando ne hai bisogno — non è debolezza, è saggezza. Mantieni i contatti anche quando preferiresti isolarti.

Se ti senti sola, considera gruppi di supporto, volontariato, corsi, attività di quartiere. Anche i contatti digitali contano."""),
    )

    // ── Linfedema ──
    private fun articoliLinfedema() = listOf(
        article("Esercizi sicuri per chi convive con il linfedema", "Linfedema",
            "Come muoversi in sicurezza per favorire il drenaggio linfatico.",
            """L'attività fisica è sicura e raccomandata per chi ha il linfedema. Il movimento muscolare favorisce il drenaggio linfatico e può aiutare a ridurre il gonfiore.

Indossa sempre il tutore compressivo durante l'esercizio, se prescritto. Inizia con intensità bassa e aumenta gradualmente. Evita di sollevare pesi molto pesanti con l'arto interessato.

Esercizi consigliati: cammino, nuoto, cyclette, yoga dolce, esercizi di respirazione, movimenti ritmici dell'arto interessato."""),
        article("Riconoscere i segnali del linfedema", "Linfedema",
            "Segni precoci da monitorare e quando rivolgersi allo specialista.",
            """Il linfedema si manifesta con gonfiore, pesantezza, tensione o formicolio in un arto. Può svilupparsi settimane, mesi o anni dopo il trattamento.

Monitora quotidianamente: confronta gli arti, nota se i vestiti o i gioielli stringono più del solito. Se noti differenze, parlane tempestivamente con il medico.

La diagnosi e il trattamento precoce sono fondamentali per gestire il linfedema in modo efficace."""),
        article("La cura della pelle nel linfedema", "Linfedema",
            "Perché la pelle dell'arto interessato richiede attenzioni particolari.",
            """La pelle dell'arto con linfedema è più vulnerabile alle infezioni. Mantienila pulita, idratata e protetta. Usa creme emollienti senza profumo.

Proteggi la pelle da tagli, graffi e punture di insetto. Usa guanti per il giardinaggio e le pulizie. Evita il sole diretto sull'arto interessato.

In caso di arrossamento, calore, dolore o febbre, contatta subito il medico: potrebbero essere segni di infezione."""),
        article("Indumenti compressivi e movimento", "Linfedema",
            "Come usare correttamente gli indumenti compressivi durante l'esercizio.",
            """Gli indumenti compressivi (bracciali, calze, guanti) aiutano il drenaggio linfatico e prevengono il peggioramento del gonfiore.

Indossali durante l'esercizio fisico, ma assicurati che siano della misura giusta: non devono stringere troppo né essere troppo larghi. Falli controllare regolarmente.

Rimuovili per il nuoto in piscina e l'acquagym, dove la pressione dell'acqua svolge una funzione compressiva naturale."""),
        article("Automassaggio linfodrenante", "Linfedema",
            "Tecniche semplici di automassaggio per favorire il drenaggio.",
            """L'automassaggio linfodrenante è una tecnica dolce che puoi fare a casa per favorire il drenaggio. Non è un massaggio muscolare: i tocchi sono leggeri, lenti e ritmici.

La direzione è sempre verso il cuore: dal polso alla spalla, dalla caviglia all'inguine. Inizia dalla zona non gonfia e procedi verso la zona con linfedema.

Chiedi al tuo fisioterapista specializzato di insegnarti la tecnica corretta. Un massaggio fatto male può peggiorare la situazione."""),
    )

    // ── Helper per creare un ArticleEntity in modo compatto ──
    private fun article(
        title: String,
        category: String,
        summary: String,
        body: String,
        isFeatured: Boolean = false
    ) = ArticleEntity(
        title = title,
        category = category,
        summary = summary,
        body = body,
        isFeatured = isFeatured
    )
}
