package com.adi.exam.services;

import android.app.IntentService;
import android.app.NotificationManager;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.widget.Toast;

import androidx.core.app.NotificationCompat;

import com.adi.exam.callbacks.DownloadAPIs;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Paths;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Retrofit;

public class ApkFileDownloader extends IntentService {
    private static final String DOWNLOAD_PATH = "my_Download_path";
    private static final String DESTINATION_PATH = "my_Destination_path";
    private static final String FILE_NAME = "my_file_name";

    public ApkFileDownloader() {
        super("ApkFileDownloader");
    }

    private NotificationCompat.Builder notificationBuilder;
    private NotificationManager notificationManager;
    private int totalFileSize;
    private String file_name_nv;


    public static Intent getDownloadService(final Context callingClassContext, final String downloadPath, final String destinationPath, String f_name) {
        return new Intent(callingClassContext, ApkFileDownloader.class)
                .putExtra(DOWNLOAD_PATH, downloadPath)
                .putExtra(DESTINATION_PATH, destinationPath)
                .putExtra(FILE_NAME, f_name);
    }

    @Override
    protected void onHandleIntent(Intent intent) {

        String downloadPath = intent.getStringExtra(DOWNLOAD_PATH);
        String destinationPath = intent.getStringExtra(DESTINATION_PATH);
        String f_name = intent.getStringExtra(FILE_NAME);

        initDownload(downloadPath, destinationPath, f_name);

    }

    private void initDownload(String downloadPath, String destinationPath, String f_name) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://bsecuresoftechsolutions.com/viswa_dev/assets/upload/")
                .build();
        DownloadAPIs retrofitInterface = retrofit.create(DownloadAPIs.class);
        Call<ResponseBody> request = retrofitInterface.downloadFile(downloadPath + f_name);
        try {
            downloadFile(request.execute().body(), f_name, downloadPath);
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();

        }
    }

    private void downloadFile(ResponseBody body, String f_name, String downloadPath)  {

        try {
            int count;
            byte data[] = new byte[1024 * 4];
            long fileSize = body.contentLength();
            InputStream bis = new BufferedInputStream(body.byteStream(), 1024 * 8);
            String externalDirectory = Environment.getExternalStorageDirectory().toString();
            File outputFile = new File(externalDirectory, f_name);
            OutputStream output = new FileOutputStream(outputFile);
            long total = 0;
            long startTime = System.currentTimeMillis();
            int timeCount = 1;
            while ((count = bis.read(data)) != -1) {

                total += count;
                totalFileSize = (int) (fileSize / (Math.pow(1024, 2)));
                double current = Math.round(total / (Math.pow(1024, 2)));

                int progress = (int) ((total * 100) / fileSize);

                long currentTime = System.currentTimeMillis() - startTime;

                Download download = new Download();
                download.setTotalFileSize(totalFileSize);

                if (currentTime > 1000 * timeCount) {

                    download.setCurrentFileSize((int) current);
                    download.setProgress(progress);
                    // sendNotification(download);
                    timeCount++;
                }

                output.write(data, 0, count);
            }
            onDownloadComplete(f_name, downloadPath);
            output.flush();
            output.close();
            bis.close();
        }catch (Exception e){
            e.printStackTrace();
            Toast.makeText(this, "App downloading faild try again...", Toast.LENGTH_LONG).show();
        }

    }

    private void onDownloadComplete(String file_name, String downloadPath) {

        file_name_nv=file_name;
        getstartshow(file_name_nv);
    }

    private void getstartshow(String file_name_nv) {
        try {

            Intent task=new Intent("com.attach.apk");
            task.putExtra("attachname",file_name_nv);
            sendBroadcast(task);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {
      //  notificationManager.cancel(0);
    }

}
