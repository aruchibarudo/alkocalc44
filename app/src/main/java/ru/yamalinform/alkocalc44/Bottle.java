package ru.yamalinform.alkocalc44;

import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonParser;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.sql.Array;
import java.sql.Date;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.List;

/**
 * Created by archy on 4/27/16.
 */
public class Bottle {
    private int id;
    private String sId;
    private String Stype;
    private int volume;
    private int type;
    private JSONArray source1;
    private ArrayList<Source> source;
    private String alkach = "lehichu@gmail.com";
    private boolean done;
    private int alco;
    private int sugar;
    private int peregon;
    private Date date, timestamp;
    private String description;
    private float stars;
    private int report;

    public Bottle(){
        timestamp = new Date(Calendar.getInstance().getTime().getTime());
        //date = timestamp;
    }

    public Bottle(String sId, int volume, int type, int alco, int peregon) {
        super();
        this.sId = sId;
        this.volume = volume;
        this.alco = alco;
        this.peregon = peregon;
        this.type = type;
        timestamp = new Date(Calendar.getInstance().getTime().getTime());
        //date = timestamp;

    }

    //getters & setters

    @Override
    public String toString() {
        return "Bottle [" + sId + ", " + volume + ", " + type + ", " + source.toString() + "]";
    }

    public String getStype() { return this.Stype; }
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getId() {
        return id;
    }

    public String getsId() {
        return sId;
    }

    public int getVolume() {
        return volume;
    }

    public int getType() {
        return type;
    }

    public String getAlkach() { return alkach; }

    public int getAlco() { return alco; }

    public int getSugar() { return sugar; }

    public int getPeregon() { return peregon; }

    public Date getTimeStamp() { return timestamp; }

    public Date getDate() { return date; }

    public ArrayList<Source> getSource() { return source; }

    public int calcGradus(int iGr1, int iGr2, int iVolume, int progress) {
        int iMax = 100;
        double iEd = iVolume / iMax;
        double iVolAlco = progress * iGr1 * iEd / 100 + (iMax - progress) * iGr2 * iEd / 100;
        return (int) Math.round(iVolAlco / iVolume * 100);
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setsId(String sId) {
        this.sId = sId;
    }

    public void setVolume(int volume) {
        this.volume = volume;
    }

    public void setType(int type) {
        this.type = type;
    }

    public void setSType(String stype) { this.Stype = stype; }

    public void setAlkach(String alkach) {
        this.alkach = alkach;
    }

    public void setDone(Boolean done) {
        this.done = done;
    }

    public void setSource(String source) {
        Gson gson = new Gson();
        this.source = new ArrayList<>();
        Log.d("BOTTLE", source);
        /*this.source = gson.fromJson(source, Source.class);
        this.source = new ArrayList<Source>();
        this.source.add(new Source(3, 100));
        this.source.add(new Source(4, 500));*/

        JsonParser parser = new JsonParser();
        JsonArray array = parser.parse(source).getAsJsonArray();
        //Source src1 = gson.fromJson(array.get(0), Source.class);
        //Source src2 = gson.fromJson(array.get(1), Source.class);
        this.source.add(gson.fromJson(array.get(0), Source.class));
        if(array.size() == 2) this.source.add(gson.fromJson(array.get(1), Source.class));
    }

    public void setSugar(int sugar) { this.sugar = sugar; }

    public void setAlco(int alco) { this.alco = alco; }

    public void setPeregon(int peregon) { this.peregon = peregon; }

    public void setDate (Date date) { this.date = date;}

    public void setTimeStamp(Date timestamp) { this.timestamp = timestamp; }

    public String makeCoupage(ArrayList<Bottle> bottles) {
        this.source = new ArrayList<>();
        for (int i = 0; i < bottles.size(); i++) {
            source.add(new Source(bottles.get(i).getId(), bottles.get(i).getVolume()));
        }
        return new Gson().toJson(this.source);
    }

    public String makeCoupage(Bottle b1, Bottle b2) {
        this.source = new ArrayList<>();
        source.add(new Source(b1.getId(), b1.getVolume()));
        source.add(new Source(b2.getId(), b2.getVolume()));
        return new Gson().toJson(this.source);
    }

    public float getStars() {
        return this.stars;
    }

    public void setStars(float stars) {
       this.stars = stars;
    }

    public int getReport() {
        return report;
    }

    public void setReport(int report) {
        this.report = report;
    }
}

class BottleList {
    // TODO Class for XML output
    private List<Bottle> bottles;

    public BottleList() {
        bottles = new ArrayList<>();
    }

    public void add(Bottle b) {
        bottles.add(b);
    }
}

class Source {
    private int id;
    private int volume;

    public Source(int id, int volume) {
        this.id = id;
        this.volume = volume;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getVolume() {
        return volume;
    }

    public void setVolume(int volume) {
        this.volume = volume;
    }
}