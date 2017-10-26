package com.gawk.voicenotes.adapters;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.BitmapFactory;
import android.media.AudioManager;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.gawk.voicenotes.activities.ViewNoteActivity;
import com.gawk.voicenotes.R;
import com.gawk.voicenotes.models.Note;
import com.gawk.voicenotes.models.Statistics;
import com.gawk.voicenotes.adapters.preferences.PrefUtil;

import java.util.Date;

/**
 * Created by GAWK on 23.02.2017.
 */

public class TimeNotification extends BroadcastReceiver {
    public static final String NOTIFY_TAG = "VOICE_NOTE_GAWK" ;

    private NotificationAdapter mNotificationAdapter;
    private PrefUtil mPrefUtil;
    NotificationManager nm;
    @Override
    public void onReceive(Context context, Intent intent) {
        Log.e("GAWK_ERR","OnReceive()");
        nm = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationAdapter = new NotificationAdapter(context);
        mPrefUtil = new PrefUtil(context);
        //Интент для активити, которую мы хотим запускать при нажатии на уведомление
        Intent intentTL = new Intent(context, ViewNoteActivity.class);
        Note note = ParcelableUtil.unmarshall(intent.getByteArrayExtra("note"), Note.CREATOR);
        com.gawk.voicenotes.models.Notification notification =
                ParcelableUtil.unmarshall(intent.getByteArrayExtra("notification"),
                        com.gawk.voicenotes.models.Notification.CREATOR);
        boolean voice = notification.isSound();
        boolean vibration = notification.isShake();
        int idNotification = (int) notification.getId();
        String text = note.getText_note();
        if (note.getText_note().length() > 100) {
            text = text.substring(0,100)+"...";
        }
        Resources res = context.getResources();
        intentTL.putExtra("id", note.getId());
        intentTL.putExtra("id_notification", notification.getId());
        NotificationCompat.Builder nb = new NotificationCompat.Builder(context)
                .setSmallIcon(R.drawable.icon175x175)
                .setLargeIcon(BitmapFactory.decodeResource(res, R.drawable.icon_notification))
                .setAutoCancel(true) //уведомление закроется по клику на него
                .setTicker(context.getString(R.string.new_note_notification)) //текст, который отобразится вверху статус-бара при создании уведомления
                .setContentText(text) // Основной текст уведомления
                .setWhen(System.currentTimeMillis()) //отображаемое время уведомления
                .setContentIntent(PendingIntent.getActivity(context, idNotification, intentTL, PendingIntent.FLAG_UPDATE_CURRENT))
                .setPriority(NotificationCompat.PRIORITY_MAX)
                .setCategory(NotificationCompat.CATEGORY_REMINDER)
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                .setContentTitle(context.getString(R.string.new_note_notification)); //заголовок уведомления
        String sound_link = mPrefUtil.getString(PrefUtil.NOTIFICATION_SOUND,"a");
        long long_interval = mPrefUtil.getLong(PrefUtil.NOTIFICATION_INTERVAL,0);
        if (vibration) {
            nb.setVibrate(new long[]{0, 800, 500, 800});
        }
        if(!sound_link.equalsIgnoreCase("")) {
            Uri defaultRingtoneUri = Uri.parse(sound_link);
            nb.setSound(defaultRingtoneUri,AudioManager.STREAM_RING);
        } else if (voice){
            nb.setDefaults(Notification.DEFAULT_SOUND); // выставляет звук по умолчанию
        }
        nm.notify(NOTIFY_TAG, idNotification, nb.build());
        Statistics mStatistics = new Statistics(context);
        mStatistics.addPointGetNotifications();

        if (long_interval != 0 && notification.isRepeat()) {
            Date date = new Date(notification.getDate().getTime() + long_interval);
            notification.setDate(date);
            mNotificationAdapter.restartNotify(note,notification);
        }
    }
}

