package com.larrynguyen.notewall.menu.colorpicker

import android.content.Context
import com.larrynguyen.notewall.BasicListViewModel

class ColorPickerDialogViewModel(context: Context): BasicListViewModel<Int>(context) {
	override fun createListAdapter(context: Context) = ColorPickerAdapter(context)
}