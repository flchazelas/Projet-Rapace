package com.example.projetRapace.Alerte;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONArray;

import java.util.ArrayList;

public class CheckLocalAlerteLists  extends IntentService {
    private static CheckLocalAlerteLists instance;
    public Context context = this;
    public Handler handler = null;
    private Runnable runnable;
    private Thread checkLoading;

    private int id_local = -1;
    private boolean isChanged = false;

    private boolean check_actif_done = false;
    private boolean check_nonactif_done = false;
    private ArrayList<Alerte> alertesActives = new ArrayList<>();
    private ArrayList<Alerte> alertesNonActives = new ArrayList<>();

    public CheckLocalAlerteLists() {
        super(CheckLocalAlerteLists.class.getSimpleName());
    }
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
    @Override
    protected void onHandleIntent(Intent intent) {
        this.instance = this;
        isChanged = intent.getBooleanExtra("isChanged",false);
        id_local = intent.getIntExtra("id_local",-1);
        HandlerThread handlerThread = new HandlerThread("background-thread");
        handlerThread.start();
        handler = new Handler(handlerThread.getLooper());
        runnable = new Runnable() {
            public void run() {
                handler.postDelayed(this, 2000);


                final ArrayList<Alerte> buffer_alertesActives = new ArrayList<>();
                final ArrayList<Alerte> buffer_alertesNonActives = new ArrayList<>();
                //test
                check_actif_done = false;
                check_nonactif_done = false;
                AlerteDBManager.AlerteDBCallbackInterface callback = new AlerteDBManager.AlerteDBCallbackInterface() {
                    @Override
                    public void onQueryFinished(String operation, String output) {
                        Log.d("CheckLocalAlerteLists", "(onQueryFinished) -> "+ operation);
                        if(operation.equals(AlerteDBManager.ALERTE_DB_GETLOCALACTIVEALERTS)){
                            try {
                                Log.d("CheckLocalAlerteLists", "(retour ALERTE_DB_GETLOCALACTIVEALERTS) -> "+ output);

                                JSONArray jsonResult = new JSONArray(output);
                                Log.d("CheckLocalAlerteLists", "(retour CAMERA_DB_GETBYLOCAL) -> "+ Alerte.alertesFromJSON(jsonResult));
                                buffer_alertesActives.clear();
                                for(Alerte a : Alerte.alertesFromJSON(jsonResult))
                                    buffer_alertesActives.add(a);
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
                        Log.d("CheckLocalAlerteLists", "(onQueryFinished) -> "+ operation);
                        if(operation.equals(AlerteDBManager.ALERTE_DB_GETLOCALNONACTIVEALERTS)){
                            try {
                                Log.d("CheckLocalAlerteLists", "(retour ALERTE_DB_GETLOCALNONACTIVEALERTS) -> "+ output);

                                JSONArray jsonResult = new JSONArray(output);
                                Log.d("CheckLocalAlerteLists", "(retour CAMERA_DB_GETBYLOCAL) -> "+ Alerte.alertesFromJSON(jsonResult));
                                buffer_alertesNonActives.clear();
                                for(Alerte a : Alerte.alertesFromJSON(jsonResult))
                                    buffer_alertesNonActives.add(a);
                                check_nonactif_done = true;
                            } catch (Exception e) {
                                Toast.makeText(context, "Erreur lors de la vérification des alertes courantes.\nVeuillez réessayer ou contacter un administrateur.",Toast.LENGTH_LONG).show();
                                e.printStackTrace();
                            }
                        }
                    }
                };
                AlerteDBManager.getLocalNActiveAlerts(callback2,id_local);

                checkLoading = new Thread(new Runnable(){
                    @Override
                    public void run() {
                        while(!(check_actif_done && check_nonactif_done)){
                            Log.d("CheckNewAlertService", "(Waiting for checking to end) -> (check_actif_done : " + check_actif_done + ") (check_nonactif_done : "+check_nonactif_done);
                            if (checkLoading.interrupted())
                                return;
                            try {
                                Thread.sleep(500);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                        Log.d("CheckLocalAlerteLists", "(Waiting for checking to end) -> (check_actif_done : " + check_actif_done + ") (check_nonactif_done : "+ check_nonactif_done + ")");

                        if((buffer_alertesNonActives.size() + buffer_alertesActives.size()) == (alertesNonActives.size() + alertesActives.size())){
                            isChanged = true;
                            Intent intent1 = new Intent();
                            intent1.setAction("com.example.Alerte.ModifiedLocalActiveAlertesList");
                            intent1.putExtra("nbActiveAlert", isChanged);
                            sendBroadcast(intent1);
                        }
                    }
                });
                checkLoading.start();
            }
        };

        handler.postDelayed(runnable, 0);
    }


    public void stop(Intent name){
        stopForeground(true);
        handler.removeCallbacks(runnable);
        handler.removeCallbacksAndMessages(runnable);
        checkLoading.interrupt();
        this.stopService(name);
    }

    public static CheckLocalAlerteLists getInstance() {
        return instance;
    }
}