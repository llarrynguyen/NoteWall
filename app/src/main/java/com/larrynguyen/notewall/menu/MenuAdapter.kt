package com.larrynguyen.notewall.menu

import android.content.Context
import android.databinding.DataBindingUtil
import android.view.View
import android.view.ViewGroup
import com.larrynguyen.notewall.BasicListAdapter
import com.larrynguyen.notewall.databinding.MenuItemBinding

class MenuAdapter(context: Context)
	: BasicListAdapter<MenuItem>(context), MenuItemViewModel.Listener {

	override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
		val  binding = convertView?.let { view ->
			DataBindingUtil.getBinding<MenuItemBinding>(view)
		} ?: MenuItemBinding.inflate(inflater, parent, false)

		val viewModel = MenuItemViewModel(getItem(position))
		binding.viewModel = viewModel

		viewModel.setListener(this)

		return binding.root
	}

	override fun onItemClicked(item: MenuItem) = itemClickedEvent.onNext(item)
}