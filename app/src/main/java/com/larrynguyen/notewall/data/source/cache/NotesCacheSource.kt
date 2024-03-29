package com.larrynguyen.notewall.data.source.cache

import com.larrynguyen.notewall.data.model.Note
import com.larrynguyen.notewall.data.model.Topic

object NotesCacheSource {

	private val cache: MutableMap<Long, Note> = HashMap()

	private val notesList get() = cache.values.toList()

	fun getAll() = notesList

	fun getImportant() = notesList.filter { it.isImportant }

	fun getAllByTopic(topic: Topic) = notesList.filter { it.topic == topic.name }

	fun getItem(id: Long) = notesList.find { it.id == id }

	fun insertItem(item: Note) { cache[item.id] = item }

	fun insertUniqueItems(remoteNotes: List<Note>) {
		remoteNotes.forEach {
			if (cache.containsKey(it.id).not())
				insertItem(it)
		}
	}

	fun updateItem(item: Note) {
		if (cache.containsKey(item.id)) {
			cache[item.id] = item
		}
	}

	fun reset(newItems: List<Note>) {
		cache.clear()
		newItems.forEach {
			cache[it.id] = it
		}
	}

	fun deleteAll() = cache.clear()

	fun deleteItem(id: Long) = cache.remove(id)

	fun switchImportance(id: Long, isImportant: Boolean) {
		if (cache.containsKey(id)) {
			cache[id]?.isImportant = isImportant
		}
	}

	fun switchComptetion(id: Long, isCompleted: Boolean) {
		if (cache.containsKey(id)) {
			cache[id]?.isCompleted = isCompleted
		}
	}
}