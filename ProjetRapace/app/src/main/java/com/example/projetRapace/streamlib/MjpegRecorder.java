package com.example.projetRapace.streamlib;

import android.Manifest;
import android.graphics.Bitmap;
import android.os.Environment;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

import androidx.core.content.ContextCompat;

import com.example.projetRapace.Camera.Camera;
import com.example.projetRapace.ImageVideo.Image;
import com.example.projetRapace.ImageVideo.ImageDBManager;
import com.example.projetRapace.ImageVideo.Video;
import com.example.projetRapace.ImageVideo.VideoDBManager;

import org.jcodec.api.android.AndroidSequenceEncoder;
import org.jcodec.common.io.FileChannelWrapper;
import org.jcodec.common.io.NIOUtils;
import org.jcodec.common.model.Rational;
import org.json.JSONArray;
import org.json.JSONException;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class MjpegRecorder {
    private final ReadWriteLock readWriteLock = new ReentrantReadWriteLock();
    private final Lock writeLock = readWriteLock.writeLock();

    private AndroidSequenceEncoder encoder;
    private FileChannelWrapper out;
    Handler handler = new Handler();
    private MjpegView streamView;
    public boolean isRecordOn;
    public ArrayList<Bitmap> bitmaps;
    public String fileName;

    long recordStart;
    long recordEnd;

    public static boolean done = false;
    public static boolean result = false;

    private class RecordRun implements Runnable {
        public void run() {
            if (isRecordOn) {
                addFrameToRecord(streamView.getBitmap());
            }
            handler.postDelayed(new RecordRun(),1000/60);
        }
    }

    public MjpegRecorder(MjpegView streamView){
        isRecordOn = false;
        this.streamView = streamView;
        handler.postDelayed(new RecordRun(),1000/60);
    }

    public boolean startRecord(String fileName){
        this.fileName = fileName;
        writeLock.lock();
        if(isRecordOn){
            Log.v("RECORD","Record déja en cours -> impossible d'en lancer un nouveau");
            writeLock.unlock();
            return false;
        }

        try {
            Log.v("RECORD", "Recording Started");
            recordStart = System.currentTimeMillis();
            bitmaps = new ArrayList<>();
            isRecordOn = true;

            writeLock.unlock();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }

        writeLock.unlock();
        return false;
    }

    public boolean addFrameToRecord(Bitmap b){
        writeLock.lock();
        if(!isRecordOn){
            Log.v("RECORD","Aucun record en cours -> impossible d'ajouter de frame au buffer");
            writeLock.unlock();
            return false;
        }
        Log.v("RECORD","Started Writing Frame");
        try {
            bitmaps.add(b);
        } catch (Exception e) {
            e.printStackTrace();
            writeLock.unlock();
            return false;
        }
        Log.v("RECORD","Finished Writing Frame");
        writeLock.unlock();
        return true;
    }

    public boolean stopRecording(int idCam){
        writeLock.lock();
        if(!isRecordOn){
            Log.v("RECORD","Aucun record en cours -> impossible d'arreter de record");
            writeLock.unlock();
            return false;
        }

        try {
            isRecordOn = false;
            recordEnd = System.currentTimeMillis();

            Log.v("RECORD", "Recording finished");
            saveRecord(idCam, bitmaps, recordEnd - recordStart);
            writeLock.unlock();
            return true;
        } catch (Exception e) {
            Log.v("RECORD",e.getMessage());
        }

        writeLock.unlock();
        return false;
    }

    public boolean saveRecord(int id_alerte, ArrayList<Bitmap> array, long timeLenght){
        File path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        File mjpegFile;
        try {
            mjpegFile = new File(path, fileName + ".mjpeg");

            out = NIOUtils.writableFileChannel(mjpegFile.getAbsolutePath());
            encoder = new AndroidSequenceEncoder(out, Rational.R((int)((long) array.size() / (timeLenght/1000)) , 1));

            for(Bitmap b : array)
                encoder.encodeImage(b);
            encoder.finish();
            NIOUtils.closeQuietly(out);

            ServerUploader.uploadFile(mjpegFile.getPath());

            VideoDBManager.VideoDBCallbackInterface callback = new VideoDBManager.VideoDBCallbackInterface() {
                @Override
                public void onQueryFinished(String operation, String output) {
                    Log.d("saveRecord", "(onQueryFinished) -> "+ operation);
                    if(operation.equals(VideoDBManager.VIDEO_DB_ADDFORALERTE)){
                        try {
                            Log.d("saveRecord", "(retour VIDEO_DB_ADDFORALERTE) -> "+ output);
                            if(output == "INSERT_SUCCESSFUL")
                                result = true;
                            else
                                result = false;

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            };
            Video v = new Video("Data/videos/"+mjpegFile.getName());
            VideoDBManager.addVideoForAlerte(callback,id_alerte, v);

            return true;
        } catch (Exception e) {
            Log.v("RECORD",e.getMessage());
            writeLock.unlock();
            return false;
        }
    }

    public boolean screenshot(int id_alerte, String fileName, Bitmap b){
        FileOutputStream fileOutputStream = null;
        File path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);

        File file = new File(path, fileName + ".jpeg");
        try {
            fileOutputStream = new FileOutputStream(file);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return false;
        }
        b.compress(Bitmap.CompressFormat.JPEG, 30, fileOutputStream);

        try {
            fileOutputStream.flush();
            fileOutputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        ServerUploader.uploadFile(file.getPath());

        ImageDBManager.ImageDBCallbackInterface callback = new ImageDBManager.ImageDBCallbackInterface() {
            @Override
            public void onQueryFinished(String operation, String output) {
                Log.d("screenshot", "(onQueryFinished) -> "+ operation);
                if(operation.equals(ImageDBManager.IMAGE_DB_ADDFORALERTE)){
                    try {
                        Log.d("screenshot", "(retour IMAGE_DB_ADD_FOR_CAMERA) -> "+ output);
                        if(output == "INSERT_SUCCESSFUL")
                            result = true; //gestion success
                        else
                            result = false; //gestion error

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        };

        Image i = new Image("Data/images/"+file.getName());
        ImageDBManager.addImageForAlerte(callback,id_alerte,i);

        return true;
    }

    public final void setBool(boolean bool, boolean value){
        bool = value;
    }
}
