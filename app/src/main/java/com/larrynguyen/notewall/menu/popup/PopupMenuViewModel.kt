package com.larrynguyen.notewall.menu.popup

import android.content.Context
import com.larrynguyen.notewall.BasicListViewModel
import com.larrynguyen.notewall.menu.MenuAdapter
import com.larrynguyen.notewall.menu.MenuItem

class PopupMenuViewModel(context: Context): BasicListViewModel<MenuItem>(context) {
	override fun createListAdapter(context: Context) = MenuAdapter(context)
}