package com.example.projetRapace;

import android.content.Context;
import android.os.Debug;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class UtilisateurManagerDistant implements AsyncReponse{

    private Context context;

    //constante
    private static final String SERVER_ADDR = "http://51.178.182.46/serveur_utilisateur.php";

    public UtilisateurManagerDistant(Context c){
        super();
        context = c;
    }

    /**
     * Méthode processFinish(),
     * Gère le retour des requêtes du fichier php : serveur_utilisateur.php
     */
    @Override
    public void processFinish(String output) {
        Log.d("serveur", "---------------"+output);

        //Découpage msg reçut
        String[] msg = output.split("%");
        Log.d("serveur", "---------------"+msg[0]);

        //dans msg[0] : "utilisateur", "enregistrement", "Erreur !"
        //dans msg[1] : reste du msg du retour de la requête

        //si 2 cases
        if(msg.length > 1){

            //Si c'est un enregistrement d'Utilisateur sans erreur,
            //redirection vers la méthode vérifConnexion() de l'activité MainEnregistrement
            //Sinon Affiche Erreur de connexion et redirection vers la méthode echecConnexion() de la même activité
            if(msg[0].equals("enregistrement")){
                Log.d("Enregistrement", "---------------"+msg[1]);
                if(!msg[1].equals("Erreur connexion !")) {
                    ((MainEnregistrement) context).verifConnexion();
                }
                else{
                    Log.d("Erreur", "Erreur de connexion!");
                    ((MainEnregistrement) context).echecConnexion();
                }
            }

            //Si c'est une connexion d'Utilisateur sans erreur,
            //redirection vers la méthode vérifConnexion() de l'activité MainConnexion
            //Sinon Affiche Erreur de conversion JSON et redirection vers la méthode echecConnexion() de la même activité
            else if(msg[0].equals("recherche")){
                Log.d("connexion", "---------------"+msg[1]);
                try {
                    //Décode le msg reçut du retour de la requête en String
                    JSONObject info = new JSONObject(msg[1]);

                    //Récupération des données d'un utilisateur
                    String pseudo = info.getString("username");
                    String mdp = info.getString("password");
                    int id = info.getInt("id");
                    int isActif = info.getInt("isActif");
                    int isAdmin = info.getInt("isAdmin");
                    Utilisateur utilisateur = new Utilisateur(id, pseudo, mdp, isAdmin, isActif);

                    //Affiche l'utilisateur dans la console et redirection
                    Log.d("utilisateur", "---------------"+utilisateur.getPseudo_utilisateur());
                    ((MainConnexion) context).verifConnexion(utilisateur);
                } catch (JSONException e) {
                    Log.d("Erreur", "Erreur de conversion JSON!" + e.toString());
                    ((MainConnexion) context).echecConnexion();
                }
            }

            //Déconnexion de l'Utilisateur
            //redirection vers la méthode clear() du Service RapaceService
            else if(msg[0].equals("deconnexion")){
                Log.d("Deconnexion", "---------------"+msg[1]);
                ((RapaceService) context).clear();
            }

            //Si c'est un changement de la date d'accès de l'Utilisateur sans erreur,
            //redirection vers la méthode verifConnecte() du Service RapaceService
            //Sinon Affiche Erreur de conversion JSON
            else if(msg[0].equals("changeDate")){
                Log.d("ChangeDate", "---------------"+msg[1]);
                if(!msg[1].equals("Erreur connexion !")) {
                    try {
                        //Décode le msg reçut du retour de la requête en String
                        JSONObject info = new JSONObject(msg[1]);

                        ((RapaceService) context).verifConnecte(info.getString("dataAccess"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                else{
                    Log.d("Erreur", "Erreur de connexion!");
                }

            }

            //Récup tous les utilisateurs en BDD sauf celui effectuant la requête,
            //redirection vers la méthode affichage() de MainAdministrationUtilisateur
            //Sinon Affiche Erreur de conversion JSON
            else if(msg[0].equals("recupUtilisateurs")){
                Log.d("RécupUtilisateurs", "---------------"+msg[1]);
                if(!msg[1].equals("Erreur connexion !")) {
                    try {
                        //String[] s = new String[msg.length-1];
                        List<Utilisateur> listeU = new ArrayList<>();
                        for (int i=1; i<msg.length; i++){
                            //Décode le msg reçut du retour de la requête en String
                            JSONObject info = new JSONObject(msg[i]);
                            //s[i-1] = info.getString("username");

                            String pseudo = info.getString("username");
                            String mdp = info.getString("password");
                            int id = info.getInt("id");
                            int isActif = info.getInt("isActif");
                            int isAdmin = info.getInt("isAdmin");
                            Utilisateur utilisateur = new Utilisateur(id, pseudo, mdp, isAdmin, isActif);
                            listeU.add(utilisateur);
                        }

                        ((MainAdministrationUtilisateur) context).affichage(listeU);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                else{
                    Log.d("Erreur", "Erreur de connexion!");
                }
            }

            //Récup l'utilisateur demandé en BDD,
            //redirection vers la méthode affichage() de MainModificationUtilisateur
            //Sinon Affiche Erreur de conversion JSON
            else if(msg[0].equals("recupUtilisateur")){
                Log.d("RécupUtilisateur", "---------------"+msg[1]);
                if(!msg[1].equals("Erreur connexion !")) {
                    try {
                        //Décode le msg reçut du retour de la requête en String
                        JSONObject info = new JSONObject(msg[1]);
                        //s[i-1] = info.getString("username");

                        String pseudo = info.getString("username");
                        String mdp = info.getString("password");
                        int id = info.getInt("id");
                        String num = info.getString("numTel");
                        Utilisateur utilisateur = new Utilisateur(id, pseudo, mdp, num);

                        ((MainModificationUtilisateur) context).affichage(utilisateur);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                else{
                    Log.d("Erreur", "Erreur de connexion!");
                }

            }

            //Désactivation de l'Utilisateur
            //lancement de la requête recupUtilisateurs
            else if(msg[0].equals("changeIsActif")){
                Log.d("ChangeIsActif", "---------------"+msg[1]);
                SessionManager session = SessionManager.getInstance(context);
                String pseudo = session.getDonneesSession().get(SessionManager.KEY_PSEUDO);
                Utilisateur u = new Utilisateur(pseudo, "password");
                this.envoi("recupUtilisateurs", u.convertionJSONArray());
            }

            //Utilisateur passe en Admin
            //lancement de la requête recupUtilisateurs
            else if(msg[0].equals("changeIsAdmin")){
                Log.d("ChangeIsAdmin", "---------------"+msg[1]);
                SessionManager session = SessionManager.getInstance(context);
                String pseudo = session.getDonneesSession().get(SessionManager.KEY_PSEUDO);
                Utilisateur u = new Utilisateur(pseudo, "password");
                this.envoi("recupUtilisateurs", u.convertionJSONArray());
            }

            //Update du profil d'un Utilisateur,
            //redirection vers la méthode xhangements() de l'activité MainModifiationUtilisateur
            if(msg[0].equals("updateProfil")){
                if(!msg[1].equals("Erreur update !")) {
                    Log.d("UpdateProfil", "---------------"+msg[1]);
                    try {
                        //Décode le msg reçut du retour de la requête en String
                        JSONObject info = new JSONObject(msg[1]);
                        String pseudo = info.getString("username");
                        String mdp = info.getString("password");
                        int id = info.getInt("id");
                        int isActif = info.getInt("isActif");
                        int isAdmin = info.getInt("isAdmin");
                        Utilisateur utilisateur = new Utilisateur(id, pseudo, mdp, isAdmin, isActif);
                        ((MainModificationUtilisateur) context).changements(utilisateur);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                else{
                    Log.d("Erreur", "Erreur de Update !");
                }
            }

            //Déconnexion de l'Utilisateur
            //redirection vers la méthode clear() du Service RapaceService
            else if(msg[0].equals("supprimerUtilisateur")){
                Log.d("SupprimerUtilisateur", "---------------"+msg[1]);
                SessionManager session = SessionManager.getInstance(context);
                String pseudo = session.getDonneesSession().get(SessionManager.KEY_PSEUDO);
                Utilisateur u = new Utilisateur(pseudo, "password");
                this.envoi("recupUtilisateurs", u.convertionJSONArray());
            }

            //Récup numéro des utilisateurs liés à l'alerte d'une caméra,
            //redirection vers la méthode
            //Sinon Affiche Erreur de conversion JSON
            else if(msg[0].equals("recupNumUtilisateur")){
                Log.d("RécupNumUtilisateur", "---------------"+msg[1]);
                if(!msg[1].equals("Erreur connexion !")) {
                    try {
                        List<Utilisateur> listeU = new ArrayList<>();
                        for (int i=1; i<msg.length; i++){
                            //Décode le msg reçut du retour de la requête en String
                            JSONObject info = new JSONObject(msg[i]);

                            String pseudo = info.getString("username");
                            String mdp = info.getString("password");
                            int id = info.getInt("id");
                            String num = info.getString("numTel");
                            Utilisateur utilisateur = new Utilisateur(id, pseudo, mdp, num);
                            listeU.add(utilisateur);
                        }

                        ((MainAlerteActive) context).recupListeUtilisateurs(listeU);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                else{
                    Log.d("Erreur", "Erreur de connexion!");
                }
            }

            //Si erreur, l'affiche dans la console
            else if(msg[0].equals("Erreur !")){
                Log.d("Erreur", "---------------"+msg[1]);
            }
        }

    }

    /**
     * Récupération des opérations à effectuer dans la BDD et des données à transmettre
     * Délagation de l'envoi via le fichier php : serveur_utilisateur.php à la classe AccesHTTP
     */
    public void envoi(String operation, JSONArray donneesJSON){
        AccesHTTP accesHTTP = new AccesHTTP();

        //lien de délégation
        accesHTTP.delegate = this;

        //ajout paramètres
        accesHTTP.addParam("operation", operation);
        accesHTTP.addParam("donnees", donneesJSON.toString());

        //appel du serveur via doInBackground() de execute() de la classe mère
        accesHTTP.execute(SERVER_ADDR);
    }
}
