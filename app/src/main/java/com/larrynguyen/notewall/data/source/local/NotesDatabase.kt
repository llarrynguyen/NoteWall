package com.larrynguyen.notewall.data.source.local

import android.arch.persistence.db.SupportSQLiteDatabase
import android.arch.persistence.room.Database
import android.arch.persistence.room.Room
import android.arch.persistence.room.RoomDatabase
import android.arch.persistence.room.migration.Migration
import android.content.Context
import com.larrynguyen.notewall.data.model.Note
import com.larrynguyen.notewall.SingletonHolder
import com.larrynguyen.notewall.data.model.Topic
import com.larrynguyen.notewall.data.source.local.NotesDatabase.Companion.MIGRATION_1_2

@Database(entities = [Note::class, Topic::class], version = 2)
abstract class NotesDatabase : RoomDatabase() {

	abstract fun notesDao(): NotesDao

	abstract fun topicsDao(): TopicsDao

	companion object : SingletonHolder<NotesDatabase, Context>({
		Room.databaseBuilder(it, NotesDatabase::class.java, "notes.db")
				.addMigrations(MIGRATION_1_2)
				.build()
	}) {
		@JvmField val MIGRATION_1_2: Migration = object : Migration(1, 2) {
			override fun migrate(database: SupportSQLiteDatabase) {
				database.execSQL("CREATE TABLE topics(name TEXT PRIMARY KEY NOT NULL);")
			}
		}
	}
}