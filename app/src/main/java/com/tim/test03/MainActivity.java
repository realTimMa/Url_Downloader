package com.tim.test03;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;

import java.io.Closeable;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

public class MainActivity extends AppCompatActivity {
    private static final String TAG =MainActivity.class.getSimpleName() ;
    private Button btndownload;
    private ProgressBar pbdownload;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setViewsListeners();
        findViews();

    }

    private void setViewsListeners() {
        btndownload=findViewById(R.id.btn_download);
        pbdownload=findViewById(R.id.pb_download);
    }

    private void findViews() {
        btndownload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String urlstr="https://dldir1.qq.com/weixin/android/weixin7018android1740_arm64.apk\n";
                new Thread(){
                    @Override
                    public void run() {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                btndownload.setEnabled(false);
                            }
                        });
                        try {
                            downloadAndUpdateUI(urlstr);
                        }catch (Exception e){
                            Log.e(TAG, "download: ",e);
                        }
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                btndownload.setEnabled(true);
                            }
                        });
                    }
                }.start();



            }
        });
    }

    private void downloadAndUpdateUI(String urlstr) {
        if (null==urlstr||urlstr.trim().length()==0){
            return;
        }
        urlstr= urlstr.trim();
        //创建URL
        URL url = null;
        try {
            url = new URL(urlstr);
        } catch (MalformedURLException e) {
            //   e.printStackTrace();
            Log.e(TAG, "downloadAndUpdateUI: ",e);
            return;
        }
        //从URL获取连接URLConnection
        URLConnection urlConnection=null;
        try {
            urlConnection= url.openConnection();
        } catch (IOException e) {
            //   e.printStackTrace();
            Log.e(TAG, "downloadAndUpdateUI: ",e);
            return;
        }
        final long contentlength = urlConnection.getContentLength();
        //从URLConnection获取输入流 将输入流数据存到指定文件
        String name = urlstr.substring(urlstr.lastIndexOf(  "/")+1);
        InputStream is=null;
        OutputStream os = null;
        try {
            is = urlConnection.getInputStream();
            os =new FileOutputStream(getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS)+ File.separator+name);
            //将输入流数据存入输出流
            long currentLength = 0;
            byte[]bs= new byte[1024*1024*2];
            while(true){
                int size=is.read(bs);
                if(size==-1){
                    break;
                }
                os.write(bs,0,size);
                currentLength+=size;
                //更新下载UI
                final long finalCurrentLength = currentLength;
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        updateDownloadUI(contentlength, finalCurrentLength);
                    }
                });


            }
            os.flush();
        } catch (IOException e) {
            // e.printStackTrace();
            Log.e(TAG, "downloadAndUpdateUI: ", e);
        }finally {
            closeCloseable(is);
            closeCloseable(os);
        }
    }
    private void updateDownloadUI(long total,long current){
        if(total<=0){
            return;
        }
        float percent=((float)current)/total;
        Log.i(TAG, "updateDownloadUI: percent="+percent+",total="+total+",current="+current);
        int progress=Math.round(pbdownload.getMax()*percent);
        pbdownload.setProgress(progress);
    }

    private void closeCloseable(Closeable closeable){
        if (null==closeable){
            return;
        }
        try {
            closeable.close();
        } catch (IOException e) {
            //    e.printStackTrace();
            Log.e(TAG, "closeCloseable: ",e );
        }
    }
}