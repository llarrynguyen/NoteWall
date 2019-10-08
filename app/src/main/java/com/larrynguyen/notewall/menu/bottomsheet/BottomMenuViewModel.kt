package com.larrynguyen.notewall.menu.bottomsheet

import android.content.Context
import com.larrynguyen.notewall.BasicListViewModel
import com.larrynguyen.notewall.menu.MenuAdapter
import com.larrynguyen.notewall.menu.MenuItem

class BottomMenuViewModel(context: Context): BasicListViewModel<MenuItem>(context) {
	override fun createListAdapter(context: Context) = MenuAdapter(context)
}