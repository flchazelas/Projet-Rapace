package com.example.projetRapace.Alerte;
import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import com.example.projetRapace.Camera.Camera;

import org.json.JSONArray;

import java.util.ArrayList;

public class CheckLocalNbActiveAlerts extends IntentService {
    public Context context = this;
    public Handler handler = null;

    private int id_local = -1;
    private int nbActiveAlert = -1;

    private boolean check_done = false;
    private boolean check_result = false;
    private ArrayList<Alerte> alertes = new ArrayList<>();

    public CheckLocalNbActiveAlerts() {
        super(CheckLocalNbActiveAlerts.class.getSimpleName());
    }
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
    @Override
    protected void onHandleIntent(Intent intent) {
        nbActiveAlert = intent.getIntExtra("nbActiveAlert",-1);
        id_local = intent.getIntExtra("id_local",-1);
        HandlerThread handlerThread = new HandlerThread("background-thread");
        handlerThread.start();
        handler = new Handler(handlerThread.getLooper());
        final Runnable runnable = new Runnable() {
            public void run() {
                handler.postDelayed(this, 2000);

                //test
                check_done = false;
                AlerteDBManager.AlerteDBCallbackInterface callback = new AlerteDBManager.AlerteDBCallbackInterface() {
                    @Override
                    public void onQueryFinished(String operation, String output) {
                        Log.d("CheckLocalNbActiveAlerts", "(onQueryFinished) -> "+ operation);
                        if(operation.equals(AlerteDBManager.ALERTE_DB_GETLOCALACTIVEALERTS)){
                            try {
                                Log.d("CheckLocalNbActiveAlerts", "(retour ALERTE_DB_GETLOCALACTIVEALERTS) -> "+ output);

                                JSONArray jsonResult = new JSONArray(output);
                                Log.d("MainCardViewCamera", "(retour CAMERA_DB_GETBYLOCAL) -> "+ Camera.camerasFromJSON(jsonResult));
                                alertes.clear();
                                for(Alerte a : Alerte.alertesFromJSON(jsonResult))
                                    alertes.add(a);
                                check_done = true;
                            } catch (Exception e) {
                                Toast.makeText(context, "Erreur lors de la vérification des alertes courantes.\nVeuillez réessayer ou contacter un administrateur.",Toast.LENGTH_LONG).show();
                                e.printStackTrace();
                            }
                        }
                    }
                };
                AlerteDBManager.getLocalActiveAlerts(callback,id_local);

                Thread checkLoading = new Thread(new Runnable(){
                    @Override
                    public void run() {
                        while(!(check_done))
                            Log.d("CheckLocalNbActiveAlerts", "(Waiting for checking to end) -> (check_done : " + check_done + ")");

                        Log.d("CheckLocalNbActiveAlerts", "(Waiting for checking to end) -> (check_done : " + check_done + ")");

                        if(alertes.size() != nbActiveAlert){
                            nbActiveAlert = alertes.size();
                            Intent intent1 = new Intent();
                            intent1.setAction("com.example.Alerte.ModifiedLocalActiveAlertesNumber");
                            intent1.putExtra("nbActiveAlert", nbActiveAlert);
                            sendBroadcast(intent1);
                        }
                    }
                });
                checkLoading.start();
            }
        };

        handler.postDelayed(runnable, 2000);
    }
}
