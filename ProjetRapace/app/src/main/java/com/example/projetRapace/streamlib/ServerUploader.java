package com.example.projetRapace.streamlib;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import android.os.AsyncTask;
import android.util.Log;
import android.widget.ProgressBar;

import javax.xml.parsers.SAXParserFactory;

public class ServerUploader {


    private static String upLoadServerUri = "http://vps814672.ovh.net/ServerUpload.php";

    private static class UploadFileTask extends AsyncTask<File, Float, Integer> {
        private Exception exception;

        private HttpURLConnection conn = null;
        private DataOutputStream dos = null;
        private String lineEnd = "\r\n";
        private String twoHyphens = "--";
        private String boundary = "*****";
        private int bytesRead, bytesAvailable, bufferSize;
        private byte[] buffer;
        private int maxBufferSize = 1 * 1024 * 1024;

        protected Integer doInBackground(File... sourceFile) {
            try {
                // open a URL connection to the Servlet
                FileInputStream fileInputStream = new FileInputStream(sourceFile[0]);
                URL url = new URL(upLoadServerUri);

                // Open a HTTP  connection to  the URL
                conn = (HttpURLConnection) url.openConnection();
                conn.setDoInput(true); // Allow Inputs
                conn.setDoOutput(true); // Allow Outputs
                conn.setUseCaches(false); // Don't use a Cached Copy
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Connection", "Keep-Alive");
                conn.setRequestProperty("ENCTYPE", "multipart/form-data");
                conn.setRequestProperty("Content-Type", "multipart/form-data;boundary=" + boundary);
                conn.setRequestProperty("uploaded_file", sourceFile[0].getName());

                dos = new DataOutputStream(conn.getOutputStream());

                dos.writeBytes(twoHyphens + boundary + lineEnd);
                dos.writeBytes("Content-Disposition: form-data; name=\"uploaded_file\";filename=\""
                        + sourceFile[0].getName() + "\"" + lineEnd);

                dos.writeBytes(lineEnd);

                // create a buffer of  maximum size
                bytesAvailable = fileInputStream.available();

                bufferSize = Math.min(bytesAvailable, maxBufferSize);
                buffer = new byte[bufferSize];

                // read file and write it into form...
                bytesRead = fileInputStream.read(buffer, 0, bufferSize);

                while (bytesRead > 0) {
                    dos.write(buffer, 0, bufferSize);
                    bytesAvailable = fileInputStream.available();
                    bufferSize = Math.min(bytesAvailable, maxBufferSize);
                    bytesRead = fileInputStream.read(buffer, 0, bufferSize);
                }

                // send multipart form data necesssary after file data...
                dos.writeBytes(lineEnd);
                dos.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd);

                //close the streams
                fileInputStream.close();
                dos.flush();
                dos.close();

                Log.i("uploadFile", "all done");

                //Retour
                return conn.getResponseCode();

            } catch (MalformedURLException ex) {
                ex.printStackTrace();

                //Gestion url script incorrect
                Log.e("Upload file to server", "error: " + ex.getMessage(), ex);
            } catch (Exception e) {
                e.printStackTrace();

                //Gestion erreur lambda
                Log.e("Upload file to server Exception", "Exception : " + e.getMessage(), e);
            }
            return -1;
        }

        protected void onProgressUpdate(Float... progress) {
            //To do
        }

        protected void onPostExecute(Integer serverResponseCode) {
            // Responses from the server (code and message)
            try {
                BufferedReader br;
                if (200 <= conn.getResponseCode() && conn.getResponseCode() <= 299) {
                    br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                } else {
                    br = new BufferedReader(new InputStreamReader(conn.getErrorStream()));
                }

                String echomsg = "\n";
                String strCurrentLine;
                while ((strCurrentLine = br.readLine()) != null) {
                    echomsg += strCurrentLine;
                }

                Log.i("uploadFile", "HTTP Response is : "+ conn.getResponseMessage() + ": " + serverResponseCode + " : " + echomsg);
            } catch (Exception e) {
                e.printStackTrace();
                //Gestion erreur lambda
                Log.e("Upload file to server Exception", "Exception : " + e.getMessage(), e);
            }
        }
    }

    public static void  uploadFile(String sourceFileUri) {
        String fileName = sourceFileUri;
        File sourceFile = new File(sourceFileUri);

        if (!sourceFile.isFile()) { //File to upload incorrect
            Log.e("uploadFile", "Source File not exist :"
            +sourceFileUri);

            //Gestion source file inexistante
        }else{ //Fil to upload correct
            new UploadFileTask().execute(sourceFile);
        }
    }
}
