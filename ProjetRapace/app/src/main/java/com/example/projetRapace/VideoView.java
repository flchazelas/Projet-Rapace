package com.example.projetRapace;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.MediaController;
import android.widget.RelativeLayout;

import com.example.projetRapace.streamlib.MjpegView;
import com.example.projetRapace.streamlib.VideoLoaderAsync;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class VideoView extends AppCompatActivity {
    private MjpegView mv;
    private static final int MENU_QUIT = 1;

    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        Intent intent = getIntent();
        setContentView(R.layout.activity_video_view);

        ((Button) findViewById(R.id.boutonRetour)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        if(intent != null) {
            final String ip = intent.getStringExtra("ip");
            if (ip != null) {

                final android.widget.VideoView videoView;
                videoView = (android.widget.VideoView) findViewById(R.id.videoView);
                videoView.setVideoPath(ip);
                MediaController mediaController = new
                        MediaController(this);
                mediaController.setAnchorView(videoView);
                videoView.setMediaController(mediaController);
                videoView.start();
            }
        }
    }
}
