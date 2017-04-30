package com.gawk.voicenotes.adapters;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;

import com.gawk.voicenotes.NoteView;
import com.gawk.voicenotes.R;
import com.gawk.voicenotes.models.Note;

/**
 * Created by GAWK on 23.02.2017.
 */

public class TimeNotification extends BroadcastReceiver {
    private static final int NOTIFY_CODE = 101;
    public static final String NOTIFY_TAG = "VOICE_NOTE_GAWK" ;

    NotificationManager nm;
    @Override
    public void onReceive(Context context, Intent intent) {
        nm = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        //Интент для активити, которую мы хотим запускать при нажатии на уведомление
        Intent intentTL = new Intent(context, NoteView.class);
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
        NotificationCompat.Builder nb = new NotificationCompat.Builder(context)
                .setSmallIcon(R.drawable.icon175x175)
                .setLargeIcon(BitmapFactory.decodeResource(res, R.drawable.icon175x175_big))
                .setAutoCancel(true) //уведомление закроется по клику на него
                .setTicker(context.getString(R.string.notification_title)) //текст, который отобразится вверху статус-бара при создании уведомления
                .setContentText(text) // Основной текст уведомления
                .setContentIntent(PendingIntent.getActivity(context, idNotification, intentTL, PendingIntent.FLAG_UPDATE_CURRENT))
                .setWhen(System.currentTimeMillis()) //отображаемое время уведомления
                .setContentTitle(context.getString(R.string.notification_title)) //заголовок уведомления
                .setDefaults(Notification.DEFAULT_LIGHTS); // звук, вибро и диодный индикатор выставляются по умолчанию
        if (voice && vibration) {
            nb.setDefaults(Notification.DEFAULT_ALL); // звук, вибро и диодный индикатор выставляются по умолчанию
        } else if (voice) {
            nb.setDefaults(Notification.DEFAULT_SOUND); // звук, вибро и диодный индикатор выставляются по умолчанию
        } else if (vibration) {
            nb.setDefaults(Notification.DEFAULT_VIBRATE); // звук, вибро и диодный индикатор выставляются по умолчанию
        }
        nm.notify(NOTIFY_TAG, idNotification, nb.build());
    }
}

