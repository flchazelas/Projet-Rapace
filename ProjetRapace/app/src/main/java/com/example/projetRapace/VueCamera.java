package com.example.projetRapace;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.InputType;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.projetRapace.Camera.Camera;
import com.example.projetRapace.Camera.CameraDBManager;
import com.example.projetRapace.ImageVideo.Image;
import com.example.projetRapace.ImageVideo.ImageDBManager;
import com.example.projetRapace.ImageVideo.Video;
import com.example.projetRapace.ImageVideo.VideoDBManager;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class VueCamera extends BaseActivity {
    private static final int MENU_QUIT = 1;
    private SessionManager session;
    private boolean modif_camera_query_done;
    private boolean modif_camera_query_result;

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
    private String addr_serv = "http://vps814672.ovh.net/";
    private boolean doneGetCamera;
    private Camera c;
    private ProgressDialog mProgressDialog;

    private boolean removedone = false;
    private boolean removeresult = false;

    private boolean CameraLoading = false;

    private Button buttonModif;

    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        Intent intent = getIntent();

        if(intent != null) {
            setContentView(R.layout.maquette_gestion_camera);

            // Lancement du Service de vérification de connexion
            Intent intentSession = new Intent(VueCamera.this, RapaceService.class);
            startService(intentSession);

            // Lancement du Session Manager pour stocker l'utilisateur
            session = new SessionManager(getApplicationContext());
            session.checkLogin();

            final Activity context = this;

            int id = intent.getIntExtra("id",-1);
            if(id != -1){
                buttonModif = (Button) findViewById(R.id.boutonModifier);
                //Méthode onClick() du bouton d'ajout d'une Caméra
                buttonModif.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        ((EditText) findViewById(R.id.editTextName)).setText(c.getName());
                        ((EditText) findViewById(R.id.editTextAddress)).setText(c.getIp());
                        findViewById(R.id.modifCameraLayout).setVisibility(View.VISIBLE);
                    }
                });

                ((Button) findViewById(R.id.validerModifCamera)).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        hideKeyboard(context);
                        if (modifierCamera())//Si tout ok on cache
                            findViewById(R.id.modifCameraLayout).setVisibility(View.GONE);
                    }
                });
                ((Button) findViewById(R.id.retourModifCamera)).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        hideKeyboard(context);
                        findViewById(R.id.modifCameraLayout).setVisibility(View.GONE);
                    }
                });
                findViewById(R.id.modifCameraLayout).setVisibility(View.GONE);

                loadPage(id);
            }
        }
    }

    private void loadPage(int id){
        final Activity context = this;
        c = null;

        mProgressDialog = ProgressDialog.show(this, "Downloading", "Downloading Data ...");
        mProgressDialog.setCanceledOnTouchOutside(false); // main method that force user cannot click outside
        CameraLoading = false;

        ((Button) findViewById(R.id.boutonRetour)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        CameraDBManager.CameraDBCallbackInterface callbackCamera = new CameraDBManager.CameraDBCallbackInterface() {
            @Override
            public void onQueryFinished(String operation, String output) {
                Log.d("VueCamera", "(onQueryFinished) -> "+ operation);
                if(operation.equals(CameraDBManager.CAMERA_DB_GETBYID)){
                    try {
                        Log.d("VueCamera", "(retour CAMERA_DB_GETBYID) -> "+ output);
                        JSONObject jsonResult = new JSONObject(output);
                        c = Camera.cameraFromJSON(jsonResult);
                        Log.d("VueCamera", "(retour CAMERA_DB_GETBYID) -> "+ c);

                        TextView tw = (TextView) findViewById(R.id.NomCamera);
                        tw.setText(c.getName());

                        ((Button) findViewById(R.id.boutonSupprimer)).setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                removedone = false;
                                removeresult = false;

                                mProgressDialog = ProgressDialog.show(context, "Downloading", "Downloading Data ...");
                                mProgressDialog.setCanceledOnTouchOutside(false); // main method that force user cannot click outside

                                final Camera cam = c;

                                CameraDBManager.CameraDBCallbackInterface callbackImage = new CameraDBManager.CameraDBCallbackInterface() {
                                    @Override
                                    public void onQueryFinished(String operation, String output) {
                                        Log.d("VueCamera", "(onQueryFinished) -> "+ operation);
                                        if(operation.equals(CameraDBManager.CAMERA_DB_REMOVE)){
                                            try {
                                                Log.d("VueCamera", "(retour IMAGE_DB_GETBYCAMERA) -> " + output);
                                                if(output.equals("REMOVE_SUCCESSFUL"))
                                                    removeresult = true;
                                                else
                                                    removeresult = false;
                                                removedone = true;
                                            } catch (Exception e) {
                                                e.printStackTrace();
                                            }
                                        }
                                    }
                                };
                                CameraDBManager.removeCamera(callbackImage,cam);

                                Thread checkLoading = new Thread(new Runnable(){
                                    @Override
                                    public void run() {
                                        while(!(removedone))
                                            Log.d("VueCamera", "(Waiting for loading to endd) -> (CameraLoading : " + CameraLoading + ");");

                                        context.runOnUiThread(new Runnable(){
                                            @Override
                                            public void run() {
                                                if(removeresult){
                                                    finish();
                                                }
                                                if (mProgressDialog != null)
                                                    mProgressDialog.dismiss();
                                            }
                                        });
                                    }
                                });

                                checkLoading.start();
                            }
                        });

                        ((Button) findViewById(R.id.boutonDirect)).setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent intent = new Intent(context, CameraView.class);

                                intent.putExtra("id", c.getId());
                                intent.putExtra("ip", c.getIp());
                                startActivity(intent);
                            }
                        });

                        CameraLoading = true;
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        };
        CameraDBManager.getById(callbackCamera,id);

        Thread checkLoading = new Thread(new Runnable(){
            @Override
            public void run() {
                while(!(CameraLoading))
                    Log.d("VueCamera", "(Waiting for loading to endd) -> (CameraLoading : " + CameraLoading+")");

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

    private boolean modifierCamera(){
        final Camera cam = this.c;
        mProgressDialog = ProgressDialog.show(this, "Chargement...", " Ajout de la Camera ...");
        mProgressDialog.setCanceledOnTouchOutside(false); // main method that force user cannot click outside

        final Activity context = this;

        String name = ((EditText) findViewById(R.id.editTextName)).getText().toString();
        String address = ((EditText) findViewById(R.id.editTextAddress)).getText().toString();
        if (name.equals("")) {
            ((EditText) findViewById(R.id.editTextName)).setError("Veuillez choisir un nom de camera.");
            if (mProgressDialog != null)
                mProgressDialog.dismiss();
            return false;
        }if (address.equals("")) {
            ((EditText) findViewById(R.id.editTextAddress)).setError("Veuillez choisir une adresse ip.");
            if (mProgressDialog != null)
                mProgressDialog.dismiss();
            return false;
        }

        if(name.equals(cam.getName()) && address.equals(cam.getIp())){
            if (mProgressDialog != null)
                mProgressDialog.dismiss();

            return true;
        }

        cam.setName(name);
        cam.setIp(address);

        modif_camera_query_done = false;
        modif_camera_query_result = false;

        CameraDBManager.CameraDBCallbackInterface callback = new CameraDBManager.CameraDBCallbackInterface() {
            @Override
            public void onQueryFinished(String operation, String output) {
                Log.d("MainCardViewCamera", "(onQueryFinished) -> "+ operation);
                if(operation.equals(CameraDBManager.CAMERA_DB_UPDATE)){
                    try {
                        Log.d("MainCardViewCamera", "(retour CAMERA_DB_UPDATE) -> "+ output);
                        if(!output.equals("UPDATE_ERROR")){
                            modif_camera_query_result = true;
                            JSONObject jsonResult = new JSONObject(output);
                            c = Camera.cameraFromJSON(jsonResult);
                            Log.d("VueCamera", "(retour CAMERA_DB_UPDATE) -> "+ c);
                        } else
                            modif_camera_query_result = false;
                        modif_camera_query_done = true;
                    } catch (Exception e) {
                        Toast.makeText(context, "Erreur lors de l'ajout du Camera.\nVeuillez réessayer ou contacter un administrateur.",Toast.LENGTH_LONG).show();
                        e.printStackTrace();
                    }
                }
            }
        };

        if(cam == null){
            Toast.makeText(context, "Id de Local invalide.\nVeuillez réessayer ou relancer l'application.",Toast.LENGTH_LONG).show();
            return false;
        }
        CameraDBManager.updateCamera(callback,cam);

        Thread checkLoading = new Thread(new Runnable(){
            @Override
            public void run() {
                while(!(modif_camera_query_done))
                    Log.d("MainCardViewCamera", "(Waiting for loading to end) -> (modif_camera_query_done : " + modif_camera_query_done + ")");

                context.runOnUiThread(new Runnable(){
                    @Override
                    public void run() {
                        if (mProgressDialog != null)
                            mProgressDialog.dismiss();
                        //affichage
                        if(modif_camera_query_result){
                            ((TextView) findViewById(R.id.NomCamera)).setText(c.getName());

                            ((Button) findViewById(R.id.boutonDirect)).setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    Intent intent = new Intent(context, CameraView.class);

                                    intent.putExtra("id", c.getId());
                                    intent.putExtra("ip", c.getIp());
                                    startActivity(intent);
                                }
                            });
                        }
                        else
                            Toast.makeText(context, "Erreur lors de l'ajout du Camera.\nVeuillez réessayer ou contacter un administrateur.",Toast.LENGTH_LONG).show();
                    }
                });
            }
        });

        checkLoading.start();

        return true;
    }
}
