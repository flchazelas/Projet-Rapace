package com.example.projetRapace;

import android.Manifest;
import android.app.Activity;
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
import android.widget.RelativeLayout;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.projetRapace.streamlib.MjpegView;
import com.example.projetRapace.streamlib.VideoLoaderAsync;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class CameraAlerteView extends BaseActivity {
    private MjpegView mv;
    private static final int MENU_QUIT = 1;
    private Intent intentSession;

    /**
     * Création d'un menu d'Items dans la Barre du Haut de l'application
     * Ajout de l'option de déconnexion
     * */
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.add(0, MENU_QUIT, 0, R.string.logout);
        return true;
    }

    /**
     * Gère le menu d'Items
     * Pour l'appuie de Déconnexion appel finish() qui ferme l'activité courante
     * */
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case MENU_QUIT:

                //ferme l'activité courante
                finish();
                return true;
        }
        return false;
    }

    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        Intent intent = getIntent();
        if(intent != null) {

            // Lancement du Service de vérification de connexion
            intentSession = new Intent(CameraAlerteView.this, RapaceService.class);
            startService(intentSession);

            final int id = intent.getIntExtra("id", -1);
            final String ip = intent.getStringExtra("ip");
            if (id != -1) {
                requestWindowFeature(Window.FEATURE_NO_TITLE);
                getWindow().setFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN, WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);
                mv = new MjpegView(this);
                mv.setZOrderOnTop(false);
                setContentView(R.layout.activity_camera_alerte_view);

                RelativeLayout rl = (RelativeLayout) findViewById(R.id.ControlsHolder);
                ((FrameLayout) findViewById(R.id.FrameHolder)).removeAllViews();
                ((FrameLayout) findViewById(R.id.FrameHolder)).addView(mv);
                ((FrameLayout) findViewById(R.id.FrameHolder)).addView(rl);

                final Activity context = this;

                ((Button) findViewById(R.id.screenshotControl)).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (ContextCompat.checkSelfPermission(context, Manifest.permission.READ_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED) {
                            ActivityCompat.requestPermissions(context, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
                        }
                        if (ContextCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED) {
                            ActivityCompat.requestPermissions(context, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
                        }

                        //Création du nom selon la date et l'heure
                        Calendar ca = Calendar.getInstance();
                        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd_HH:mm:ss.SSS");
                        String filename = df.format(ca.getTime());

                        mv.screenshot(id,filename);
                    }
                });

                ((Button) findViewById(R.id.recordControl)).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (!mv.isRecording()) {
                            //Création du nom selon la date et l'heure
                            Calendar ca = Calendar.getInstance();
                            SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd_HH:mm:ss.SSS");
                            String filename = df.format(ca.getTime());

                            mv.startRecord(filename);
                            ((Button) findViewById(R.id.recordControl)).setText("Stop Recording");
                        } else {
                            if (ContextCompat.checkSelfPermission(context, Manifest.permission.READ_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED) {
                                ActivityCompat.requestPermissions(context, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
                            }
                            if (ContextCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED) {
                                ActivityCompat.requestPermissions(context, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
                            }
                            mv.stopRecord(id);
                            ((Button) findViewById(R.id.recordControl)).setText("Start Recording");
                        }
                    }
                });
                ((Button) findViewById(R.id.exitControl)).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        mv.stopPlayback();
                        //retour activité précédente
                        finish();
                    }
                });

                Runnable r = new VideoLoaderAsync(mv, ip);
                new Thread(r).start();

                mv.startPlayback();
            }
        }
    }

    public void onPause() {
        super.onPause();
        if(mv != null)
            mv.stopPlayback();
    }

    public void onResume() {
        super.onResume();
        if(mv != null)
            mv.startPlayback();
    }
}