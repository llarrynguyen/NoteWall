package com.larrynguyen.notewall.utils

import android.app.*
import android.content.Context
import android.content.Intent
import android.os.Build
import android.support.annotation.RequiresApi
import android.support.v4.app.NotificationCompat
import android.support.v4.content.ContextCompat
import android.text.Html
import android.text.Spanned
import android.util.Log
import com.larrynguyen.notewall.R
import com.larrynguyen.notewall.data.model.Note
import com.larrynguyen.notewall.notice.NoticeActionReceiver
import com.larrynguyen.notewall.notice.AlarmReceiver
import java.text.DateFormat

val Note.noticeId get() = id.toInt()

fun Note.getText(): String? {
	return when {
		title.isNotEmpty() -> title
		body.isNotEmpty() -> plainBody
		else -> {
			Log.e("Note", "Error! Note $id have nither title nor body")
			null
		}
	}
}

fun Note.getListTitle(context: Context): String = getText() ?: context.getString(R.string.note_num, id)
fun Note.getTitle(context: Context): String = if (title.isNotEmpty()) title else context.getString(R.string.note_num, id)
fun Note.spannedBody(): Spanned = Html.fromHtml(body)

fun Note.tryScheduleAlarm(context: Context): Boolean {
	return if (isNeedAlarm()) {
		scheduleAlarm(context)
		true
	} else false
}

fun Note.isNeedAlarm() = timeNotify > System.currentTimeMillis()

fun Note.scheduleAlarm(context: Context) {
	val intent = Intent(context, AlarmReceiver::class.java)
	intent.putExtra(AlarmReceiver.EXTRA_NOTE_ID, id)

	val operation = PendingIntent.getBroadcast(context, noticeId, intent, PendingIntent.FLAG_UPDATE_CURRENT)
	context.scheduleAlarm(timeNotify, operation)

	context.createNotification(noticeId, createNotice(context))
}

private fun Note.createNotice(context: Context): Notification {
	return createNotificationBuilder(context)
			.setSmallIcon(R.drawable.ic_statusbar)
			.setTicker(getText())
			.setContentTitle(getTitle(context))
			.setContentText(getDateNotify(DateFormat.MEDIUM, DateFormat.SHORT))
			.setContentInfo(context.getString(R.string.app_name))
			.setPriority(NotificationManager.IMPORTANCE_HIGH)
			.setColor(ContextCompat.getColor(context, R.color.colorPrimary))
			.addAction(R.drawable.ic_cancel, context.getString(R.string.cancel), createCancelPendingIntent(context))
			.setOngoing(true)
			.build()
}

private fun createNotificationBuilder(context: Context): NotificationCompat.Builder {
	if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
		acquireNotificationChannel(context, AlarmReceiver.DEFAULT_CHANNEL_ID)
	return NotificationCompat.Builder(context, AlarmReceiver.DEFAULT_CHANNEL_ID)
}

@RequiresApi(Build.VERSION_CODES.O)
private fun acquireNotificationChannel(context: Context, id: String): NotificationChannel {
	val noticeManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
	return noticeManager.getNotificationChannel(id)
			?: noticeManager.createChannel(id, context.getString(R.string.notes_channel_name), NotificationManager.IMPORTANCE_HIGH)
}

private fun Note.createCancelPendingIntent(context: Context): PendingIntent {
	val intent = Intent(context, NoticeActionReceiver::class.java)
	intent.action = NoticeActionReceiver.ACTION_CANCEL_NOTICE
	intent.putExtra(NoticeActionReceiver.EXTRA_NOTE_ID, noticeId)
	return PendingIntent.getBroadcast(context, noticeId, intent, PendingIntent.FLAG_UPDATE_CURRENT)
}