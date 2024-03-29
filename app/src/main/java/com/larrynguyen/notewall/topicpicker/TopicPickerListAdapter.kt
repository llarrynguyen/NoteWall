package com.larrynguyen.notewall.topicpicker

import android.content.Context
import android.databinding.DataBindingUtil
import android.view.View
import android.view.ViewGroup
import com.larrynguyen.notewall.FilterableListAdapter
import com.larrynguyen.notewall.data.model.Topic
import com.larrynguyen.notewall.databinding.TopicpickerItemBinding

class TopicPickerListAdapter(context: Context)
	: FilterableListAdapter<Topic>(context), TopicPickerItemViewModel.Listener {

	override fun createFilter() = TopicFilter()

	override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
		val  binding = convertView?.let { view ->
			DataBindingUtil.getBinding<TopicpickerItemBinding>(view)
		} ?: TopicpickerItemBinding.inflate(inflater, parent, false)

		val viewModel = TopicPickerItemViewModel(getItem(position))
		binding.viewModel = viewModel

		viewModel.setListener(this)

		return binding.root
	}
	override fun onItemClicked(item: Topic) = itemClickedEvent.onNext(item)

	inner class TopicFilter: ListFilter() {
		override fun satisfy(item: Topic, candidate: String) = item.name.toLowerCase().startsWith(candidate)
	}
}