package com.larrynguyen.notewall

import android.app.Application
import android.arch.lifecycle.AndroidViewModel
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler

abstract class BasicViewModel(application: Application): AndroidViewModel(application) {

	private val uiHandler = Handler(application.mainLooper)

	protected val context: Context get() = getApplication<Application>().applicationContext

	open fun saveState(bundle: Bundle) { }

	open fun loadState(bundle: Bundle) { }

	open fun handleOptionSelection(option: Int) = false

	open fun handleActivityResult(requestCode: Int, resultCode: Int, data: Intent?) { }

	protected fun runOnUiThread(action: Runnable) { uiHandler.post(action) }
}