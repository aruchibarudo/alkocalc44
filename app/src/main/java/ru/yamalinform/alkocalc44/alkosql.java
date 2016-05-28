package ru.yamalinform.alkocalc44;

import android.app.Application;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.support.v4.widget.CursorAdapter;
import android.util.Log;
import android.widget.ArrayAdapter;

import org.json.JSONException;

import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

/**
 * Created by archy on 4/27/16.
 */
public class alkosql extends SQLiteOpenHelper {


    // Database Version
    private static final int DATABASE_VERSION = 9;
    public static final String TYPE_ALKO = "ALKO";
    // Database Name
    private static final String DATABASE_NAME = "alkodb";
    private String CREATE_BOTTLES_TABLE;
    private String CREATE_REPORTS_TABLE;
    private String CREATE_DICT_TABLE;
    private Context context;
    //private SQLiteDatabase db;

    public alkosql(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context = context;
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
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "bId,"+
                "alkach,"+
                "stars,"+
                "report,"+
                "date,"+
                "timestamp"+
                ")";

        CREATE_DICT_TABLE = "CREATE TABLE IF NOT EXISTS dict ( " +
                "_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "type, "+
                "value"+
                ")";

        // create books table
        db.execSQL(CREATE_BOTTLES_TABLE);
        db.execSQL(CREATE_REPORTS_TABLE);
        db.execSQL(CREATE_DICT_TABLE);
        initDB(db);
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
            case 6:
                db.beginTransaction();
                if(oldVersion < 4) {
                    db.execSQL("ALTER TABLE bottles ADD description");
                }else if (oldVersion < 5) {
                    db.execSQL("UPDATE bottles SET date=date/1000");
                }
                db.execSQL("UPDATE bottles SET type=1");
                db.setTransactionSuccessful();
                db.endTransaction();
                Log.d("_SQL", "Upgrade to 6");
                break;
            case 7:
                db.beginTransaction();
                if(oldVersion < 4) {
                    db.execSQL("ALTER TABLE bottles ADD description");
                }else if (oldVersion < 5) {
                    db.execSQL("UPDATE bottles SET date=date/1000");
                }else if (oldVersion < 6){
                    db.execSQL("UPDATE bottles SET type=1");
                }
                db.setTransactionSuccessful();
                db.endTransaction();
                Log.d("_SQL", "Upgrade to 7");
                break;
            case 9:
                db.beginTransaction();
                if(oldVersion < 4) {
                    db.execSQL("ALTER TABLE bottles ADD description");
                }else if (oldVersion < 5) {
                    db.execSQL("UPDATE bottles SET date=date/1000");
                }else if (oldVersion < 6){
                    db.execSQL("UPDATE bottles SET type=1");
                }
                convertSrc(db);
                db.setTransactionSuccessful();
                db.endTransaction();
                Log.d("_SQL", "Upgrade to 9");
                break;
        }


        this.onCreate(db);
    }

    private void initDB(SQLiteDatabase db) {
        Cursor cursor = db.query(TABLE_DICT, // a. table
                COLUMNS_DICT, // b. column names
                " type = ?", // c. selections
                new String[] { TYPE_ALKO }, // d. selections args
                null, // e. group by
                null, // f. having
                null, // g. order by
                null); // h. limit

        if(cursor.getCount() == 0){
            db.execSQL(initDict(TYPE_ALKO, "самогон"));
            db.execSQL(initDict(TYPE_ALKO, "коньяк"));
            db.execSQL(initDict(TYPE_ALKO, "граппа"));
            db.execSQL(initDict(TYPE_ALKO, "отрава"));
            db.execSQL(initDict(TYPE_ALKO, "спирт"));
            db.execSQL(initDict(TYPE_ALKO, "портвейн"));
            db.execSQL(initDict(TYPE_ALKO, "вино"));
            db.execSQL(initDict(TYPE_ALKO, "пиво"));
        }
    }

    private String initDict(String type, String value) {
        return "insert into " + TABLE_DICT + "(" + KEY_TYPE +"," + KEY_VALUE + ") values ('" + TYPE_ALKO + "','" + value + "')";
    }

    private static final String TABLE_DICT = "dict";

    // Bottles Table Columns names
    public static final String KEY_DICT_ID = "_id";
    //private static final String KEY_TYPE = "type";
    public static final String KEY_VALUE = "value";

    private static final String TABLE_BOTTLES = "bottles";

    // Bottles Table Columns names
    public static final String KEY_ID = "id";
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

    private static final String[] COLUMNS_DICT = {KEY_DICT_ID,KEY_TYPE,KEY_VALUE};

    private static final String[] COLUMNS = {KEY_ID,KEY_SID,KEY_VOLUME,KEY_TYPE,KEY_SOURCE,
            KEY_ALKACH,KEY_DONE,KEY_ALCO,KEY_SUGAR,KEY_PEREGON,KEY_DATE,KEY_TIMESTAMP,KEY_DESCR};

    public void convertSrc(SQLiteDatabase db) {
        List<Bottle> bottles = this.searchBottles(db, null);

        for (Bottle bottle: bottles) {
            try {
                if(bottle.getSource().length() == 2) {
                    String src1 = bottle.getSource().getJSONObject(0).getString("id");
                    int vol1 = bottle.getSource().getJSONObject(0).getInt("volume");
                    Log.d("_SQL convertSrc()", src1);
                    Bottle b1 = this.getBottle(db, src1);
                    String src2 = bottle.getSource().getJSONObject(1).getString("id");
                    int vol2 = bottle.getSource().getJSONObject(1).getInt("volume");
                    Log.d("_SQL convertSrc()", src2);
                    Bottle b2 = this.getBottle(db, src2);

                    if (b1!=null && b2!=null){
                        b1.setVolume(vol1);
                        b2.setVolume(vol2);
                        bottle.makeCoupage(b1,b2);
                        this.updateBottle(db, bottle);
                    }
                }
            }catch (JSONException e) {
                Log.e("_SQL convertSrc()", e.getMessage());
                e.printStackTrace();
            }
        }

    }

    public void addDict(String type, String value){
        //for logging
        Log.d("_SQL addDict", "first");

        // 1. get reference to writable DB
        SQLiteDatabase db = this.getWritableDatabase();

        // 2. create ContentValues to add key "column"/value
        ContentValues values = new ContentValues();
        values.put(KEY_TYPE, type);
        values.put(KEY_VALUE, value);

        // 3. insert
        db.beginTransaction();
        db.insert(TABLE_DICT, // table
                null, //nullColumnHack
                values); // key/value -> keys = column names/ values = column values

        // 4. close
        db.setTransactionSuccessful();
        db.endTransaction();
        db.close();
    }

    public Cursor searchDict(String type, String text) {
        String query = "SELECT  * FROM " + TABLE_DICT + " where type='" + type + "' and like('" + text + "%', value)";
        Log.d("_SQL", query);
        // 2. get reference to writable DB
        SQLiteDatabase db = this.getWritableDatabase();
        return db.rawQuery(query, null);
    }

    public Cursor searchDict(String type) {
        String query = "SELECT  * FROM " + TABLE_DICT;
        Log.d("_SQL", query);
        // 2. get reference to writable DB
        SQLiteDatabase db = this.getWritableDatabase();
        return db.rawQuery(query, null);
    }

    public String[] arrayDict(String type) {
        ArrayList<String> res = new ArrayList<String>();

        Cursor cursor = this.searchDict(type);
        if (cursor != null && cursor.getCount()>0){
            cursor.moveToFirst();
            do{
                //for(int i = 0; i < cursor.getCount(); i ++){
                res.add(cursor.getString(cursor.getColumnIndex(KEY_VALUE)));
                //}
            }while(cursor.moveToNext());
        }

        return res.toArray(new String[res.size()]);
    }

    public String getDict(int id){

        // 1. get reference to readable DB
        SQLiteDatabase db = this.getReadableDatabase();

        // 2. build query
        Cursor cursor =
                db.query(TABLE_DICT, // a. table
                        COLUMNS_DICT, // b. column names
                        " id = ?", // c. selections
                        new String[] { String.valueOf(id) }, // d. selections args
                        null, // e. group by
                        null, // f. having
                        null, // g. order by
                        null); // h. limit

        // 3. if we got results get the first one
        if (cursor != null) {
            Log.d("_SQL", "total selected dict: " + String.valueOf(cursor.getCount()));
            cursor.moveToFirst();

            //log
            Log.d("getDict", "test");

            // 5. return book
            return cursor.getString(2);
        }

        return null;
    }

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

    public Bottle getBottle(String sid){

        // 1. get reference to readable DB
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT  * FROM " +
                TABLE_BOTTLES + " as b inner join " + TABLE_DICT + " as d on b.type=d._id " +
                "where sid=?",
                new String[] {sid});
        // 2. build query
/*        Cursor cursor =
                db.query(TABLE_BOTTLES, // a. table
                        COLUMNS, // b. column names
                        " sId = ?", // c. selections
                        new String[] { id }, // d. selections args
                        null, // e. group by
                        null, // f. having
                        null, // g. order by
                        null); // h. limit*/

        // 3. if we got results get the first one
        if (cursor != null) {
            Log.d("_SQL", "total selected bottles: " + String.valueOf(cursor.getCount()));
            cursor.moveToFirst();
            Bottle bottle = fillBottle(cursor);
            //log
            Log.d("getBottle("+sid+")", bottle.toString());

            // 5. return book
            return bottle;
        }

        return null;
    }

    public Bottle getBottle(SQLiteDatabase db, String sid){

        // 1. get reference to readable DB
        Cursor cursor = db.rawQuery("SELECT  * FROM " +
                        TABLE_BOTTLES + " as b inner join " + TABLE_DICT + " as d on b.type=d._id " +
                        "where sid=?",
                new String[] {sid});
        // 2. build query
/*        Cursor cursor =
                db.query(TABLE_BOTTLES, // a. table
                        COLUMNS, // b. column names
                        " sId = ?", // c. selections
                        new String[] { id }, // d. selections args
                        null, // e. group by
                        null, // f. having
                        null, // g. order by
                        null); // h. limit*/

        // 3. if we got results get the first one
        if (cursor != null && cursor.getCount()>0) {
            Log.d("_SQL", "total selected bottles: " + String.valueOf(cursor.getCount()));
            cursor.moveToFirst();
            Bottle bottle = fillBottle(cursor);
            //log
            Log.d("getBottle("+sid+")", bottle.toString());

            // 5. return book
            return bottle;
        }

        return null;
    }

    public Bottle getBottle(int id){

        // 1. get reference to readable DB
        SQLiteDatabase db = this.getReadableDatabase();

        // 2. build query
        Cursor cursor = db.rawQuery("SELECT  * FROM " + TABLE_BOTTLES + " as b inner join " + TABLE_DICT + " as d on b.type=d._id "+
                "where id=?",
                new String[] {String.valueOf(id)});
        /*Cursor cursor =
                db.query(TABLE_BOTTLES, // a. table
                        COLUMNS, // b. column names
                        " id = ?", // c. selections
                        new String[] { String.valueOf(id) }, // d. selections args
                        null, // e. group by
                        null, // f. having
                        null, // g. order by
                        null); // h. limit
*/
        // 3. if we got results get the first one
        if (cursor != null && cursor.getCount() > 0) {
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
        bottle.setType(cursor.getInt(3));
        bottle.setSType(cursor.getString(cursor.getColumnIndex("value")));
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

    private String makeFilter(String filter) {
        String res = "";

        //String[] alkotype = this.context.getResources().getStringArray(R.array.alkotype);

        //ArrayList<String> type = new ArrayList<>(Arrays.asList(
        //       this.context.getResources().getStringArray(R.array.alkotype)));

        if(filter.startsWith("SRC:")) {
            res = " where d.value='" + filter + "' ";
        }else{
            res = " where like('%" + filter + "%', " + KEY_SID + ") "+
                    " or like ('%" + filter + "%', " + KEY_DESCR + ") " +
                    " or d.value='" + filter + "' ";
        }

        return res;
    }

    public List<Bottle> searchBottles(SQLiteDatabase db, String filter){
        List<Bottle> bottles = new LinkedList<>();

        // 1. build the query

        String where = "";
        if (filter != null) {
            where = makeFilter(filter);
        }

        //String query = "SELECT  * FROM " + TABLE_BOTTLES + where + " ORDER BY " + KEY_DATE + " DESC";
        String query = "SELECT  * FROM " + TABLE_BOTTLES + " as b inner join " + TABLE_DICT + " as d on b.type=d._id "
                + where + " ORDER BY " + KEY_DATE + " DESC";
        Log.d("_SQL", query);
        // 2. get reference to writable DB

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

    public List<Bottle> searchBottles(String filter){
        List<Bottle> bottles = new LinkedList<>();

        // 1. build the query

        String where = "";
        if (filter != null) {
            where = makeFilter(filter);
        }

        //String query = "SELECT  * FROM " + TABLE_BOTTLES + where + " ORDER BY " + KEY_DATE + " DESC";
        String query = "SELECT  * FROM " + TABLE_BOTTLES + " as b inner join " + TABLE_DICT + " as d on b.type=d._id "
                + where + " ORDER BY " + KEY_DATE + " DESC";
        Log.d("_SQL", query);
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

    public List<Bottle> searchBottles(int id1, int id2){
        List<Bottle> bottles = new LinkedList<>();

        // 1. build the query

        String where = "where b.id=? or b.id=?";

        //String query = "SELECT  * FROM " + TABLE_BOTTLES + where + " ORDER BY " + KEY_DATE + " DESC";
        String query = "SELECT  * FROM " + TABLE_BOTTLES + " as b inner join " + TABLE_DICT + " as d on b.type=d._id "
                + where + " ORDER BY " + KEY_DATE + " DESC";
        Log.d("_SQL", query);
        // 2. get reference to writable DB
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(query, new String[] {String.valueOf(id1),String.valueOf(id2)});

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
        values.put(KEY_ALCO, bottle.getAlco());
        values.put(KEY_SUGAR, bottle.getSugar());
        values.put(KEY_PEREGON, bottle.getPeregon());
        values.put(KEY_DATE, String.valueOf(Math.round(bottle.getDate().getTime()/1000)));
        values.put(KEY_TIMESTAMP, String.valueOf(Math.round(bottle.getTimeStamp().getTime()/1000)));
        values.put(KEY_SOURCE, bottle.getSource().toString());
        values.put(KEY_DESCR, bottle.getDescription());

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
    public int updateBottle(SQLiteDatabase db, Bottle bottle) {

        // 1. get reference to writable DB

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

        // 3. updating row
        db.beginTransaction();
        int i = db.update(TABLE_BOTTLES, //table
                values, // column/value
                KEY_ID+" = ?", // selections
                new String[] { String.valueOf(bottle.getId()) }); //selection args

        // 4. close
        db.setTransactionSuccessful();
        db.endTransaction();
        //db.close();

        return i;

    }

    //public Bottle[]

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
