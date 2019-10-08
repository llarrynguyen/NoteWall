package com.larrynguyen.styledstring

import android.text.style.ClickableSpan
import android.view.View

class ClickableTextSpan(private val text: CharSequence,
						private val action: OnSpanClickListener)
	: ClickableSpan() {

	override fun onClick(widget: View) { action.onClick(text) }
}