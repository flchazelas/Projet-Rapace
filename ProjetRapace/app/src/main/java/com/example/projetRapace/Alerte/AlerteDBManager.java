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
    public static String ALERTE_DB_IGNOREALERTE = "IGNORE_ALERTE";
    public static String ALERTE_DB_GETCURRENTFORCAMERA = "GET_CURRENT_FOR_CAMERA";
    public static String ALERTE_DB_GETLOCALACTIVEALERTS = "GET_LOCAL_ACTIVE_ALERTS";
    public static String ALERTE_DB_GETLOCALNONACTIVEALERTS = "GET_LOCAL_NONACTIVE_ALERTS";

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
        Log.d("AlerteDBManager", "(processFinish = " + output + ")");
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

    public static void addAlerte(AlerteDBCallbackInterface callback, Alerte a, int id_camera){
        AccesHTTP accesHTTP = new AccesHTTP();;
        accesHTTP.delegate = new AlerteDBManager(callback);

        accesHTTP.addParam("operation", ALERTE_DB_ADD);
        Map<String,Object> data = new HashMap<>();
        data.put("isActive", a.isActive() ? 1 : 0);
        data.put("dateDebut", a.getDateDebut());
        data.put("id_camera", id_camera);
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

        Log.d("AlerteDBManager", "(getCurrentForCamera) -> " +  gson.toJson(data));

        accesHTTP.execute(SERVER_ADDR);
    }

    public static void getLocalActiveAlerts(AlerteDBCallbackInterface callback, int id_local){
        AccesHTTP accesHTTP = new AccesHTTP();;
        accesHTTP.delegate = new AlerteDBManager(callback);

        accesHTTP.addParam("operation", ALERTE_DB_GETLOCALACTIVEALERTS);
        Map<String,Object> data = new HashMap<>();
        data.put("id_local", id_local);
        accesHTTP.addParam("donnees", gson.toJson(data));

        Log.d("AlerteDBManager", "(getCurrentForCamera) -> " +  gson.toJson(data));

        accesHTTP.execute(SERVER_ADDR);
    }

    public static void getLocalNActiveAlerts(AlerteDBCallbackInterface callback, int id_local){
        AccesHTTP accesHTTP = new AccesHTTP();;
        accesHTTP.delegate = new AlerteDBManager(callback);

        accesHTTP.addParam("operation", ALERTE_DB_GETLOCALNONACTIVEALERTS);
        Map<String,Object> data = new HashMap<>();
        data.put("id_local", id_local);
        accesHTTP.addParam("donnees", gson.toJson(data));

        Log.d("AlerteDBManager", "(getCurrentForCamera) -> " +  gson.toJson(data));

        accesHTTP.execute(SERVER_ADDR);
    }

    public static void getById(AlerteDBManager.AlerteDBCallbackInterface callback, int id){
        AccesHTTP accesHTTP = new AccesHTTP();;
        accesHTTP.delegate = new AlerteDBManager(callback);

        accesHTTP.addParam("operation", ALERTE_DB_GETBYID);
        Map<String,Object> data = new HashMap<>();
        data.put("id", id);
        accesHTTP.addParam("donnees", gson.toJson(data));

        Log.d("AlerteDBManager", "(getById) -> " + gson.toJson(data));

        accesHTTP.execute(SERVER_ADDR);
    }

    public static void ignoreAlerte(AlerteDBManager.AlerteDBCallbackInterface callback, int id_alerte){
        AccesHTTP accesHTTP = new AccesHTTP();;
        accesHTTP.delegate = new AlerteDBManager(callback);

        accesHTTP.addParam("operation", ALERTE_DB_IGNOREALERTE);
        Map<String,Object> data = new HashMap<>();
        data.put("id_alerte", id_alerte);
        accesHTTP.addParam("donnees", gson.toJson(data));

        Log.d("AlerteDBManager", "(ignoreAlerte) -> " + gson.toJson(data));

        accesHTTP.execute(SERVER_ADDR);
    }
}
