package com.rain_app;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import androidx.annotation.Nullable;

import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;
import java.util.Date;

public class DatabaseHelper extends SQLiteOpenHelper {

    private Context context;
    private static final String DATABASE_NAME = "RainValues.db";
    private static final int DATABASE_VERSION = 1;

    private static final String TABLE_NAME = "my_values";
    private static final String VALUE_ID = "_id";
    private static final String DATE = "date";
    private static final String RAINVALUE = "rain_value";

    public DatabaseHelper(@Nullable Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String query = "CREATE TABLE " + TABLE_NAME +
                        " (" + VALUE_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        DATE + " TEXT, " +
                        RAINVALUE + " TEXT);";
        db.execSQL(query);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }

    void addValue(String date, int rainvalue) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();

        cv.put(DATE, date);
        cv.put(RAINVALUE, rainvalue);
        long result = db.insert(TABLE_NAME, null, cv);
        if(result == -1){
            Toast.makeText(context, "Erreur", Toast.LENGTH_SHORT).show();
        }else{
            Toast.makeText(context, "Ajout effectué", Toast.LENGTH_SHORT).show();
        }
    }

    Cursor readAllData() {
        String query = "SELECT * FROM " + TABLE_NAME + " ORDER BY " + DATE + " DESC";
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = null;
        if(db != null){
            cursor = db.rawQuery(query, null);
        }

        return cursor;
    }

    void deleteOneRow(String row_id) {
        SQLiteDatabase db = this.getWritableDatabase();
        System.out.println("Received row id : "+row_id);
        long result = db.delete(TABLE_NAME, "_id=?", new String[]{row_id});
        if (result == -1){
            Toast.makeText(context, "Erreur de suppression", Toast.LENGTH_SHORT).show();
        }else{
            Toast.makeText(context, "Suppression effectuée", Toast.LENGTH_SHORT).show();
        }
    }

    String getRainMonth(){
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM");
        Date date = new Date(System.currentTimeMillis());
        String currentmthyr = formatter.format(date);

        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT SUM("+RAINVALUE+") " +
                       "FROM "+TABLE_NAME+
                       " WHERE substr("+DATE+", 1, 7) = "+'"'+currentmthyr+'"';

        Cursor cursor = null;
        cursor = db.rawQuery(query, null);
        String query_res = "";
        if (cursor.moveToFirst()) query_res = "" + cursor.getString(0);
        if (query_res.equals("null")){
            query_res = "0";
        }
        return query_res;
    }
}
