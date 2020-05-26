package com.example.projetRapace;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.app.ProgressDialog;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.projetRapace.Alerte.CheckLocalNbActiveAlerts;
import com.example.projetRapace.Alerte.CheckNewAlertService;
import com.example.projetRapace.Camera.Camera;
import com.example.projetRapace.Camera.CameraDBManager;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.List;

public class MainCardViewCamera extends BaseActivity {
    private int nbActiveAlert = -1;
    BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.d("broadcastReceiver", "(givenValue) -> " + intent.getIntExtra("nbActiveAlert",-1));
            Log.d("broadcastReceiver", "(actualValue) -> " + nbActiveAlert);

            updateAlertStatus(intent.getIntExtra("nbActiveAlert",-1));
        }
    };
    boolean shouldExecuteOnResume;
    private ProgressDialog mProgressDialog;

    private int id;

    boolean add_camera_query_done = false;
    boolean add_camera_query_result = false;

    boolean fetch_cameras_query_result = false;
    boolean fetch_cameras_query_done = false;

    private RecyclerView recyclerView;

    private List<Object> cameras = new ArrayList<Object>();
    private ImageButton buttonAjout;
    private Intent intent;
    private Intent intentSession;
    private Intent intentService;
    private Service service;
    private AdapterCardView adapterCardView;

    private static final int MENU_QUIT = 1;

    private void updateAlertStatus(int nbActiveAlert) {
        this.nbActiveAlert = nbActiveAlert;
        if(nbActiveAlert != 0){
            ((ImageButton) findViewById(R.id.buttonAlert)).setImageTintList(getColorStateList(R.color.colorSoftRed));
        }else{
            ((ImageButton) findViewById(R.id.buttonAlert)).setImageTintList(getColorStateList(R.color.colorFullWhite));
        }
    }

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
                SessionManager.getInstance(this).deconnexionSession();
                finish();
                return true;
        }
        return false;
    }

    @Override
    protected void onResume() {
        super.onResume();

        if(shouldExecuteOnResume){
            intentService = new Intent(MainCardViewCamera.this, CheckLocalNbActiveAlerts.class);
            intentService.putExtra("id_local",id);
            startService(intentService);

            startService(intentSession);
            ajouterCameras();
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
        intent = getIntent();
        Log.d("debugId", "intent -> "+ intent);


        final Activity context = this;

        if(intent != null) {
            final int id_local = intent.getIntExtra("id", -1);
            this.id = id_local;
            Log.d("debugId", "id_local -> "+ id_local);
            if (id_local != -1) {
                ((EditText) findViewById(R.id.editTextName)).setHint("Cour Interieur");
                ((TextView) findViewById(R.id.textAddress)).setText("IP");
                ((EditText) findViewById(R.id.editTextAddress)).setHint("http://vps814672.ovh.net/Data/videos/2020-05-10_23:54:27.123.mjpeg");
                ((LinearLayout) findViewById(R.id.layoutNumero)).setVisibility(View.GONE);
                buttonAjout = (ImageButton) findViewById(R.id.buttonAdd);

                intentService = new Intent(MainCardViewCamera.this, CheckLocalNbActiveAlerts.class);
                intentService.putExtra("id_local",id_local);
                startService(intentService);

                //Navigation vers liste des alertes pour le local
                ((ImageButton) findViewById(R.id.buttonAlert)).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(context, ListeAlertesLocal.class);

                        intent.putExtra("id", id_local);
                        startActivity(intent);
                    }
                });
                //Méthode onClick() du bouton d'ajout d'une Caméra
                buttonAjout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        //Check si connecté et relance le service
                        startService(intentSession);

                        findViewById(R.id.addLocalLayout).setVisibility(View.VISIBLE);
                    }
                });

                ((Button) findViewById(R.id.ajoutLocal)).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        hideKeyboard(context);
                        if (ajouterCamera())//Si tout ok on cache
                            findViewById(R.id.addLocalLayout).setVisibility(View.GONE);
                    }
                });
                ((Button) findViewById(R.id.retourAjoutLocal)).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        hideKeyboard(context);
                        findViewById(R.id.addLocalLayout).setVisibility(View.GONE);
                    }
                });
                findViewById(R.id.addLocalLayout).setVisibility(View.GONE);

                // Lancement du Service de vérification de connexion
                intentSession = new Intent(MainCardViewCamera.this, RapaceService.class);
                startService(intentSession);

                //Ajout d'une Caméra fictive
                ajouterCameras();

                //Récupération du RecyclerView qui s'occupe du lancement des CardViews
                recyclerView = (RecyclerView) findViewById(R.id.recyclerView);

                //définit l'agencement des cellules, ici de façon verticale, comme une ListView
                //recyclerView.setLayoutManager(new LinearLayoutManager(this));

                //pour adapterCardView en grille comme un RecyclerView, avec 2 cellules par ligne
                recyclerView.setLayoutManager(new GridLayoutManager(this, 2));

                //Création d'un AdapterCardView, lui fournir notre liste de Caméras.
                //AdapterCardView servira à remplir notre Recyclerview
                adapterCardView = new AdapterCardView(cameras);
                recyclerView.setAdapter(adapterCardView);


            }
        }
    }

    /**
     * onDestroy(), gère la destruction de l'activité
     * Déconnecte l'utilisateur si l'activité est détruite via la session
     * */
    /*
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(!session.isLoggedIn())
            session.deconnexionSession();
    }*/

    private boolean ajouterCamera(){
        final int id_local = this.id;
        mProgressDialog = ProgressDialog.show(this, "Chargement...", " Ajout de la Camera ...");
        mProgressDialog.setCanceledOnTouchOutside(false); // main method that force user cannot click outside

        final Activity context = this;

        String name = ((EditText) findViewById(R.id.editTextName)).getText().toString();
        String address = ((EditText) findViewById(R.id.editTextAddress)).getText().toString();
        if (name.equals("")) {
            ((EditText) findViewById(R.id.editTextName)).setError("Veuillez choisir un nom de camera.");
            return false;
        }if (address.equals("")) {
            ((EditText) findViewById(R.id.editTextAddress)).setError("Veuillez choisir une adresse ip.");
            return false;
        }

        add_camera_query_done = false;
        add_camera_query_result = false;

        CameraDBManager.CameraDBCallbackInterface callback = new CameraDBManager.CameraDBCallbackInterface() {
            @Override
            public void onQueryFinished(String operation, String output) {
                Log.d("MainCardViewCamera", "(onQueryFinished) -> "+ operation);
                if(operation.equals(CameraDBManager.CAMERA_DB_ADD)){
                    try {
                        Log.d("MainCardViewCamera", "(retour CAMERA_DB_ADD) -> "+ output);
                        if(output.equals("INSERT_SUCCESSFUL"))
                            add_camera_query_result = true;
                        else
                            add_camera_query_result = false;
                        add_camera_query_done = true;
                    } catch (Exception e) {
                        Toast.makeText(context, "Erreur lors de l'ajout du Camera.\nVeuillez réessayer ou contacter un administrateur.",Toast.LENGTH_LONG).show();
                        e.printStackTrace();
                    }
                }
            }
        };
        Camera v = new Camera(name,address);

        if(id_local ==-1){
            Toast.makeText(context, "Id de Local invalide.\nVeuillez réessayer ou relancer l'application.",Toast.LENGTH_LONG).show();
            return false;
        }
        CameraDBManager.addCamera(callback,v,id_local);

        Thread checkLoading = new Thread(new Runnable(){
            @Override
            public void run() {
                while(!(add_camera_query_done))
                    Log.d("MainCardViewCamera", "(Waiting for loading to end) -> (add_local_query_done : " + add_camera_query_done + ")");

                    context.runOnUiThread(new Runnable(){
                    @Override
                    public void run() {
                    if (mProgressDialog != null)
                        mProgressDialog.dismiss();
                    //affichage
                    if(add_camera_query_result)
                        ajouterCameras();
                    else
                        Toast.makeText(context, "Erreur lors de l'ajout du Camera.\nVeuillez réessayer ou contacter un administrateur.",Toast.LENGTH_LONG).show();
                    }
                });
            }
        });

        checkLoading.start();

        return true;
    }

    private void ajouterCameras() {
        final int id_local = this.id;
        cameras.clear();
        mProgressDialog = ProgressDialog.show(this, "Chargement...", " Affichage des Cameras ...");
        mProgressDialog.setCanceledOnTouchOutside(false); // main method that force user cannot click outside
        final Activity context = this;

        fetch_cameras_query_result = false;
        fetch_cameras_query_done = false;

        CameraDBManager.CameraDBCallbackInterface callback = new CameraDBManager.CameraDBCallbackInterface() {
            @Override
            public void onQueryFinished(String operation, String output) {
                Log.d("MainCardViewCamera", "(onQueryFinished) -> "+ operation);
                if(operation.equals(CameraDBManager.CAMERA_DB_GETBYLOCAL)){
                    try {
                        Log.d("MainCardViewCamera", "(retour CAMERA_DB_GETBYLOCAL) -> "+ output);
                        JSONArray jsonResult = new JSONArray(output);
                        Log.d("MainCardViewCamera", "(retour CAMERA_DB_GETBYLOCAL) -> "+ Camera.camerasFromJSON(jsonResult));
                        for(Camera c : Camera.camerasFromJSON(jsonResult))
                            cameras.add(c);

                        fetch_cameras_query_result = true;
                        fetch_cameras_query_done = true;
                    } catch (Exception e) {
                        Toast.makeText(context, "Erreur lors de la récupération des Cameras.\nVeuillez réessayer ou contacter un administrateur.",Toast.LENGTH_LONG).show();
                        e.printStackTrace();
                    }
                }
            }
        };
        Log.d("MainCardViewCamera", "id_local -> "+ id_local);

        if(id_local ==-1){
            Toast.makeText(context, "Id de Local invalide.\nVeuillez réessayer ou relancer l'application.",Toast.LENGTH_LONG).show();
            return;
        }
        CameraDBManager.getByLocal(callback,id_local);

        Thread checkLoading = new Thread(new Runnable(){
            @Override
            public void run() {
                while(!(fetch_cameras_query_done))
                    Log.d("MainCardViewCamera", "(Waiting for loading to end) -> (add_local_query_done : " + fetch_cameras_query_done + ")");

                    context.runOnUiThread(new Runnable(){
                    @Override
                    public void run() {
                    if (mProgressDialog != null)
                        mProgressDialog.dismiss();
                    //affichage
                    if(fetch_cameras_query_result)
                        adapterCardView.notifyDataSetChanged();
                    else
                        Toast.makeText(context, "Erreur lors de la récupération des Cameras.\nVeuillez réessayer ou contacter un administrateur.",Toast.LENGTH_LONG).show();
                    }
                });
            }
        });

        checkLoading.start();
    }

    @Override
    protected void onStart() {
        super.onStart();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("com.example.Alerte.ModifiedLocalActiveAlertesNumber");
        registerReceiver(broadcastReceiver, intentFilter);
    }

    @Override
    protected void onStop() {
        super.onStop();
        unregisterReceiver(broadcastReceiver);
    }

    @Override
    protected void onDestroy() {
        CheckLocalNbActiveAlerts.getInstance().stop(intentService);
        super.onDestroy();
    }

    @Override
    protected void onPause() {
        CheckLocalNbActiveAlerts.getInstance().stop(intentService);
        super.onPause();
    }
}
