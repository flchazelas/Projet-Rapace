package com.example.projetRapace;

public class Local{
    private String nom;
    private String image;

    public Local(String nom, String image){
        this.nom = nom;
        this.image = image;
    }

    /**
     * Getter et Setter du nom et de l'image d'un Local
     * */
    public String getNom() {
        return nom;
    }

    public String getImage() {
        return image;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public void setImage(String image) {
        this.image = image;
    }
}
