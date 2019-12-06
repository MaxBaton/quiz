package com.nadershamma.apps.database;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DataBaseHelper extends SQLiteOpenHelper {
   public static final int VERSION = 3;
   public static final String DATABASE_NAME = "results.db";

   public DataBaseHelper(Context context){
       super(context, DATABASE_NAME,null,VERSION);
   }

   @Override
   public void onCreate(SQLiteDatabase db) {
       db.execSQL("create table " + Fields.ResultTable.NAME + "(" +
               Fields.ResultTable.Cols.ID + " integer primary key autoincrement, " +
               Fields.ResultTable.Cols.CATEGORIES + ", " +
               Fields.ResultTable.Cols.BUTTONS + ", " +
               Fields.ResultTable.Cols.GUESSES + ", " +
               Fields.ResultTable.Cols.POINTS + ", " +
               Fields.ResultTable.Cols.TIME +
               ")"
       );
   }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("drop table if exists " + Fields.ResultTable.NAME);
        onCreate(db);
    }

    public Cursor getAllData(){
        String orderPoints = Fields.ResultTable.Cols.POINTS;
        String orderButtons = Fields.ResultTable.Cols.BUTTONS;
        String orderCategories = Fields.ResultTable.Cols.CATEGORIES;
        String orderTime = Fields.ResultTable.Cols.TIME;
        SQLiteDatabase db = this.getWritableDatabase();
        String sort = orderPoints + " DESC" + ", " + orderButtons + " DESC" + ", "
                + orderCategories + " DESC" + ", " + orderTime + " ASC";
        String group = orderPoints + ", " + orderButtons + ", " + orderCategories
                + ", " + orderTime;
        Cursor res = db.query(true,Fields.ResultTable.NAME,null,null,null,
                group,null,sort,"10");
        return res;
    }
}
