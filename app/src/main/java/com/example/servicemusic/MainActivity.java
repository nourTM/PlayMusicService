package com.example.servicemusic;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    Button play;
    Button stop;
    ArrayList<Music> musicList;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getMusicList();
        play=(Button)findViewById(R.id.play);
        play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startService(new Intent(getApplicationContext(),
                        MusicService.class));
            }
        });

        stop=(Button)findViewById(R.id.stop);
        stop.setOnClickListener(new
            View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    stopService(new
                            Intent(getApplicationContext(),
                            MusicService.class));
                }
            });
    }

    private ArrayList getMusicList() {
        ContentResolver contentResolver = getContentResolver();
        Uri songUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;

        Cursor cursor = contentResolver.query(songUri, null, null, null, null);

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