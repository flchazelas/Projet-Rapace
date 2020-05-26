package com.example.projetRapace.ImageVideo;

import android.util.Log;

import com.example.projetRapace.AccesHTTP;
import com.example.projetRapace.AsyncReponse;
import com.example.projetRapace.Camera.Camera;
import com.google.gson.Gson;

import java.util.HashMap;
import java.util.Map;

public class VideoDBManager  implements AsyncReponse {
    public static String VIDEO_DB_ADD = "ADD";
    public static String VIDEO_DB_GETBYID = "GET_BY_ID";
    public static String VIDEO_DB_GETALL = "GET_ALL";
    public static String VIDEO_DB_REMOVE = "REMOVE";
    public static String VIDEO_DB_ADDFORALERTE = "ADD_FOR_ALERTE";
    public static String VIDEO_DB_GETBYALERTE = "GET_BY_ALERTE";

    private static final String SERVER_ADDR = "http://51.178.182.46/video_db_access.php";

    public interface VideoDBCallbackInterface {
        void onQueryFinished(String operation, String output);
    }

    private VideoDBManager.VideoDBCallbackInterface callback;
    private static Gson gson = new Gson();
    public VideoDBManager(VideoDBManager.VideoDBCallbackInterface callback){
        super();
        this.callback = callback;
    }

    @Override
    public void processFinish(String output) {
        String[] msg = output.split("%");
        if(msg.length > 1){
            try {
                String operation = msg[0].replace(" ", "");
                Log.d("VideoDBManager", "(processFinish) -> "+msg[1]);
                this.callback.onQueryFinished(operation, msg[1]);
            } catch (Exception e) {
                //Gestion de l'erreur extraction JSON
                Log.d("VideoDBManager", "(processFinish) -> "+e.getMessage());
            }
        }
    }

    public static void getById(VideoDBManager.VideoDBCallbackInterface callback, int id){
        AccesHTTP accesHTTP = new AccesHTTP();;
        accesHTTP.delegate = new VideoDBManager(callback);

        accesHTTP.addParam("operation", VIDEO_DB_GETBYID);
        Map<String,Object> data = new HashMap<>();
        data.put("id", id);
        accesHTTP.addParam("donnees", gson.toJson(data));

        Log.d("VideoDBManager", "(getById) -> " + gson.toJson(data));

        accesHTTP.execute(SERVER_ADDR);
    }

    public static void getByAlerte(VideoDBManager.VideoDBCallbackInterface callback, int id_alerte){
        AccesHTTP accesHTTP = new AccesHTTP();;
        accesHTTP.delegate = new VideoDBManager(callback);

        accesHTTP.addParam("operation", VIDEO_DB_GETBYALERTE);
        Map<String,Object> data = new HashMap<>();
        data.put("id_alerte", id_alerte);
        accesHTTP.addParam("donnees", gson.toJson(data));

        Log.d("VideoDBManager", "getByAlerte " + gson.toJson(data));

        accesHTTP.execute(SERVER_ADDR);
    }

    public static void getAll(VideoDBManager.VideoDBCallbackInterface callback){
        AccesHTTP accesHTTP = new AccesHTTP();;
        accesHTTP.delegate = new VideoDBManager(callback);

        accesHTTP.addParam("operation", VIDEO_DB_GETALL);
        Log.d("VideoDBManager", "(getAll) -> ");

        accesHTTP.execute(SERVER_ADDR);
    }

    public static void addVideo(VideoDBManager.VideoDBCallbackInterface callback, Video c){
        AccesHTTP accesHTTP = new AccesHTTP();;
        accesHTTP.delegate = new VideoDBManager(callback);

        accesHTTP.addParam("operation", VIDEO_DB_ADD);
        Map<String,Object> data = new HashMap<>();
        data.put("chemin", c.getChemin());
        accesHTTP.addParam("donnees", gson.toJson(data));
        Log.d("VideoDBManager", "(addVideo) -> "+ gson.toJson(data));

        accesHTTP.execute(SERVER_ADDR);
    }

    public static void addVideoForAlerte(VideoDBManager.VideoDBCallbackInterface callback, int id_alerte, Video v){
        AccesHTTP accesHTTP = new AccesHTTP();;
        accesHTTP.delegate = new VideoDBManager(callback);

        accesHTTP.addParam("operation", VIDEO_DB_ADDFORALERTE);
        Map<String,Object> data = new HashMap<>();
        data.put("id_alerte", id_alerte);
        data.put("chemin", v.getChemin());
        data.put("date", v.getDate());
        accesHTTP.addParam("donnees", gson.toJson(data));
        Log.d("VideoDBManager", "(addVideoForAlerte) -> "+ gson.toJson(data));

        accesHTTP.execute(SERVER_ADDR);
    }

    public static void removeVideo(VideoDBManager.VideoDBCallbackInterface callback, Video c){
        AccesHTTP accesHTTP = new AccesHTTP();;
        accesHTTP.delegate = new VideoDBManager(callback);

        accesHTTP.addParam("operation", VIDEO_DB_REMOVE);Map<String,Object> data = new HashMap<>();
        data.put("id", c.getId());
        accesHTTP.addParam("donnees", gson.toJson(data));
        Log.d("VideoDBManager", "(removeVideo) -> "+ c.toJSON().toString());

        accesHTTP.execute(SERVER_ADDR);
    }
}
