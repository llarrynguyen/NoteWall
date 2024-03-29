package com.larrynguyen.notewall.data.source.local

import android.arch.persistence.room.*
import com.larrynguyen.notewall.data.model.Note

@Dao
interface NotesDao {

	@Query("SELECT * FROM notes ORDER BY id DESC")
	fun getAll(): List<Note>

	@Query("SELECT * FROM notes WHERE isImportant = 1 ORDER BY id DESC")
	fun getImportant(): List<Note>

	@Query("SELECT * FROM notes WHERE topic = :topicName")
	fun getAllByTopic(topicName: String): List<Note>

	@Query("SELECT * FROM notes WHERE id IN (:ids)")
	fun loadAllByIds(ids: LongArray): List<Note>

	@Query("SELECT * FROM notes WHERE id LIKE :id")
	fun findById(id: Long): Note?

	@Insert(onConflict = OnConflictStrategy.REPLACE)
	fun insert(note: Note): Long

	@Insert(onConflict = OnConflictStrategy.IGNORE)
	fun insert(note: List<Note>): LongArray

	@Update
	fun update(vararg note: Note)

	@Delete
	fun delete(note: Note)

	@Query("DELETE FROM notes WHERE id = :id")
	fun deleteById(id: Long)

	@Query("DELETE FROM notes")
	fun deleteAll()

	@Query("UPDATE notes SET isImportant = :isImportant WHERE id = :id")
	fun switchImportance(id: Long, isImportant: Boolean)

	@Query("UPDATE notes SET isCompleted = :isCompleted WHERE id = :id")
	fun switchCompletion(id: Long, isCompleted: Boolean)
}