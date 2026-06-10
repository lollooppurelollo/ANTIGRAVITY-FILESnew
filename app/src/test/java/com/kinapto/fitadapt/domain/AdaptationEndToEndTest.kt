package com.kinapto.fitadapt.domain

import com.kinapto.fitadapt.data.local.dao.AdaptationLogDao
import com.kinapto.fitadapt.data.local.entity.AdaptationLogEntity
import com.kinapto.fitadapt.data.local.entity.AdaptationRuleEntity
import com.kinapto.fitadapt.data.local.entity.CardExerciseEntity
import com.kinapto.fitadapt.data.local.entity.TrainingCardEntity
import com.kinapto.fitadapt.data.repository.ScaleEntryRepository
import com.kinapto.fitadapt.data.repository.SessionRepository
import com.kinapto.fitadapt.data.repository.TrainingCardRepository
import com.kinapto.fitadapt.notification.NotificationHelper
import io.mockk.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import javax.inject.Provider

@OptIn(ExperimentalCoroutinesApi::class)
class AdaptationEndToEndTest {

    private lateinit var cardRepository: TrainingCardRepository
    private lateinit var scaleRepository: ScaleEntryRepository
    private lateinit var sessionRepository: SessionRepository
    private lateinit var sessionRepositoryProvider: Provider<SessionRepository>
    private lateinit var notificationHelper: NotificationHelper
    private lateinit var adaptationLogDao: AdaptationLogDao
    private lateinit var adaptationManager: AdaptationManager

    @Before
    fun setup() {
        cardRepository = mockk(relaxed = true)
        scaleRepository = mockk(relaxed = true)
        sessionRepository = mockk(relaxed = true)
        sessionRepositoryProvider = mockk(relaxed = true)
        notificationHelper = mockk(relaxed = true)
        adaptationLogDao = mockk(relaxed = true)

        every { sessionRepositoryProvider.get() } returns sessionRepository

        adaptationManager = AdaptationManager(
            cardRepository,
            scaleRepository,
            sessionRepositoryProvider,
            notificationHelper,
            adaptationLogDao
        )
    }

    @Test
    fun `when AND rule group is satisfied, adaptation should trigger`() = runTest {
        // Arrange
        val card = TrainingCardEntity(id = 1L, title = "Card 1")
        val rules = listOf(
            AdaptationRuleEntity(
                id = 1, cardId = 1L, groupId = "G1", triggerType = "BIOMETRIC",
                parameter = "PAIN", operator = "GT", threshold = 7f,
                actionType = "DELTA", actionValue = """{"reps": -2}"""
            ),
            AdaptationRuleEntity(
                id = 2, cardId = 1L, groupId = "G1", triggerType = "BIOMETRIC",
                parameter = "EFFORT", operator = "GT", threshold = 8f,
                actionType = "DELTA", actionValue = """{"reps": -2}"""
            )
        )

        coEvery { cardRepository.getRulesForCardSync(1L) } returns rules
        coEvery { scaleRepository.getAveragePainSince(any()) } returns 8.0f
        coEvery { scaleRepository.getAverageEffortSince(any()) } returns 9.0f

        // Act
        adaptationManager.checkAndApplyAdaptation(card)

        // Assert
        coVerify {
            cardRepository.duplicateAndAdaptCard(
                cardId = 1L,
                adaptationDescription = match { it.contains("PAIN") && it.contains("EFFORT") },
                globalRepsDelta = -2
            )
        }
        coVerify { adaptationLogDao.insert(any()) }
    }

    @Test
    fun `when AND rule group is partially satisfied, adaptation should NOT trigger`() = runTest {
        // Arrange
        val card = TrainingCardEntity(id = 1L, title = "Card 1")
        val rules = listOf(
            AdaptationRuleEntity(
                id = 1, cardId = 1L, groupId = "G1", triggerType = "BIOMETRIC",
                parameter = "PAIN", operator = "GT", threshold = 7f,
                actionType = "DELTA", actionValue = "{}"
            ),
            AdaptationRuleEntity(
                id = 2, cardId = 1L, groupId = "G1", triggerType = "BIOMETRIC",
                parameter = "EFFORT", operator = "GT", threshold = 8f,
                actionType = "DELTA", actionValue = "{}"
            )
        )

        coEvery { cardRepository.getRulesForCardSync(1L) } returns rules
        coEvery { scaleRepository.getAveragePainSince(any()) } returns 8.0f // Satisfied
        coEvery { scaleRepository.getAverageEffortSince(any()) } returns 5.0f // NOT satisfied

        // Act
        adaptationManager.checkAndApplyAdaptation(card)

        // Assert
        coVerify(exactly = 0) { cardRepository.duplicateAndAdaptCard(any(), any(), any(), any(), any(), any()) }
    }

    @Test
    fun `when multiple OR groups exist, any satisfied group triggers adaptation`() = runTest {
        // Arrange
        val card = TrainingCardEntity(id = 1L, title = "Card 1")
        val rules = listOf(
            // Group 1: PAIN > 9 (not satisfied)
            AdaptationRuleEntity(
                id = 1, cardId = 1L, groupId = "G1", triggerType = "BIOMETRIC",
                parameter = "PAIN", operator = "GT", threshold = 9f,
                actionType = "DELTA", actionValue = """{"reps": -5}"""
            ),
            // Group 2: EFFORT > 7 (satisfied)
            AdaptationRuleEntity(
                id = 2, cardId = 1L, groupId = "G2", triggerType = "BIOMETRIC",
                parameter = "EFFORT", operator = "GT", threshold = 7f,
                actionType = "DELTA", actionValue = """{"reps": -2}"""
            )
        )

        coEvery { cardRepository.getRulesForCardSync(1L) } returns rules
        coEvery { scaleRepository.getAveragePainSince(any()) } returns 5.0f
        coEvery { scaleRepository.getAverageEffortSince(any()) } returns 8.0f

        // Act
        adaptationManager.checkAndApplyAdaptation(card)

        // Assert
        coVerify {
            cardRepository.duplicateAndAdaptCard(
                cardId = 1L,
                adaptationDescription = match { it.contains("EFFORT") },
                globalRepsDelta = -2
            )
        }
    }

    @Test
    fun `verify adaptation log contains human readable description for CRF`() = runTest {
        // Arrange
        val card = TrainingCardEntity(id = 1L, title = "Card 1")
        val rules = listOf(
            AdaptationRuleEntity(
                id = 1, cardId = 1L, groupId = "G1", triggerType = "BIOMETRIC",
                parameter = "NAUSEA", operator = "GT", threshold = 5f,
                actionType = "DELTA", actionValue = """{"reps": -1, "intensity": -1}"""
            )
        )

        coEvery { cardRepository.getRulesForCardSync(1L) } returns rules
        coEvery { scaleRepository.getAverageNauseaSince(any()) } returns 7.5f

        val logSlot = slot<AdaptationLogEntity>()
        coEvery { adaptationLogDao.insert(capture(logSlot)) } returns 1L

        // Act
        adaptationManager.checkAndApplyAdaptation(card)

        // Assert
        val log = logSlot.captured
        assert(log.triggerDescription.contains("NAUSEA (7,5) GT 5,0"))
        assert(log.actionTaken.contains("Reps -1"))
        assert(log.actionTaken.contains("Intensità -1"))
    }
}
