package com.example.projetRapace;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.app.NotificationChannel;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.example.projetRapace.Local.Local;
import com.example.projetRapace.Local.LocalDBManager;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.List;

public class MainCardViewLocal extends BaseActivity {
    boolean shouldExecuteOnResume;
    private ProgressDialog mProgressDialog;

    boolean add_local_query_done = false;
    boolean add_local_query_result = false;

    boolean fetch_locals_query_done = false;
    boolean fetch_locals_query_result = false;

    private RecyclerView recyclerView;

    private List<Object> locaux = new ArrayList<Object>();
    private ImageButton buttonAjout;
    private Intent intent;
    private Intent intentSession;
    private AdapterCardView adapterCardView;

    private static final int MENU_QUIT = 1;
    private static final int MENU_ADMIN = 2;
    private static final int MENU_PROFIL = 3;

    private static final int CODE_ACTIVITY = 1;

    /**
     * Création d'un menu d'Items dans la Barre du Haut de l'application
     * Ajout de l'option de déconnexion
     * */
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.add(0, MENU_QUIT, 0, R.string.logout);
        if(SessionManager.getInstance(this).isAdmin()) {
            menu.add(0, MENU_ADMIN, 0, R.string.user_administration);
        }
        menu.add(0, MENU_PROFIL, 0, R.string.user_profile);
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
                SessionManager.getInstance(this).deconnexionSession();
                finish();
                return true;

            case MENU_ADMIN:

                //redirection vers MainAdministrationUtilisateur
                intent = new Intent(MainCardViewLocal.this, MainAdministrationUtilisateur.class);
                startActivity(intent);
                SessionManager.getInstance(this).checkLogin();
                return true;

            case MENU_PROFIL:

                //redirection vers MainModificationUtilisateur
                intent = new Intent(MainCardViewLocal.this, MainModificationUtilisateur.class);
                intent.putExtra("PSEUDO", SessionManager.getInstance(this).getDonneesSession().get(SessionManager.KEY_PSEUDO));
                startActivityForResult(intent, CODE_ACTIVITY);
                return true;
        }
        return false;
    }

    @Override
    protected void onResume() {
        super.onResume();

        if(shouldExecuteOnResume){
            startService(intentSession);
            SessionManager.getInstance(this).checkLogin();

            ajouterLocaux();
        } else{
            shouldExecuteOnResume = true;
        }
    }

    /**
     * OnCreate(), lancement de l'activité
     * */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_recycler);
        shouldExecuteOnResume = false;

        final Activity context = this;

        //Lancement de la notification
        NotificationHelper notificationHelper = new NotificationHelper(MainCardViewLocal.this);
        notificationHelper.notify(1, false, "My title", "My content" );
        Log.i("MainActivity", "Notification launched");

        ((ImageButton)findViewById(R.id.buttonAlert)).setVisibility(View.GONE);

        ((Button)findViewById(R.id.ajoutLocal)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                hideKeyboard(context);
                if(ajouterLocal())//Si tout ok on cache
                    findViewById(R.id.addLocalLayout).setVisibility(View.GONE);
            }
        });
        ((Button)findViewById(R.id.retourAjoutLocal)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                hideKeyboard(context);
                findViewById(R.id.addLocalLayout).setVisibility(View.GONE);
            }
        });
        findViewById(R.id.addLocalLayout).setVisibility(View.GONE);
        buttonAjout = (ImageButton) findViewById(R.id.buttonAdd);

        // Lancement du Service de vérification de connexion
        intentSession = new Intent(MainCardViewLocal.this, RapaceService.class);
        startService(intentSession);
        SessionManager.getInstance(this).checkLogin();

        //Ajout d'un local fictif
        ajouterLocaux();

        //Récupération du RecyclerView qui s'occupe du lancement des CardViews
        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);

        //définit l'agencement des cellules, ici de façon verticale, comme une ListView
        //recyclerView.setLayoutManager(new LinearLayoutManager(this));

        //pour adapterCardView en grille comme un RecyclerView, avec 2 cellules par ligne
        recyclerView.setLayoutManager(new GridLayoutManager(this,2));

        //Création d'un AdapterCardView, lui fournir notre liste de Locaux.
        //AdapterCardView servira à remplir notre Recyclerview
        adapterCardView = new AdapterCardView(locaux);
        recyclerView.setAdapter(adapterCardView);

        //Méthode onClick() du bouton d'ajout d'un Local
        buttonAjout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Check si connecté et relance le service
                startService(intentSession);

                findViewById(R.id.addLocalLayout).setVisibility(View.VISIBLE);
            }
        });
    }

    /**
     * Méthode d'ajout d'un Local dans la liste locaux
     * */
    private void ajouterLocaux() {
        locaux.clear();
        mProgressDialog = ProgressDialog.show(this, "Chargement...", " Chargement des locaux ...");
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
                        Toast.makeText(context, "Erreur lors de la récupération des locaux (JSON convert).\nVeuillez réessayer ou contacter un administrateur.",Toast.LENGTH_LONG).show();
                        fetch_locals_query_result = false;
                        fetch_locals_query_done = true;
                        e.printStackTrace();
                    }
                }
            }
        };

        startService(intentSession);
        /*if(session.user_id ==-1){
            Toast.makeText(context, "Session invalide.\nVeuillez réessayer ou relancer l'application.",Toast.LENGTH_LONG).show();
            return;
        }*/
        //session.checkLogin();

        Log.i("onResume", "id = " + Integer.parseInt(SessionManager.getInstance(this).getDonneesSession().get(SessionManager.KEY_ID)));
        Log.i("onResume", "id = " + SessionManager.getInstance(this).getDonneesSession().get(SessionManager.KEY_PSEUDO));
        LocalDBManager.getByUser(callback, Integer.parseInt(SessionManager.getInstance(this).getDonneesSession().get(SessionManager.KEY_ID)));

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
                            adapterCardView.notifyDataSetChanged();
                        else
                            Toast.makeText(context, "Erreur lors de la récupération des locaux (JSON convert).\nVeuillez réessayer ou contacter un administrateur.",Toast.LENGTH_LONG).show();
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
        SessionManager.getInstance(this).deconnexionSession();
    }

    private boolean ajouterLocal(){
        mProgressDialog = ProgressDialog.show(this, "Chargement...", " Ajout du Local ...");
        mProgressDialog.setCanceledOnTouchOutside(false); // main method that force user cannot click outside

        final Activity context = this;

        String name = ((EditText) findViewById(R.id.editTextName)).getText().toString();
        String address = ((EditText) findViewById(R.id.editTextAddress)).getText().toString();
        String number = ((EditText) findViewById(R.id.editPhoneNumber)).getText().toString();
        if (name.equals("")) {
            ((EditText) findViewById(R.id.editTextName)).setError("Veuillez choisir un nom de local.");
            return false;
        }if (address.equals("")) {
            ((EditText) findViewById(R.id.editTextAddress)).setError("Veuillez choisir une adresse.");
            return false;
        }if (number.equals("")) {
            ((EditText) findViewById(R.id.editPhoneNumber)).setError("Veuillez entrer le numéro a contacter en cas d'urgence.");
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
        Local v = new Local(name,address,number);
        Log.d("saveRecord", "(retour USER_ID) -> "+ SessionManager.getInstance(this).getDonneesSession().get(SessionManager.KEY_ID));

        startService(intentSession);
        LocalDBManager.addLocal(callback,v, Integer.parseInt(SessionManager.getInstance(this).getDonneesSession().get(SessionManager.KEY_ID)));

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
