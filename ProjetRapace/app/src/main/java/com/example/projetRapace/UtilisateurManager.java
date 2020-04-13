package com.example.projetRapace;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class UtilisateurManager {

    private static final String TABLE_NAME = "utilisateur";
    public static final String KEY_ID_UTILISATEUR="id_utilisateur";
    public static final String KEY_PSEUDO_UTILISATEUR="pseudo_utilisateur";
    public static final String KEY_MDP_UTILISATEUR="mdp_utilisateur";
    public static final String CREATE_TABLE_UTILISATEUR = "CREATE TABLE "+TABLE_NAME+
            " (" +
            " "+KEY_ID_UTILISATEUR+" INTEGER primary key," +
            " "+KEY_PSEUDO_UTILISATEUR+" TEXT" +
            " "+KEY_MDP_UTILISATEUR+" TEXT" +
            ");";
    private MySQLite maBaseSQLite; // notre gestionnaire du fichier SQLite
    private SQLiteDatabase db;

    // Constructeur
    public UtilisateurManager(Context context)
    {
        maBaseSQLite = MySQLite.getInstance(context);
    }

    public void open()
    {
        //on ouvre la table en lecture/écriture
        db = maBaseSQLite.getWritableDatabase();
    }

    public void close()
    {
        //on ferme l'accès à la BDD
        db.close();
    }

    public long addUtilisateur(Utilisateur utilisateur) {
        // Ajout d'un enregistrement dans la table

        ContentValues values = new ContentValues();
        values.put(KEY_PSEUDO_UTILISATEUR, utilisateur.getPseudo_utilisateur());

        // insert() retourne l'id du nouvel enregistrement inséré, ou -1 en cas d'erreur
        return db.insert(TABLE_NAME,null,values);
    }

    public int modUtilisateur(Utilisateur utilisateur) {
        // modification d'un enregistrement
        // valeur de retour : (int) nombre de lignes affectées par la requête

        ContentValues values = new ContentValues();
        values.put(KEY_PSEUDO_UTILISATEUR, utilisateur.getPseudo_utilisateur());

        String where = KEY_ID_UTILISATEUR+" = ?";
        String[] whereArgs = {utilisateur.getId_utilisateur()+""};

        return db.update(TABLE_NAME, values, where, whereArgs);
    }

    public int supUtilisateur(Utilisateur utilisateur) {
        // suppression d'un enregistrement
        // valeur de retour : (int) nombre de lignes affectées par la clause WHERE, 0 sinon

        String where = KEY_ID_UTILISATEUR+" = ?";
        String[] whereArgs = {utilisateur.getId_utilisateur()+""};

        return db.delete(TABLE_NAME, where, whereArgs);
    }

    public Utilisateur getUtilisateur(int id) {
        // Retourne l'animal dont l'id est passé en paramètre

        Utilisateur u=new Utilisateur("", "");

        Cursor c = db.rawQuery("SELECT * FROM "+TABLE_NAME+" WHERE "+KEY_ID_UTILISATEUR+"="+id, null);
        if (c.moveToFirst()) {
            u.setId_utilisateur(c.getInt(c.getColumnIndex(KEY_ID_UTILISATEUR)));
            u.setPseudo_utilisateur(c.getString(c.getColumnIndex(KEY_PSEUDO_UTILISATEUR)));
            c.close();
        }

        return u;
    }

    public Cursor getUtilisateurs() {
        // sélection de tous les enregistrements de la table
        return db.rawQuery("SELECT * FROM "+TABLE_NAME, null);
    }

}

