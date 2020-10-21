package com.example.servicemusic;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    final static int REQUEST_PERMISSION = 99 ;

    Button play;
    Button stop;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // ensure reading permission for all android users versions
        if(ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},REQUEST_PERMISSION);
        }



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

    /*@Override
    protected void onResume() {
        super.onResume();
        for (int i = 0; i < musicList.size();i++) {
            Toast.makeText(getApplicationContext(),musicList.get(i).getTitle(),Toast.LENGTH_SHORT).show();
        }
    }*/

}