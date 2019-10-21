package com.adi.exam.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.text.TextUtils;

import com.adi.exam.utils.TraceUtils;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.Iterator;

public class App_Table {

    private Database database;

    public App_Table(Context context) {

        database = new Database(context);

    }

    public boolean isRecordExits(String iwhereClause, String tableName) {

        try {

            if (database != null) {

                SQLiteDatabase db = database.getWritableDatabase();

                Cursor cursor = db.query(tableName, null, iwhereClause,
                        null, null, null, null);

                if (cursor != null) {

                    if (cursor.getCount() > 0) {

                        cursor.close();
                        db.close();

                        return true;

                    }

                    cursor.close();

                }

                db.close();

            }

        } catch (Exception e) {

            TraceUtils.logException(e);

        }

        return false;

    }

    public void checkNInsertARecord(JSONObject jsonObject, String tableName, String iwhereClause) {

        try {

            boolean isRecordAvailable = isRecordExits(iwhereClause, tableName);

            if (isRecordAvailable) {

                int rowId = deleteRecord(iwhereClause, tableName);

                if (rowId > 0) {

                    insertSingleRecords(jsonObject, tableName);

                }

            } else {

                insertSingleRecords(jsonObject, tableName);

            }

        } catch (Exception e) {

            TraceUtils.logException(e);

        }

    }
    public void checkNInsertARecord_vv(String jsonObject, String tableName, String iwhereClause, String exam_id) {
        try {

            boolean isRecordAvailable = isRecordExits(iwhereClause, tableName);

            if (isRecordAvailable) {

                int rowId = deleteRecord(iwhereClause, tableName);

                if (rowId > 0) {

                    insertSingleRecords_vv(jsonObject, tableName, exam_id);

                }

            } else {

                insertSingleRecords_vv(jsonObject, tableName, exam_id);

            }

        } catch (Exception e) {

            TraceUtils.logException(e);

        }

    }
    public long insertSingleRecords_vv(String jsonObject, String tableName, String exam_id) {

        try {

            if (database != null) {

                SQLiteDatabase db = database.getWritableDatabase();

                ContentValues contentValues = new ContentValues();
                contentValues.put("exam_id", exam_id);
                contentValues.put("questions", jsonObject);

                return db.insert(tableName, null, contentValues);

            }

        } catch (Exception e) {

            TraceUtils.logException(e);

        }

        return -1;

    }
    public void insertMultipleRecords(JSONArray jsonArray, String tableName) {

        try {

            if (database != null) {

                SQLiteDatabase db = database.getWritableDatabase();

                for (int i = 0; i < jsonArray.length(); i++) {

                    ContentValues contentValues = new ContentValues();

                    JSONObject jsonObject = jsonArray.getJSONObject(i);

                    Iterator<String> iter = jsonObject.keys();

                    while (iter.hasNext()) {

                        String key = iter.next();

                        try {

                            String value = jsonObject.optString(key);
                            if (value != null && !TextUtils.isEmpty(value))
                                contentValues.put(key, value);

                        } catch (Exception e) {

                            TraceUtils.logException(e);

                        }

                    }

                    long rawId = db.insert(tableName, null, contentValues);

                }

            }

        } catch (Exception e) {

            TraceUtils.logException(e);

        }

    }

    public long insertSingleRecords(JSONObject jsonObject, String tableName) {

        try {

            if (database != null) {

                SQLiteDatabase db = database.getWritableDatabase();

                ContentValues contentValues = new ContentValues();

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

                return db.insert(tableName, null, contentValues);

            }

        } catch (Exception e) {

            TraceUtils.logException(e);

        }

        return -1;

    }

    public int deleteRecord(String iwhereClause, String tableName) {

        int rowId = -1;

        try {

            if (database != null) {

                SQLiteDatabase db = database.getWritableDatabase();

                rowId = db.delete(tableName, iwhereClause, null);

                db.close();

            }

        } catch (Exception e) {

            TraceUtils.logException(e);

        }

        return rowId;

    }

    public int updateRecord(JSONObject jsonObject, String iwhereClause, String tableName) {

        int rowId = -1;

        try {

            if (database != null) {

                SQLiteDatabase db = database.getWritableDatabase();

                ContentValues contentValues = new ContentValues();

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

                rowId = db.update(tableName, contentValues, iwhereClause, null);

                db.close();

            }

        } catch (Exception e) {

            TraceUtils.logException(e);

        }

        return rowId;

    }

    public String[] getColumnNames(String tableName) {

        try {

            if (database != null) {

                SQLiteDatabase db = database.getWritableDatabase();

                Cursor cursor = db.query(tableName, null, null, null, null, null, null);

                String[] temp = cursor.getColumnNames();

                cursor.close();

                db.close();

                return temp;

            }

        } catch (Exception e) {

            TraceUtils.logException(e);

        }

        return null;

    }

    public void update(String CID, String path, String mimetype, String status) {

        if (database != null) {

            String iwhereClause = "material_data_id=" + CID;

            SQLiteDatabase db = database.getWritableDatabase();

            ContentValues cv = new ContentValues();

            cv.put("savedpath", path);

            cv.put("mimetype", mimetype);

            cv.put("downloadstatus", status);

            db.update("DOWNLOADQUEUE", cv, iwhereClause, null);

            db.close();

        }

    }

    public void updateDownloadStatus(String CID, String status) {

        if (database != null) {

            String iwhereClause = "material_data_id=" + CID;

            SQLiteDatabase db = database.getWritableDatabase();

            ContentValues cv = new ContentValues();

            cv.put("downloadstatus", status);

            db.update("DOWNLOADQUEUE", cv, iwhereClause, null);

            db.close();

        }

    }

    public void updateErrMsgOnFail(String CID, String errMsg) {

        if (database != null) {

            String iwhereClause = "material_data_id=" + CID;

            SQLiteDatabase db = database.getWritableDatabase();

            ContentValues cv = new ContentValues();

            cv.put("errorMsg", errMsg);

            db.update("DOWNLOADQUEUE", cv, iwhereClause, null);

            db.close();

        }

    }

    public void updateOnFailSavedPath(String CID) {

        if (database != null) {

            String iwhereClause = "material_data_id=" + CID;

            SQLiteDatabase db = database.getWritableDatabase();

            ContentValues cv = new ContentValues();

            cv.put("savedpath", "");

            db.update("DOWNLOADQUEUE", cv, iwhereClause, null);

            db.close();

        }

    }

    public void deleteTrackWithCID(String cid, String uniqueId) {

        if (database != null) {

            SQLiteDatabase db = database.getWritableDatabase();

            db.delete("DOWNLOADQUEUE", "material_data_id = " + cid + " AND uniqid = " + uniqueId, null);

            db.close();

        }

    }

    public JSONObject getTrack(String uniqid) {

        JSONObject jsonObject = null;

        try {

            if (database != null) {

                SQLiteDatabase db = database.getWritableDatabase();

                Cursor cursor = db.rawQuery("SELECT * FROM DOWNLOADQUEUE WHERE downloadstatus='Q' ORDER BY ROWID ASC LIMIT 1",
                        null);

                if (cursor != null) {

                    while (cursor.moveToNext()) {

                        jsonObject = new JSONObject();

                        String[] resultsColumns = cursor.getColumnNames();

                        for (String key : resultsColumns) {

                            String value = cursor.getString(cursor
                                    .getColumnIndexOrThrow(key));

                            if (value != null)
                                jsonObject.put(key, value);

                        }

                    }

                    cursor.close();

                }

                db.close();

            }

        } catch (Exception e) {

            TraceUtils.logException(e);

        }

        return jsonObject;

    }

    public JSONObject getAssignmentHistoryList() {

        JSONObject jsonObject = new JSONObject();

        try {

            if (database != null) {
                JSONArray array = new JSONArray();
                SQLiteDatabase db = database.getWritableDatabase();

                Cursor cursor = db.rawQuery("SELECT * FROM ASSIGNMENT",
                        null);

                if (cursor != null) {

                    while (cursor.moveToNext()) {

                        JSONObject obj = new JSONObject();


                        String[] resultsColumns = cursor.getColumnNames();

                        for (String key : resultsColumns) {

                            String value = cursor.getString(cursor
                                    .getColumnIndexOrThrow(key));

                            obj.put(key, value);

                        }
                        array.put(obj);
                    }
                    jsonObject.put("assign_body", array);
                    cursor.close();

                }

                db.close();

            }

        } catch (Exception e) {

            TraceUtils.logException(e);

        }

        return jsonObject;

    }

    public JSONObject getExamsResult(int exam_id, int student_id) {

        JSONObject jsonObject = new JSONObject();
        try {

            if (database != null) {
                JSONArray jsonArray = new JSONArray();
                SQLiteDatabase db = database.getWritableDatabase();

                Cursor cursor = db.rawQuery("SELECT * FROM STUDENTQUESTIONTIME WHERE student_id='" + student_id + "' and exam_id='" + exam_id + "'",
                        null);

                if (cursor != null) {

                    while (cursor.moveToNext()) {

                        JSONObject obb = new JSONObject();

                        String[] resultsColumns = cursor.getColumnNames();

                        for (String key : resultsColumns) {

                            String value = cursor.getString(cursor
                                    .getColumnIndexOrThrow(key));

                            obb.put(key, value);

                        }
                        jsonArray.put(obb);

                    }
                    jsonObject.put("student_question_time", jsonArray);
                    cursor.close();

                }

                db.close();

            }

        } catch (Exception e) {

            TraceUtils.logException(e);

        }
        return jsonObject;
    }

    public JSONObject getAssignmentResult(int assign_id, int student_id) {
        JSONObject jsonObject = new JSONObject();
        try {

            if (database != null) {
                JSONArray jsonArray = new JSONArray();
                SQLiteDatabase db = database.getWritableDatabase();

                Cursor cursor = db.rawQuery("SELECT * FROM ASSIGNMENTSTUDENTQUESTIONRESULTS WHERE assignment_id='" + assign_id + "' and student_id='" + student_id + "'",
                        null);

                if (cursor != null) {

                    while (cursor.moveToNext()) {

                        JSONObject json = new JSONObject();

                        String[] resultsColumns = cursor.getColumnNames();

                        for (String key : resultsColumns) {

                            String value = cursor.getString(cursor
                                    .getColumnIndexOrThrow(key));


                            json.put(key, value);

                        }
                        jsonArray.put(json);

                    }
                    jsonObject.put("student_question_time", jsonArray);
                    cursor.close();

                }

                db.close();

            }

        } catch (Exception e) {

            TraceUtils.logException(e);

        }
        return jsonObject;
    }

    public JSONObject getAssignmentHistoryResult(String id) {
        JSONObject jsonObject = null;
        try {

            if (database != null) {

                SQLiteDatabase db = database.getWritableDatabase();

                Cursor cursor = db.rawQuery("SELECT * FROM ASSIGNMENTRESULTS WHERE assignment_id='" + id + "'",
                        null);

                if (cursor != null) {

                    while (cursor.moveToNext()) {

                        jsonObject = new JSONObject();

                        String[] resultsColumns = cursor.getColumnNames();

                        for (String key : resultsColumns) {

                            String value = cursor.getString(cursor
                                    .getColumnIndexOrThrow(key));

                            if (value != null)
                                jsonObject.put(key, value);

                        }

                    }

                    cursor.close();

                }

                db.close();

            }

        } catch (Exception e) {

            TraceUtils.logException(e);

        }
        return jsonObject;
    }

    public JSONArray getRecords(String whereClause, String tableName) {

        JSONArray jsonArray = new JSONArray();

        try {

            if (database != null) {

                SQLiteDatabase db = database.getWritableDatabase();

                Cursor cursor = db.query(tableName, null, whereClause,
                        null, null, null, null);

                if (cursor != null) {

                    while (cursor.moveToNext()) {

                        JSONObject jsonObject = new JSONObject();

                        String[] resultsColumns = cursor.getColumnNames();

                        for (String key : resultsColumns) {

                            String value = cursor.getString(cursor
                                    .getColumnIndexOrThrow(key));

                            if (value != null)
                                jsonObject.put(key, value);

                        }

                        jsonArray.put(jsonObject);

                    }

                    cursor.close();

                }

                db.close();

            }

        } catch (Exception e) {

            TraceUtils.logException(e);

        }

        return jsonArray;

    }

    public void insertFileData(int exam_id, String fileName, String path) {
        SQLiteDatabase db = null;
        try {
            if (database != null) {
                db = database.getWritableDatabase();
                ContentValues cv = new ContentValues();
                cv.put("exam_id", exam_id);
                cv.put("filename", fileName);
                cv.put("path", path);
                db.insert("FILESDATA", null, cv);
                db.close();
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public Object getResultFiles() {
        JSONObject jsonObject = new JSONObject();
        try {
            if (database != null) {

                JSONArray array = new JSONArray();

                SQLiteDatabase db = database.getWritableDatabase();

                Cursor cursor = db.rawQuery("SELECT * FROM FILESDATA",
                        null);

                if (cursor != null) {

                    while (cursor.moveToNext()) {

                        JSONObject obj = new JSONObject();


                        String[] resultsColumns = cursor.getColumnNames();

                        for (String key : resultsColumns) {

                            String value = cursor.getString(cursor
                                    .getColumnIndexOrThrow(key));

                            obj.put(key, value);

                        }
                        array.put(obj);
                    }
                    jsonObject.put("files_body", array);
                    cursor.close();

                }

                db.close();

            }

        } catch (Exception e) {

            TraceUtils.logException(e);

        }

        return jsonObject;

    }

    public String getFileName(String name) {
        String exam_id = "";
        try {
            if (database != null) {

                String cursor_q = "select * from FILESDATA where filename='" + name + "'";
                SQLiteDatabase db = database.getWritableDatabase();
                Cursor cursor = db
                        .rawQuery(cursor_q,
                                null);
                try {
                    if (null != cursor)
                        if (cursor.getCount() > 0) {
                            cursor.moveToFirst();
                            exam_id = cursor.getString(cursor.getColumnIndex("exam_id"));
                        }
                    cursor.close();
                    db.close();
                } finally {
                    db.close();
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return exam_id;
    }

    public void updateUser(String username, String status) {

        SQLiteDatabase db = null;
        try {
            if (database != null) {
                db = database.getWritableDatabase();

                String iwhereClause = "username='" + username + "'";
                ContentValues cv = new ContentValues();
                cv.put("status", status);
                db.update("STUDENTS", cv, iwhereClause, null);
                db.close();
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    public void checkNInsertImageFile(String file_name, String tableName, String iwhereClause, String status) {
        try {

            boolean isRecordAvailable = isRecordExits(iwhereClause, tableName);

            if (isRecordAvailable) {

                int rowId = deleteRecord(iwhereClause, tableName);

                if (rowId > 0) {

                    insertImageFile(file_name, tableName, status);

                }

            } else {

                insertImageFile(file_name, tableName, status);

            }

        } catch (Exception e) {

            TraceUtils.logException(e);

        }

    }

    public void insertImageFile(String fileName, String tableName, String status) {
        SQLiteDatabase db = null;
        try {
            if (database != null) {
                db = database.getWritableDatabase();
                ContentValues cv = new ContentValues();
                cv.put("image_name", fileName);
                cv.put("status", status);
                db.insert(tableName, null, cv);
                db.close();
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void updateImageFile(String fileName, String tableName, String status) {
        SQLiteDatabase db = null;
        try {
            if (database != null) {
                db = database.getWritableDatabase();
                String iwhereClause = "image_name='" + fileName + "'";
                ContentValues cv = new ContentValues();
                cv.put("status", status);
                db.update(tableName, cv, iwhereClause, null);
                db.close();
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public String getassignFileName(String name) {
        String assign_id = "";
        try {
            if (database != null) {

                String cursor_q = "select * from FILESDATA where filename='" + name + "'";
                SQLiteDatabase db = database.getWritableDatabase();
                Cursor cursor = db
                        .rawQuery(cursor_q,
                                null);
                try {
                    if (null != cursor)
                        if (cursor.getCount() > 0) {
                            cursor.moveToFirst();
                            assign_id = cursor.getString(cursor.getColumnIndex("assignment_id"));
                        }
                    cursor.close();
                    db.close();
                } finally {
                    db.close();
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return assign_id;
    }

    public String getTypeID(String question_paper_id, int section_type, String subject) {
        String id = "";
        try {
            if (database != null) {

                String cursor_q = "select * from STUDENTQUESTIONPAPER where question_paper_id='" + question_paper_id + "' and section='" + section_type + "' and subject='" + subject + "'";
                SQLiteDatabase db = database.getWritableDatabase();
                Cursor cursor = db
                        .rawQuery(cursor_q,
                                null);
                try {
                    if (null != cursor)
                        if (cursor.getCount() > 0) {
                            cursor.moveToFirst();
                            id = cursor.getString(cursor.getColumnIndex("type_id"));
                        }
                    cursor.close();
                    db.close();
                } finally {
                    db.close();
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return id;
    }

    public String getQuestions(String question_paper_id, int section_type, String subject) {
        String qs = "";
        try {
            if (database != null) {

                String cursor_q = "select * from STUDENTQUESTIONPAPER where question_paper_id='" + question_paper_id + "' and section='" + section_type + "' and subject='" + subject + "'";
                SQLiteDatabase db = database.getWritableDatabase();
                Cursor cursor = db
                        .rawQuery(cursor_q,
                                null);
                try {
                    if (null != cursor)
                        if (cursor.getCount() > 0) {
                            cursor.moveToFirst();
                            qs = cursor.getString(cursor.getColumnIndex("no_of_questions"));
                        }
                    cursor.close();
                    db.close();
                } finally {
                    db.close();
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return qs;
    }
    public String getparagrahs(String id) {

        String ph_image = "";
        try {
            if (database != null) {

                String cursor_q = "select * from PARAGRAPS where paragraph_id='" + id + "'";
                SQLiteDatabase db = database.getWritableDatabase();
                Cursor cursor = db
                        .rawQuery(cursor_q,
                                null);
                try {
                    if (null != cursor)
                        if (cursor.getCount() > 0) {
                            cursor.moveToFirst();
                            ph_image = cursor.getString(cursor.getColumnIndex("paragraph"));
                        }
                    cursor.close();
                    db.close();
                } finally {
                    db.close();
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return ph_image;
    } public String getparagrahQuestions(String id) {

        String ph_id = "";
        try {
            if (database != null) {

                String cursor_q = "select * from PARAGRAPS where paragraph_id='" + id + "'";
                SQLiteDatabase db = database.getWritableDatabase();
                Cursor cursor = db
                        .rawQuery(cursor_q,
                                null);
                try {
                    if (null != cursor)
                        if (cursor.getCount() > 0) {
                            cursor.moveToFirst();
                            ph_id = cursor.getString(cursor.getColumnIndex("question_ids"));
                        }
                    cursor.close();
                    db.close();
                } finally {
                    db.close();
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return ph_id;
    }
    public String getAllQuestionView(int exam_id) {

        String qs = "";
        try {
            if (database != null) {

                String cursor_q = "select * from JEEQS where exam_id='" + exam_id + "'";
                SQLiteDatabase db = database.getWritableDatabase();
                Cursor cursor = db
                        .rawQuery(cursor_q,
                                null);
                try {
                    if (null != cursor)
                        if (cursor.getCount() > 0) {
                            cursor.moveToFirst();
                            qs = cursor.getString(cursor.getColumnIndex("questions"));
                        }
                    cursor.close();
                    db.close();
                } finally {
                    db.close();
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return qs;
    }

    public void clearData()
    {
        try {
            if (database != null) {

                SQLiteDatabase db = database.getWritableDatabase();
                db.execSQL("delete from  EXAM");
                db.execSQL("delete from QUESTIONPAPER");
                db.execSQL("delete from STUDENTQUESTIONPAPER");
                db.execSQL("delete from STUDENTEXAMRESULT");
                db.execSQL("delete from STUDENTQUESTIONTIME");
                db.execSQL("delete from STUDENTQUESTIONTIME");

            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void updateDownloadStatus_qs(String qs_id, String status,String table) {

        if (database != null) {

            SQLiteDatabase db = database.getWritableDatabase();

            String iwhereClause = "question_paper_id=" + Integer.parseInt(qs_id);

            ContentValues cv = new ContentValues();

            cv.put("down_status", status);

            db.update(table, cv, iwhereClause, null);

            db.close();

        }

    }

    public String getQuestionFileName(int question_id) {

        String question_name="";

        try {
            if (database != null) {

                String cursor_q = "select * from QUESTIONS where question_id=" + question_id ;
                SQLiteDatabase db = database.getWritableDatabase();
                Cursor cursor = db
                        .rawQuery(cursor_q,
                                null);
                try {
                    if (null != cursor)
                        if (cursor.getCount() > 0) {
                            cursor.moveToFirst();
                            question_name = cursor.getString(cursor.getColumnIndex("question_name"));
                        }
                    cursor.close();
                    db.close();
                } finally {
                    db.close();
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return question_name;

    }
}
