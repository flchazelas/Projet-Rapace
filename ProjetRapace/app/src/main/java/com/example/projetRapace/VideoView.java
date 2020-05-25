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

public class VideoView extends BaseActivity {
    private MjpegView mv;
    private static final int MENU_QUIT = 1;
    private SessionManager session;

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
                session.deconnexionSession();
                finish();
                return true;
        }
        return false;
    }

    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        Intent intent = getIntent();
        setContentView(R.layout.activity_video_view);
        final Activity context = this;
        ((Button) findViewById(R.id.boutonRetour)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, VueCamera.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }
        });

        if(intent != null) {
            // Lancement du Service de vérification de connexion
            intent = new Intent(VideoView.this, RapaceService.class);
            startService(intent);

            // Lancement du Session Manager pour stocker l'utilisateur
            session = new SessionManager(getApplicationContext());
            session.checkLogin();

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

    /*
    @Override
    protected void onDestroy() {
        super.onDestroy();
        session.deconnexionSession();
    }*/
}
