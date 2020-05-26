package com.example.projetRapace.Alerte;

import android.util.Log;

import com.example.projetRapace.AccesHTTP;
import com.example.projetRapace.AsyncReponse;
import com.example.projetRapace.Camera.Camera;
import com.google.gson.Gson;

import java.util.HashMap;
import java.util.Map;

public class AlerteDBManager  implements AsyncReponse {
    public static String ALERTE_DB_ADD = "ADD";
    public static String ALERTE_DB_UPDATE = "UPDATE";
    public static String ALERTE_DB_GETBYID = "GET_BY_ID";
    public static String ALERTE_DB_GETALL = "GET_ALL";
    public static String ALERTE_DB_REMOVE = "REMOVE";
    public static String ALERTE_DB_GETBYCAMERA = "GET_BY_CAMERA";
    public static String ALERTE_DB_GETCURRENTFORCAMERA = "GET_CURRENT_FOR_CAMERA";

    private static final String SERVER_ADDR = "http://51.178.182.46/alerte_db_access.php";

    public interface AlerteDBCallbackInterface {
        void onQueryFinished(String operation, String output);
    }

    private AlerteDBCallbackInterface callback;
    private static Gson gson = new Gson();
    public AlerteDBManager(AlerteDBCallbackInterface callback){
        super();
        this.callback = callback;
    }

    @Override
    public void processFinish(String output) {
        String[] msg = output.split("%");
        if(msg.length > 1){
            try {
                String operation = msg[0].replace(" ", "");
                Log.d("AlerteDBManager", "(processFinish = " + operation + ") -> "+msg[1]);
                this.callback.onQueryFinished(operation, msg[1]);
            } catch (Exception e) {
                //Gestion de l'erreur extraction JSON
                Log.d("AlerteDBManager", "(processFinish) -> "+e.getMessage());
            }
        }
    }

    public static void addAlerte(AlerteDBCallbackInterface callback, Alerte a){
        AccesHTTP accesHTTP = new AccesHTTP();;
        accesHTTP.delegate = new AlerteDBManager(callback);

        accesHTTP.addParam("operation", ALERTE_DB_ADD);
        Map<String,Object> data = new HashMap<>();
        data.put("isActive", a.isActive());
        data.put("dateDebut", a.getDateDebut());
        accesHTTP.addParam("donnees", gson.toJson(data));
        Log.d("AlerteDBManager", "(addAlerte) -> "+ gson.toJson(data));

        accesHTTP.execute(SERVER_ADDR);
    }

    public static void getCurrentForCamera(AlerteDBCallbackInterface callback, int id_camera){
        AccesHTTP accesHTTP = new AccesHTTP();;
        accesHTTP.delegate = new AlerteDBManager(callback);

        accesHTTP.addParam("operation", ALERTE_DB_GETCURRENTFORCAMERA);
        Map<String,Object> data = new HashMap<>();
        data.put("id_camera", id_camera);
        accesHTTP.addParam("donnees", gson.toJson(data));

        Log.d("AlerteDBManager", "(getCurrentForCamera) -> " + gson.toJson(data));

        accesHTTP.execute(SERVER_ADDR);
    }
}
