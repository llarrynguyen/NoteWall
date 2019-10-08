package com.larrynguyen.notewall

import android.app.Application
import android.content.Context
import android.support.annotation.CallSuper
import com.larrynguyen.notewall.data.source.NotesRepository
import com.larrynguyen.notewall.data.source.TopicsRepository
import com.larrynguyen.notewall.data.source.local.NotesDatabase
import com.larrynguyen.notewall.notice.AlarmReceiver
import com.larrynguyen.notewall.utils.cancelReminder
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser


abstract class NotesViewModel(application: Application) : BasicViewModel(application) {

	protected val repository = NotesRepository
	protected val topicsRep = TopicsRepository

	init {
		initRepositories(application.baseContext)
	}

	protected fun getFirebaseUser() = FirebaseAuth.getInstance().currentUser

	protected fun initRemoteRepositories(user: FirebaseUser) {
		repository.initRemoteSource(user.uid)
		topicsRep.initRemoteSource(user.uid)
	}

	private fun initLocalRepositories(database: NotesDatabase) {
		repository.initLocalSource(database.notesDao())
		topicsRep.initLocalSource(database.topicsDao())
	}

	private fun initRepositories(context: Context) {
		initLocalRepositories(NotesDatabase.getInstance(context))
		getFirebaseUser()?.let {
			initRemoteRepositories(it)
		}
	}

	@CallSuper
	protected open fun deleteNote(id: Long) {
		context.cancelReminder(id.toInt(), AlarmReceiver::class.java)
		repository.deleteItem(id)
	}
}