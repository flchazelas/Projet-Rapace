package com.example.projetRapace;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.example.projetRapace.Camera.Camera;

import java.util.ArrayList;
import java.util.List;

public class MainCardViewCamera extends AppCompatActivity {

    private RecyclerView recyclerView;

    private List<Object> cameras = new ArrayList<Object>();
    private SessionManager session;
    private Button buttonAjout;
    private Intent intent;
    private Adapter adapter;

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

    /**
     * OnCreate(), lancement de l'activité
     * */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_recycler);

        buttonAjout = (Button)findViewById(R.id.buttonAdd);

        // Lancement du Service de vérification de connexion
        intent = new Intent(MainCardViewCamera.this, RapaceService.class);
        startService(intent);

        // Lancement du Session Manager pour stocker l'utilisateur
        session = new SessionManager(getApplicationContext());
        session.checkLogin();

        //Ajout d'une Caméra fictive
        ajouterCameras();

        //Récupération du RecyclerView qui s'occupe du lancement des CardViews
        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);

        //définit l'agencement des cellules, ici de façon verticale, comme une ListView
        //recyclerView.setLayoutManager(new LinearLayoutManager(this));

        //pour adapter en grille comme un RecyclerView, avec 2 cellules par ligne
        recyclerView.setLayoutManager(new GridLayoutManager(this,2));

        //Création d'un Adapter, lui fournir notre liste de Caméras.
        //Adapter servira à remplir notre Recyclerview
        adapter = new Adapter(cameras);
        recyclerView.setAdapter(adapter);

        //Méthode onClick() du bouton d'ajout d'une Caméra
        buttonAjout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Check si connecté et relance le service
                //session.checkLogin();
                //startService(intent);

                //Ajout d'une Caméra fictive dans le RecylcerView
                cameras.add(new Camera("Camera "+cameras.size(),"https://telesurveillance.securitas.fr/medium/W1siZiIsIjIwMTgvMDgvMjkvNDVhcmFnbTExa19jYW1lcmFfdmlkZW9zdXJ2ZWlsbGFuY2VfOTYwLmpwZyJdLFsicCIsInRodW1iIiwiOTYweDY0MD4iXV0/camera-videosurveillance-960.jpg?sha=ffb4cb9a3b8ac9ea"));

                //RecyclerView transmet la liste de Caméras à l'adaptateur
                recyclerView.setAdapter(adapter);
            }
        });
    }

    /**
     * Méthode d'ajout d'une Caméra dans la liste cameras
     * */
    private void ajouterCameras() {
        cameras.add(new Camera("Camera "+cameras.size(),"https://telesurveillance.securitas.fr/medium/W1siZiIsIjIwMTgvMDgvMjkvNDVhcmFnbTExa19jYW1lcmFfdmlkZW9zdXJ2ZWlsbGFuY2VfOTYwLmpwZyJdLFsicCIsInRodW1iIiwiOTYweDY0MD4iXV0/camera-videosurveillance-960.jpg?sha=ffb4cb9a3b8ac9ea"));
        cameras.add(new Camera("Camera "+cameras.size(),"https://www.cnil.fr/sites/default/files/styles/contenu-generique-visuel/public/thumbnails/image/video-commerces-828584278.jpg?itok=SsIbhMcB"));
    }

    /**
     * onDestroy(), gère la destruction de l'activité
     * Déconnecte l'utilisateur si l'activité est détruite via la session
     * */
    @Override
    protected void onDestroy() {
        super.onDestroy();
        session.deconnexionSession();
    }
}
