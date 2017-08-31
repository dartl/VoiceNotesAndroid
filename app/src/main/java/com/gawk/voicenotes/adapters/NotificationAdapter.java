package com.gawk.voicenotes.adapters;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;

import com.gawk.voicenotes.models.Note;
import com.gawk.voicenotes.models.Notification;
import com.gawk.voicenotes.preferences.PrefUtil;

/**
 * Created by GAWK on 31.08.2017.
 */

public class NotificationAdapter {
    private Context mContext;

    public NotificationAdapter(Context mContext) {
        this.mContext = mContext;
    }

    public void restartNotify(Note note, Notification notification) {
        Log.e("GAWK_ERR","restartNotify()");
        AlarmManager am = (AlarmManager) mContext.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(mContext, TimeNotification.class);
        intent.addFlags(Intent.FLAG_INCLUDE_STOPPED_PACKAGES);

        Bundle c = new Bundle();
        c.putByteArray("note", ParcelableUtil.marshall(note));
        c.putByteArray("notification", ParcelableUtil.marshall(notification));
        intent.putExtras(c);
        int requestCodeIntent =  (int) notification.getId();
        PendingIntent pendingIntent = PendingIntent.getBroadcast(mContext, -requestCodeIntent,
                intent, 0);
        // На случай, если мы ранее запускали активити, а потом поменяли время,
        // откажемся от уведомления
        am.cancel(pendingIntent);
        if(Build.VERSION.SDK_INT < 23){
            if(Build.VERSION.SDK_INT >= 19){
                Log.e("GAWK_ERR","Build.VERSION.SDK_INT >= 19");
                am.setExact(AlarmManager.RTC_WAKEUP,  notification.getDate().getTime() , pendingIntent);
            }
            else{
                Log.e("GAWK_ERR","NO Build.VERSION.SDK_INT >= 19");
                am.set(AlarmManager.RTC_WAKEUP,  notification.getDate().getTime() , pendingIntent);
            }
        }
        else{
            Log.e("GAWK_ERR","NO Build.VERSION.SDK_INT < 23");
            am.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP,  notification.getDate().getTime() , pendingIntent);
        }
    }

    public void removeNotify(long id) {
        AlarmManager am = (AlarmManager) mContext.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(mContext, TimeNotification.class);
        int requestCodeIntent = (int) id;
        PendingIntent pendingIntent = PendingIntent.getBroadcast(mContext, -requestCodeIntent,
                intent, 0);
        am.cancel(pendingIntent);
    }
}
