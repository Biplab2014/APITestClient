package com.app.apiclient.data.database

import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import android.content.Context
import com.app.apiclient.data.database.dao.*
import com.app.apiclient.data.model.*

@Database(
    entities = [
        ApiRequest::class,
        ApiResponse::class,
        RequestCollection::class,
        Environment::class
    ],
    version = 1,
    exportSchema = true
)
@TypeConverters(Converters::class)
abstract class ApiClientDatabase : RoomDatabase() {
    
    abstract fun apiRequestDao(): ApiRequestDao
    abstract fun apiResponseDao(): ApiResponseDao
    abstract fun collectionDao(): CollectionDao
    abstract fun environmentDao(): EnvironmentDao
    
    companion object {
        const val DATABASE_NAME = "api_client_database"
        
        @Volatile
        private var INSTANCE: ApiClientDatabase? = null
        
        fun getDatabase(context: Context): ApiClientDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    ApiClientDatabase::class.java,
                    DATABASE_NAME
                )
                .fallbackToDestructiveMigration()
                .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
