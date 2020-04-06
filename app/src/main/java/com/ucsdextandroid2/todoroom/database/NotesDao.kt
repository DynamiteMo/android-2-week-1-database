package com.ucsdextandroid2.todoroom.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.ucsdextandroid2.todoroom.model.Note
import kotlinx.coroutines.flow.Flow

/**
 * Created by rjaylward on 2019-07-05
 */

@Dao
interface NotesDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertNote(note: Note)

    @Delete
    fun deleteNote(note: Note)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNoteAsync(note: Note)

    @Delete
    suspend fun deleteNoteAsync(note: Note)

//    @Query("SELECT * FROM notes")
//    fun getAllNotes(): List<Note>

    @Query("SELECT * FROM notes ORDER BY created_at DESC")
    fun getAllNotesFlow(): Flow<List<Note>>

}
