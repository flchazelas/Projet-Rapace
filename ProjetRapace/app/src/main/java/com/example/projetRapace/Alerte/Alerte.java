package com.example.projetRapace.Alerte;

import com.example.projetRapace.Camera.Camera;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.TimeZone;

public class Alerte {
    private int id;
    private boolean isActive;
    private String dateDebut;
    private String dateFin;

    public Alerte(int id,boolean isActive, String dateDebut){
        this.id = id;
        this.isActive = isActive;
        this.dateDebut = dateDebut;
    }

    public Alerte(int id,boolean isActive, String dateDebut, String dateFin){
        this.id = id;
        this.isActive = isActive;
        this.dateDebut = dateDebut;
        this.dateFin = dateFin;
    }

    public Alerte(){
        isActive = true;
        SimpleDateFormat format = new SimpleDateFormat("YYYY-MM-dd HH:mm:ss");
        format.setTimeZone(TimeZone.getTimeZone("UTC"));
        dateDebut = format.format( new Date());
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }

    public String getDateDebut() {
        return dateDebut;
    }

    public void setDateDebut(String dateDebut) {
        this.dateDebut = dateDebut;
    }

    public String getDateFin() {
        return dateFin;
    }

    public void setDateFin(String dateFin) {
        this.dateFin = dateFin;
    }

    public static Alerte alerteFromJSON(JSONObject jsonObject){
        Alerte a;
        try {
            a = new Alerte(jsonObject.getInt("id"),jsonObject.getBoolean("isActive"),jsonObject.getString("dateDebut"),jsonObject.getString("dateFin"));
            return a;
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return null;
    }

    public static ArrayList<Alerte> alertesFromJSON(JSONArray jsonArray){
        ArrayList<Alerte> alertes = new ArrayList<>();

        JSONObject object;
        Alerte alerte;
        for (int i = 0; i < jsonArray.length(); ++i) {
            try {
                object = jsonArray.getJSONObject(i);
                alerte = new Alerte(object.getInt("id"),object.getBoolean("isActive"),object.getString("dateDebut"),object.getString("dateFin"));
                alertes.add(alerte);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        return alertes;
    }

    public JSONArray toJSON(){
        ArrayList list = new ArrayList();
        list.add(this.getId());
        list.add(this.isActive());
        list.add(this.getDateDebut());
        list.add(this.getDateFin());
        return new JSONArray(list);
    }
}
