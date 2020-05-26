package com.example.projetRapace;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.projetRapace.Alerte.Alerte;
import com.example.projetRapace.Alerte.AlerteDBManager;
import com.example.projetRapace.Camera.Camera;
import com.example.projetRapace.Camera.CameraDBManager;
import com.example.projetRapace.streamlib.MjpegView;
import com.example.projetRapace.streamlib.VideoLoaderAsync;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class CameraView extends BaseActivity {
    final Activity context = this;
    private MjpegView mv;
    private static final int MENU_QUIT = 1;
    private Intent intentSession;
    private ProgressDialog mProgressDialog;

    private boolean check_done = false;
    private boolean check_result = false;
    private boolean add_done = false;
    private boolean add_result = false;
    private boolean isThereActiveAlert = false;

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
            intentSession = new Intent(CameraView.this, RapaceService.class);
            startService(intentSession);

            final int id = intent.getIntExtra("id", -1);
            final String ip = intent.getStringExtra("ip");
            if (id != -1) {
                requestWindowFeature(Window.FEATURE_NO_TITLE);
                getWindow().setFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN, WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);
                mv = new MjpegView(this);
                mv.setZOrderOnTop(false);
                setContentView(R.layout.activity_camera_view);

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

                Runnable r = new VideoLoaderAsync(mv, ip);
                new Thread(r).start();

                mv.startPlayback();
            }
        }
    }

    private void updateAlertStatus(boolean isThereActiveAlert){
        this.isThereActiveAlert = isThereActiveAlert;

        if(isThereActiveAlert){
            ((ImageButton) findViewById(R.id.alertButton)).setBackgroundColor(getResources().getColor(R.color.colorSoftRed));
            ((ImageButton) findViewById(R.id.alertButton)).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(context);
                    builder.setCancelable(true);
                    builder.setTitle("Alerte déja en cours !");
                    builder.setMessage("Une alerte est déja en cours, voulez-vous la visualiser ?");
                    builder.setPositiveButton("Oui",
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    Toast.makeText(context, "Redirection vers l'alerte",Toast.LENGTH_LONG).show();
                                }
                            });
                    builder.setNegativeButton("Non", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
                    AlertDialog dialog = builder.create();
                    dialog.show();
                }
            });
        }else{
            ((ImageButton) findViewById(R.id.alertButton)).setBackgroundColor(getResources().getColor(R.color.colorFullWhite));
            ((ImageButton) findViewById(R.id.alertButton)).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(context);
                    builder.setCancelable(true);
                    builder.setTitle("Lancement d'une Alerte");
                    builder.setMessage("Voulez-vous vraiment lancer une alerte pour cette caméra ?");
                    builder.setPositiveButton("Oui",
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    mProgressDialog = ProgressDialog.show(context, "Chargement...", " Lancement de l'alerte ...");
                                    mProgressDialog.setCanceledOnTouchOutside(false); // main method that force user cannot click outside

                                    add_done = false;
                                    add_result = false;

                                    final Alerte alerte = new Alerte();

                                    AlerteDBManager.AlerteDBCallbackInterface callbackImage = new AlerteDBManager.AlerteDBCallbackInterface() {
                                        @Override
                                        public void onQueryFinished(String operation, String output) {
                                            Log.d("updateAlertStatus", "(onQueryFinished) -> "+ operation);
                                            if(operation.equals(AlerteDBManager.ALERTE_DB_ADD)){
                                                try {
                                                    Log.d("updateAlertStatus", "(retour ALERTE_DB_ADD) -> " + output);
                                                    if(output.equals("INSERT_SUCCESSFUL"))
                                                        add_result = true;
                                                    else
                                                        add_result = false;
                                                    add_done = true;
                                                } catch (Exception e) {
                                                    e.printStackTrace();
                                                }
                                            }
                                        }
                                    };
                                    AlerteDBManager.addAlerte(callbackImage,alerte);

                                    Thread checkLoading = new Thread(new Runnable(){
                                        @Override
                                        public void run() {
                                            while(!(add_done))
                                                Log.d("loadPage", "(Waiting for add to end) -> (add_done : " + add_done);

                                            context.runOnUiThread(new Runnable(){
                                                @Override
                                                public void run() {
                                                    if(add_result)
                                                        Toast.makeText(context, "Création de l'alerte réussi, redirection ... ",Toast.LENGTH_LONG).show();
                                                    else
                                                        Toast.makeText(context, "Erreur lors de la création de l'alerte.",Toast.LENGTH_LONG).show();

                                                    if (mProgressDialog != null)
                                                        mProgressDialog.dismiss();
                                                }
                                            });
                                        }
                                    });

                                    checkLoading.start();
                                }
                            });
                    builder.setNegativeButton("Non", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
                    AlertDialog dialog = builder.create();
                    dialog.show();
                }
            });
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