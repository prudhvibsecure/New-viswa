package com.adi.exam.tasks;

import android.content.Context;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;

import com.adi.exam.R;
import com.adi.exam.callbacks.IItemHandler;
import com.adi.exam.common.AESEncryptionDecryption;
import com.adi.exam.utils.TraceUtils;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;

import javax.crypto.Cipher;
import javax.crypto.CipherOutputStream;
import javax.crypto.spec.SecretKeySpec;

public class ImageProcesser {

    private Context mContext;

    private IItemHandler callback;

    private int requestId = -1;

    private String PATH = Environment.getExternalStorageDirectory().toString();

    private final String IMGPATH = PATH + "/System/allFiles";

    public ImageProcesser(Context aContext, IItemHandler callback) {

        this.mContext = aContext;

        this.callback = callback;

    }

    public void startProcess(int requestId, final String folderPath) {

        this.requestId = requestId;

        new Thread(new Runnable() {

            @Override
            public void run() {

                try {

                    File allImagesFolder = new File(folderPath);

                    File f = new File(IMGPATH);

                    if (!f.exists()) {

                        f.mkdirs();

                    }
                    if (allImagesFolder.exists()) {

                        if (allImagesFolder.isDirectory()) {

                            File[] imageFile = allImagesFolder.listFiles();

                            if (imageFile != null) {

                                for (File image : imageFile) {

                                    try {

                                        FileOutputStream outStream;

                                        FileInputStream inputStream;

                                        Cipher aes = Cipher.getInstance("ARC4");

                                        aes.init(Cipher.ENCRYPT_MODE, new SecretKeySpec("filepickerapp".getBytes(), "ARC4"));

                                        outStream = new FileOutputStream(IMGPATH + "/" + image.getName());

//                                        CipherOutputStream out = new CipherOutputStream(outStream, aes);
//
//                                        inputStream = new FileInputStream(image);
//
//                                        int bufferSize = 50;
//
//                                        byte[] buffer = new byte[bufferSize];
//
//                                        int bytesRead;
//
//                                        while ((bytesRead = inputStream.read(buffer, 0, bufferSize)) >= 0) {
//
//                                            out.write(buffer, 0, bytesRead);
//
//                                        }
//
//                                        buffer = null;
//
//                                        outStream.close();
//
//                                        inputStream.close();

                                        StringBuilder html_content = new StringBuilder();

                                        try {
                                            BufferedReader br = new BufferedReader(new FileReader(image));
                                            String line;

                                            while ((line = br.readLine()) != null) {
                                                html_content.append(line);
                                                html_content.append('\n');
                                            }
                                            br.close();
                                        }
                                        catch (Exception e) {
                                            e.printStackTrace();
                                        }
                                        String encrypted=AESEncryptionDecryption.encrypt(html_content.toString());
                                        byte[] bytes = encrypted.getBytes();
                                        outStream.write(bytes);
                                        outStream.flush();
                                        outStream.close();
                                    } catch (Exception e) {

                                        TraceUtils.logException(e);

                                        throw e;

                                    } finally {


                                    }

                                }

                            }

                        }

                    }

                    postUserAction(0, "");

                } catch (Exception ex) {

                    TraceUtils.logException(ex);

                    postUserAction(-1, mContext.getString(R.string.snr3));

                }

            }
        }).start();

    }

    private void postUserAction(final int status, final String response) {

        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {

                showUserActionResult(status, response);

            }
        });
    }

    private void showUserActionResult(int status, String response) {

        switch (status) {
            case 0:
                callback.onFinish(response, requestId);
                break;

            case -1:
                callback.onError(response, requestId);
                break;

            default:
                break;
        }

    }


}
