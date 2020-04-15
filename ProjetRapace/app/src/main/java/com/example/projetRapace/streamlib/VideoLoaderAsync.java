package com.example.projetRapace.streamlib;

import android.os.AsyncTask;
import android.util.Log;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import java.net.URI;

public class VideoLoaderAsync implements Runnable {
    MjpegView m_mv;
    String m_url;

    public VideoLoaderAsync(MjpegView mv, String url)
    {
        m_mv = mv;
        m_url = url;
    }

    @Override
    public void run() {
        m_mv.setDisplayMode(MjpegView.SIZE_BEST_FIT);
        m_mv.showFps(false);
        m_mv.setSource(MjpegInputStream.read(m_url));
    }
}