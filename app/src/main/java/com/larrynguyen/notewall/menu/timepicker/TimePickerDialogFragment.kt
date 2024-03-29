package com.larrynguyen.notewall.menu.timepicker

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.larrynguyen.notewall.BasicDialogFragment
import com.larrynguyen.notewall.R
import com.larrynguyen.notewall.databinding.TimePickerDialogBinding

class TimePickerDialogFragment: BasicDialogFragment<TimePickerDialogViewModel>() {

	companion object {
	}

	override fun getFragmentTag() = "timePicker"

	override fun createView(inflater: LayoutInflater, container: ViewGroup?): View =
		inflater.inflate(R.layout.time_picker_dialog, container)

	override fun createViewModel(context: Context): TimePickerDialogViewModel {
		return TimePickerDialogViewModel(context)
	}

	override fun bind(view: View, viewModel: TimePickerDialogViewModel) {
		val binding = TimePickerDialogBinding.bind(view)
		binding.viewModel = viewModel
	}
}