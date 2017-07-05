package com.gawk.voicenotes.adapters;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.BitmapFactory;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.gawk.voicenotes.NoteView;
import com.gawk.voicenotes.R;
import com.gawk.voicenotes.models.Note;

import java.io.IOException;

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
        int state = Notification.DEFAULT_LIGHTS;
        NotificationCompat.Builder nb = new NotificationCompat.Builder(context)
                .setSmallIcon(R.drawable.icon175x175)
                .setLargeIcon(BitmapFactory.decodeResource(res, R.drawable.icon175x175_big))
                .setAutoCancel(true) //уведомление закроется по клику на него
                .setTicker(context.getString(R.string.new_note_notification)) //текст, который отобразится вверху статус-бара при создании уведомления
                .setContentText(text) // Основной текст уведомления
                .setContentIntent(PendingIntent.getActivity(context, idNotification, intentTL, PendingIntent.FLAG_UPDATE_CURRENT))
                .setWhen(System.currentTimeMillis()) //отображаемое время уведомления
                .setContentTitle(context.getString(R.string.new_note_notification)); //заголовок уведомления
        nb.setDefaults(state); // звук, вибро и диодный индикатор выставляются по умолчанию
        String sound_link = intent.getStringExtra("sound_link");
        if(!sound_link.equalsIgnoreCase("")) {
            nb.setSound(null);
            state |= Notification.DEFAULT_SOUND;
            Uri defaultRingtoneUri = Uri.parse(sound_link);
            MediaPlayer mediaPlayer = new MediaPlayer();
            try {
                mediaPlayer.setDataSource(context, defaultRingtoneUri);
                mediaPlayer.setAudioStreamType(AudioManager.STREAM_NOTIFICATION);
                mediaPlayer.prepare();
                mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {

                    @Override
                    public void onCompletion(MediaPlayer mp)
                    {
                        mp.release();
                    }
                });
                mediaPlayer.start();
            } catch (IllegalArgumentException | SecurityException | IllegalStateException | IOException e) {
                e.printStackTrace();
            }
            Log.e("GAWK_ERR","uri.toString() = " + Uri.parse(sound_link).getPath());
            Log.e("GAWK_ERR","RingtoneManager.getDefaultUri = " + RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION));
        }
        if (voice && sound_link.equalsIgnoreCase("")) {
            state |= Notification.DEFAULT_SOUND;
        }
        if (vibration) {
            state |= Notification.DEFAULT_VIBRATE;
        }
        nm.notify(NOTIFY_TAG, idNotification, nb.build());
    }


}

