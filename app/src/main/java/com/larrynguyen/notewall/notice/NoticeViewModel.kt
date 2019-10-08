package com.larrynguyen.notewall.notice

import android.app.Application
import android.databinding.ObservableField
import com.larrynguyen.notewall.NotesViewModel
import com.larrynguyen.notewall.data.model.Note
import com.larrynguyen.notewall.data.source.DataSource
import com.larrynguyen.notewall.utils.getTitle

class NoticeViewModel(application: Application, noteId: Long): NotesViewModel(application) {

	val titleField = ObservableField<String>("")

	init { loadNote(noteId) }

	private fun loadNote(id: Long) {
		repository.getItem(id, object : DataSource.SimpleRequestCallback<Note>() {
			override fun onSuccess(result: Note) = bind(result)
		})
	}

	private fun bind(note: Note) {
		titleField.set(note.getTitle(context))
	}
}