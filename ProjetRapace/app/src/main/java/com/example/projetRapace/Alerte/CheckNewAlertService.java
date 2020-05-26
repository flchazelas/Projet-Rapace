package com.example.projetRapace.Alerte;
import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

public class CheckNewAlertService extends IntentService {
    public Context context = this;
    public Handler handler = null;
    public static Runnable runnable = null;

    private int id_camera = -1;
    private boolean isThereActiveAlert = false;
    private boolean check_done = false;
    private boolean check_result = false;

    public CheckNewAlertService() {
        super(CheckNewAlertService.class.getSimpleName());
    }
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
    @Override
    protected void onHandleIntent(Intent intent) {
        isThereActiveAlert = intent.getBooleanExtra("alertStatus",false);
        id_camera = intent.getIntExtra("id_camera",-1);
        handler = new Handler();
        runnable = new Runnable() {
            public void run() {
                //test
                check_done = false;
                check_result = false;
                AlerteDBManager.AlerteDBCallbackInterface callback = new AlerteDBManager.AlerteDBCallbackInterface() {
                    @Override
                    public void onQueryFinished(String operation, String output) {
                        Log.d("CheckNewAlertService", "(onQueryFinished) -> "+ operation);
                        if(operation.equals(AlerteDBManager.ALERTE_DB_GETCURRENTFORCAMERA)){
                            try {
                                Log.d("CheckNewAlertService", "(retour ALERTE_DB_GETCURRENTFORCAMERA) -> "+ output);
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

                        if(check_result && !isThereActiveAlert){
                            Intent intent1 = new Intent();
                            intent1.setAction("com.example.Alerte.ModifiedAlertStatus");
                            intent1.putExtra("isThereActiveAlert", isThereActiveAlert);
                            sendBroadcast(intent1);
                        }else  if(!check_result && isThereActiveAlert){
                            Intent intent1 = new Intent();
                            intent1.setAction("com.example.Alerte.ModifiedAlertStatus");
                            intent1.putExtra("isThereActiveAlert", isThereActiveAlert);
                            sendBroadcast(intent1);
                        }
                    }
                });
                handler.postDelayed(runnable, 10000);
            }
        };

        handler.postDelayed(runnable, 10000);
    }
}
