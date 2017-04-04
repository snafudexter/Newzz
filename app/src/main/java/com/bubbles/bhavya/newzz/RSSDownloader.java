package com.bubbles.bhavya.newzz;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.PowerManager;
import android.provider.ContactsContract;
import android.util.Log;
import android.widget.Toast;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by Prabh on 4/1/2017.
 */

public class RSSDownloader {

    interface RSSDownloaderNotify{ public void onRSSDownloadDone();}
    public RSSDownloaderNotify rssDownloaderNotify;

    public void setRssDownloaderNotify(RSSDownloaderNotify n)
    {
        this.rssDownloaderNotify = n;
    }

    Handler handler;

    String currentDir;

    String[] list;
    ProgressDialog mProgressDialog;
    DownloadTask downloadTask;

    public RSSDownloader(final Context context)
    {
        handler = new Handler()
        {
            @Override
            public void handleMessage(Message msg) {

                String sMsg = msg.getData().getString("message").toString();
                Toast.makeText(context, "Loading...", Toast.LENGTH_SHORT).show();
                rssDownloaderNotify.onRSSDownloadDone();
            }
        };

        downloadTask  = new DownloadTask(context);
        list = context.getResources().getStringArray(R.array.urls);

        mProgressDialog = new ProgressDialog(context);
        mProgressDialog.setMessage("Please wait...");
        mProgressDialog.setIndeterminate(true);
        mProgressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);

    }

    public void begin()
    {
        downloadTask.execute(list);
    }


    class DownloadTask extends AsyncTask<String, Integer, String>
    {


        private Context context;
        private PowerManager.WakeLock mWakeLock;
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
            mWakeLock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK,
                    getClass().getName());
            mWakeLock.acquire();
            mProgressDialog.show();
        }
        public DownloadTask(Context context) {
            this.context = context;

        }

        @Override
        protected void onPostExecute(String s)
        {
            super.onPostExecute(s);
            mWakeLock.release();
            mProgressDialog.dismiss();
            if (s != null)
            {
                Message message = handler.obtainMessage();
                Bundle b = new Bundle();
                b.putString("message", "download error: " + s);
                message.setData(b);
                message.sendToTarget();
            }
            else
                {
                Message message = handler.obtainMessage();
                Bundle b = new Bundle();
                b.putString("message", "File downloaded");
                message.setData(b);
                message.sendToTarget();
            }

        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
            mProgressDialog.setIndeterminate(false);
            mProgressDialog.setMax(100);
            mProgressDialog.setProgress(values[0]);
        }

        @Override
        protected String doInBackground(String... params)
        {
            for(int i = 0; i < params.length; i++)
            {
                if(params[i].contains("bbc"))
                    currentDir = "bbc";
                else if(params[i].contains("timesof"))
                    currentDir = "toi";

                InputStream input = null;
                OutputStream output = null;
                HttpURLConnection connection = null;
                try {
                    URL url = new URL(params[i]);
                    connection = (HttpURLConnection) url.openConnection();
                    connection.connect();

                    if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
                        Log.e("error", connection.getResponseCode() + " " + connection.getResponseMessage().toString() );
                        return "Server returned HTTP " + connection.getResponseCode()
                                + " " + connection.getResponseMessage();

                    }

                    int fileLength = connection.getContentLength();

                    input = connection.getInputStream();
                    output = new FileOutputStream(context.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS)+ "/" + currentDir + i+".xml");

                    byte data[] = new byte[4096];
                    long total = 0;
                    int count;
                    while ((count = input.read(data)) != -1) {

                        if (isCancelled()) {
                            input.close();
                            return null;
                        }
                        total += count;

                        if (fileLength > 0)
                            publishProgress((int) (total * 100 / fileLength));
                        output.write(data, 0, count);
                    }
                } catch (Exception e) {
                    return e.toString();
                } finally {
                    try {
                        if (output != null)
                            output.close();
                        if (input != null)
                            input.close();
                    } catch (IOException ignored) {
                    }

                    if (connection != null)
                        connection.disconnect();
                }

            }

            return null;
        }
    }
}
