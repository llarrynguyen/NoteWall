package com.larrynguyen.styledstring

import android.text.SpannableStringBuilder

fun SpannableStringBuilder.toStyledString() = StyledString(toString())