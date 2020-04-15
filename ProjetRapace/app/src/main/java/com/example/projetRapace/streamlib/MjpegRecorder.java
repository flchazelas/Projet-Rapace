package com.example.projetRapace.streamlib;

import android.graphics.Bitmap;
import android.os.Environment;
import android.os.Handler;
import android.util.Log;
import org.jcodec.api.android.AndroidSequenceEncoder;
import org.jcodec.common.io.FileChannelWrapper;
import org.jcodec.common.io.NIOUtils;
import org.jcodec.common.model.Rational;
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

    public boolean stopRecording(){
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
            saveRecord(bitmaps, recordEnd - recordStart);
            writeLock.unlock();
            return true;
        } catch (Exception e) {
            Log.v("RECORD",e.getMessage());
        }

        writeLock.unlock();
        return false;
    }

    public boolean saveRecord(ArrayList<Bitmap> array, long timeLenght){
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
            return true;
        } catch (Exception e) {
            Log.v("RECORD",e.getMessage());
            writeLock.unlock();
            return false;
        }
    }

    public boolean screenshot(String fileName, Bitmap b){
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
        return true;
    }
}
