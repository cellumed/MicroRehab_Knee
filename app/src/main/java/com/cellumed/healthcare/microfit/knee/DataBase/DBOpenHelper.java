package com.cellumed.healthcare.microfit.knee.DataBase;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class DBOpenHelper implements SqlImp {
    private DataBases helper;
    private SQLiteDatabase db;

    public DBOpenHelper(Context mContext){
        helper = new DataBases(mContext);
        db = helper.getWritableDatabase();
    }

    public void close(){
        helper.close();
    }

    public String getField(String table, String field, String where , String orderBy, String limit) {
        String ver = null;
        Cursor mCursor = db.query(false, table, new String[] { field }, where,
                null, null, null, orderBy, limit);
        if (mCursor.moveToFirst())
            ver = mCursor.getString(0);
        mCursor.close();
        return ver;
    }

    public Cursor getField(String table, String[] field, String where , String orderBy, String limit) {
        if(field[0].equals("*")){
            return db.query(false, table, null, where, null, null, null, orderBy,
                    limit);
        }else{
            return db.query(false, table, field, where, null, null, null, orderBy,
                    limit);
        }

    }

    public Cursor getField(String table, String[] field, String where ,String groupBy, String orderBy, String limit) {
        if(field[0].equals("*")){
            return db.query(false, table, null, where, null, groupBy, null, orderBy,
                    limit);
        }else{
            return db.query(false, table, field, where, null, groupBy, null, orderBy,
                    limit);
        }

    }

    public boolean setField(String table, String field, String data) {
        ContentValues args = new ContentValues();
        args.put(field, data);
        return db.update(table, args, null, null) > 0;
    }

    public boolean setField(String table, ContentValues data, String where) {
        return db.update(table, data, where, null) > 0;
    }

    public boolean setRecords(String table, ContentValues data) {
        return db.insert(table, null, data) > 0;
    }
    public int dataDelete(String table, String where){
        return db.delete(table, where, null);
    }
}
