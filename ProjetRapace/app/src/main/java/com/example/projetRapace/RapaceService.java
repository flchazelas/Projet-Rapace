package com.example.projetRapace;

import android.app.Activity;
import android.app.IntentService;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.TimeZone;

public class RapaceService extends IntentService {

    private SessionManager session;
    //Attribut de format de Date
    private SimpleDateFormat format = new SimpleDateFormat("YYYY-MM-dd HH:mm:ss");
    private Date now;

    /**
     * Constructeur du Service
     * */
    public RapaceService() throws ParseException {
        super("RapaceService");
        format.setTimeZone(TimeZone.getTimeZone("UTC"));
        //nowBDD = format.parse("2010-10-15 09:35:57");
    }

    /**
     * Méthode appellée à chaque appel au Service
     * */
    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        // Lancement du Session Manager
        session = SessionManager.getInstance(getApplicationContext());

        //Lancement du ManagerUtilisateur qui gère les requêtes faîtes à la BDD
        UtilisateurManagerDistant m = new UtilisateurManagerDistant(RapaceService.this);

        //Récupération des données de Session
        HashMap<String, String> user = session.getDonneesSession();
        Utilisateur u = new Utilisateur(user.get(SessionManager.KEY_PSEUDO), "test");

        //Log.d("LOGGED : ", String.valueOf(session.isLoggedIn()));

        //Si l'utilisateur est connecté
        if(session.isLoggedIn()){
            Date now = new Date();

            //Récupère la date actuelle et la date en BDD de l'utilisateur
            String dateFormatee = format.format(now);
            //Log.d("DATE : ", dateFormatee);
            String dateFormateeBDD = session.getDonneesSession().get(SessionManager.KEY_DATE);

            //Log.d("DATE BDD : ", dateFormateeBDD);

            //Compare les deux dates, si en BDD inférieure de 1 minutes => deconnexion
            Date nowBDD = null;
            try {
                nowBDD = format.parse(dateFormateeBDD);
                now = format.parse(dateFormatee);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            int t = 5 ;

            //Récupération de la valeur en Entier de chacune des dates pour savoir si l'utilisateur n'est pas actif sur l'application depuis plus de 1min
            int time = now.getDate();
            Log.d("DATE : ", String.valueOf(time));

            int timeBDD = nowBDD.getDate();
            Log.d("DATE BDD : ", String.valueOf(timeBDD));

            //Si la date est le même jour
            //Sinon Requête de déconnexion envoyée
            if(time == timeBDD){
                time = now.getHours();
                Log.d("DATE : ", String.valueOf(time));
                timeBDD = nowBDD.getHours();
                Log.d("DATE BDD : ", String.valueOf(timeBDD));
                if(time == timeBDD){
                    time = now.getMinutes();
                    Log.d("DATE : ", String.valueOf(time));
                    timeBDD = nowBDD.getMinutes();
                    Log.d("DATE BDD : ", String.valueOf(timeBDD));
                    //Si les minutes sont supérieures à 1 => Requête de déconnexion envoyée
                    //Sinon on met à jour la date en BDD
                    if(time > timeBDD + t)
                        m.envoi("deconnexion", u.convertionJSONArray());
                    else{
                        u.setDate(dateFormatee);
                        m.envoi("changeDate", u.convertionJSONArray());
                    }
                }
                else{
                    time = now.getMinutes();
                    Log.d("DATE : ", String.valueOf(time));
                    timeBDD = nowBDD.getMinutes();
                    Log.d("DATE BDD : ", String.valueOf(timeBDD));
                    int total = 60 - timeBDD + time;
                    Log.d("DATE TOTAL : ", String.valueOf(total));
                    if(total > t)
                        m.envoi("deconnexion", u.convertionJSONArray());
                    else{
                        u.setDate(dateFormatee);
                        m.envoi("changeDate", u.convertionJSONArray());
                    }
                }

            }
            else {
                m.envoi("deconnexion", u.convertionJSONArray());
            }
        }
        //Sinon Requête de déconnexion envoyée
        else{
            m.envoi("deconnexion", u.convertionJSONArray());
        }
    }


    /**
     * Vérifie si l'utilisateur est tjrs connecté
     * */
    public void verifConnecte(String s){
        session.setData(s);
    }

    /**
     * Nettoie la Session
     * */
    public void clear(){
        Log.d("RapaceService ", "CLEAR");

        session.clearSharedPref();
    }
}
