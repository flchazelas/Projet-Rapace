package com.example.projetRapace.ImageVideo;

import android.util.Log;

import com.example.projetRapace.AccesHTTP;
import com.example.projetRapace.AsyncReponse;
import com.example.projetRapace.Camera.Camera;
import com.google.gson.Gson;

import java.util.HashMap;
import java.util.Map;

public class ImageDBManager implements AsyncReponse {
    public static String IMAGE_DB_ADD = "ADD";
    public static String IMAGE_DB_GETBYID = "GET_BY_ID";
    public static String IMAGE_DB_GETALL = "GET_ALL";
    public static String IMAGE_DB_REMOVE = "REMOVE";
    public static String IMAGE_DB_ADDFORCAMERA = "ADD_FOR_CAMERA";
    public static String IMAGE_DB_GETBYCAMERA = "GET_BY_CAMERA";

    private static final String SERVER_ADDR = "http://51.178.182.46/image_db_access.php";

    public interface ImageDBCallbackInterface {
        void onQueryFinished(String operation, String output);
    }

    private ImageDBManager.ImageDBCallbackInterface callback;
    private static Gson gson = new Gson();
    public ImageDBManager(ImageDBManager.ImageDBCallbackInterface callback){
        super();
        this.callback = callback;
    }

    @Override
    public void processFinish(String output) {
        String[] msg = output.split("%");
        if(msg.length > 1){
            try {
                String operation = msg[0].replace(" ", "");
                Log.d("ImageDBManager", "(processFinish) -> "+msg[1]);
                this.callback.onQueryFinished(operation, msg[1]);
            } catch (Exception e) {
                //Gestion de l'erreur extraction JSON
                Log.d("ImageDBManager", "(processFinish) -> "+e.getMessage());
            }
        }
    }

    public static void getById(ImageDBManager.ImageDBCallbackInterface callback, int id){
        AccesHTTP accesHTTP = new AccesHTTP();;
        accesHTTP.delegate = new ImageDBManager(callback);

        accesHTTP.addParam("operation", IMAGE_DB_GETBYID);
        Map<String,Object> data = new HashMap<>();
        data.put("id", id);
        accesHTTP.addParam("donnees", gson.toJson(data));

        Log.d("ImageDBManager", "(getById) -> " + gson.toJson(data));

        accesHTTP.execute(SERVER_ADDR);
    }

    public static void getByCamera(ImageDBManager.ImageDBCallbackInterface callback, int idCam){
        AccesHTTP accesHTTP = new AccesHTTP();;
        accesHTTP.delegate = new ImageDBManager(callback);

        accesHTTP.addParam("operation", IMAGE_DB_GETBYCAMERA);
        Map<String,Object> data = new HashMap<>();
        data.put("id_camera", idCam);
        accesHTTP.addParam("donnees", gson.toJson(data));

        Log.d("ImageDBManager", "(getByCamera) -> " + gson.toJson(data));

        accesHTTP.execute(SERVER_ADDR);
    }

    public static void getAll(ImageDBManager.ImageDBCallbackInterface callback){
        AccesHTTP accesHTTP = new AccesHTTP();;
        accesHTTP.delegate = new ImageDBManager(callback);

        accesHTTP.addParam("operation", IMAGE_DB_GETALL);
        Log.d("ImageDBManager", "(getAll) -> ");

        accesHTTP.execute(SERVER_ADDR);
    }

    public static void addImage(ImageDBManager.ImageDBCallbackInterface callback, Image c){
        AccesHTTP accesHTTP = new AccesHTTP();;
        accesHTTP.delegate = new ImageDBManager(callback);

        accesHTTP.addParam("operation", IMAGE_DB_ADD);
        Map<String,Object> data = new HashMap<>();
        data.put("chemin", c.getChemin());
        accesHTTP.addParam("donnees", gson.toJson(data));
        Log.d("ImageDBManager", "(addImage) -> "+ gson.toJson(data));

        accesHTTP.execute(SERVER_ADDR);
    }

    public static void addImageForCamera(ImageDBManager.ImageDBCallbackInterface callback, int idCam, Image i){
        AccesHTTP accesHTTP = new AccesHTTP();;
        accesHTTP.delegate = new ImageDBManager(callback);

        accesHTTP.addParam("operation", IMAGE_DB_ADDFORCAMERA);
        Map<String,Object> data = new HashMap<>();
        data.put("id_camera", idCam);
        data.put("chemin", i.getChemin());
        accesHTTP.addParam("donnees", gson.toJson(data));
        Log.d("ImageDBManager", "(addImageForCamera) -> "+ gson.toJson(data));

        accesHTTP.execute(SERVER_ADDR);
    }

    public static void removeImage(ImageDBManager.ImageDBCallbackInterface callback, Image c){
        AccesHTTP accesHTTP = new AccesHTTP();;
        accesHTTP.delegate = new ImageDBManager(callback);

        accesHTTP.addParam("operation", IMAGE_DB_REMOVE);Map<String,Object> data = new HashMap<>();
        data.put("id", c.getId());
        accesHTTP.addParam("donnees", gson.toJson(data));
        Log.d("ImageDBManager", "(removeImage) -> "+ c.toJSON().toString());

        accesHTTP.execute(SERVER_ADDR);
    }
}
