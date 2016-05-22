package ru.yamalinform.alkocalc44;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;

import java.sql.Date;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Locale;

public class addBottle extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_bottle);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        final EditText etSId = (EditText) findViewById(R.id.sId);
        final EditText etAlco = (EditText) findViewById(R.id.alco);
        final EditText etVolume = (EditText) findViewById(R.id.volume);
        final EditText etPeregon = (EditText) findViewById(R.id.peregon);
        final EditText etSugar = (EditText) findViewById(R.id.sugar);
        final EditText etDate = (EditText) findViewById(R.id.date);
        final Button btnOk = (Button) findViewById(R.id.btnOk);
        final CheckBox cbWrite = (CheckBox) findViewById(R.id.cbWrite);
        final Date date;

        btnOk.setOnClickListener(new Button.OnClickListener() {

            public void onClick(View v) {


//                FileSaver doWr = new FileSaver(getApplicationContext(), filename);
                // TODO: Make add button with form for new Bottle

                if (cbWrite.isChecked()) {
                    // TODO: insert Bottle into table
//                    Date date = new Date();
//                    doWr.writeXml(date, tvOutput.getText().toString(), sType);
                    Log.d("MAIN", "Start add bottle");
                    Bottle bottle = new Bottle(etSId.getText().toString(),
                            Integer.parseInt(etVolume.getText().toString()),
                            "vodka",
                            Integer.parseInt(etAlco.getText().toString()),
                            Integer.parseInt(etPeregon.getText().toString()));
                    //bottle.setTimeStamp(new java.sql.Date(Calendar.getInstance().getTime().getTime()));
                    //bottle.setDate(new java.sql.Date(Calendar.getInstance().getTime().getTime()));
                    String stDate = etDate.getText().toString();
                    DateFormat format = new SimpleDateFormat("d/MM/yy", Locale.ENGLISH);
                    try {
                        //SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd");
                        java.util.Date parsed = format.parse(stDate);
                        bottle.setDate(new java.sql.Date(parsed.getTime()));
                    } catch (Exception e) {
                        Log.d("MAIN", e.getMessage());
                    }
                    bottle.setSugar(Integer.parseInt(etSugar.getText().toString()));
                    bottle.setSource("[{id:0,volume:0}]");

                    alkosql db = new alkosql(getApplicationContext());
                    //ArrayList<Bottle> bb = null;
                    db.addBottle(bottle);
                    Log.d("MAIN", "End add bottle");
                    finish();
                }
            }
        });
    }

}