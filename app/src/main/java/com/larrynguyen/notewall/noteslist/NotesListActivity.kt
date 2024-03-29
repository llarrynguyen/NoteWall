package com.larrynguyen.notewall.noteslist

import android.content.Intent
import android.content.res.Resources
import android.graphics.Rect
import android.os.Build
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.SearchView
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.ViewTreeObserver
import android.widget.PopupMenu
import android.widget.Toast
import com.larrynguyen.notewall.*
import com.larrynguyen.notewall.addeditnote.AddEditNoteActivity
import com.larrynguyen.notewall.databinding.ActivityNoteslistBinding
import com.larrynguyen.notewall.notedetail.NoteDetailActivity
import com.larrynguyen.notewall.noteslist.NotesListViewModel.Companion.OPTION_CLOUD
import com.larrynguyen.notewall.noteslist.NotesListViewModel.Companion.OPTION_EXPORT
import com.larrynguyen.notewall.noteslist.NotesListViewModel.Companion.OPTION_FILTER
import com.larrynguyen.notewall.noteslist.NotesListViewModel.Companion.OPTION_IMPORT
import com.larrynguyen.notewall.noteslist.NotesListViewModel.Companion.OPTION_SORT
import com.larrynguyen.notewall.sortdialog.SortDialogFragment
import com.larrynguyen.notewall.topicpicker.TopicPickerActivity
import com.larrynguyen.notewall.utils.tryStartActivity
import com.firebase.ui.auth.AuthUI
import java.util.*
import kotlinx.android.synthetic.main.activity_noteslist.*

class NotesListActivity : BasicActivity<ActivityNoteslistBinding, NotesListViewModel>(),
		ViewTreeObserver.OnGlobalLayoutListener, SearchView.OnQueryTextListener {

	private val sreenHeight get() = Resources.getSystem().displayMetrics.heightPixels

	private lateinit var searchView: SearchView

	override fun onGlobalLayout() {
		val maybeKeyboard = rootContainer.height < sreenHeight * 2 / 3
		onKeyboardStateChanged(maybeKeyboard)
	}

	override fun getLayoutId() = R.layout.activity_noteslist

	override fun getBindingVariable() = BR.viewModel

	override fun createViewModel() = NotesListViewModel(application, getListAdapter())

	override fun onCreate(savedInstanceState: Bundle?) {
		setTheme(R.style.AppTheme)
		super.onCreate(savedInstanceState)
	}

	override fun onResume() {
		super.onResume()
		rootContainer.viewTreeObserver.addOnGlobalLayoutListener(this)
	}

	override fun onPause() {
		super.onPause()
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
			rootContainer.viewTreeObserver.removeOnGlobalLayoutListener(this)
		} else rootContainer.viewTreeObserver.removeGlobalOnLayoutListener(this)
	}

	override fun onCreateOptionsMenu(menu: Menu): Boolean {
		menuInflater.inflate(R.menu.notes_list_menu, menu)

		val searchItem = menu.findItem(R.id.miSearch)
		searchView = searchItem.actionView as SearchView
		searchView.setOnQueryTextListener(this)
		searchView.maxWidth = Integer.MAX_VALUE

		return true
	}

	override fun onOptionsItemSelected(item: MenuItem) = when (item.itemId) {
		R.id.filter -> viewModel.handleOptionSelection(OPTION_FILTER)
		R.id.sort -> viewModel.handleOptionSelection(OPTION_SORT)
		R.id.export_list -> viewModel.handleOptionSelection(OPTION_EXPORT)
		R.id.import_list -> viewModel.handleOptionSelection(OPTION_IMPORT)
		R.id.cloudsync -> viewModel.handleOptionSelection(OPTION_CLOUD)
		else -> super.onOptionsItemSelected(item)
	}

	override fun onQueryTextSubmit(query: String): Boolean {
		viewModel.filterNotesList(query)
		return false
	}

	override fun onQueryTextChange(newText: String): Boolean {
		viewModel.filterNotesList(newText)
		return false
	}

	override fun setupView(savedInstanceState: Bundle?) {
		super.setupView(savedInstanceState)
		setupActionBar()
		setupList()
	}

	override fun setupViewModel() {
		super.setupViewModel()

		subscriptions.add(viewModel.observeOpenNoteFormRequest()
				.subscribe { startNoteAddition() })

		subscriptions.add(viewModel.observeEditNote()
				.subscribe { startNoteEditing(it) })

		subscriptions.add(viewModel.observeShowNote()
				.subscribe { openNoteDetails(it) })

		subscriptions.add(viewModel.observeShowFilterMenu()
				.subscribe { showFilterMenu() })

		subscriptions.add(viewModel.observeShowSortMenu()
				.subscribe { showSortMenu(it) })

		subscriptions.add(viewModel.observeScrollToTopRequest()
				.subscribe { notesListView.smoothScrollToPosition(0) })

		subscriptions.add(viewModel.observeShowTopicPickerRequest()
				.subscribe { showTopicPicker() })

		subscriptions.add(viewModel.observeExportNotesRequest()
				.subscribe { exportNotes() })

		subscriptions.add(viewModel.observeImportNotesRequest()
				.subscribe { importNotes() })

		subscriptions.add(viewModel.observeCloudAuthRequest()
				.subscribe { showCloudAuthForm() })
	}

	private fun setupList() {
		notesListView.adapter = NotesListAdapter(this, mutableListOf())
		notesListView.layoutManager = object: LinearLayoutManager(this) {
			override fun requestChildRectangleOnScreen(parent: RecyclerView, child: View, rect: Rect, immediate: Boolean) = false
			override fun requestChildRectangleOnScreen(parent: RecyclerView, child: View, rect: Rect, immediate: Boolean, focusedChildVisible: Boolean) = false
		}
	}

	private fun getListAdapter() = notesListView.adapter as NotesListAdapter

	private fun showFilterMenu() {
		val popup = PopupMenu(this, findViewById(R.id.filter))
		popup.menuInflater.inflate(R.menu.notes_list_filter_menu, popup.menu)

		popup.setOnMenuItemClickListener { onFilterSelected(it.itemId) }

		popup.show()
	}

	private fun onFilterSelected(itemId: Int): Boolean {
		when (itemId) {
			R.id.all -> viewModel.handleFilterSelection(NotesListViewModel.FILTER_ALL)
			R.id.important -> viewModel.handleFilterSelection(NotesListViewModel.FILTER_IMPORTANT)
			R.id.topic -> viewModel.handleFilterSelection(NotesListViewModel.FILTER_TOPIC)
			else -> return false
		}

		return true
	}

	private fun startNoteAddition() {
		val intent = Intent(this, AddEditNoteActivity::class.java)
		startActivityForResult(intent, NotesListViewModel.INTENT_ADD_NOTE)
	}

	private fun startNoteEditing(id: Long) {
		val intent = Intent(this, AddEditNoteActivity::class.java).apply {
			putExtra(C.EXTRA_NOTE_ID, id)
		}
		startActivityForResult(intent, NotesListViewModel.INTENT_EDIT_NOTE)
	}

	private fun openNoteDetails(id: Long) {
		val intent = Intent(this, NoteDetailActivity::class.java).apply {
			putExtra(C.EXTRA_NOTE_ID, id)
		}
		startActivityForResult(intent, NotesListViewModel.INTENT_SHOW_NOTE)
	}

	private fun showSortMenu(@SortOrder currentSortOrder: String) {
		val dialog = SortDialogFragment.newInstance(currentSortOrder)

		dialog.listener = object : SortDialogFragment.Listener {
			override fun onSortSelected(@SortField field: String, @SortOrder order: String) {
				viewModel.sort(field, order)
			}
		}

		dialog.show(supportFragmentManager, "SortDialog")
	}

	private fun showTopicPicker() {
		val intent = Intent(this, TopicPickerActivity::class.java)
		startActivityForResult(intent, NotesListViewModel.INTENT_SHOW_TOPIC_PICKER)
	}

	private fun exportNotes() {
		if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) {
			Toast.makeText(this, R.string.warning_no_app_intent, Toast.LENGTH_SHORT).show()
			return
		}
		val intent = Intent(Intent.ACTION_CREATE_DOCUMENT).apply {
			addCategory(Intent.CATEGORY_OPENABLE)
			type = "*/*"
			putExtra(Intent.EXTRA_TITLE, ".txt")
		}

		val started = tryStartActivity(intent, NotesListViewModel.INTENT_EXPORT_NOTES)
		if (started.not())
			Toast.makeText(this, R.string.warning_no_app_intent, Toast.LENGTH_SHORT).show()
	}

	private fun importNotes() {
		if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) {
			Toast.makeText(this, R.string.warning_no_app_intent, Toast.LENGTH_SHORT).show()
			return
		}
		val intent = Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
			addCategory(Intent.CATEGORY_OPENABLE)
			type = "*/*"
		}

		val started = tryStartActivity(intent, NotesListViewModel.INTENT_IMPORT_NOTES)
		if (started.not())
			Toast.makeText(this, R.string.warning_no_app_intent, Toast.LENGTH_SHORT).show()
	}

	private fun showCloudAuthForm() {
		val providers = Arrays.asList(
				AuthUI.IdpConfig.EmailBuilder().build())

		val intent = AuthUI.getInstance()
				.createSignInIntentBuilder()
				.setAvailableProviders(providers)
				.build()

		startActivityForResult(intent, NotesListViewModel.INTENT_CLOUD_AUTH)
	}

	private fun onKeyboardStateChanged(opened: Boolean) {
//		switchOpenNoteFormButton(!opened)
		viewModel.onKeyboardStateChanged(opened)
	}

	private fun switchOpenNoteFormButton(show: Boolean) {
		hintFullNote.animate()
				.alpha(if(show) 1f else 0f)
				.translationY(if (show) 0f else -50f)
				.setDuration(100)
				.start()
		openNoteFormButton.animate()
				.alpha(if(show) 1f else 0f)
				.translationY(if (show) 0f else -50f)
				.setDuration(100)
				.start()
	}
}
