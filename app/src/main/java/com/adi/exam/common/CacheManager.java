package com.adi.exam.common;

import android.content.Context;

import com.adi.exam.utils.TraceUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Vector;

public class CacheManager {

    private final int CACHE_ITEMS_INTRN = 3;

    private final int CACHE_ITEMS_INTRN_ONLY = 7;

    private final int CACHE_ITEMS_INTRN_TEMP = 4;

    private static CacheManager mCacheManager = null;

    public Vector<String> delFileNames = new Vector<>();

    private CacheManager() {

    }

    public static CacheManager getInstance() {

        if (mCacheManager == null)
            mCacheManager = new CacheManager();

        return mCacheManager;

    }

    private void cacheObjIntrn(Context aContext, String fileName, Object object, int cacheType) {

        fileName = String.valueOf(fileName.hashCode());

        if (cacheType == 4) {
            if (!delFileNames.contains(fileName)) {
                delFileNames.add(fileName);
            }
        }

        FileOutputStream fileOS = null;

        ObjectOutputStream objectOS = null;

        try {

            fileOS = aContext.openFileOutput(fileName, Context.MODE_PRIVATE);

            objectOS = new ObjectOutputStream(fileOS);

            objectOS.writeObject(object);

            objectOS.flush();
        } catch (Exception e) {
            TraceUtils.logException(e);
        } finally {

            try {
                if (fileOS != null)
                    fileOS.close();
                fileOS = null;

                if (objectOS != null)
                    objectOS.close();
                objectOS = null;

            } catch (Exception e) {
                TraceUtils.logException(e);
            }
        }
    }

    public boolean isFileExists(Context aContext, String fileName) {
        try {
            fileName = String.valueOf(fileName.hashCode());

            File iFile = aContext.getFileStreamPath(fileName);

            return iFile.exists();

        } catch (Exception e) {
            TraceUtils.logException(e);
        }
        return false;
    }

    private Object getIntrnObj(Context aContext, String fileName, int cacheType) throws Exception {

        FileInputStream fileIS = null;
        ObjectInputStream objectIS = null;
        Object object = null;

        try {
            fileName = String.valueOf(fileName.hashCode());

            if (cacheType == 4) {
                if (!delFileNames.contains(fileName)) {
                    delFileNames.add(fileName);
                }
            }

            File iFile = aContext.getFileStreamPath(fileName);
            if (iFile.exists()) {
                fileIS = aContext.openFileInput(fileName);
                objectIS = new ObjectInputStream(fileIS);
                object = objectIS.readObject();
                objectIS.close();
                return object;
            }

        } catch (Exception e) {
            TraceUtils.logException(e);
            throw e;
        } finally {

            try {

                if (fileIS != null)
                    fileIS.close();
                fileIS = null;

                if (objectIS != null)
                    objectIS.close();
                objectIS = null;

            } catch (Exception e) {
                TraceUtils.logException(e);
            }
        }

        return null;
    }


    public void setCache(Context aContext, String requestUrl, int aCacheType, Object object) {
        switch (aCacheType) {

            case CACHE_ITEMS_INTRN:
            case CACHE_ITEMS_INTRN_TEMP:
                cacheObjIntrn(aContext, requestUrl, object, aCacheType);
                break;

            case CACHE_ITEMS_INTRN_ONLY:
                cacheObjIntrn(aContext, requestUrl, object, aCacheType);
                break;

            default:
                break;
        }
    }

    public Object getCache(Context aContext, String requestUrl, int aCacheType) {
        Object object = null;
        try {

            switch (aCacheType) {

                case CACHE_ITEMS_INTRN_TEMP:
                case CACHE_ITEMS_INTRN:
                    object = getIntrnObj(aContext, requestUrl, aCacheType);
                    break;

                case CACHE_ITEMS_INTRN_ONLY:
                    break;

                default:
                    break;
            }

        } catch (Exception e) {
            TraceUtils.logException(e);
        }
        return object;
    }

}