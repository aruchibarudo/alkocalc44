package ru.yamalinform.alkocalc44;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import org.json.JSONException;

import java.sql.Date;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by archy on 4/27/16.
 */
public class alkosql extends SQLiteOpenHelper {


    // Database Version
    private static final int DATABASE_VERSION = 12;
    public static final String TYPE_ALKO = "ALKO";
    // Database Name
    private static final String DATABASE_NAME = "alkodb";
    private String CREATE_BOTTLES_TABLE;
    private String CREATE_REPORTS_TABLE;
    private String CREATE_DICT_TABLE;
    private Context context;
    private ProgressDialog alert;
    private Context app;
    //private SQLiteDatabase db;

    private static final String TABLE_DICT = "dict";
    public static final String KEY_DICT_ID = "_id";
    private static final String KEY_DICT_TYPE = "type";
    public static final String KEY_DICT_VALUE = "value";

    private static final String TABLE_REPORTS = "reports";
    public static final String KEY_REP_ID = "id";
    public static final String KEY_REP_BID = "bId";
    public static final String KEY_REP_DATE = "date";
    public static final String KEY_REP_ALKACH = "alkach";
    public static final String KEY_REP_STARS = "stars";
    public static final String KEY_REP_TEXT = "report";

    private static final String TABLE_BOTTLES = "bottles";

    // Bottles Table Columns names
    public static final String KEY_ID = "id";
    public static final String KEY_SID = "sId";
    public static final String KEY_VOLUME = "volume";
    public static final String KEY_TYPE = "type";
    public static final String KEY_SOURCE = "source";
    public static final String KEY_ALKACH = "alkach";
    public static final String KEY_DONE = "done";
    public static final String KEY_ALCO = "alco";
    public static final String KEY_SUGAR = "sugar";
    public static final String KEY_PEREGON = "peregon";
    public static final String KEY_DATE = "date";
    public static final String KEY_TIMESTAMP = "timestamp";
    public static final String KEY_DESCR = "description";
    public static final String KEY_LOCATION = "location";
    public static final String KEY_LOCATION_DESCR = "location_descr";
    public static final String KEY_LAT = "lat";
    public static final String KEY_LONG = "long";

    private static final String[] COLUMNS_DICT = {KEY_DICT_ID,KEY_DICT_TYPE,KEY_DICT_VALUE};

    private static final String[] COLUMNS_REP = {KEY_REP_ID,KEY_REP_BID,KEY_REP_DATE,
            KEY_REP_ALKACH,KEY_REP_STARS,KEY_REP_TEXT};

    private static final String[] COLUMNS = {KEY_ID,KEY_SID,KEY_VOLUME,KEY_TYPE,KEY_SOURCE,
            KEY_ALKACH,KEY_DONE,KEY_ALCO,KEY_SUGAR,KEY_PEREGON,KEY_DATE,KEY_TIMESTAMP,KEY_DESCR,
            KEY_LOCATION,KEY_LOCATION_DESCR,KEY_LAT,KEY_LONG};


    public alkosql(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context = context;
    }

    public alkosql(Context context, Context app) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context = context;
        this.app = app;
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
                "timestamp,"+
                "location,"+
                "location_descr,"+
                "lat,"+
                "long"+
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
        //db.close();
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older books table if existed
        //db.execSQL("DROP TABLE IF EXISTS bottles");
        alert = new ProgressDialog(this.app);
        alert.setTitle("Обновление БД до версии " + String.valueOf(newVersion));
        alert.setMessage("Обновляем");
        alert.setButton(Dialog.BUTTON_POSITIVE, "OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
            }
        });
        alert.show();
        db.beginTransaction();
        try {
            switch (newVersion) {
                case 3:
                    db.execSQL("DROP TABLE IF EXISTS reports");
                    Log.d("_SQL", "Upgrade to " + String.valueOf(newVersion));
                    break;
                case 4:
                    db.execSQL("ALTER TABLE bottles ADD description");
                    Log.d("_SQL", "Upgrade to " + String.valueOf(newVersion));
                    break;
                case 5:
                    if(oldVersion < 4) {
                        db.execSQL("ALTER TABLE bottles ADD description");
                    }
                    db.execSQL("UPDATE bottles SET date=date/1000");
                    Log.d("_SQL", "Upgrade to " + String.valueOf(newVersion));
                    break;
                case 6:
                    if(oldVersion < 4) {
                        db.execSQL("ALTER TABLE bottles ADD description");
                    }else if (oldVersion < 5) {
                        db.execSQL("UPDATE bottles SET date=date/1000");
                    }
                    db.execSQL("UPDATE bottles SET type=1");
                    Log.d("_SQL", "Upgrade to " + String.valueOf(newVersion));
                    break;
                case 7:
                    if(oldVersion < 4) {
                        db.execSQL("ALTER TABLE bottles ADD description");
                    }else if (oldVersion < 5) {
                        db.execSQL("UPDATE bottles SET date=date/1000");
                    }else if (oldVersion < 6){
                        db.execSQL("UPDATE bottles SET type=1");
                    }
                    Log.d("_SQL", "Upgrade to " + String.valueOf(newVersion));
                    break;
                case 9:
                    if(oldVersion < 4) {
                        alert.setMessage("ALTER TABLE bottles ADD description");
                        db.execSQL("ALTER TABLE bottles ADD description");
                    }else if (oldVersion < 5) {
                        alert.setMessage("UPDATE bottles SET date=date/1000");
                        db.execSQL("UPDATE bottles SET date=date/1000");
                    }else if (oldVersion < 6){
                        alert.setMessage("UPDATE bottles SET type=1");
                        db.execSQL("UPDATE bottles SET type=1");
                    }
                    alert.setMessage("Конвертирую исходники");
                    convertSrc(db);
                    alert.setMessage("Готово");
                    Log.d("_SQL", "Upgrade to " + String.valueOf(newVersion));
                    break;
                case 10:

                    if(oldVersion < 4) {
                        alert.setMessage("ALTER TABLE bottles ADD description");
                        db.execSQL("ALTER TABLE bottles ADD description");
                    }else if(oldVersion < 5) {
                        alert.setMessage("UPDATE bottles SET date=date/1000");
                        db.execSQL("UPDATE bottles SET date=date/1000");
                    }else if(oldVersion < 6){
                        alert.setMessage("UPDATE bottles SET type=1");
                        db.execSQL("UPDATE bottles SET type=1");
                    }else if(oldVersion < 9) {
                        alert.setMessage("Конвертирую исходники");
                        convertSrc(db);
                    }
                    alert.setMessage("ALTER TABLE bottles ADD location,location_descr,lat,long");
                    db.execSQL("ALTER TABLE bottles ADD location,location_descr,lat,long");
                    alert.setMessage("Готово");
                    Log.d("_SQL", "Upgrade to " + String.valueOf(newVersion));
                    break;
                case 12:
                    if(oldVersion < 4) {
                        alert.setMessage("ALTER TABLE bottles ADD description");
                        db.execSQL("ALTER TABLE bottles ADD description");
                    }else if(oldVersion < 5) {
                        alert.setMessage("UPDATE bottles SET date=date/1000");
                        db.execSQL("UPDATE bottles SET date=date/1000");
                    }else if(oldVersion < 6){
                        alert.setMessage("UPDATE bottles SET type=1");
                        db.execSQL("UPDATE bottles SET type=1");
                    }else if(oldVersion < 9) {
                        alert.setMessage("Конвертирую исходники");
                        convertSrc(db);
                    }else if(oldVersion < 10) {
                        alert.setMessage("ALTER TABLE bottles ADD location,location_descr,lat,long");
                        db.execSQL("ALTER TABLE bottles ADD location,location_descr,lat,long");
                        alert.setMessage("Готово");
                    }

                    Log.d("_SQL", "Upgrade to " + String.valueOf(newVersion));
                    break;
            }
        }catch (SQLiteException e){
            db.endTransaction();
            alert.setMessage("Что-то пошло не так :(");
            e.printStackTrace();
        }
        db.setTransactionSuccessful();
        db.endTransaction();
        alert.setMessage("Обновлено :)");
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

        if(cursor.getCount() == 0 && cursor != null){
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
        return "insert into " + TABLE_DICT + "(" + KEY_DICT_TYPE +"," + KEY_DICT_VALUE + ") values ('" + type + "','" + value + "')";
    }

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
        values.put(KEY_DICT_TYPE, type);
        values.put(KEY_DICT_VALUE, value);

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
        Cursor res = db.rawQuery(query, null);
        //db.close();
        return res;
    }

    public Cursor searchDict(String type) {
        String query = "SELECT  * FROM " + TABLE_DICT;
        Log.d("_SQL", query);
        // 2. get reference to writable DB
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor res = db.rawQuery(query, null);
        //db.close();
        return res;
    }

    public String[] arrayDict(String type) {
        ArrayList<String> res = new ArrayList<String>();

        Cursor cursor = this.searchDict(type);
        if (cursor != null && cursor.getCount()>0){
            cursor.moveToFirst();
            do{
                //for(int i = 0; i < cursor.getCount(); i ++){
                res.add(cursor.getString(cursor.getColumnIndex(KEY_DICT_VALUE)));
                //}
            }while(cursor.moveToNext());
        }

        return res.toArray(new String[res.size()]);
    }

    public String getDict(int id){
        String res = null;
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
            res = cursor.getString(2);
        }

        db.close();
        return res;
    }

    public int getAlkotype(int alco) {
        int res = 0;
        if(alco > 7 && alco < 14) { res = 6; }
        else if(alco > 14 && alco <= 25) { res = 5; }
        else if(alco >= 40 && alco < 45) { res = 1; }
        else if(alco >= 45 && alco < 55) { res = 0; }
        else if(alco >= 55) { res = 4; }
        return res;
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
        values.put(KEY_DATE, String.valueOf((int)Math.ceil((double)bottle.getDate().getTime()/1000)));
        values.put(KEY_TIMESTAMP, String.valueOf((int)Math.ceil((double)bottle.getTimeStamp().getTime()/1000)));
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
        Cursor cursor = db.rawQuery("SELECT *,avg(r.stars) as stars, count(r.id) as report FROM " +
                TABLE_BOTTLES + " as b left join " + TABLE_DICT + " as d on b.type=d._id " +
                " left join reports r on b.id = r.bId " +
                " where sid=?"+
                " GROUP BY b.id" +
                " ORDER BY b.date DESC",
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
            db.close();
            return bottle;
        }
        db.close();
        return null;
    }

    public Bottle getBottle(SQLiteDatabase db, String sid){

        // 1. get reference to readable DB
        Cursor cursor = db.rawQuery("SELECT *, avg(r.stars) as stars, count(r.id) as report FROM " +
                        TABLE_BOTTLES + " as b left join " + TABLE_DICT + " as d on b.type=d._id " +
                        " left join reports r on b.id = r.bId " +
                        " where sid=?"+
                        " GROUP BY b.id" +
                        " ORDER BY b.date DESC",
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
        Cursor cursor = db.rawQuery("SELECT *, avg(r.stars) as stars, count(r.id) as report FROM " +
                TABLE_BOTTLES + " as b left join " + TABLE_DICT + " as d on b.type=d._id " +
                " left join reports r on b.id = r.bId " +
                " where b.id=?"+
                " GROUP BY b.id" +
                " ORDER BY b.date DESC",
                new String[] {String.valueOf(id)});


        // 3. if we got results get the first one
        if (cursor != null && cursor.getCount() > 0) {
            Log.d("_SQL", "total selected bottles: " + String.valueOf(cursor.getCount()));
            cursor.moveToFirst();
            Bottle bottle = fillBottle(cursor);
            //log
            Log.d("getBottle("+id+")", bottle.toString());

            // 5. return book
            db.close();
            return bottle;
        }
        db.close();
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
        bottle.setDescription(cursor.getString(cursor.getColumnIndex(KEY_DESCR)));
        bottle.setStars(cursor.getFloat(cursor.getColumnIndex(KEY_REP_STARS)));
        bottle.setReport(cursor.getInt(cursor.getColumnIndex(KEY_REP_TEXT)));
        Log.d("_SQL","end fillBottle");
        //TODO: Use cursor.getColumnIndex()

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
        String query = "SELECT  *, avg(r.stars) as stars, count(r.id) as report FROM " +
                TABLE_BOTTLES + " as b inner join " + TABLE_DICT + " as d on b.type=d._id " +
                where +
                " GROUP BY b.id" +
                " ORDER BY b." + KEY_DATE + " DESC";
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
        String query = "SELECT  *, avg(r.stars) as stars, count(r.id) as report FROM " +
                TABLE_BOTTLES + " as b left join " + TABLE_DICT + " as d on b.type=d._id " +
                " left join reports r on b.id = r.bId " +
                where +
                " GROUP BY b.id" +
                " ORDER BY b." + KEY_DATE + " DESC";
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
        db.close();
        return bottles;
    }

    public List<Bottle> searchBottles(String filter, String order){
        List<Bottle> bottles = new LinkedList<>();

        // 1. build the query

        String where = "";
        if (filter != null) {
            where = makeFilter(filter);
        }

        //String query = "SELECT  * FROM " + TABLE_BOTTLES + where + " ORDER BY " + KEY_DATE + " DESC";
        String query = "SELECT  *, avg(r.stars) as stars, count(r.id) as report FROM " +
                TABLE_BOTTLES + " as b left join " + TABLE_DICT + " as d on b.type=d._id " +
                " left join reports r on b.id = r.bId " +
                where +
                " GROUP BY b.id" +
                " ORDER BY " + order + " DESC";
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
        db.close();
        return bottles;
    }

    public List<Bottle> searchBottles(int id1, int id2){
        List<Bottle> bottles = new LinkedList<>();

        // 1. build the query

        String where = "where b.id=? or b.id=?";

        //String query = "SELECT  * FROM " + TABLE_BOTTLES + where + " ORDER BY " + KEY_DATE + " DESC";
        String query = "SELECT *, avg(r.stars) as stars, count(r.id) as report FROM " +
                TABLE_BOTTLES + " as b left join " + TABLE_DICT + " as d on b.type=d._id " +
                " left join reports r on b.id = r.bId " +
                where +
                " GROUP BY b.id" +
                " ORDER BY b." + KEY_DATE + " DESC";
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
        db.close();
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
        values.put(KEY_DATE, String.valueOf((int)Math.ceil((double)bottle.getDate().getTime()/1000)));
        values.put(KEY_TIMESTAMP, String.valueOf((int)Math.ceil((double)bottle.getTimeStamp().getTime()/1000)));
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
        values.put(KEY_DATE, String.valueOf((int)Math.ceil((double)bottle.getDate().getTime()/1000)));
        values.put(KEY_TIMESTAMP, String.valueOf((int)Math.ceil((double)bottle.getTimeStamp().getTime()/1000)));
        values.put(KEY_SOURCE, bottle.getSource().toString());
        values.put(KEY_DESCR, bottle.getDescription());

        // 3. updating row
        int i = db.update(TABLE_BOTTLES, //table
                values, // column/value
                KEY_ID+" = ?", // selections
                new String[] { String.valueOf(bottle.getId()) }); //selection args

        // 4. close

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

    private Report fillReport(Cursor cursor) {
        Report report = new Report();
        Log.d("_SQL","start fillReport");
        report.setId(cursor.getInt(cursor.getColumnIndex(KEY_REP_ID)));
        Date date = new Date(cursor.getLong(cursor.getColumnIndex(KEY_REP_DATE))*1000);
        report.setDate(date);
        report.setAlkach(cursor.getString(cursor.getColumnIndex(KEY_REP_ALKACH)));
        report.setStars(cursor.getInt(cursor.getColumnIndex(KEY_REP_STARS)));
        report.setBottleId(cursor.getInt(cursor.getColumnIndex(KEY_REP_BID)));
        report.setReport(cursor.getString(cursor.getColumnIndex(KEY_REP_TEXT)));
        Log.d("_SQL","end fillReport");
        //TODO: Add other field

        return report;
    }

    public Report getReport(int id){

        // 1. get reference to readable DB
        SQLiteDatabase db = this.getReadableDatabase();

        // 2. build query
        Cursor cursor = db.rawQuery("SELECT r.id as id, r.date as date, r.alkach as alkach, r.stars as stars, r.report as report, r.bId as bId FROM " +
                        TABLE_REPORTS + " as r " +
                        " where r.id=?",
                new String[] {String.valueOf(id)});


        // 3. if we got results get the first one
        if (cursor != null && cursor.getCount() > 0) {
            Log.d("_SQL", "total reports: " + String.valueOf(cursor.getCount()));
            cursor.moveToFirst();
            Report report = fillReport(cursor);
            //log
            Log.d("getReport("+id+")", report.toString());

            // 5. return book
            db.close();
            return report;
        }
        db.close();
        return null;
    }

    public List<Report> searchReports(int bid){
        List<Report> reports = new LinkedList<>();

        // 1. build the query

        String where = "where b.id=?";

        //String query = "SELECT  * FROM " + TABLE_BOTTLES + where + " ORDER BY " + KEY_DATE + " DESC";
        String query = "SELECT r.id as id, r.date as date, r.alkach as alkach, r.stars as stars, r.report as report, r.bId as bId FROM " +
                TABLE_REPORTS + " as r left join " + TABLE_BOTTLES + " as b on r.bid=b.id " +
                " left join " + TABLE_DICT + " d on b.type = d._id " +
                where + " ORDER BY r." + KEY_REP_DATE + " DESC";
        Log.d("_SQL", query);
        // 2. get reference to writable DB
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(query, new String[] {String.valueOf(bid)});

        // 3. go over each row, build book and add it to list
        Report report = null;
        if (cursor.moveToFirst()) {
            do {
                report = fillReport(cursor);
                reports.add(report);
            } while (cursor.moveToNext());
        }

        Log.d("searchReport()", reports.toString());

        // return books
        db.close();
        return reports;
    }

    public void addReport(Report report){
        //for logging
        Log.d("addReport", report.toString());

        // 1. get reference to writable DB
        SQLiteDatabase db = this.getWritableDatabase();

        // 2. create ContentValues to add key "column"/value
        ContentValues values = new ContentValues();
        values.put(KEY_REP_BID, report.getBottleId());
        values.put(KEY_REP_ALKACH, report.getAlkach());
        values.put(KEY_REP_DATE, String.valueOf(Math.round(report.getDate().getTime()/1000)));
        values.put(KEY_REP_STARS, report.getStars());
        values.put(KEY_REP_TEXT, report.getReport());

        // 3. insert
        db.beginTransaction();
        db.insert(TABLE_REPORTS, // table
                null, //nullColumnHack
                values); // key/value -> keys = column names/ values = column values

        // 4. close
        db.setTransactionSuccessful();
        db.endTransaction();
        db.close();
    }
}
