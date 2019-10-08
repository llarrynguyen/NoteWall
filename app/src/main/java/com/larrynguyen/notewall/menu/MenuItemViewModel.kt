package com.larrynguyen.notewall.menu

import com.larrynguyen.notewall.BasicListItemViewModel

class MenuItemViewModel(item: MenuItem): BasicListItemViewModel<MenuItem>(item) {
	interface Listener: BasicListItemViewModel.Listener<MenuItem>
}