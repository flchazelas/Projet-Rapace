package com.example.projetRapace.Camera;

import com.google.gson.JsonObject;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class Camera {
    private int id;
    private String name;
    private String ip;

    public Camera(String name, String ip){
        this.setName(name); this.setIp(ip);
    }
    public Camera(int id, String name, String ip){
        this.setId(id); this.setName(name); this.setIp(ip);
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public static Camera cameraFromJSON(JSONObject jsonObject){
        Camera c;
        try {
            c = new Camera(jsonObject.getInt("id"),jsonObject.getString("name"),jsonObject.getString("ip"));
            return c;
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return null;
    }

    public static ArrayList<Camera> camerasFromJSON(JSONArray jsonArray){
        ArrayList<Camera> cameras = new ArrayList<>();

        JSONObject object;
        Camera camera;
        for (int i = 0; i < jsonArray.length(); ++i) {
            try {
                object = jsonArray.getJSONObject(i);
                camera = new Camera(object.getInt("id"),object.getString("name"),object.getString("ip"));
                cameras.add(camera);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        return cameras;
    }

    public JSONArray toJSON(){
        ArrayList list = new ArrayList();
        list.add(this.getId());
        list.add(this.getName());
        list.add(this.getIp());
        return new JSONArray(list);
    }
}
