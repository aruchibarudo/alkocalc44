package ru.yamalinform.alkocalc44;

import android.content.Context;
import android.util.Log;
import android.util.Xml;

import org.xmlpull.v1.XmlSerializer;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.StringWriter;
import java.util.Date;

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



    public void writeXml(Date time, String msg, String type){
        XmlSerializer serializer = Xml.newSerializer();
        StringWriter writer = new StringWriter();
        try {
            serializer.setOutput(writer);
            serializer.startDocument("UTF-8", true);
            serializer.startTag("", "items");
            serializer.startTag("", "item");
            serializer.attribute("", "timestamp", String.valueOf(time.getTime()));
            serializer.attribute("", "type", type);
            serializer.text(msg);
            serializer.endTag("", "item");
            serializer.endTag("", "items");
            serializer.endDocument();
            this.write(writer.toString());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}

