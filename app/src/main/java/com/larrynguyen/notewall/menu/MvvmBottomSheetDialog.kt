package com.larrynguyen.notewall.menu

import android.content.Context
import android.os.Bundle
import android.view.View

abstract class MvvmBottomSheetDialog<VM> : BasicBottomSheetDialog() {

	abstract fun createViewModel(context: Context): VM

	abstract fun bind(view: View, viewModel: VM)

	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		super.onViewCreated(view, savedInstanceState)

		val viewModel = createViewModel(view.context)

		bind(view, viewModel)
	}
}