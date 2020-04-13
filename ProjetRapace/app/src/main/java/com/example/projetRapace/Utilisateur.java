package com.example.projetRapace;

import org.json.JSONArray;

import java.util.ArrayList;

public class Utilisateur {
    private int id_utilisateur;
    private String pseudo_utilisateur;
    private String mdp_utilisateur;

    // Constructeur
    public Utilisateur(String pseudo, String mdp) {
        this.pseudo_utilisateur=pseudo;
        this.mdp_utilisateur=mdp;
    }

    public int getId_utilisateur() {
        return id_utilisateur;
    }

    public void setId_utilisateur(int id) {
        this.id_utilisateur = id;
    }

    public String getPseudo_utilisateur() {
        return pseudo_utilisateur;
    }

    public void setPseudo_utilisateur(String pseudo) {
        this.pseudo_utilisateur = pseudo;
    }

    public String getMdp_utilisateur() { return mdp_utilisateur; }

    public void setMdp_utilisateur(String mdp_utilisateur) { this.mdp_utilisateur = mdp_utilisateur; }

    /**
     * conversion de l'utilisateur au format JSONArray
     * @return
     */
    public JSONArray convertionJSONArray(){
        ArrayList list = new ArrayList();
        list.add(pseudo_utilisateur);
        list.add(mdp_utilisateur);
        return new JSONArray(list);
    }
}
