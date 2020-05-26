package com.example.projetRapace.Appel;

import android.util.Log;

import com.example.projetRapace.AccesHTTP;
import com.example.projetRapace.AsyncReponse;
import com.google.gson.Gson;

import java.util.HashMap;
import java.util.Map;

public class AppelDBManager implements AsyncReponse {
    public static String CAMERA_DB_ADD = "ADD";
    public static String CAMERA_DB_GETBYALERTE = "GET_BY_ALERTE";
    private static final String SERVER_ADDR = "http://51.178.182.46/camera_db_access.php";

    public interface AppelDBCallbackInterface {
        void onQueryFinished(String operation, String output);
    }

    private AppelDBCallbackInterface callback;
    private static Gson gson = new Gson();
    public AppelDBManager(AppelDBCallbackInterface callback){
        super();
        this.callback = callback;
    }

    @Override
    public void processFinish(String output) {
        String[] msg = output.split("%");
        if(msg.length > 1){
            try {
                String operation = msg[0].replace(" ", "");
                Log.d("AppelDBManager", "(processFinish = " + operation + ") -> "+msg[1]);
                this.callback.onQueryFinished(operation, msg[1]);
            } catch (Exception e) {
                //Gestion de l'erreur extraction JSON
                Log.d("AppelDBManager", "(processFinish) -> "+e.getMessage());
            }
        }
    }

    public static void addAppel(AppelDBManager.AppelDBCallbackInterface callback, Appel a){
        AccesHTTP accesHTTP = new AccesHTTP();;
        accesHTTP.delegate = new AppelDBManager(callback);

        accesHTTP.addParam("operation", CAMERA_DB_ADD);
        Map<String,Object> data = new HashMap<>();
        data.put("id_alerte", a.getId_alerte());
        data.put("id_initiateur", a.getId_initiateur());
        data.put("id_receveur", a.getId_receveur());
        data.put("dateAppel", a.getDateAppel());
        accesHTTP.addParam("donnees", gson.toJson(data));
        Log.d("AppelDBManager", "(addAppel) -> "+ gson.toJson(data));

        accesHTTP.execute(SERVER_ADDR);
    }

    public static void getByAlerte(AppelDBManager.AppelDBCallbackInterface callback, int id_alerte){
        AccesHTTP accesHTTP = new AccesHTTP();;
        accesHTTP.delegate = new AppelDBManager(callback);

        accesHTTP.addParam("operation", CAMERA_DB_GETBYALERTE);
        Map<String,Object> data = new HashMap<>();
        data.put("id_alerte", id_alerte);
        accesHTTP.addParam("donnees", gson.toJson(data));

        Log.d("AppelDBManager", "(getByAlerte) -> " + gson.toJson(data));

        accesHTTP.execute(SERVER_ADDR);
    }
}
