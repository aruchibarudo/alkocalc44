package ru.yamalinform.alkocalc44;

import android.util.Log;
import android.widget.SeekBar;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.sql.Date;
import java.util.ArrayList;
import java.util.Calendar;

/**
 * Created by archy on 4/27/16.
 */
public class Bottle {
    private int id;
    private String sId;
    private int volume;
    private String type;
    private JSONArray source;
    private String alkach = "lehichu@gmail.com";
    private boolean done;
    private int alco;
    private int sugar;
    private int peregon;
    private Date date, timestamp;
    private String description;



    public Bottle(){
        timestamp = new Date(Calendar.getInstance().getTime().getTime());
        //date = timestamp;
    }

    public Bottle(String sId, int volume, String type, int alco, int peregon) {
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

    public String getType() {
        return type;
    }

    public String getAlkach() { return alkach; }

    public int getAlco() { return alco; }

    public int getSugar() { return sugar; }

    public int getPeregon() { return peregon; }

    public Date getTimeStamp() { return timestamp; }

    public Date getDate() { return date; }

    public JSONArray getSource() { return source; }

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

    public void setType(String type) {
        this.type = type;
    }

    public void setAlkach(String alkach) {
        this.alkach = alkach;
    }

    public void setDone(Boolean done) {
        this.done = done;
    }

    public void setSource(String source) {
        if(!source.equals("")) {
            try {
                this.source = new JSONArray(source);
            } catch (JSONException e) {
                Log.d("BOTTLE: " + source, e.getMessage());
                e.printStackTrace();
            }
        }

    }

    public void setSugar(int sugar) { this.sugar = sugar; }

    public void setAlco(int alco) { this.alco = alco; }

    public void setPeregon(int peregon) { this.peregon = peregon; }

    public void setDate (Date date) { this.date = date;}

    public void setTimeStamp(Date timestamp) { this.timestamp = timestamp; }

    public JSONArray makeCoupage(ArrayList<Bottle> bottles) {
        String res = "";
        JSONArray jsonA = null;

        try {
            jsonA = new JSONArray();
            for (int i = 0; i < bottles.size(); i++) {
                jsonA.put(i,new JSONObject("{id:" + String.valueOf(bottles.get(i).getsId()) +
                        ",volume:" + String.valueOf(bottles.get(i).getVolume())+"}"));
            }
            this.source = jsonA;
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return jsonA;
    }

    public JSONArray makeCoupage(Bottle b1, Bottle b2) {
        JSONArray res = null;

        try {
            res = new JSONArray("[{id:" + String.valueOf(b1.getsId()) + ",volume:" + String.valueOf(b1.getVolume())+"}," +
                    "{id:" + String.valueOf(b2.getsId()) + ",volume:" + String.valueOf(b2.getVolume())+"}]");
            this.source = res;
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return res;
    }
}

