package com.larrynguyen.notewall.data.source.remote

import android.util.Log
import com.larrynguyen.notewall.SingletonHolder
import com.larrynguyen.notewall.data.model.Note
import com.larrynguyen.notewall.data.model.Topic
import com.larrynguyen.notewall.data.source.DataSource
import com.larrynguyen.notewall.data.source.EmptyResultException
import com.larrynguyen.notewall.data.source.NotesSource
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot

class FirestoreNotesSource private constructor(private val userId: String): NotesSource {

	internal companion object : SingletonHolder<FirestoreNotesSource, String>(
			{ userId -> FirestoreNotesSource(userId) }) {
		private const val TAG = "FirestoreNotesSource"

		private const val USER_DATA = "data"
		private const val USER_NOTES = "notes"

		private const val FIELD_IMPORTANT = "isImportant"
		private const val FIELD_COMPLETED = "isCompleted"
		private const val FIELD_TOPIC = "topic"
	}

	private val db = FirebaseFirestore.getInstance()

	override fun getAll(callback: DataSource.RequestCallback<List<Note>>) {
		requestUserNotes().get()
				.addOnSuccessListener { snapshot ->
					deserialize(snapshot).takeIf { it.isNotEmpty() }
							?.let { callback.onSuccess(it) }
							?: callback.onFailure(EmptyResultException("$TAG: empty"))
				}
				.addOnFailureListener { callback.onFailure(it) }
	}

	override fun getImportant(callback: DataSource.RequestCallback<List<Note>>) {
		requestUserNotes()
				.whereEqualTo(FIELD_IMPORTANT, true).get()
				.addOnSuccessListener { snapshot ->
					deserialize(snapshot).takeIf { it.isNotEmpty() }
							?.let { callback.onSuccess(it) }
							?: callback.onFailure(EmptyResultException("$TAG: no important notes"))
				}
				.addOnFailureListener { callback.onFailure(it) }
	}

	override fun getAllByTopic(topic: Topic, callback: DataSource.RequestCallback<List<Note>>) {
		requestUserNotes()
				.whereEqualTo(FIELD_TOPIC, topic.name).get()
				.addOnSuccessListener { snapshot ->
					deserialize(snapshot).takeIf { it.isNotEmpty() }
							?.let { callback.onSuccess(it) }
							?: callback.onFailure(EmptyResultException("$TAG: no items with the topic ${topic.name}"))
				}
				.addOnFailureListener { callback.onFailure(it) }
	}

	override fun getItem(id: Long, callback: DataSource.RequestCallback<Note>) {
		requestUserNote(id).get()
				.addOnSuccessListener { snapshot ->
					deserialize(snapshot)
							?.let { callback.onSuccess(it) }
							?: callback.onFailure(EmptyResultException("$TAG: no item with the id $id"))
				}
				.addOnFailureListener { callback.onFailure(it) }
	}

	override fun insertItem(item: Note, callback: DataSource.RequestCallback<Long>?) {
		requestUserNote(item.id)
				.set(item)
				.addOnSuccessListener { callback?.onSuccess(item.id) }
				.addOnFailureListener { callback?.onFailure(it) }
	}

	override fun insertItems(items: List<Note>, callback: DataSource.RequestCallback<LongArray>?) {
		items.forEach { note ->
			requestUserNote(note.id).set(note.id)
					.addOnFailureListener { callback?.onFailure(it) }
		}
	}

	override fun updateItem(item: Note) {
		requestUserNote(item.id)
				.set(item)
				.addOnSuccessListener {
					Log.d(TAG, "The note with id ${item.id} has been updated")
				}
				.addOnFailureListener {
					Log.w(TAG, "Faild to update the note with id ${item.id}")
				}
	}

	override fun reset(newItems: List<Note>) {
		deleteAll()
		requestUserNotes().add(newItems)
	}

	override fun deleteAll() {
		val batch = db.batch()
		requestUserNotes().get().result?.forEach {
			batch.delete(it.reference)
		}
		batch.commit()
	}

	override fun deleteItem(id: Long) {
		requestUserNote(id)
				.delete()
				.addOnSuccessListener { Log.d(TAG, "The note with id $id has been deleted") }
				.addOnFailureListener { Log.w(TAG, "Failed to delete the note with id $id") }
	}

	override fun switchImportance(id: Long, isImportant: Boolean) {
		requestUserNote(id)
				.update(FIELD_IMPORTANT, isImportant)
				.addOnSuccessListener { Log.d(TAG, "The note's $id importance has been set to $isImportant") }
				.addOnFailureListener { Log.w(TAG, "Failed to update the note's $id importance") }
	}

	override fun switchCompletion(id: Long, isCompleted: Boolean) {
		requestUserNote(id)
				.update(FIELD_COMPLETED, isCompleted)
				.addOnSuccessListener { Log.d(TAG, "The note's $id completion has been set to $isCompleted") }
				.addOnFailureListener { Log.w(TAG, "Failed to update the note's $id completion") }
	}

	private fun requestUserNotes() = db.collection(USER_DATA).document(userId).collection(USER_NOTES)

	private fun requestUserNote(id: Long) = requestUserNotes().document(id.toString())

	private fun deserialize(snapshot: DocumentSnapshot) = snapshot.toObject(Note::class.java)

	private fun deserialize(snapshot: QuerySnapshot) = snapshot.toObjects(Note::class.java)
}