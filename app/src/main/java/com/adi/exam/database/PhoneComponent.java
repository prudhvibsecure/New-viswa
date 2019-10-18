package com.adi.exam.database;

import android.content.Context;
import android.os.Handler;

import com.adi.exam.callbacks.IItemHandler;

import org.json.JSONArray;

public class PhoneComponent {

    private Context mContext;

    private IItemHandler dataHandler;

    private int requestId;

    private FetchLocalData fetchLocal = null;

    private Handler handler = new Handler();

    private String[] columns = null;

    private String[] columnsData = null;

    private String whereClause = null;

    public PhoneComponent(IItemHandler handler, Context context, int requestId) {

        this.dataHandler = handler;

        this.requestId = requestId;

        mContext = context;

    }

    public void defineColumns(String[] columns) {

        this.columns = columns;

    }

    public void defineWhereClause(String whereClause) {

        this.whereClause = whereClause;

    }

    public void executeLocalDBInBackground(String table) {

        getLocalDataSource(table);

    }

    public void insertRecords(JSONArray jsonArray, String table) {

        insertMultipleRecords(jsonArray, table);

    }

    private void getLocalDataSource(final String table) {

        new Thread(new Runnable() {

            @Override
            public void run() {

                try {

                    fetchLocal = new FetchLocalData(mContext);

                    JSONArray results = fetchLocal.getContent(table,
                            columns, whereClause);

                    postUserAction(0, results);

                } catch (Exception ex) {

                    postUserAction(-1, null);

                }

            }

        }).start();

    } //returns all records

    private void insertMultipleRecords(final JSONArray jsonArray, final String table) {

        new Thread(new Runnable() {

            @Override
            public void run() {

                try {

                    fetchLocal = new FetchLocalData(mContext);

                    long results = fetchLocal.insertMultipleRecords(jsonArray, table);

                    postUserAction(0, results);

                } catch (Exception ex) {

                    postUserAction(-1, null);

                }

            }

        }).start();

    }

    private void postUserAction(final int action, final Object items) {

        handler.post(new Runnable() {
            @Override
            public void run() {

                showUserActionResult(action, items);

            }

        });
    }

    private void showUserActionResult(int action, Object items) {
        switch (action) {

            case 0:
                dataHandler.onFinish(items, requestId);
                break;

            case -1:
                dataHandler.onError("ERROR", requestId);
                break;

            default:
                break;
        }
    }

    public void defineColumnsData(String[] columnsData) {
        this.columnsData = columnsData;
    }
}
