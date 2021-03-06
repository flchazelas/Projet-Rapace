package com.example.projetRapace;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.MediaController;

import com.squareup.picasso.Picasso;

public class ImageViewActivity extends BaseActivity {

    private static final int MENU_QUIT = 1;

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

    @Override
    protected void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        Intent intent = getIntent();
        setContentView(R.layout.activity_image_view);
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
            intent = new Intent(ImageViewActivity.this, RapaceService.class);
            startService(intent);

            final String ip = intent.getStringExtra("ip");
            if (ip != null) {
                final android.widget.ImageView imageView;
                imageView = (android.widget.ImageView) findViewById(R.id.imageView);
                Picasso.get().load(ip).into(imageView);
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        SessionManager.getInstance(this).deconnexionSession();
    }
}
