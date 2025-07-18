package com.app.apiclient.data.database.dao

import androidx.room.*
import com.app.apiclient.data.model.RequestCollection
import kotlinx.coroutines.flow.Flow

@Dao
interface CollectionDao {
    
    @Query("SELECT * FROM collections ORDER BY updatedAt DESC")
    fun getAllCollections(): Flow<List<RequestCollection>>

    @Query("SELECT * FROM collections WHERE id = :id")
    suspend fun getCollectionById(id: String): RequestCollection?

    @Query("SELECT * FROM collections WHERE name LIKE '%' || :query || '%'")
    fun searchCollections(query: String): Flow<List<RequestCollection>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCollection(collection: RequestCollection)

    @Update
    suspend fun updateCollection(collection: RequestCollection)

    @Delete
    suspend fun deleteCollection(collection: RequestCollection)
    
    @Query("DELETE FROM collections WHERE id = :id")
    suspend fun deleteCollectionById(id: String)
}
