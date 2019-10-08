package com.larrynguyen.notewall.addeditnote

import android.app.Activity
import android.content.Intent
import android.util.Log
import android.view.Menu
import android.widget.Toast
import com.larrynguyen.notewall.BR
import com.larrynguyen.notewall.BasicActivity
import com.larrynguyen.notewall.C
import com.larrynguyen.notewall.R
import com.larrynguyen.notewall.addeditnote.AddEditNoteViewModel.Companion.OPTION_COMPLETED
import com.larrynguyen.notewall.addeditnote.AddEditNoteViewModel.Companion.OPTION_IMPORTANT
import com.larrynguyen.notewall.addeditnote.AddEditNoteViewModel.Companion.OPTION_PICKCOLOR
import com.larrynguyen.notewall.addeditnote.AddEditNoteViewModel.Companion.OPTION_SCHEDULEALARM
import com.larrynguyen.notewall.menu.colorpicker.ColorPickerDialogFragment
import com.larrynguyen.notewall.databinding.ActivityAddeditnoteBinding
import com.larrynguyen.notewall.menu.MenuItem
import com.larrynguyen.notewall.menu.bottomsheet.BottomMenuDialog
import com.github.florent37.singledateandtimepicker.dialog.SingleDateAndTimePickerDialog

class AddEditNoteActivity : BasicActivity<ActivityAddeditnoteBinding, AddEditNoteViewModel>() {

	companion object {
		const val TAG = "AddEditNoteActivity"
	}

	override fun onCreateOptionsMenu(menu: Menu?): Boolean {
		menuInflater.inflate(R.menu.note_addedit_menu, menu)

		return true
	}

	override fun getLayoutId() = R.layout.activity_addeditnote

	override fun getBindingVariable() = BR.viewModel

	override fun createViewModel() = getNoteId().takeIf { isValidId(it) }
			?.let { AddEditNoteViewModel(application, it) }
			?: AddEditNoteViewModel(application)

	override fun setupActionBar() {
		super.setupActionBar()
		supportActionBar?.setDisplayShowTitleEnabled(false)
	}

	override fun setupViewModel() {
		super.setupViewModel()

		subscriptions.add(viewModel.observeNoteAdded()
				.subscribe { finishWithResult(it) })

		subscriptions.add(viewModel.observeMenuCalled()
				.subscribe { openMenu(it) })

		subscriptions.add(viewModel.observeColorPickerCalled()
				.subscribe { openColorPicker(it) })

		subscriptions.add(viewModel.observeTimePickerCalled()
				.subscribe { openTimePicker() })

		subscriptions.add(viewModel.observeAlert()
				.subscribe { showAlert(getAlertMessage(it)) })
	}

	private fun getNoteId() = intent.getLongExtra(C.EXTRA_NOTE_ID, C.DUMMY_ID)

	private fun isValidId(id: Long) = id != C.DUMMY_ID

	private fun finishWithResult(noteId: Long) {
		val intent = Intent().apply {
			putExtra(C.EXTRA_NOTE_ID, noteId)
		}
		setResult(Activity.RESULT_OK, intent)
		finish()
	}

	private fun openMenu(currentState: AddEditNoteViewModel.State) {
		val menu = AddEditMenuDialog.newInstance(currentState)

		menu.setListener(object : BottomMenuDialog.Listener {
			override fun onItemSelected(item: MenuItem) {
				menu.hide()
				onMenuItemSelected(item)
			}
		})

		menu.show(supportFragmentManager)
	}

	private fun onMenuItemSelected(item: MenuItem) {
		val option = when (item.id) {
			R.id.im_important -> OPTION_IMPORTANT
			R.id.im_completed -> OPTION_COMPLETED
			R.id.im_colorpicker -> OPTION_PICKCOLOR
			R.id.im_alarm -> OPTION_SCHEDULEALARM
			else -> throw RuntimeException("Unknown options menu item!")
		}
		viewModel.handleOptionSelection(option)
	}

	private fun openColorPicker(currentColor: Int) {
		val colors = resources.getIntArray(R.array.noteColors)
		val colorPicker = ColorPickerDialogFragment.newInstance(colors, currentColor)

		colorPicker.setListener(object : ColorPickerDialogFragment.Listener {
			override fun onColorPicked(color: Int) {
				colorPicker.hide()
				viewModel.setColor(color)
			}
		})

		colorPicker.show(supportFragmentManager)
	}

	private fun openTimePicker() {
		SingleDateAndTimePickerDialog.Builder(this)
				.title(getString(R.string.pick_reminder_time))
				.listener { viewModel.setReminderTime(it) }
				.minutesStep(1)
				.mustBeOnFuture()
				.curved()
				.build()
				.display()
	}

	private fun showAlert(message: String) = Toast.makeText(this, message, Toast.LENGTH_SHORT).show()

	private fun getAlertMessage(alertId: Int) = when (alertId) {
		AddEditNoteViewModel.ALERT_EMPTY -> getString(R.string.alert_note_title_empty)
		AddEditNoteViewModel.ALERT_TITLE_LONG -> getString(R.string.alert_note_title_long)
		AddEditNoteViewModel.ALERT_BODY_LONG -> getString(R.string.alert_note_body_long)
		else -> {
			Log.w(TAG, "Unknown alert message id!")
			""
		}
	}
}