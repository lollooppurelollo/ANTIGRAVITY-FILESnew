package com.kinapto.fitadapt.domain

import com.kinapto.fitadapt.data.local.entity.CardExerciseEntity
import com.kinapto.fitadapt.data.local.entity.TrainingCardEntity
import com.kinapto.fitadapt.data.repository.ScaleEntryRepository
import com.kinapto.fitadapt.data.repository.TrainingCardRepository
import com.kinapto.fitadapt.notification.NotificationHelper
import io.mockk.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class AdaptationEndToEndTest {

    private lateinit var cardRepository: TrainingCardRepository
    private lateinit var scaleRepository: ScaleEntryRepository
    private lateinit var notificationHelper: NotificationHelper
    private lateinit var adaptationManager: AdaptationManager

    @Before
    fun setup() {
        cardRepository = mockk(relaxed = true)
        scaleRepository = mockk(relaxed = true)
        notificationHelper = mockk(relaxed = true)
        adaptationManager = AdaptationManager(cardRepository, scaleRepository, notificationHelper)
    }

    @Test
    fun `when nausea is high, card should be downregulated`() = runTest {
        // Arrange
        val card = TrainingCardEntity(
            id = 1L,
            title = "Test Card",
            adaptationBiometricType = "NAUSEA",
            adaptationThreshold = 5,
            adaptationWindowDays = 7,
            adaptationAction = "DOWNREGULATE",
            isAdapted = false
        )

        val exercise = CardExerciseEntity(
            id = 10L,
            cardId = 1L,
            exerciseId = 100L,
            customIntensity = "alta",
            orderIndex = 0
        )

        coEvery { scaleRepository.getAverageNauseaSince(any()) } returns 7.0f
        coEvery { cardRepository.getCardExercisesSync(1L) } returns listOf(exercise)

        // Act
        adaptationManager.checkAndApplyAdaptation(card)

        // Assert
        // Verify intensity was reduced from "alta" to "moderata"
        coVerify { 
            cardRepository.updateCardExercise(match { it.customIntensity == "moderata" }) 
        }
        
        // Verify card was marked as adapted
        coVerify { 
            cardRepository.updateCard(match { it.isAdapted }) 
        }

        // Verify notification was sent
        verify { 
            notificationHelper.showAdaptationNotification(any(), any()) 
        }
    }

    @Test
    fun `when nausea is low, no adaptation should occur`() = runTest {
        // Arrange
        val card = TrainingCardEntity(
            id = 1L,
            title = "Test Card",
            adaptationBiometricType = "NAUSEA",
            adaptationThreshold = 5,
            adaptationWindowDays = 7,
            adaptationAction = "DOWNREGULATE",
            isAdapted = false
        )

        coEvery { scaleRepository.getAverageNauseaSince(any()) } returns 2.0f

        // Act
        adaptationManager.checkAndApplyAdaptation(card)

        // Assert
        coVerify(exactly = 0) { cardRepository.updateCardExercise(any()) }
        coVerify(exactly = 0) { cardRepository.updateCard(any()) }
        verify(exactly = 0) { notificationHelper.showAdaptationNotification(any(), any()) }
    }
}
