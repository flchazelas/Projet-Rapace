package com.example.projetRapace;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
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
import com.example.projetRapace.Alerte.CheckNewAlertService;
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
    private Intent intentService;
    private ProgressDialog mProgressDialog;

    private int id_camera;

    private boolean check_done = false;
    private boolean check_result = false;
    private boolean add_done = false;
    private boolean add_result = false;
    private boolean isThereActiveAlert = false;

    BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.d("broadcastReceiver", "(givenValue) -> " + intent.getBooleanExtra("isThereActiveAlert",false));
            Log.d("broadcastReceiver", "(actualValue) -> " + isThereActiveAlert);

            updateAlertStatus(intent.getBooleanExtra("isThereActiveAlert",false));
        }
    };

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
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN, WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);

        setContentView(R.layout.activity_camera_view);
        if(intent != null) {
            // Lancement du Service de vérification de connexion
            intentSession = new Intent(CameraView.this, RapaceService.class);
            startService(intentSession);

            final int id = intent.getIntExtra("id", -1);

            final String ip = intent.getStringExtra("ip");
            if (id != -1) {
                id_camera = id;
                mv = new MjpegView(this);
                mv.setZOrderOnTop(false);

                RelativeLayout rl = (RelativeLayout) findViewById(R.id.ControlsHolder);
                ((FrameLayout) findViewById(R.id.FrameHolder)).removeAllViews();
                ((FrameLayout) findViewById(R.id.FrameHolder)).addView(mv);
                ((FrameLayout) findViewById(R.id.FrameHolder)).addView(rl);

                final Activity context = this;
                Runnable r = new VideoLoaderAsync(mv, ip);
                new Thread(r).start();

                mv.startPlayback();

                initView();
            }
        }
    }

    private void initView(){
        mProgressDialog = ProgressDialog.show(context, "Chargement...", " Chargement des informations de la caméra ...");
        mProgressDialog.setCanceledOnTouchOutside(false); // main method that force user cannot click outside

        check_done = false;
        check_result = false;
        AlerteDBManager.AlerteDBCallbackInterface callback = new AlerteDBManager.AlerteDBCallbackInterface() {
            @Override
            public void onQueryFinished(String operation, String output) {
                Log.d("initView", "(onQueryFinished) -> "+ operation);
                if(operation.equals(AlerteDBManager.ALERTE_DB_GETCURRENTFORCAMERA)){
                    try {
                        Log.d("initView", "(retour ALERTE_DB_GETCURRENTFORCAMERA) -> "+ output);
                        if(!output.equals("NO_RESULT")){
                            check_result = true;
                        } else
                            check_result = false;
                        check_done = true;
                    } catch (Exception e) {
                        Toast.makeText(context, "Erreur lors de la vérification des alertes courantes.\nVeuillez réessayer ou contacter un administrateur.",Toast.LENGTH_LONG).show();
                        e.printStackTrace();
                    }
                }
            }
        };
        AlerteDBManager.getCurrentForCamera(callback,id_camera);

        Thread checkLoading = new Thread(new Runnable(){
            @Override
            public void run() {
                while(!(check_done))
                    Log.d("MainCardViewCamera", "(Waiting for checking to end) -> (check_done : " + check_done + ")");

                context.runOnUiThread(new Runnable(){
                    @Override
                    public void run() {
                        intentService = new Intent(CameraView.this, CheckNewAlertService.class);
                        intentService.putExtra("alertStatus",check_result);
                        intentService.putExtra("id_camera",id_camera);
                        startService(intentService);
                        updateAlertStatus(check_result);
                        if (mProgressDialog != null)
                            mProgressDialog.dismiss();
                    }
                });
            }
        });
        checkLoading.start();
    }

    private void updateAlertStatus(boolean isThereActiveAlert){
        this.isThereActiveAlert = isThereActiveAlert;
        Log.d("updateAlertStatus", "(isThereActiveAlert) -> (isThereActiveAlert : " + isThereActiveAlert + ")");

        if(isThereActiveAlert){
            ((ImageButton) findViewById(R.id.alertButton)).setBackgroundTintList(getColorStateList(R.color.colorSoftRed));
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
            ((ImageButton) findViewById(R.id.alertButton)).setBackgroundTintList(getColorStateList(R.color.colorFullWhite));

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
                                            Log.d("createAlert", "(onQueryFinished) -> "+ operation);
                                            if(operation.equals(AlerteDBManager.ALERTE_DB_ADD)){
                                                try {
                                                    Log.d("createAlert", "(retour ALERTE_DB_ADD) -> " + output);
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
                                    AlerteDBManager.addAlerte(callbackImage,alerte,id_camera);

                                    Thread checkLoading = new Thread(new Runnable(){
                                        @Override
                                        public void run() {
                                            while(!(add_done))
                                                Log.d("createAlert", "(Waiting for add to end) -> (add_done : " + add_done);

                                            context.runOnUiThread(new Runnable(){
                                                @Override
                                                public void run() {
                                                    if (mProgressDialog != null)
                                                        mProgressDialog.dismiss();

                                                    if(add_result){
                                                        AlerteDBManager.AlerteDBCallbackInterface callbackImage = new AlerteDBManager.AlerteDBCallbackInterface() {
                                                            @Override
                                                            public void onQueryFinished(String operation, String output) {
                                                                Log.d("createAlert", "(onQueryFinished) -> "+ operation);
                                                                if(operation.equals(AlerteDBManager.ALERTE_DB_GETCURRENTFORCAMERA)){
                                                                    try {
                                                                        Log.d("createAlert", "(retour ALERTE_DB_ADD) -> " + output);
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
                                                        AlerteDBManager.getCurrentForCamera(callbackImage,id_camera);
                                                    }
                                                    else
                                                        Toast.makeText(context, "Erreur lors de la création de l'alerte.",Toast.LENGTH_LONG).show();

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

    @Override
    protected void onStart() {
        super.onStart();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("com.example.Alerte.ModifiedAlertStatus");
        registerReceiver(broadcastReceiver, intentFilter);
    }

    @Override
    protected void onStop() {
        super.onStop();
        unregisterReceiver(broadcastReceiver);
    }

    @Override
    protected void onDestroy() {
        CheckNewAlertService.getInstance().stop(intentService);
        super.onDestroy();
    }
}