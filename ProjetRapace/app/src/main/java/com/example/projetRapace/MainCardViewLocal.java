package com.example.projetRapace;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.projetRapace.Local.Local;
import com.example.projetRapace.Local.LocalDBManager;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.List;

public class MainCardViewLocal extends AppCompatActivity {
    private ProgressDialog mProgressDialog;

    boolean add_local_query_done = false;
    boolean add_local_query_result = false;

    boolean fetch_locals_query_done = false;
    boolean fetch_locals_query_result = false;

    private RecyclerView recyclerView;

    private List<Object> locaux = new ArrayList<Object>();
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

        ((Button)findViewById(R.id.ajoutLocal)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(ajouterLocal())//Si tout ok on cache
                    findViewById(R.id.addLocalLayout).setVisibility(View.GONE);
            }
        });
        ((Button)findViewById(R.id.retourAjoutLocal)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                findViewById(R.id.addLocalLayout).setVisibility(View.GONE);
            }
        });
        findViewById(R.id.addLocalLayout).setVisibility(View.GONE);
        buttonAjout = (Button)findViewById(R.id.buttonAdd);

        // Lancement du Service de vérification de connexion
        intent = new Intent(MainCardViewLocal.this, RapaceService.class);
        startService(intent);
        // Lancement du Session Manager pour stocker l'utilisateur
        session = new SessionManager(getApplicationContext());
        //Vérifie si l'utilisateur est connecté
        session.checkLogin();

        //Ajout d'un local fictif
        ajouterLocaux();

        //Récupération du RecyclerView qui s'occupe du lancement des CardViews
        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);

        //définit l'agencement des cellules, ici de façon verticale, comme une ListView
        //recyclerView.setLayoutManager(new LinearLayoutManager(this));

        //pour adapter en grille comme un RecyclerView, avec 2 cellules par ligne
        recyclerView.setLayoutManager(new GridLayoutManager(this,2));

        //Création d'un Adapter, lui fournir notre liste de Locaux.
        //Adapter servira à remplir notre Recyclerview
        adapter = new Adapter(locaux);
        recyclerView.setAdapter(adapter);

        //Méthode onClick() du bouton d'ajout d'un Local
        buttonAjout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Check si connecté et relance le service
                //session.checkLogin();
                //startService(intent);

                findViewById(R.id.addLocalLayout).setVisibility(View.VISIBLE);
            }
        });
    }

    /**
     * Méthode d'ajout d'un Local dans la liste locaux
     * */
    private void ajouterLocaux() {
        locaux.clear();
        mProgressDialog = ProgressDialog.show(this, "Chargement...", " Ajout du Local ...");
        mProgressDialog.setCanceledOnTouchOutside(false); // main method that force user cannot click outside
        final Activity context = this;

        fetch_locals_query_done = false;
        fetch_locals_query_result = false;

        LocalDBManager.LocalDBCallbackInterface callback = new LocalDBManager.LocalDBCallbackInterface() {
            @Override
            public void onQueryFinished(String operation, String output) {
                Log.d("saveRecord", "(onQueryFinished) -> "+ operation);
                if(operation.equals(LocalDBManager.LOCAL_DB_GET_BY_USER)){
                    try {
                        Log.d("saveRecord", "(retour LOCAL_DB_GET_BY_USER) -> "+ output);
                        JSONArray jsonResult = new JSONArray(output);
                        Log.d("CameraListView", "(retour CAMERA_DB_GETALL) -> "+ Local.localsFromJSON(jsonResult));
                        for(Local c : Local.localsFromJSON(jsonResult)){
                            locaux.add(c);
                        }

                        fetch_locals_query_result = true;
                        fetch_locals_query_done = true;
                    } catch (Exception e) {
                        Toast.makeText(context, "Erreur lors de la récupération des locaux.\nVeuillez réessayer ou contacter un administrateur.",Toast.LENGTH_LONG).show();
                        e.printStackTrace();
                    }
                }
            }
        };
        if(session.user_id ==-1){
            Toast.makeText(context, "Session invalide.\nVeuillez réessayer ou relancer l'application.",Toast.LENGTH_LONG).show();
            return;
        }
        LocalDBManager.getByUser(callback,session.user_id);

        Thread checkLoading = new Thread(new Runnable(){
            @Override
            public void run() {
                while(!(fetch_locals_query_done))
                    Log.d("MainCardViewLocal", "(Waiting for loading to end) -> (add_local_query_done : " + fetch_locals_query_done + ")");

                context.runOnUiThread(new Runnable(){
                    @Override
                    public void run() {
                        if (mProgressDialog != null)
                            mProgressDialog.dismiss();
                        //affichage
                        if(fetch_locals_query_result)
                            adapter.notifyDataSetChanged();
                        else
                            Toast.makeText(context, "Erreur lors de la récupération des locaux.\nVeuillez réessayer ou contacter un administrateur.",Toast.LENGTH_LONG).show();
                    }
                });
            }
        });

        checkLoading.start();
    }

    /**
     * onDestroy(), gère la destruction de l'activité
     * Déconnecte l'utilisateur si l'activité est détruite via la session
     * */
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(!session.isLoggedIn())
            session.deconnexionSession();
    }

    private boolean ajouterLocal(){
        mProgressDialog = ProgressDialog.show(this, "Chargement...", " Ajout du Local ...");
        mProgressDialog.setCanceledOnTouchOutside(false); // main method that force user cannot click outside

        final Activity context = this;

        String name = ((EditText) findViewById(R.id.editTextName)).getText().toString();
        String address = ((EditText) findViewById(R.id.editTextAddress)).getText().toString();
        if (name.equals("")) {
            ((EditText) findViewById(R.id.editTextName)).setError("Veuillez choisir un nom de local.");
            return false;
        }if (address.equals("")) {
            ((EditText) findViewById(R.id.editTextAddress)).setError("Veuillez choisir une adresse.");
            return false;
        }

        add_local_query_done = false;
        add_local_query_result = false;

        LocalDBManager.LocalDBCallbackInterface callback = new LocalDBManager.LocalDBCallbackInterface() {
            @Override
            public void onQueryFinished(String operation, String output) {
                Log.d("saveRecord", "(onQueryFinished) -> "+ operation);
                if(operation.equals(LocalDBManager.LOCAL_DB_ADD)){
                    try {
                        Log.d("saveRecord", "(retour LOCAL_DB_ADD) -> "+ output);
                        if(output.equals("INSERT_SUCCESSFUL"))
                            add_local_query_result = true;
                        else
                            add_local_query_result = false;
                        add_local_query_done = true;
                    } catch (Exception e) {
                        Toast.makeText(context, "Erreur lors de l'ajout du Local.\nVeuillez réessayer ou contacter un administrateur.",Toast.LENGTH_LONG).show();
                        e.printStackTrace();
                    }
                }
            }
        };
        Local v = new Local(name,address);
        Log.d("saveRecord", "(retour USER_ID) -> "+ session.user_id);

        if(session.user_id ==-1){

            Toast.makeText(context, "Session invalide.\nVeuillez réessayer ou relancer l'application.",Toast.LENGTH_LONG).show();
            return false;
        }
        LocalDBManager.addLocal(callback,v,session.user_id);

        Thread checkLoading = new Thread(new Runnable(){
            @Override
            public void run() {
                while(!(add_local_query_done))
                    Log.d("MainCardViewLocal", "(Waiting for loading to end) -> (add_local_query_done : " + add_local_query_done + ")");

                context.runOnUiThread(new Runnable(){
                    @Override
                    public void run() {
                        if (mProgressDialog != null)
                            mProgressDialog.dismiss();
                        //affichage
                        if(add_local_query_result)
                            ajouterLocaux();
                        else
                            Toast.makeText(context, "Erreur lors de l'ajout du Local.\nVeuillez réessayer ou contacter un administrateur.",Toast.LENGTH_LONG).show();
                    }
                });
            }
        });

        checkLoading.start();

        return true;
    }
}
