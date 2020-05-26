package com.example.projetRapace;

public class Action {

    private String type;
    private String dateEffectuee;
    private String utilisateur;
    private String description;

    public Action(String type, String dateEffectuee, String utilisateur, String description) {
        this.type = type;
        this.dateEffectuee = dateEffectuee;
        this.utilisateur = utilisateur;
        this.description = description;
    }

    public String getType() {
        return type;
    }

    public String getDateEffectuee() {
        return dateEffectuee;
    }

    public String getUtilisateur() {
        return utilisateur;
    }

    public String getDescription() {
        return description;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setDateEffectuee(String dateEffectuee) {
        this.dateEffectuee = dateEffectuee;
    }

    public void setUtilisateur(String utilisateur) {
        this.utilisateur = utilisateur;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
