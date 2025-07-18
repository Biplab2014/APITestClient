package com.app.apiclient.data.database.dao

import androidx.room.*
import com.app.apiclient.data.model.Environment
import kotlinx.coroutines.flow.Flow

@Dao
interface EnvironmentDao {
    
    @Query("SELECT * FROM environments ORDER BY updatedAt DESC")
    fun getAllEnvironments(): Flow<List<Environment>>
    
    @Query("SELECT * FROM environments WHERE id = :id")
    suspend fun getEnvironmentById(id: String): Environment?
    
    @Query("SELECT * FROM environments WHERE isActive = 1 LIMIT 1")
    suspend fun getActiveEnvironment(): Environment?
    
    @Query("SELECT * FROM environments WHERE isActive = 1 LIMIT 1")
    fun getActiveEnvironmentFlow(): Flow<Environment?>
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertEnvironment(environment: Environment)
    
    @Update
    suspend fun updateEnvironment(environment: Environment)
    
    @Delete
    suspend fun deleteEnvironment(environment: Environment)
    
    @Query("DELETE FROM environments WHERE id = :id")
    suspend fun deleteEnvironmentById(id: String)
    
    @Query("UPDATE environments SET isActive = 0")
    suspend fun deactivateAllEnvironments()
    
    @Transaction
    suspend fun setActiveEnvironment(environmentId: String) {
        deactivateAllEnvironments()
        val environment = getEnvironmentById(environmentId)
        environment?.let {
            updateEnvironment(it.copy(isActive = true))
        }
    }
}
