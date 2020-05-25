package com.example.projetRapace;

import java.util.HashMap;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

public class SessionManager {
    // Shared Preferences
    SharedPreferences pref;

    // Editor pour les Shared preferences
    Editor editor;

    // Contexte
    Context _context;

    // Shared pref / mode
    int PRIVATE_MODE = 0;

    // Shared pref / nom du fichier
    private static final String PREF_NAME = "AndroidSession";

    // Toutes les Shared Preferences Keys

    // Boolean IsLogged
    private static final String IS_LOGGED = "IsLogged";

    // Pseudo Utilisateur
    public static final String KEY_PSEUDO = "pseudo";

    // DateAccess Utilisateur
    public static final String KEY_DATE = "date";

    // ID
    public static final String KEY_ID = "id";
    //public static int user_id = -1;

    // Boolean IsAdmin
    public static final String IS_ADMIN = "IsAdmin";

    // Constructeur
    public SessionManager(Context context){
        this._context = context;
        pref = _context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        editor = pref.edit();
    }

    /**
     * Crée la Session
     * */
    public void creationLoginSession(String pseudo, String dateNow, int user_id, boolean isAdmin){
        // Mais le boolean IS_Logged à true
        editor.putBoolean(IS_LOGGED, true);

        // Stock le pseudo
        editor.putString(KEY_PSEUDO, pseudo);

        // Stock la date
        editor.putString(KEY_DATE, dateNow);

        //Stock l'id
        editor.putInt(KEY_ID, user_id);
        //this.user_id = user_id;

        //Stock si admin
        editor.putBoolean(IS_ADMIN, isAdmin);

        // prend en compte les changements
        editor.commit();
    }

    /**
     * Vérifie si l'Utilisateur est logué
     * Si IsLogged est faux redirige vers MainConnexion
     * Sinon rien de plus
     * */
    public void checkLogin(){
        // Check login status
        if(!this.isLoggedIn()){
            // si l'utilisateur n'est pas logué redirige vers MainConnexion
            Intent i = new Intent(_context, MainConnexion.class);

            // Ferme toutes les activités
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

            // Ajout d'un flag pour la nouvelle activité
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

            _context.startActivity(i);
        }
    }

    /**
     * Retourne les données de Session
     * */
    public HashMap<String, String> getDonneesSession(){
        HashMap<String, String> utilisateur = new HashMap<String, String>();

        utilisateur.put(KEY_PSEUDO, pref.getString(KEY_PSEUDO, null));

        utilisateur.put(KEY_DATE, pref.getString(KEY_DATE, null));

        utilisateur.put(KEY_ID, String.valueOf(pref.getInt(KEY_ID, 0)));

        return utilisateur;
    }

    /**
     * Change la valeur isLogged à false et redirige vers MainConnexion
     * */
    public void deconnexionSession(){

        editor.putBoolean(IS_LOGGED, false);
        editor.commit();

        // Redirection vers MainConnexion
        Intent i = new Intent(_context, MainConnexion.class);

        // Ferme toutes les activités
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        // Ajout d'un flag pour la nouvelle activité
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        _context.startActivity(i);
    }

    /**
     * Modifie la dateAccès en Session
     * */
    public void setData(String date){
        // Stock la date
        editor.putString(KEY_DATE, date);

        // prend en compte les changements
        editor.commit();
    }

    /**
     * Modifie le pseudo en Session
     * */
    public void setPseudo(String pseudo){
        // Stock la date
        editor.putString(KEY_PSEUDO, pseudo);

        // prend en compte les changements
        editor.commit();
    }

    /**
     * Nettoie la Session
     * */
    public void clearSharedPref(){

        // Nettoie toutes les Shared Pref
        editor.clear();
        editor.commit();
    }

    /**
     * Vérifie si Utilisateur est logué
     * **/
    public boolean isLoggedIn(){
        return pref.getBoolean(IS_LOGGED, false);
    }

    /**
     * Vérifie si Utilisateur est admin
     * **/
    public boolean isAdmin(){
        return pref.getBoolean(IS_ADMIN, false);
    }
}
