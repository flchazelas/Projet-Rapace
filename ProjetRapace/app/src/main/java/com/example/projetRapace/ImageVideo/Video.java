package com.example.projetRapace.ImageVideo;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class Video{
    private int id;
    private String chemin;

    public Video(String chemin){
        this.setChemin(chemin);
    }
    public Video(int id, String chemin){
        this.setId(id); this.setChemin(chemin);
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getChemin() {
        return chemin;
    }

    public void setChemin(String name) {
        this.chemin = name;
    }

    public static Video videoFromJSON(JSONArray jsonArray){
        Video c;
        JSONObject object;
        try {
            object = jsonArray.getJSONObject(0);
            c = new Video(object.getInt("id"),object.getString("chemin"));
            return c;
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return null;
    }

    public static ArrayList<Video> videosFromJSON(JSONArray jsonArray){
        ArrayList<Video> videos = new ArrayList<>();

        JSONObject object;
        Video video;
        for (int i = 0; i < jsonArray.length(); ++i) {
            try {
                object = jsonArray.getJSONObject(i);
                video = new Video(object.getInt("id"),object.getString("chemin"));
                videos.add(video);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        return videos;
    }

    public JSONArray toJSON(){
        ArrayList list = new ArrayList();
        list.add(this.getId());
        list.add(this.getChemin());
        return new JSONArray(list);
    }
}
