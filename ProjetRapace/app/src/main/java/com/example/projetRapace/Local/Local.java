package com.example.projetRapace.Local;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class Local{
    private int id;
    private String name;
    private String address;
    private String phoneNumber;

    private String image; //présent uniquement en local
    public static String defaultImage = "https://fr.zenit.org/wp-content/uploads/2018/05/no-image-icon.png";

    public Local(int id, String name, String address, String phoneNumber){
        this.setId(id);
        this.setName(name);
        this.setAddress(address);
        this.setPhoneNumber(phoneNumber);

        this.setImage(null);
    }

    public Local(String name, String address, String phoneNumber){
        this.setName(name);
        this.setAddress(address);
        this.setPhoneNumber(phoneNumber);

        this.setImage(null);
    }

    /**
     * Getter et Setter du nom et de l'image d'un Local
     * */
    public String getName() {
        return name;
    }

    public void setName(String nom) {
        this.name = nom;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String adress) {
        this.address = adress;
    }

    public static Local localFromJSON(JSONObject jsonObject){
        Local l;
        try {
            l = new Local(jsonObject.getInt("id"),jsonObject.getString("name"),jsonObject.getString("address"), jsonObject.getString("numeroUrgence"));
            if(jsonObject.has("CameraIP"))
                l.setImage(jsonObject.getString("CameraIP"));
            return l;
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return null;
    }

    public static ArrayList<Local> localsFromJSON(JSONArray jsonArray){
        ArrayList<Local> locaux = new ArrayList<>();

        JSONObject object;
        Local l;
        for (int i = 0; i < jsonArray.length(); ++i) {
            try {
                object = jsonArray.getJSONObject(i);
                l = new Local(object.getInt("id"),object.getString("name"),object.getString("address"), object.getString("numeroUrgence"));

                if(object.has("CameraIP"))
                    l.setImage(object.getString("CameraIP"));

                locaux.add(l);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        return locaux;
    }

    public JSONArray toJSON(){
        ArrayList list = new ArrayList();
        list.add(this.getId());
        list.add(this.getName());
        list.add(this.getAddress());
        return new JSONArray(list);
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }
}
