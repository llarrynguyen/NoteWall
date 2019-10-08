package com.larrynguyen.notewall.topicpicker

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.app.AppCompatActivity
import com.larrynguyen.notewall.C
import com.larrynguyen.notewall.R
import com.larrynguyen.notewall.data.model.Topic

class TopicPickerActivity : AppCompatActivity(), TopicPickerFragment.Listener {

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)

		setContentView(R.layout.activity_topicpicker)

		initUi()
	}

	override fun onTopicSelected(topic: Topic) {
		val intent = Intent().apply {
			putExtra(C.EXTRA_TOPIC, topic)
		}
		setResult(Activity.RESULT_OK, intent)
		finish()
	}

	private fun initUi() {
		val fragment = supportFragmentManager.findFragmentById(R.id.content)
				?: TopicPickerFragment()
		if (fragment is TopicPickerFragment)
			fragment.setListener(this)
		addFragment(fragment, R.id.content)
	}

	private fun addFragment(fragment: Fragment, id: Int) {
		supportFragmentManager.beginTransaction()
				.add(id, fragment)
				.commitNow()
	}
}