package com.app.apiclient.data.repository

import com.app.apiclient.data.database.dao.EnvironmentDao
import com.app.apiclient.data.model.Environment
import kotlinx.coroutines.flow.Flow
import java.util.*
class EnvironmentRepository(
    private val environmentDao: EnvironmentDao
) {
    
    fun getAllEnvironments(): Flow<List<Environment>> = environmentDao.getAllEnvironments()
    
    suspend fun getEnvironmentById(id: String): Environment? = environmentDao.getEnvironmentById(id)
    
    suspend fun getActiveEnvironment(): Environment? = environmentDao.getActiveEnvironment()
    
    fun getActiveEnvironmentFlow(): Flow<Environment?> = environmentDao.getActiveEnvironmentFlow()
    
    suspend fun createEnvironment(
        name: String,
        variables: Map<String, String> = emptyMap()
    ): Environment {
        val environment = Environment(
            id = UUID.randomUUID().toString(),
            name = name,
            variables = variables,
            isActive = false,
            createdAt = System.currentTimeMillis(),
            updatedAt = System.currentTimeMillis()
        )
        environmentDao.insertEnvironment(environment)
        return environment
    }
    
    suspend fun updateEnvironment(environment: Environment) {
        val updatedEnvironment = environment.copy(
            updatedAt = System.currentTimeMillis()
        )
        environmentDao.updateEnvironment(updatedEnvironment)
    }
    
    suspend fun setActiveEnvironment(environmentId: String) {
        environmentDao.setActiveEnvironment(environmentId)
    }
    
    suspend fun deleteEnvironment(environment: Environment) {
        environmentDao.deleteEnvironment(environment)
    }
    
    suspend fun deleteEnvironmentById(id: String) {
        environmentDao.deleteEnvironmentById(id)
    }
    
    /**
     * Replaces variables in the given text with values from the active environment
     * Variables should be in the format {{variableName}}
     */
    suspend fun replaceVariables(text: String): String {
        val activeEnvironment = getActiveEnvironment() ?: return text
        
        var result = text
        activeEnvironment.variables.forEach { (key, value) ->
            result = result.replace("{{$key}}", value)
        }
        return result
    }
    
    /**
     * Replaces variables in a map of strings
     */
    suspend fun replaceVariablesInMap(map: Map<String, String>): Map<String, String> {
        return map.mapValues { (_, value) ->
            replaceVariables(value)
        }
    }
}
