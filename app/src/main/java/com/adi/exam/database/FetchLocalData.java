package com.adi.exam.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.adi.exam.utils.TraceUtils;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.Iterator;

class FetchLocalData {

    private Database database;

    FetchLocalData(Context context) {
        database = new Database(context);
    }

    JSONArray getContent(String table, String[] columns,
                         String whereClause) {

        JSONArray content = new JSONArray();

        try {

            if (database == null)
                return content;

            SQLiteDatabase db = database.getWritableDatabase();

            Cursor cursor = db.query(table, columns, whereClause,
                    null, null, null, null);

            if (cursor != null) {

                while (cursor.moveToNext()) {

                    JSONObject item = new JSONObject();

                    String[] resultsColumns = cursor.getColumnNames();

                    for (String key : resultsColumns) {

                        String value = cursor.getString(cursor
                                .getColumnIndexOrThrow(key));

                        if (value != null)
                            item.put(key, value);

                    }

                    content.put(item);

                }

                cursor.close();

                db.close();

            }

        } catch (Exception e) {

            TraceUtils.logException(e);

        }

        return content;

    }

    long insertMultipleRecords (JSONArray jsonArray, String tableName) {

        long recordCount = -1;

        try {

            if (database != null) {

                SQLiteDatabase db = database.getWritableDatabase();

                for (int i = 0; jsonArray.length() > 0; i++) {

                    ContentValues contentValues = new ContentValues();

                    JSONObject jsonObject = jsonArray.getJSONObject(i);

                    Iterator<String> iter = jsonObject.keys();

                    while (iter.hasNext()) {

                        String key = iter.next();

                        try {

                            String value = jsonObject.optString(key);

                            contentValues.put(key, value);

                        } catch (Exception e) {

                            TraceUtils.logException(e);

                        }

                    }

                    long rawId = db.insert(tableName, null, contentValues);

                    recordCount = recordCount+rawId;

                }

            }

        } catch (Exception e) {

            TraceUtils.logException(e);

        }

        return recordCount;

    }


    void updateImage(String table, String[] columns,
                     String whereClause, String[] columnsData) {

        try {

            if (database != null) {

                SQLiteDatabase db = database.getWritableDatabase();

                ContentValues cv = new ContentValues();

                for (int i = 0; i < columns.length; i++) {

                    cv.put(columns[i], columnsData[i]);

                }

                db.update(table, cv, whereClause, null);

                db.close();

            }

        } catch (Exception e) {

            TraceUtils.logException(e);

        }

    }


}