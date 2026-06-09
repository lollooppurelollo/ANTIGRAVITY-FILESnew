package com.kinapto.fitadapt.data.local.dao

import androidx.room.*
import com.kinapto.fitadapt.data.local.entity.AdaptationRuleEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface AdaptationRuleDao {
    @Query("SELECT * FROM adaptation_rules WHERE cardId = :cardId")
    fun getRulesForCard(cardId: Long): Flow<List<AdaptationRuleEntity>>

    @Query("SELECT * FROM adaptation_rules WHERE cardId = :cardId")
    suspend fun getRulesForCardSync(cardId: Long): List<AdaptationRuleEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(rule: AdaptationRuleEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(rules: List<AdaptationRuleEntity>)

    @Update
    suspend fun update(rule: AdaptationRuleEntity)

    @Delete
    suspend fun delete(rule: AdaptationRuleEntity)

    @Query("DELETE FROM adaptation_rules WHERE cardId = :cardId")
    suspend fun deleteRulesForCard(cardId: Long)
}
