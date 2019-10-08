package com.larrynguyen.notewall.menu.colorpicker

import com.larrynguyen.notewall.BasicListItemViewModel

class ColorPickerItemViewModel(color: Int): BasicListItemViewModel<Int>(color) {
	interface Listener: BasicListItemViewModel.Listener<Int>
}