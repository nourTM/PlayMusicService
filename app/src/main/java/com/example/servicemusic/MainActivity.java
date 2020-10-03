package com.example.servicemusic;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {
    Button play;
    Button stop;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
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

}