package com.example.projetRapace.Camera;

import android.content.Context;
import android.util.Log;

import com.example.projetRapace.AccesHTTP;
import com.example.projetRapace.AsyncReponse;
import com.example.projetRapace.Local.Local;
import com.example.projetRapace.MainConnexion;
import com.example.projetRapace.MainEnregistrement;
import com.example.projetRapace.Utilisateur;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class CameraDBManager implements AsyncReponse {
    public static String CAMERA_DB_ADD = "ADD";
    public static String CAMERA_DB_GETBYID = "GET_BY_ID";
    public static String CAMERA_DB_GETALL = "GET_ALL";
    public static String CAMERA_DB_REMOVE = "REMOVE";
    public static String CAMERA_DB_GETBYLOCAL = "GET_BY_LOCAL";

    private static final String SERVER_ADDR = "http://51.178.182.46/camera_db_access.php";

    public interface CameraDBCallbackInterface {
        void onQueryFinished(String operation, String output);
    }

    private CameraDBCallbackInterface callback;
    private static Gson gson = new Gson();
    public CameraDBManager(CameraDBCallbackInterface callback){
        super();
        this.callback = callback;
    }

    @Override
    public void processFinish(String output) {
        String[] msg = output.split("%");
        if(msg.length > 1){
            try {
                String operation = msg[0].replace(" ", "");
                Log.d("CameraDBManager", "(processFinish) -> "+msg[1]);
                this.callback.onQueryFinished(operation, msg[1]);
            } catch (Exception e) {
                //Gestion de l'erreur extraction JSON
                Log.d("CameraDBManager", "(processFinish) -> "+e.getMessage());
            }
        }
    }

    public static void getById(CameraDBCallbackInterface callback, int id){
        AccesHTTP accesHTTP = new AccesHTTP();;
        accesHTTP.delegate = new CameraDBManager(callback);

        accesHTTP.addParam("operation", CAMERA_DB_GETBYID);
        Map<String,Object> data = new HashMap<>();
        data.put("id", id);
        accesHTTP.addParam("donnees", gson.toJson(data));

        Log.d("CameraDBManager", "(getById) -> " + gson.toJson(data));

        accesHTTP.execute(SERVER_ADDR);
    }

    public static void getAll(CameraDBCallbackInterface callback){
        AccesHTTP accesHTTP = new AccesHTTP();;
        accesHTTP.delegate = new CameraDBManager(callback);

        accesHTTP.addParam("operation", CAMERA_DB_GETALL);
        Log.d("CameraDBManager", "(getAll) -> ");

        accesHTTP.execute(SERVER_ADDR);
    }

    public static void addCamera(CameraDBCallbackInterface callback, Camera c, int id_local){
        AccesHTTP accesHTTP = new AccesHTTP();;
        accesHTTP.delegate = new CameraDBManager(callback);

        accesHTTP.addParam("operation", CAMERA_DB_ADD);
        Map<String,Object> data = new HashMap<>();
        data.put("name", c.getName());
        data.put("ip", c.getIp());
        data.put("id_local", id_local);
        accesHTTP.addParam("donnees", gson.toJson(data));
        Log.d("CameraDBManager", "(addCamera) -> "+ gson.toJson(data));

        accesHTTP.execute(SERVER_ADDR);
    }

    public static void removeCamera(CameraDBCallbackInterface callback, Camera c){
        AccesHTTP accesHTTP = new AccesHTTP();;
        accesHTTP.delegate = new CameraDBManager(callback);

        accesHTTP.addParam("operation", CAMERA_DB_REMOVE);Map<String,Object> data = new HashMap<>();
        data.put("id", c.getId());
        accesHTTP.addParam("donnees", gson.toJson(data));
        Log.d("CameraDBManager", "(removeCamera) -> "+ c.toJSON().toString());

        accesHTTP.execute(SERVER_ADDR);
    }

    public static void getByLocal(CameraDBCallbackInterface callback, int id_local){
        AccesHTTP accesHTTP = new AccesHTTP();;
        accesHTTP.delegate = new CameraDBManager(callback);

        accesHTTP.addParam("operation", CAMERA_DB_GETBYLOCAL);Map<String,Object> data = new HashMap<>();
        data.put("id", id_local);
        accesHTTP.addParam("donnees", gson.toJson(data));
        Log.d("CameraDBManager", "(getById) -> " + id_local);

        accesHTTP.execute(SERVER_ADDR);
    }
}
