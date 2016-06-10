package ru.yamalinform.alkocalc44;

import android.content.Context;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.lang.reflect.Type;
import java.sql.Date;
import java.util.List;

/**
 * Created by archy on 4/27/16.
 */
public class FileSaver {


    /**
     * The file we save the image into.
     */
    private final String mFile;
    private final Context context;
    private String sString;

    public FileSaver(Context cntxt, String file) {
        mFile = file;
        context = cntxt;
    }

    public void write(String sString) {
        try {
            //OutputStreamWriter outputStreamWriter = new OutputStreamWriter(context.openFileOutput(mFile, Context.MODE_PRIVATE));
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(context.openFileOutput(mFile, Context.MODE_PRIVATE));
            outputStreamWriter.write(sString);
            outputStreamWriter.close();
        }
        catch (IOException e) {
            Log.e("Exception", "File write failed: " + e.toString());
        }
    }



    public String read() {
        String ret = "";

        try {
            InputStream inputStream = context.openFileInput(mFile);

            if ( inputStream != null ) {
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                String receiveString = "";
                StringBuilder stringBuilder = new StringBuilder();

                while ( (receiveString = bufferedReader.readLine()) != null ) {
                    stringBuilder.append(receiveString).append("--");
                }

                inputStream.close();
                ret = stringBuilder.toString();
            }
        }
        catch (FileNotFoundException e) {
            Log.e("login activity", "File not found: " + e.toString());
        } catch (IOException e) {
            Log.e("login activity", "Can not read file: " + e.toString());
        }

        return ret;
    }



    public void writeJSON(List<Bottle> bottles){
        String res;

/*        GsonBuilder gsonBuilder;
        gsonBuilder = new GsonBuilder();
        gsonBuilder.registerTypeAdapter(Date.class, DateSerializer.class);
        gsonBuilder.setDateFormat("d.mm.YY");*/
        Gson gson = new GsonBuilder()
                //.registerTypeAdapter(Date.class, DateSerializer.class)
                .setDateFormat("dd.MM.yyyy HH:mm:ss")
                .setPrettyPrinting()
                .create();


        try {
            BottleList bList = new BottleList();
            for (Bottle b: bottles) {
                bList.add(b);
            }
            res = gson.toJson(bList);
            write(res);
        } catch (Exception e) {
            Log.d("FileSaver", "Что-то пошло не так");
            e.printStackTrace();
        }
    }

    private class DateSerializer implements JsonSerializer<Date> {
        public JsonElement serialize(Date src, Type typeOfSrc, JsonSerializationContext context) {
            return new JsonPrimitive(src.toString());
        }
    }
}

