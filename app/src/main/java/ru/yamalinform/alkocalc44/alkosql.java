package ru.yamalinform.alkocalc44;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

/**
 * Created by archy on 4/27/16.
 */
public class alkosql extends SQLiteOpenHelper {


    // Database Version
    private static final int DATABASE_VERSION = 5;
    // Database Name
    private static final String DATABASE_NAME = "alkodb";
    private String CREATE_BOTTLES_TABLE;
    private String CREATE_REPORTS_TABLE;

    public alkosql(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // SQL statement to create book table
        CREATE_BOTTLES_TABLE = "CREATE TABLE IF NOT EXISTS bottles ( " +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "sId, "+
                "volume, "+
                "type, "+
                "source, "+
                "done, "+
                "alkach, "+
                "alco,"+
                "sugar,"+
                "peregon,"+
                "date,"+
                "description,"+
                "timestamp"+
                ")";

        CREATE_REPORTS_TABLE = "CREATE TABLE IF NOT EXISTS reports ( " +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "bId, "+
                "alkach, "+
                "stars, "+
                "report, "+
                "date,"+
                "timestamp"+
                ")";

        // create books table
        db.execSQL(CREATE_BOTTLES_TABLE);
        db.execSQL(CREATE_REPORTS_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older books table if existed
        //db.execSQL("DROP TABLE IF EXISTS bottles");

        switch (newVersion) {
            case 3:
                db.execSQL("DROP TABLE IF EXISTS reports");
                Log.d("_SQL", "Upgrade to 3");
                break;
            case 4:
                db.beginTransaction();
                db.execSQL("ALTER TABLE bottles ADD description");
                db.setTransactionSuccessful();
                db.endTransaction();
                Log.d("_SQL", "Upgrade to 4");
                break;
            case 5:
                db.beginTransaction();
                if(oldVersion < 4) {
                    db.execSQL("ALTER TABLE bottles ADD description");
                }
                db.execSQL("UPDATE bottles SET date=date/1000");
                db.setTransactionSuccessful();
                db.endTransaction();
                Log.d("_SQL", "Upgrade to 5");
                break;
        }


        this.onCreate(db);
    }

    private void createTable() {

    }

    private static final String TABLE_BOTTLES = "bottles";

    // Books Table Columns names
    private static final String KEY_ID = "id";
    private static final String KEY_SID = "sId";
    private static final String KEY_VOLUME = "volume";
    private static final String KEY_TYPE = "type";
    private static final String KEY_SOURCE = "source";
    private static final String KEY_ALKACH = "alkach";
    private static final String KEY_DONE = "done";
    private static final String KEY_ALCO = "alco";
    private static final String KEY_SUGAR = "sugar";
    private static final String KEY_PEREGON = "peregon";
    private static final String KEY_DATE = "date";
    private static final String KEY_TIMESTAMP = "timestamp";
    private static final String KEY_DESCR = "description";

    private static final String[] COLUMNS = {KEY_ID,KEY_SID,KEY_VOLUME,KEY_TYPE,KEY_SOURCE,
            KEY_ALKACH,KEY_DONE,KEY_ALCO,KEY_SUGAR,KEY_PEREGON,KEY_DATE,KEY_TIMESTAMP,KEY_DESCR};

    public void addBottle(Bottle bottle){
        //for logging
        Log.d("addBottle", bottle.toString());

        // 1. get reference to writable DB
        SQLiteDatabase db = this.getWritableDatabase();

        // 2. create ContentValues to add key "column"/value
        ContentValues values = new ContentValues();
        values.put(KEY_SID, bottle.getsId());
        values.put(KEY_VOLUME, bottle.getVolume());
        values.put(KEY_TYPE, bottle.getType());
        values.put(KEY_ALKACH, bottle.getAlkach());
        values.put(KEY_ALCO, bottle.getAlco());
        values.put(KEY_SUGAR, bottle.getSugar());
        values.put(KEY_PEREGON, bottle.getPeregon());
        values.put(KEY_DATE, String.valueOf(Math.round(bottle.getDate().getTime()/1000)));
        values.put(KEY_TIMESTAMP, String.valueOf(Math.round(bottle.getTimeStamp().getTime()/1000)));
        values.put(KEY_SOURCE, bottle.getSource().toString());
        values.put(KEY_DESCR, bottle.getDescription());

        // 3. insert
        db.beginTransaction();
        db.insert(TABLE_BOTTLES, // table
                null, //nullColumnHack
                values); // key/value -> keys = column names/ values = column values

        // 4. close
        db.setTransactionSuccessful();
        db.endTransaction();
        db.close();
    }

    public Bottle getBottle(String id){

        // 1. get reference to readable DB
        SQLiteDatabase db = this.getReadableDatabase();

        // 2. build query
        Cursor cursor =
                db.query(TABLE_BOTTLES, // a. table
                        COLUMNS, // b. column names
                        " sId = ?", // c. selections
                        new String[] { id }, // d. selections args
                        null, // e. group by
                        null, // f. having
                        null, // g. order by
                        null); // h. limit

        // 3. if we got results get the first one
        if (cursor != null) {
            Log.d("_SQL", "total selected bottles: " + String.valueOf(cursor.getCount()));
            cursor.moveToFirst();
            Bottle bottle = fillBottle(cursor);
            //log
            Log.d("getBottle("+id+")", bottle.toString());

            // 5. return book
            return bottle;
        }

        return null;
    }

    private Bottle fillBottle(Cursor cursor) {
        Bottle bottle = new Bottle();
        Log.d("_SQL","start fillBottle1");
        bottle.setId(cursor.getInt(0));
        bottle.setsId(cursor.getString(1));
        bottle.setVolume(cursor.getInt(2));
        bottle.setType(cursor.getString(3));
        bottle.setSource(cursor.getString(4));
        bottle.setAlkach(cursor.getString(5));
        bottle.setDone(Boolean.parseBoolean(cursor.getString(6)));
        bottle.setAlco(cursor.getInt(7));
        bottle.setSugar(cursor.getInt(8));
        bottle.setPeregon(cursor.getInt(9));
        Date date = new Date(cursor.getLong(10)*1000);
        bottle.setDate(date);
        bottle.setDescription(cursor.getString(12));
        Log.d("_SQL","end fillBottle");
        //TODO: Add other field

        return bottle;
    }

    public List<Bottle> searchBottles(String filter){

        List<Bottle> bottles = new LinkedList<>();

        // 1. build the query
        String query = "SELECT  * FROM " + TABLE_BOTTLES + " ORDER BY " + KEY_DATE + " DESC";

        // 2. get reference to writable DB
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(query, null);

        // 3. go over each row, build book and add it to list
        Bottle bottle = null;
        if (cursor.moveToFirst()) {
            do {
                bottle = fillBottle(cursor);
                // Add book to books
                bottles.add(bottle);
            } while (cursor.moveToNext());
        }

        Log.d("searchBottles()", bottles.toString());

        // return books
        return bottles;
    }

    public int updateBottle(Bottle bottle) {

        // 1. get reference to writable DB
        SQLiteDatabase db = this.getWritableDatabase();

        // 2. create ContentValues to add key "column"/value
        ContentValues values = new ContentValues();
        values.put(KEY_SID, bottle.getsId());
        values.put(KEY_VOLUME, bottle.getVolume());
        values.put(KEY_TYPE, bottle.getType());
        values.put(KEY_ALKACH, bottle.getAlkach());

        // 3. updating row
        db.beginTransaction();
        int i = db.update(TABLE_BOTTLES, //table
                values, // column/value
                KEY_ID+" = ?", // selections
                new String[] { String.valueOf(bottle.getId()) }); //selection args

        // 4. close
        db.setTransactionSuccessful();
        db.endTransaction();
        db.close();

        return i;

    }

    public void deleteBottle(Bottle bottle) {

        // 1. get reference to writable DB
        SQLiteDatabase db = this.getWritableDatabase();

        // 2. delete
        db.beginTransaction();
        db.delete(TABLE_BOTTLES, //table name
                KEY_ID+" = ?",  // selections
                new String[] { String.valueOf(bottle.getId()) }); //selections args

        // 3. close
        db.setTransactionSuccessful();
        db.endTransaction();
        db.close();

        //log
        Log.d("deleteBottle", bottle.toString());

    }
}
