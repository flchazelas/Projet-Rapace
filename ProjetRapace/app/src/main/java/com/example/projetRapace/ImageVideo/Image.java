package com.example.projetRapace.ImageVideo;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.TimeZone;

public class Image {
    private int id;
    private String chemin;
    private String date;

    public Image(String chemin){
        this.setChemin(chemin);

        SimpleDateFormat format = new SimpleDateFormat("YYYY-MM-dd HH:mm:ss");
        format.setTimeZone(TimeZone.getTimeZone("UTC"));
        setDate(format.format( new Date()));
    }
    public Image(int id, String chemin,String date){
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

    public static Image imageFromJSON(JSONArray jsonArray){
        Image c;
        JSONObject object;
        try {
            object = jsonArray.getJSONObject(0);
            c = new Image(object.getInt("id"),object.getString("chemin"),object.getString("date"));
            return c;
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return null;
    }

    public static ArrayList<Image> imagesFromJSON(JSONArray jsonArray){
        ArrayList<Image> images = new ArrayList<>();

        JSONObject object;
        Image image;
        for (int i = 0; i < jsonArray.length(); ++i) {
            try {
                object = jsonArray.getJSONObject(i);
                image = new Image(object.getInt("id"),object.getString("chemin"),object.getString("date"));
                images.add(image);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        return images;
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
