package com.larrynguyen.notewall.data.source

class EmptyResultException(message: String = defaultMessage): Throwable(message) {
	companion object {
		private const val defaultMessage = "The result of the request is empty"
	}
}