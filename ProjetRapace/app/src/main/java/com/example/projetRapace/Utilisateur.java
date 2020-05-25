package com.example.projetRapace;

import org.json.JSONArray;

import java.util.ArrayList;

public class Utilisateur {
    private int id_utilisateur;
    private String pseudo_utilisateur;
    private String mdp_utilisateur;
    private int isAdmin;
    private int isActif;
    private int phone;

    private boolean autorisation = false;

    /**
     * Constructeur Utilisateur
     */
    public Utilisateur(int id,String pseudo, String mdp, int isAdmin, int isActif) {
        this.id_utilisateur=id;
        this.pseudo_utilisateur=pseudo;
        this.mdp_utilisateur=mdp;
        this.isAdmin = isAdmin;
        this.isActif = isActif;
    }
    public Utilisateur(String pseudo, String mdp, int isAdmin) {
        this.pseudo_utilisateur=pseudo;
        this.mdp_utilisateur=mdp;
        this.isAdmin = isAdmin;
    }

    public Utilisateur(int id, String pseudo, String mdp) {
        this.id_utilisateur = id;
        this.pseudo_utilisateur=pseudo;
        this.mdp_utilisateur=mdp;
    }

    public Utilisateur(int id, String pseudo, String mdp, int phone) {
        this.pseudo_utilisateur=pseudo;
        this.mdp_utilisateur=mdp;
        this.phone = phone;
    }

    public Utilisateur(String pseudo, String mdp) {
        this.pseudo_utilisateur=pseudo;
        this.mdp_utilisateur=mdp;
    }

    /**
     * Getter et Setter du pseudo, mdp et id d'un Utilisateur
     */
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

    public int getIsAdmin() {
        return isAdmin;
    }

    public void setIsAdmin(int isAdmin) {
        this.isAdmin = isAdmin;
    }

    public int getIsActif() {
        return isActif;
    }

    public void setIsActif(int isActif) {
        this.isActif = isActif;
    }

    /**
     * conversion de l'utilisateur au format JSONArray
     * @return JSONArray
     */
    public JSONArray convertionJSONArray(){
        ArrayList list = new ArrayList();
        list.add(pseudo_utilisateur);
        list.add(mdp_utilisateur);
        list.add(id_utilisateur);
        list.add(isActif);
        list.add(isAdmin);
        list.add(phone);
        return new JSONArray(list);
    }

    public boolean isAutorisation() {
        return autorisation;
    }

    public void setAutorisation(boolean autorisation) {
        this.autorisation = autorisation;
    }
}
