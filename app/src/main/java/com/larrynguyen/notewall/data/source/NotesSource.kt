package com.larrynguyen.notewall.data.source

import com.larrynguyen.notewall.data.model.Note
import com.larrynguyen.notewall.data.model.Topic

interface NotesSource: DataSource<Note> {
	fun getImportant(callback: DataSource.RequestCallback<List<Note>>)
	fun getAllByTopic(topic: Topic, callback: DataSource.RequestCallback<List<Note>>)
	fun switchImportance(id: Long, isImportant: Boolean)
	fun switchCompletion(id: Long, isCompleted: Boolean)
}