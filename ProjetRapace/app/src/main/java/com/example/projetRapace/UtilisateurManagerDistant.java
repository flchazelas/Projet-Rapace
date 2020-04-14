package com.example.projetRapace;

import android.content.Context;
import android.os.Debug;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class UtilisateurManagerDistant implements AsyncReponse{

    private Context context;

    //constante
    private static final String SERVER_ADDR = "http://51.178.182.46/serveur_utilisateur.php";

    public UtilisateurManagerDistant(Context c){
        super();
        context = c;
    }

    /**
     * retour du serveur distant
     * @param output
     */
    @Override
    public void processFinish(String output) {
        Log.d("serveur", "---------------"+output);
        //découpage msg reçut
        String[] msg = output.split("%");
        Log.d("serveur", "---------------"+msg[0]);

        //dans msg[0] : "utilisateur", "enregistrement", "Erreur !"
        //dans msg[1] : reste du msg

        //si 2 cases
        if(msg.length > 1){
            if(msg[0].equals("enregistrement")){
                Log.d("enregistrement", "---------------"+msg[1]);
            }
            else if(msg[0].equals("recherche")){
                Log.d("connexion", "---------------"+msg[1]);
                try {
                    JSONObject info = new JSONObject(msg[1]);
                    String pseudo = info.getString("username");
                    String mdp = info.getString("password");
                    Utilisateur utilisateur = new Utilisateur(pseudo, mdp);
                    Log.d("utilisateur", "---------------"+utilisateur.getPseudo_utilisateur());
                    ((MainConnexion) context).verifConnexion(utilisateur);
                } catch (JSONException e) {
                    Log.d("Erreur", "Conversion JSON impossible!" + e.toString());
                    ((MainConnexion) context).echecConnexion();
                }
            }
            else if(msg[0].equals("utilisateurs")) {
                Log.d("utilisateurs", "---------------" + msg[1]);
                try {
                    JSONObject info = new JSONObject(msg[1]);
                    String pseudo = info.getString("username");
                    String mdp = info.getString("password");
                    Utilisateur utilisateur = new Utilisateur(pseudo, mdp);
                    ((MainEnregistrement) context).retourneUtilisateur(utilisateur);
                } catch (JSONException e) {
                    Log.d("Erreur", "Conversion JSON impossible!" + e.toString());
                }
            }
            else if(msg[0].equals("Erreur !")){
                Log.d("Erreur", "---------------"+msg[1]);
            }
        }

    }

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
