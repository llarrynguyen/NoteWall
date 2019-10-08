package com.larrynguyen.notewall.topicpicker

import android.content.Context
import android.view.View
import com.larrynguyen.notewall.BasicUiFragment
import com.larrynguyen.notewall.R
import com.larrynguyen.notewall.data.model.Topic
import com.larrynguyen.notewall.databinding.TopicpickerUiBinding

class TopicPickerFragment: BasicUiFragment<TopicPickerViewModel>() {

	interface Listener {
		fun onTopicSelected(topic: Topic)
	}

	private var listener: Listener? = null

	override fun getLayoutResId() = R.layout.topicpicker_ui

	override fun createViewModel(context: Context) = TopicPickerViewModel(context)

	override fun initViewModel(viewModel: TopicPickerViewModel) {
		viewModel.observeItemClicked()
				.take(1)
				.subscribe { listener?.onTopicSelected(it) }
	}

	override fun bind(view: View, viewModel: TopicPickerViewModel) {
		val binding = TopicpickerUiBinding.bind(view)
		binding.viewModel = viewModel
	}

	fun setListener(listener: Listener) { this.listener = listener }
}