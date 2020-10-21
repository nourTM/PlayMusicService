package com.example.servicemusic;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.IBinder;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import java.io.IOException;
import java.util.ArrayList;

public class MusicService extends Service {
    private MyReceiver recv;
    private MediaPlayer mediaPlayer;
    private int currrentMusic = 0;
    private ArrayList<Music> musicList;

    private Notification notification;
    private NotificationManager notificationManager;
    String channelId = "dj_nour";
    CharSequence channelName = "DJ Nour";


    PendingIntent pendingIntent;
    PendingIntent playPausePendingIntent,nextPendingIntent,prevPendingIntent;

    public MusicService(){}

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        musicList = new ArrayList<>();
        getMusicList();

        recv = new MyReceiver();

        registerReceiver(recv,new IntentFilter("PlayPause"));
        registerReceiver(recv,new IntentFilter("Next"));
        registerReceiver(recv,new IntentFilter("Prev"));

        mediaPlayer = new MediaPlayer();
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        //ce qu’on va faire si user clique sur la notif
        Intent notificationIntent = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent =
                PendingIntent.getActivity(this, 0, notificationIntent, 0);
        // ce qu’on va faire si user clique sur l'une des boutons de la notif on envoyant des intents
        playPausePendingIntent =
                PendingIntent.getBroadcast(this, 0, new Intent("PlayPause"),
                        PendingIntent.FLAG_UPDATE_CURRENT);
        nextPendingIntent =
                PendingIntent.getBroadcast(this, 0, new Intent("Next"),
                        PendingIntent.FLAG_UPDATE_CURRENT);

        prevPendingIntent =
                PendingIntent.getBroadcast(this, 0, new Intent("Prev"),
                        PendingIntent.FLAG_UPDATE_CURRENT);

        // Notification Channel
        notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        int importance = NotificationManager.IMPORTANCE_HIGH;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel notificationChannel = new
                    NotificationChannel(channelId, channelName, importance);
            notificationManager.createNotificationChannel(notificationChannel);}

        notification =
                new NotificationCompat.Builder(this,channelId)
                        .setContentTitle("Lecture en cours")
                        .setContentText(musicList.get(currrentMusic).getTitle())
                        .setSmallIcon(R.drawable.musicnote)
                        .addAction(R.drawable.prev,"Previous",prevPendingIntent)
                        .addAction(R.drawable.play, "Play/Pause", playPausePendingIntent)
                        .addAction(R.drawable.next,"Next",nextPendingIntent)
                        .setContentIntent(pendingIntent)
                        .setPriority(Notification.PRIORITY_MAX)
                        .build();
        startForeground(110, notification);
        try {
            mediaPlayer.setDataSource(musicList.get(currrentMusic).getPath());

            mediaPlayer.prepare();
            mediaPlayer.seekTo(0);
            mediaPlayer.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
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

            Log.i("length", String.valueOf(musicList.size()));
            Log.i("notif",action);
            switch (action){
                case "PlayPause" :
                    Log.i("notif",action);
                    if(mediaPlayer.isPlaying()) {
                        mediaPlayer.pause();}
                    else {
                        mediaPlayer.start();
                    }
                    break;
                case "Prev" :
                    Log.i("notif",action);
                    currrentMusic = (currrentMusic-1) % (musicList.size());
                    startMusic();
                    break;
                case "Next" :
                    Log.i("notif",action);
                    currrentMusic = (currrentMusic+1) % (musicList.size());
                    startMusic();
                    break;
            }
        }
    }

    private void startMusic() {
        try {
            notification=  new NotificationCompat.Builder(this, channelId)
                    .setContentTitle("Lecture en cours")
                    .setSmallIcon(R.drawable.musicnote)
                    .setContentText(musicList.get(currrentMusic).getTitle())
                    .addAction(R.drawable.prev, "Previous", prevPendingIntent)
                    .addAction(R.drawable.pp, "Play/Pause", playPausePendingIntent)
                    .addAction(R.drawable.next, "Next", nextPendingIntent)
                    .setContentIntent(pendingIntent)
                    .build();
            notificationManager.notify(110, notification);

            mediaPlayer.reset();
            mediaPlayer.setDataSource(musicList.get(currrentMusic).getPath());
            mediaPlayer.prepare();
            mediaPlayer.seekTo(0);
            mediaPlayer.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // retrieve all stored music
    private ArrayList getMusicList() {
        ContentResolver contentResolver = getContentResolver();
        Uri songUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;

        @SuppressLint("Recycle") Cursor cursor = contentResolver.query(songUri, null, null, null, null);

        if (cursor != null && cursor.moveToFirst()) {

            int indexTitle = cursor.getColumnIndex(MediaStore.Audio.Media.TITLE);
            int indexArtist = cursor.getColumnIndex(MediaStore.Audio.Media.ARTIST);
            int indexPath = cursor.getColumnIndex(MediaStore.Audio.Media.DATA);

            do {
                String title = cursor.getString(indexTitle);
                String artist = cursor.getString(indexArtist);
                String path = cursor.getString(indexPath);

                Music song = new Music(title, artist, path);

                musicList.add(song);

            } while (cursor.moveToNext());
        }
        return musicList;
    }
}
