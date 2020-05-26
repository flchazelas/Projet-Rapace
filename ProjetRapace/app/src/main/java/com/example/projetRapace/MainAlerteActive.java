package com.example.projetRapace;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.projetRapace.Alerte.Alerte;
import com.example.projetRapace.Alerte.AlerteDBManager;
import com.example.projetRapace.Camera.Camera;
import com.example.projetRapace.Camera.CameraDBManager;
import com.example.projetRapace.ImageVideo.Image;
import com.example.projetRapace.ImageVideo.Video;
import com.example.projetRapace.Local.Local;
import com.example.projetRapace.Local.LocalDBManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class MainAlerteActive extends AppCompatActivity {
    private Activity context;
    private static final int MENU_QUIT = 1;
    private Intent intent;
    private ListView listViewUser;
    private ListView listViewActions;
    private ListView listViewIV;
    private Intent intentService;

    private Alerte alerte;
    private Camera camera;
    private Local local;
    private ArrayList<Video> videos;
    private ArrayList<Image> image;

    private boolean pageLoading;

    private ProgressDialog mProgressDialog;

    private int id_alerte;

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
                intent = new Intent(MainAlerteActive.this, MainAdministrationUtilisateur.class);
                startActivity(intent);
                SessionManager.getInstance(this).checkLogin();
                return true;

            case MENU_PROFIL:

                //redirection vers MainModificationUtilisateur
                intent = new Intent(MainAlerteActive.this, MainModificationUtilisateur.class);
                intent.putExtra("PSEUDO", SessionManager.getInstance(this).getDonneesSession().get(SessionManager.KEY_PSEUDO));
                startActivityForResult(intent, CODE_ACTIVITY);
                return true;
        }
        return false;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_alerte_active);
        context = this;

        // Lancement du Service de vérification de connexion
        intentService = new Intent(MainAlerteActive.this, RapaceService.class);
        startService(intentService);
        SessionManager.getInstance(this).checkLogin();

        Intent intent = getIntent();
        //Récupération des champs de données
        if(intent != null) {
            id_alerte = intent.getIntExtra("id",-1);
            if(id_alerte != -1){
                listViewUser = findViewById(R.id.listUserNum);
                listViewActions = findViewById(R.id.listActions);
                listViewIV = findViewById(R.id.listIV);

                loadPage();
            }
        }
    }

    public void recupListeUtilisateurs(final List<Utilisateur> listeU){
        
        final String[] strings = new String[listeU.size()];
        for (int i=0; i<listeU.size(); i++){
            strings[i] = listeU.get(i).getPseudo_utilisateur()+"%"+listeU.get(i).getPhone();
        }


        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, strings){
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
            View view = convertView;

            if (view == null)
            {
                LayoutInflater li = (LayoutInflater) this.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                //le layout représentant la ligne dans le listView
                view = li.inflate(R.layout.ligne_liste_num, null);
            }

            TextView text = (TextView) view.findViewById(R.id.textLigne);
            final String[] s = strings[position].split("%");
            text.setText(s[0]+"     "+s[1]);

            ImageButton buttonAppel = (ImageButton) view.findViewById(R.id.boutonLigne);
            buttonAppel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //Donner le numéro à appeller ==>
                    Uri uri = Uri.parse("tel:"+s[1]);
                    intent = new Intent(Intent.ACTION_DIAL, uri);
                    startActivity(intent);
                }
            });

            return view;
        }};

        listViewUser.setAdapter(adapter);
    }


    public void recupListeAction(final List<Action> listeA){

        final String[] strings = new String[listeA.size()];
        for (int i=0; i<listeA.size(); i++){
            strings[i] = listeA.get(i).getType()+"%"+listeA.get(i).getDateEffectuee()+"%"+listeA.get(i).getUtilisateur()+"%"+listeA.get(i).getDescription();
        }


        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, strings){
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                View view = convertView;

                if (view == null)
                {
                    LayoutInflater li = (LayoutInflater) this.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                    //le layout représentant la ligne dans le listView
                    view = li.inflate(R.layout.ligne_liste_actions, null);
                }

                final String[] s = strings[position].split("%");

                TextView text = (TextView) view.findViewById(R.id.textType);
                text.setText(s[0]);

                TextView text1 = (TextView) view.findViewById(R.id.textDate);
                text1.setText(s[1]);

                TextView text2 = (TextView) view.findViewById(R.id.textUser);
                text2.setText(s[2]);

                TextView text3 = (TextView) view.findViewById(R.id.textDescription);
                text3.setText(s[3]);

                return view;
            }};

        listViewActions.setAdapter(adapter);
    }


    public void recupListeIV(final List<Object> listeI){

        final String[] strings = new String[listeI.size()];
        for (int i=0; i<listeI.size(); i++){
            Log.d("LIST : ", String.valueOf(listeI.get(i).getClass().toString().equals(Video.class.toString())));
            if(listeI.get(i).getClass().toString().equals(Image.class.toString())){
                Image l = (Image) listeI.get(i);
                strings[i] = l.getId()+"%"+l.getChemin();
            }
            else if(listeI.get(i).getClass().toString().equals(Video.class.toString())){
                Video l = (Video) listeI.get(i);
                strings[i] = l.getId()+"%"+l.getChemin();
            }
        }


        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, strings){
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                View view = convertView;

                if (view == null)
                {
                    LayoutInflater li = (LayoutInflater) this.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                    //le layout représentant la ligne dans le listView
                    view = li.inflate(R.layout.ligne_liste_iv, null);
                }

                final String[] s = strings[position].split("%");

                TextView text = (TextView) view.findViewById(R.id.textType);
                text.setText(s[0]);

                TextView text1 = (TextView) view.findViewById(R.id.textDate);
                text1.setText(s[1]);

                return view;
            }};

        listViewIV.setAdapter(adapter);
    }

    public void loadPage(){


        pageLoading = false;
        alerte = null;
        camera = null;
        local = null;

        mProgressDialog = ProgressDialog.show(context, "Chargement...", " Chargement des informations de la caméra ...");
        mProgressDialog.setCanceledOnTouchOutside(false); // main method that force user cannot click outside

        AlerteDBManager.AlerteDBCallbackInterface callbackAlerte = new AlerteDBManager.AlerteDBCallbackInterface() {
            @Override
            public void onQueryFinished(String operation, String output) {
                Log.d("loadPage", "(onQueryFinished) -> "+ operation);
                if(operation.equals(AlerteDBManager.ALERTE_DB_GETBYID)){
                    try {
                        Log.d("loadPage", "(retour ALERTE_DB_GETBYID) -> "+ output);
                        if(!output.equals("NO_RESULT")){
                            JSONObject jsonResult = new JSONObject(output);
                            alerte = Alerte.alerteFromJSON(jsonResult);
                            Log.d("loadPage", "(retour ALERTE_DB_GETBYID) -> "+ alerte);

                            CameraDBManager.CameraDBCallbackInterface callbackCamera = new CameraDBManager.CameraDBCallbackInterface() {
                                @Override
                                public void onQueryFinished(String operation, String output) {
                                    Log.d("loadPage", "(onQueryFinished) -> "+ operation);
                                    if(operation.equals(CameraDBManager.CAMERA_DB_GETBYID)){
                                        try {
                                            Log.d("loadPage", "(retour CAMERA_DB_GETBYID) -> "+ output);
                                            if(!output.equals("NO_RESULT")){
                                                JSONObject jsonResult = new JSONObject(output);
                                                camera = Camera.cameraFromJSON(jsonResult);
                                                Log.d("loadPage", "(retour CAMERA_DB_GETBYID) -> "+ camera);

                                                LocalDBManager.LocalDBCallbackInterface callbackLocal = new LocalDBManager.LocalDBCallbackInterface() {
                                                    @Override
                                                    public void onQueryFinished(String operation, String output) {
                                                        Log.d("loadPage", "(onQueryFinished) -> "+ operation);
                                                        if(operation.equals(LocalDBManager.LOCAL_DB_GETBYID)){
                                                            try {
                                                                Log.d("loadPage", "(retour LOCAL_DB_GETBYID) -> "+ output);
                                                                if(!output.equals("NO_RESULT")){
                                                                    JSONObject jsonResult = new JSONObject(output);
                                                                    local = Local.localFromJSON(jsonResult);
                                                                    Log.d("loadPage", "(retour LOCAL_DB_GETBYID) -> "+ local);


                                                                }
                                                                pageLoading = true;
                                                            } catch (JSONException e) {
                                                                e.printStackTrace();
                                                            }
                                                        }
                                                    }
                                                };
                                                LocalDBManager.getById(callbackLocal,alerte.LocId);

                                            }
                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }
                                    }
                                }
                            };
                            CameraDBManager.getById(callbackCamera,alerte.CamId);

                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        };
        AlerteDBManager.getById(callbackAlerte,id_alerte);

        Thread checkLoading = new Thread(new Runnable(){
            @Override
            public void run() {
                while(!(pageLoading))
                    Log.d("MainAlerteActive", "(Waiting for loading to end) -> (pageLoading : " + pageLoading + ")");

                context.runOnUiThread(new Runnable(){
                    @Override
                    public void run() {
                        if (mProgressDialog != null)
                            mProgressDialog.dismiss();

                        UtilisateurManagerDistant m = new UtilisateurManagerDistant(MainAlerteActive.this);
                        ArrayList list = new ArrayList();
                        list.add(alerte.getId());
                        m.envoi("recupNumUtilisateur", new JSONArray(list));

                        ((Button) findViewById(R.id.boutonDirect)).setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent intent = new Intent(context, CameraAlerteView.class);
                                intent.putExtra("id", alerte.getId());
                                intent.putExtra("ip", camera.getIp());
                                startActivity(intent);
                            }
                        });

                        ((Button) findViewById(R.id.appellerNumLocal)).setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                //Donner le numéro à appeller ==>
                                String s = local.getPhoneNumber();
                                Uri uri = Uri.parse("tel:"+s);
                                intent = new Intent(Intent.ACTION_DIAL, uri);
                                startActivity(intent);
                            }
                        });

                        ((Button) findViewById(R.id.ignorerAlerte)).setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                mProgressDialog = ProgressDialog.show(context, "Chargement...", " Chargement des informations de la caméra ...");
                                mProgressDialog.setCanceledOnTouchOutside(false); // main method that force user cannot click outside

                                AlerteDBManager.AlerteDBCallbackInterface callback = new AlerteDBManager.AlerteDBCallbackInterface() {
                                    @Override
                                    public void onQueryFinished(String operation, String output) {
                                        Log.d("ignorerAlerte", "(onQueryFinished) -> "+ operation);
                                        if(operation.equals(AlerteDBManager.ALERTE_DB_IGNOREALERTE)){
                                            try {
                                                Log.d("ignorerAlerte", "(retour ALERTE_DB_IGNOREALERTE) -> "+ output);
                                                if (mProgressDialog != null)
                                                    mProgressDialog.dismiss();
                                                if(output.equals("UPDATE_SUCCESSFUL"))
                                                    finish();
                                                else
                                                    Toast.makeText(context, "Erreur lors de l'archivage de l'alerte.\nVeuillez réessayer ou contacter un administrateur.",Toast.LENGTH_LONG).show();
                                            } catch (Exception e) {
                                                Toast.makeText(context, "Erreur lors de la vérification des alertes courantes.\nVeuillez réessayer ou contacter un administrateur.",Toast.LENGTH_LONG).show();
                                                e.printStackTrace();
                                            }
                                        }
                                    }
                                };
                                AlerteDBManager.ignoreAlerte(callback,alerte.getId());

                            }
                        });

                    }
                });
            }
        });
        checkLoading.start();

        //A enlever quand tu auras récup la liste des actions
        List<Action> listeA = new ArrayList<>();
        listeA.add(new Action("Alarme", "15/04/2020", "Fred", "Lancement d'une alarme"));
        listeA.add(new Action("Ignorer", "23/04/2020", "Daniel", "Ignorer l'alerte"));
        listeA.add(new Action("Capture", "07/05/2019", "Gile", "Capture d'une image"));
        this.recupListeAction(listeA);

        //A enlever quand tu auras récup la liste des actions
        List<Object> listeI = new ArrayList<>();
        listeI.add(new Image(0, "https://dsrhsjyd/sdb/sdbt.com","sdasdasd"));
        listeI.add(new Video(1, "https://sgbdb/sqsgsbtsdb/srgrsdbt.com","sdasdasd"));
        this.recupListeIV(listeI);
    }
}
