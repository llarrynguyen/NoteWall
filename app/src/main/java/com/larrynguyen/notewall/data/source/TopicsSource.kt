package com.larrynguyen.notewall.data.source

import com.larrynguyen.notewall.data.model.Topic

interface TopicsSource {

	fun getAllTopics(callback: DataSource.RequestCallback<List<Topic>>)

	fun getTopic(name: String, callback: DataSource.RequestCallback<Topic>)

	fun insertTopic(topic: Topic)

	fun insertTopics(topics: List<Topic>)

	fun reset(newTopics: List<Topic>)

	fun deleteTopic(name: String)

	fun deleteAllTopics()
}