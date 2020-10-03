package com.example.servicemusic;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.IBinder;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

public class MusicService extends Service {
    private MyReceiver recv;
    private MediaPlayer mediaPlayer;

    public MusicService(){}

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        recv = new MyReceiver();
        registerReceiver(recv, new IntentFilter("PlayPause"));
        mediaPlayer = MediaPlayer.create(this, R.raw.song);
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        //ce qu’on va faire si user clique sur la notif
        Intent notificationIntent = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent =
                PendingIntent.getActivity(this, 0, notificationIntent, 0);
        // ce qu’on va faire si user clique sur le bouton de la notif
        PendingIntent pPPendingIntent =
                PendingIntent.getBroadcast(this, 0, new Intent("PlayPause"),
                        PendingIntent.FLAG_UPDATE_CURRENT);

        // Notification Channel
        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        String channelId = "my_channel_id";
        CharSequence channelName = "My Channel";
        int importance = NotificationManager.IMPORTANCE_HIGH;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel notificationChannel = new
                    NotificationChannel(channelId, channelName, importance);
            notificationManager.createNotificationChannel(notificationChannel);}

        Notification notification =
                new NotificationCompat.Builder(this,channelId)
                        .setContentTitle("Lecture en cours")
                        .setContentText("Song")
                        .setSmallIcon(R.drawable.musicnote)
                        .addAction(R.drawable.play, "Play/Pause", pPPendingIntent)
                        .setContentIntent(pendingIntent)
                        .setPriority(Notification.PRIORITY_MAX)
                        .build();
        startForeground(110, notification);
        mediaPlayer.start();
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if(mediaPlayer.isPlaying()) mediaPlayer.stop();
        unregisterReceiver(recv);
    }

    class MyReceiver extends BroadcastReceiver{
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equals("PlayPause")) {
                if(mediaPlayer.isPlaying()) {mediaPlayer.pause();}
                else {mediaPlayer.start();}
            }
        }
    }
}
