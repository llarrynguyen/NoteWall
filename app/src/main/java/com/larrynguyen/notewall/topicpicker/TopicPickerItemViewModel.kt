package com.larrynguyen.notewall.topicpicker

import com.larrynguyen.notewall.BasicListItemViewModel
import com.larrynguyen.notewall.data.model.Topic

class TopicPickerItemViewModel(item: Topic): BasicListItemViewModel<Topic>(item) {
	interface Listener: BasicListItemViewModel.Listener<Topic>
}