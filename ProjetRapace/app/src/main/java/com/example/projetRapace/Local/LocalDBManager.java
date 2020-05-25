package com.example.projetRapace.Local;

import android.util.Log;

import com.example.projetRapace.AccesHTTP;
import com.example.projetRapace.AsyncReponse;
import com.example.projetRapace.Local.Local;
import com.example.projetRapace.Utilisateur;
import com.google.gson.Gson;

import java.util.HashMap;
import java.util.Map;

public class LocalDBManager implements AsyncReponse {
    public static String LOCAL_DB_ADD = "ADD";
    public static String LOCAL_DB_UPDATE = "UPDATE";
    public static String LOCAL_DB_GETBYID = "GET_BY_ID";
    public static String LOCAL_DB_GETALL = "GET_ALL";
    public static String LOCAL_DB_REMOVE = "REMOVE";
    public static String LOCAL_DB_ADD_USER_PERMISSION = "ADD_USER_PERMISSION";
    public static String LOCAL_DB_GET_BY_USER = "GET_BY_USER";
    public static String LOCAL_DB_GET_USERS_PERMISSIONS = "GET_USERS_PERMISSION";
    public static String LOCAL_DB_CHANGE_USERS_PERMISSIONS = "CHANGE_USER_PERMISSION";

    private static final String SERVER_ADDR = "http://51.178.182.46/local_db_access.php";

    public interface LocalDBCallbackInterface {
        void onQueryFinished(String operation, String output);
    }

    private LocalDBManager.LocalDBCallbackInterface callback;
    private static Gson gson = new Gson();
    public LocalDBManager(LocalDBManager.LocalDBCallbackInterface callback){
        super();
        this.callback = callback;
    }

    @Override
    public void processFinish(String output) {
        String[] msg = output.split("%");
        if(msg.length > 1){
            try {
                String operation = msg[0].replace(" ", "");
                Log.d("LocalDBManager", "(processFinish " + msg[0] + ") -> "+msg[1]);
                this.callback.onQueryFinished(operation, msg[1]);
            } catch (Exception e) {
                //Gestion de l'erreur extraction JSON
                Log.d("LocalDBManager", "(processFinish) -> "+e.getMessage());
            }
        }
    }

    public static void getById(LocalDBManager.LocalDBCallbackInterface callback, int id){
        AccesHTTP accesHTTP = new AccesHTTP();;
        accesHTTP.delegate = new LocalDBManager(callback);

        accesHTTP.addParam("operation", LOCAL_DB_GETBYID);
        Map<String,Object> data = new HashMap<>();
        data.put("id", id);
        accesHTTP.addParam("donnees", gson.toJson(data));

        Log.d("LocalDBManager", "(getById) -> " + gson.toJson(data));

        accesHTTP.execute(SERVER_ADDR);
    }

    public static void getAll(LocalDBManager.LocalDBCallbackInterface callback){
        AccesHTTP accesHTTP = new AccesHTTP();;
        accesHTTP.delegate = new LocalDBManager(callback);

        accesHTTP.addParam("operation", LOCAL_DB_GETALL);
        Log.d("LocalDBManager", "(getAll) -> ");

        accesHTTP.execute(SERVER_ADDR);
    }

    public static void addLocal(LocalDBManager.LocalDBCallbackInterface callback, Local l, int user_id){
        AccesHTTP accesHTTP = new AccesHTTP();;
        accesHTTP.delegate = new LocalDBManager(callback);

        accesHTTP.addParam("operation", LOCAL_DB_ADD);
        Map<String,Object> data = new HashMap<>();
        data.put("name", l.getName());
        data.put("address", l.getAddress());
        data.put("num", l.getAddress());
        data.put("id_user", user_id);
        accesHTTP.addParam("donnees", gson.toJson(data));
        Log.d("LocalDBManager", "(addLocal) -> "+ gson.toJson(data));

        accesHTTP.execute(SERVER_ADDR);
    }

    public static void updateLocal(LocalDBManager.LocalDBCallbackInterface callback, Local l){
        AccesHTTP accesHTTP = new AccesHTTP();;
        accesHTTP.delegate = new LocalDBManager(callback);

        accesHTTP.addParam("operation", LOCAL_DB_UPDATE);
        Map<String,Object> data = new HashMap<>();
        data.put("id", l.getId());
        data.put("name", l.getName());
        data.put("address", l.getAddress());
        data.put("num", l.getPhoneNumber());
        accesHTTP.addParam("donnees", gson.toJson(data));
        Log.d("LocalDBManager", "(updateLocal) -> "+ gson.toJson(data));

        accesHTTP.execute(SERVER_ADDR);
    }

    public static void removeLocal(LocalDBManager.LocalDBCallbackInterface callback, Local l){
        AccesHTTP accesHTTP = new AccesHTTP();;
        accesHTTP.delegate = new LocalDBManager(callback);

        accesHTTP.addParam("operation", LOCAL_DB_REMOVE);Map<String,Object> data = new HashMap<>();
        data.put("id", l.getId());
        accesHTTP.addParam("donnees", gson.toJson(data));
        Log.d("LocalDBManager", "(removeLocal) -> "+ l.toJSON().toString());

        accesHTTP.execute(SERVER_ADDR);
    }

    public static void addUserPermissionToLocal(LocalDBManager.LocalDBCallbackInterface callback, Local l, int user_id){
        AccesHTTP accesHTTP = new AccesHTTP();;
        accesHTTP.delegate = new LocalDBManager(callback);

        accesHTTP.addParam("operation", LOCAL_DB_ADD_USER_PERMISSION);Map<String,Object> data = new HashMap<>();
        data.put("id_local", l.getId());
        data.put("id_user", user_id);
        accesHTTP.addParam("donnees", gson.toJson(data));
        Log.d("LocalDBManager", "(addUserPermissionToLocal) -> "+ l.toJSON().toString() + "; user = "+user_id);

        accesHTTP.execute(SERVER_ADDR);
    }

    public static void getByUser(LocalDBManager.LocalDBCallbackInterface callback, int user_id){
        AccesHTTP accesHTTP = new AccesHTTP();;
        accesHTTP.delegate = new LocalDBManager(callback);

        accesHTTP.addParam("operation", LOCAL_DB_GET_BY_USER);Map<String,Object> data = new HashMap<>();
        data.put("id_user", user_id);
        accesHTTP.addParam("donnees", gson.toJson(data));
        Log.d("LocalDBManager", "(getByUser) -> user = " + user_id);

        accesHTTP.execute(SERVER_ADDR);
    }

    public static void getUsersPermissions(LocalDBManager.LocalDBCallbackInterface callback, int id_local){
        AccesHTTP accesHTTP = new AccesHTTP();;
        accesHTTP.delegate = new LocalDBManager(callback);

        accesHTTP.addParam("operation", LOCAL_DB_GET_USERS_PERMISSIONS);Map<String,Object> data = new HashMap<>();
        data.put("id_local", id_local);
        accesHTTP.addParam("donnees", gson.toJson(data));
        Log.d("LocalDBManager", "(getUsersPermissions) -> local = " + id_local);

        accesHTTP.execute(SERVER_ADDR);
    }

    public static void changeUserPermission(LocalDBManager.LocalDBCallbackInterface callback, int id_local, int id_user){
        AccesHTTP accesHTTP = new AccesHTTP();;
        accesHTTP.delegate = new LocalDBManager(callback);

        accesHTTP.addParam("operation", LOCAL_DB_CHANGE_USERS_PERMISSIONS); Map<String,Object> data = new HashMap<>();
        data.put("id_local", id_local);
        data.put("id_user", id_user);
        accesHTTP.addParam("donnees", gson.toJson(data));
        Log.d("LocalDBManager", "(changeUserPermission) -> user = " + id_user);

        accesHTTP.execute(SERVER_ADDR);
    }
}
