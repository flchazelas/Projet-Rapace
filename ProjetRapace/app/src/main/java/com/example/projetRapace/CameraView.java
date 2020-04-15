package com.example.projetRapace;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;

import com.example.projetRapace.streamlib.MjpegView;
import com.example.projetRapace.streamlib.VideoLoaderAsync;

public class CameraView extends AppCompatActivity {
    private MjpegView mv;
    private static final int MENU_QUIT = 1;

    private boolean isPlaying;
    private boolean isRecording;

    /* Creates the menu items */
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.add(0, MENU_QUIT, 0, "Quit");
        return true;
    }

    /* Handles item selections */
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case MENU_QUIT:
                finish();
                return true;
        }
        return false;
    }

    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        Intent intent = getIntent();

        if(intent != null) {
            String URL = intent.getStringExtra("ip");

            requestWindowFeature(Window.FEATURE_NO_TITLE);
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN, WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);
            mv = new MjpegView(this);
            mv.setZOrderOnTop(false);
            setContentView(R.layout.activity_camera_view);

            RelativeLayout rl = (RelativeLayout) findViewById(R.id.ControlsHolder);
            ((FrameLayout) findViewById(R.id.FrameHolder)).removeAllViews();
            ((FrameLayout) findViewById(R.id.FrameHolder)).addView(mv);
            ((FrameLayout) findViewById(R.id.FrameHolder)).addView(rl);

            ((Button) findViewById(R.id.screenshotControl)).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mv.screenshot("test");
                }
            });

            ((Button) findViewById(R.id.recordControl)).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (!mv.isRecording()) {
                        mv.startRecord("test");
                        ((Button) findViewById(R.id.recordControl)).setText("Stop Recording");
                    } else {
                        mv.stopRecord();
                        ((Button) findViewById(R.id.recordControl)).setText("Start Recording");
                    }
                }
            });
            ((Button) findViewById(R.id.exitControl)).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    finish();
                }
            });

            Runnable r = new VideoLoaderAsync(mv, URL);
            new Thread(r).start();
        }
    }

    public void onPause() {
        super.onPause();
        mv.stopPlayback();
    }

    public void onResume() {
        super.onResume();
        mv.startPlayback();
    }
}