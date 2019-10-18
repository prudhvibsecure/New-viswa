
package com.adi.exam;

import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Environment;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.adi.exam.callbacks.IItemHandler;
import com.adi.exam.common.AppPreferences;
import com.adi.exam.tasks.ImageProcesser;
import com.adi.exam.utils.TraceUtils;

public class SplashActivityEnc extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_splashscreen);

        if (checkPermission("android.permission.WRITE_EXTERNAL_STORAGE", 100) == 1) {

            processImages();

        }

    }

    private void processImages() {

        try {

            ImageProcesser imageProcesser = new ImageProcesser(this, new IItemHandler() {

                @Override
                public void onFinish(Object results, int requestId) {

                    AppPreferences.getInstance(SplashActivityEnc.this).addBooleanToStore("imagesdone", true, false);

                    Toast.makeText(SplashActivityEnc.this, "Completed", Toast.LENGTH_LONG).show();

                }

                @Override
                public void onError(String errorCode, int requestId) {

                }

                @Override
                public void onProgressChange(int requestId, Long... values) {

                }

            });

            String PATH = Environment.getExternalStorageDirectory() + "/ENFILES/";

            imageProcesser.startProcess(1, PATH);

        } catch (Exception e) {

            TraceUtils.logException(e);

        }

    }

    public int checkPermission(String permissions, int code) {

        if (ActivityCompat.checkSelfPermission(this, permissions) == PackageManager.PERMISSION_GRANTED) {
            return 1;
        }

        if (ActivityCompat.checkSelfPermission(this, permissions) != PackageManager.PERMISSION_GRANTED) {

            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    permissions)) {

                ActivityCompat.requestPermissions(this,
                        new String[]{permissions},
                        code);

                return 0;

            } else {

                ActivityCompat.requestPermissions(this,
                        new String[]{permissions},
                        code);

                return 0;
            }
        }
        return -1;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode) {

            case 100:

                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    processImages();

                } else {

                    showToast(R.string.pd);

                }

                break;

        }

    }

    public void showToast(int text) {

        Toast toast = Toast.makeText(this, text, Toast.LENGTH_SHORT);

        try {

            TextView v = toast.getView().findViewById(android.R.id.message);
            v.setTextColor(Color.WHITE);

        } catch (Exception e) {

            TraceUtils.logException(e);

        } finally {

            toast.show();

        }

    }

}

