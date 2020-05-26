package com.example.projetRapace.Appel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.TimeZone;

public class Appel {
    private int id;
    private int id_alerte;
    private int id_initiateur;
    private int id_receveur;
    private String dateAppel;

    public Appel(int id, int id_alerte, int id_initiateur, int id_receveur, String dateAppel){
        this.id = id;
        this.id_alerte = id_alerte;
        this.id_initiateur = id_initiateur;
        this.id_receveur = id_receveur;
        this.dateAppel = dateAppel;
    }

    public Appel(int id, int id_alerte, int id_initiateur, int id_receveur){
        this.id = id;
        this.id_alerte = id_alerte;
        this.id_initiateur = id_initiateur;
        this.id_receveur = id_receveur;

        SimpleDateFormat format = new SimpleDateFormat("YYYY-MM-dd HH:mm:ss");
        format.setTimeZone(TimeZone.getTimeZone("UTC"));
        this.dateAppel = format.format( new Date());
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getId_alerte() {
        return id_alerte;
    }

    public void setId_alerte(int id_alerte) {
        this.id_alerte = id_alerte;
    }

    public int getId_initiateur() {
        return id_initiateur;
    }

    public void setId_initiateur(int id_initiateur) {
        this.id_initiateur = id_initiateur;
    }

    public int getId_receveur() {
        return id_receveur;
    }

    public void setId_receveur(int id_receveur) {
        this.id_receveur = id_receveur;
    }

    public String getDateAppel() {
        return dateAppel;
    }

    public void setDateAppel(String dateAppel) {
        this.dateAppel = dateAppel;
    }

    public static Appel appelFromJSON(JSONObject jsonObject){
        Appel a;
        try {
            a = new Appel(jsonObject.getInt("id"),jsonObject.getInt("id_alerte"),jsonObject.getInt("id_initiateur"),jsonObject.getInt("id_receveur"),jsonObject.getString("dateAppel"));
            return a;
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return null;
    }

    public static ArrayList<Appel> appelsFromJSON(JSONArray jsonArray){
        ArrayList<Appel> appels = new ArrayList<>();

        JSONObject object;
        Appel appel;
        for (int i = 0; i < jsonArray.length(); ++i) {
            try {
                object = jsonArray.getJSONObject(i);
                appel = new Appel(object.getInt("id"),object.getInt("id_alerte"),object.getInt("id_initiateur"),object.getInt("id_receveur"),object.getString("dateAppel"));
                appels.add(appel);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        return appels;
    }

    public JSONArray toJSON(){
        ArrayList list = new ArrayList();
        list.add(this.getId());
        list.add(this.getId_alerte());
        list.add(this.getId_initiateur());
        list.add(this.getId_receveur());
        list.add(this.getDateAppel());
        return new JSONArray(list);
    }
}
