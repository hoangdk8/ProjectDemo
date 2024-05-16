package com.example.projectdemo.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.projectdemo.data.dataclass.DataDefaultRings
import com.example.projectdemo.di.ApplicationScope
import kotlinx.coroutines.CoroutineScope
import javax.inject.Inject
import javax.inject.Provider

@Database(entities = [DataDefaultRings.RingTone::class], version = 2)
abstract class AppDatabase : RoomDatabase() {
    abstract fun musicDao(): MusicDao

    class Callback @Inject constructor(
        private val database: Provider<AppDatabase>,
        @ApplicationScope private val applicationScope: CoroutineScope
    ) : RoomDatabase.Callback()
}