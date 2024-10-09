package com.example.notesapp.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.notesapp.dao.NoteDao
import com.example.notesapp.dataClass.Note

@Database(entities = [Note::class], version = 2, exportSchema = false)  // Version numarasını artırdık
abstract class NoteDatabase : RoomDatabase() {

    abstract fun noteDao(): NoteDao

    companion object {
        @Volatile
        private var INSTANCE: NoteDatabase? = null

        fun getDatabase(context: Context): NoteDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    NoteDatabase::class.java,
                    "note_database"
                )
                    .fallbackToDestructiveMigration()  // Sürüm değişikliğinde veritabanını yeniden oluşturur
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
