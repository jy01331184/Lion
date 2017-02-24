package com.tech.android;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

/**
 * Created by tianyang on 16/9/29.
 */
public class TechService extends Service {

    private static final int ID = 17471802;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        Intent notificationIntent = new Intent(this, TechResultActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0);

        Notification myNotify = new Notification.Builder(this).setSmallIcon(android.R.drawable.sym_call_outgoing)
                .setTicker("TechManager init " )
                .setContentTitle("TechManager")
                .setContentText("Now Watching")
                .setContentIntent(pendingIntent).getNotification();

        startForeground(ID, myNotify);
        return super.onStartCommand(intent, flags, startId);
    }
}
