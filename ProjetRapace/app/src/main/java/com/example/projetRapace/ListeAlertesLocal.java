package com.example.projetRapace;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.example.projetRapace.Alerte.Alerte;
import com.example.projetRapace.Alerte.AlerteDBManager;
import com.example.projetRapace.Alerte.CheckLocalAlerteLists;

import org.json.JSONArray;

import java.util.ArrayList;

public class ListeAlertesLocal extends AppCompatActivity {
    final Activity context = this;
    private static final int MENU_QUIT = 1;
    private Intent intentSession;
    private Intent intentService;
    private Intent intent;
    boolean shouldExecuteOnResume;
    private ProgressDialog mProgressDialog;

    private int id_local;
    private boolean isChanged = false;
    private ArrayAdapter<Alerte> alertesEnCoursCamName;
    private ArrayAdapter<Alerte> alertesFiniesCamName;

    private boolean check_actif_done = false;
    private boolean check_nonactif_done = false;
    private ArrayList<Alerte> alertesActives = new ArrayList<>();
    private ArrayList<Alerte> alertesNonActives = new ArrayList<>();

    BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.d("broadcastReceiver", "(givenValue) -> " + intent.getBooleanExtra("isChanged",false));
            Log.d("broadcastReceiver", "(actualValue) -> " + isChanged);

            if(isChanged)
                initView();
        }
    };

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
                intent = new Intent(ListeAlertesLocal.this, MainAdministrationUtilisateur.class);
                startActivity(intent);
                SessionManager.getInstance(this).checkLogin();
                return true;

            case MENU_PROFIL:

                //redirection vers MainModificationUtilisateur
                intent = new Intent(ListeAlertesLocal.this, MainModificationUtilisateur.class);
                intent.putExtra("PSEUDO", SessionManager.getInstance(this).getDonneesSession().get(SessionManager.KEY_PSEUDO));
                startActivityForResult(intent, CODE_ACTIVITY);
                return true;
        }
        return false;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_liste_alertes_local);
        shouldExecuteOnResume = false;

        Intent intent = getIntent();

        if(intent != null) {
            // Lancement du Service de vérification de connexion
            intentSession = new Intent(ListeAlertesLocal.this, RapaceService.class);
            startService(intentSession);
            SessionManager.getInstance(this).checkLogin();

            final int id = intent.getIntExtra("id", -1);

            if (id != -1) {
                id_local = id;

                alertesEnCoursCamName = new ArrayAdapter<Alerte>(context, android.R.layout.simple_list_item_2, android.R.id.text1,alertesActives) {
                    @Override
                    public int getCount(){
                        return alertesActives.size();
                    }

                    @Override
                    public View getView(int position, View convertView, ViewGroup parent) {
                        View view = super.getView(position, convertView, parent);
                        TextView text1 = (TextView) view.findViewById(android.R.id.text1);
                        TextView text2 = (TextView) view.findViewById(android.R.id.text2);

                        Log.d("CamName", alertesActives.get(position).CamName);

                        text1.setText(alertesActives.get(position).CamName);
                        text2.setText(alertesActives.get(position).getDateDebut());
                        return view;
                    }
                };

                alertesFiniesCamName = new ArrayAdapter<Alerte>(context, android.R.layout.simple_list_item_2, android.R.id.text1,alertesNonActives) {
                    @Override
                    public int getCount(){
                        return alertesNonActives.size();
                    }

                    @Override
                    public View getView(int position, View convertView, ViewGroup parent) {
                        Log.d("getView", "UPDATE");

                        View view = super.getView(position, convertView, parent);
                        TextView text1 = (TextView) view.findViewById(android.R.id.text1);
                        TextView text2 = (TextView) view.findViewById(android.R.id.text2);
                        Log.d("CamName", alertesNonActives.get(position).CamName);
                        text1.setText(alertesNonActives.get(position).CamName);
                        text2.setText(alertesNonActives.get(position).getDateDebut());
                        return view;
                    }
                };

                ((ListView) findViewById(R.id.listAlertesEnCours)).setAdapter(alertesEnCoursCamName);
                ((ListView) findViewById(R.id.listAlertesEnCours)).setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        Intent intent = new Intent(context, MainAlerteActive.class);
                        intent.putExtra("id", alertesActives.get(position).getId());
                        startActivity(intent);
                    }
                });
                ((ListView) findViewById(R.id.listAlertesFinies)).setAdapter(alertesFiniesCamName);

                intentService = new Intent(ListeAlertesLocal.this, CheckLocalAlerteLists.class);
                intentService.putExtra("isChanged",false);
                intentService.putExtra("id_local",id_local);
                startService(intentService);

                final Activity context = this;
                initView();
            }
        }

        ((Button) findViewById(R.id.buttonRetour)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void initView(){
        mProgressDialog = ProgressDialog.show(context, "Chargement...", " Chargement des informations de la caméra ...");
        mProgressDialog.setCanceledOnTouchOutside(false); // main method that force user cannot click outside

        check_actif_done = false;
        check_nonactif_done = false;
        AlerteDBManager.AlerteDBCallbackInterface callback = new AlerteDBManager.AlerteDBCallbackInterface() {
            @Override
            public void onQueryFinished(String operation, String output) {
                Log.d("initView", "(onQueryFinished) -> "+ operation);
                if(operation.equals(AlerteDBManager.ALERTE_DB_GETLOCALACTIVEALERTS)){
                    try {
                        Log.d("initView", "(retour ALERTE_DB_GETLOCALACTIVEALERTS) -> "+ output);
                        JSONArray jsonResult = new JSONArray(output);
                        Log.d("initView", "(retour ALERTE_DB_GETLOCALACTIVEALERTS) -> "+ Alerte.alertesFromJSON(jsonResult));
                        alertesActives.clear();
                        for(Alerte a : Alerte.alertesFromJSON(jsonResult)){
                            alertesActives.add(a);
                        }
                        alertesEnCoursCamName.notifyDataSetChanged();

                        check_actif_done = true;
                    } catch (Exception e) {
                        Toast.makeText(context, "Erreur lors de la vérification des alertes courantes.\nVeuillez réessayer ou contacter un administrateur.",Toast.LENGTH_LONG).show();
                        e.printStackTrace();
                    }
                }
            }
        };
        AlerteDBManager.getLocalActiveAlerts(callback,id_local);

        AlerteDBManager.AlerteDBCallbackInterface callback2 = new AlerteDBManager.AlerteDBCallbackInterface() {
            @Override
            public void onQueryFinished(String operation, String output) {
                Log.d("initView", "(onQueryFinished) -> "+ operation);
                if(operation.equals(AlerteDBManager.ALERTE_DB_GETLOCALNONACTIVEALERTS)){
                    try {
                        Log.d("initView", "(retour ALERTE_DB_GETLOCALNONACTIVEALERTS) -> "+ output);
                        JSONArray jsonResult = new JSONArray(output);
                        Log.d("initView", "(retour ALERTE_DB_GETLOCALNONACTIVEALERTS) -> "+ Alerte.alertesFromJSON(jsonResult));
                        alertesNonActives.clear();
                        for(Alerte a : Alerte.alertesFromJSON(jsonResult)){
                            alertesNonActives.add(a);
                        }
                        alertesFiniesCamName.notifyDataSetChanged();
                        check_nonactif_done = true;
                    } catch (Exception e) {
                        Toast.makeText(context, "Erreur lors de la vérification des alertes courantes.\nVeuillez réessayer ou contacter un administrateur.",Toast.LENGTH_LONG).show();
                        e.printStackTrace();
                    }
                }
            }
        };
        AlerteDBManager.getLocalNActiveAlerts(callback2,id_local);

        Thread checkLoading = new Thread(new Runnable(){
            @Override
            public void run() {
                while(!(check_actif_done && check_nonactif_done))
                    Log.d("MainCardViewCamera", "(Waiting for checking to end) -> (check_actif_done : " + check_actif_done + ") (check_nonactif_done : " + check_nonactif_done + ")");

                context.runOnUiThread(new Runnable(){
                    @Override
                    public void run() {
                        if (mProgressDialog != null)
                            mProgressDialog.dismiss();
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
        intentFilter.addAction("com.example.Alerte.ModifiedLocalActiveAlertesList");
        registerReceiver(broadcastReceiver, intentFilter);
    }

    @Override
    protected void onStop() {
        super.onStop();
        unregisterReceiver(broadcastReceiver);
    }

    @Override
    protected void onDestroy() {
        CheckLocalAlerteLists.getInstance().stop(intentService);
        super.onDestroy();
    }
    @Override
    protected void onPause() {
        CheckLocalAlerteLists.getInstance().stop(intentService);
        super.onPause();
    }

    @Override
    protected void onResume() {
        if(shouldExecuteOnResume){
            initView();
            intentService = new Intent(ListeAlertesLocal.this, CheckLocalAlerteLists.class);
            intentService.putExtra("isChanged",isChanged);
            intentService.putExtra("id_local",id_local);
            startService(intentService);
            SessionManager.getInstance(this).checkLogin();
        }else
            shouldExecuteOnResume = true;
        super.onResume();
    }
}
