package com.example.projetRapace.ImageVideo;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.TimeZone;

public class Video{
    private int id;
    private String chemin;
    private String date;

    public Video(String chemin){
        this.setChemin(chemin);

        SimpleDateFormat format = new SimpleDateFormat("YYYY-MM-dd HH:mm:ss");
        format.setTimeZone(TimeZone.getTimeZone("UTC"));
        date = format.format( new Date());
    }

    public Video(int id, String chemin,String date){
        this.setId(id); this.setChemin(chemin); this.setDate(date);
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
            c = new Video(object.getInt("id"),object.getString("chemin"),object.getString("date"));
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
                video = new Video(object.getInt("id"),object.getString("chemin"),object.getString("date"));
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

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}
