package com.example.projetRapace;

import android.os.AsyncTask;
import android.util.Log;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.w3c.dom.Entity;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

public class AccesHTTP extends AsyncTask<String, Integer, Long> {

    private ArrayList<NameValuePair> param;
    private String retour = null;
    public AsyncReponse delegate = null;

    /**
     * Constructeur
     */
    public AccesHTTP(){
        param = new ArrayList<NameValuePair>();
    }

    /**
     * Ajout d'un param post
     * @param nom
     * @param valeur
     */
    public void addParam(String nom, String valeur){
        param.add(new BasicNameValuePair(nom, valeur));
    }
    /**
     * Connexion en tâche de fond dans un thread à part
     * @param strings
     * @return null
     */
    @Override
    protected Long doInBackground(String... strings) {
        HttpClient connexionHttp = new DefaultHttpClient();
        HttpPost paramCon = new HttpPost(strings[0]);

        try {
            //encodage des params
            paramCon.setEntity(new UrlEncodedFormEntity(param));

            //connexion et envoie des params et attente de réponse
            HttpResponse reponse = connexionHttp.execute(paramCon);

            //transformation de la réponse
            retour = EntityUtils.toString(reponse.getEntity());

        } catch (UnsupportedEncodingException e) {
            Log.d("Erreur encodage", "---------------"+e.toString());
        } catch (ClientProtocolException e) {
            Log.d("Erreur protocole", "---------------"+e.toString());
        } catch (IOException e) {
            Log.d("Erreur I/O", "---------------"+e.toString());
        }
        return null;
    }

    @Override
    protected void onPostExecute(Long result){
        delegate.processFinish(retour.toString());
    }
}
