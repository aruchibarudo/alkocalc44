package ru.yamalinform.alkocalc44;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.ArraySet;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;

import java.sql.Date;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Locale;

public class addBottle extends AppCompatActivity {
    protected int iType;
    protected String[] alkotype;

    private DateFormat sdf = new SimpleDateFormat("d.MM.yy", Locale.getDefault()); //"d.MM.yy", Locale.ENGLISH

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
        final EditText etDescr = (EditText) findViewById(R.id.etDescr);
        final Button btnOk = (Button) findViewById(R.id.btnOk);
        final CheckBox cbWrite = (CheckBox) findViewById(R.id.cbWrite);
        final Date date;
        final String source;
        final Spinner spAlkotype = (Spinner) findViewById(R.id.spAlkotype);
        alkosql db = new alkosql(getApplicationContext());
        //Cursor cursor = db.searchDict(db.TYPE_ALKO);
        alkotype = db.arrayDict(db.TYPE_ALKO);

        etDate.setText(sdf.format(new java.util.Date()));

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,android.R.layout.simple_spinner_item, alkotype);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        Log.d("ADD_BOTTLE", String.valueOf(adapter.getCount()));

        spAlkotype.setAdapter(adapter);

        if(getIntent().getExtras() != null && getIntent().getExtras().size() == 3) {
            etAlco.setText(getIntent().getExtras().getString("alco"));
            spAlkotype.setSelection(db.getAlkotype(Integer.valueOf(getIntent().getExtras().getString("alco"))));
            etVolume.setText(getIntent().getExtras().getString("volume"));
            source = getIntent().getExtras().getString("source");
        }else {
            source = "[{id:0,volume:0}]";
        }

        spAlkotype.setOnItemSelectedListener(new Spinner.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> parent,
                                       View itemSelected, int selectedItemPosition, long selectedId) {
                iType = selectedItemPosition + 1;
            }
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        btnOk.setOnClickListener(new Button.OnClickListener() {

            public void onClick(View v) {


//                FileSaver doWr = new FileSaver(getApplicationContext(), filename);


                if (cbWrite.isChecked()) {
//                    Date date = new Date();
//                    doWr.writeXml(date, tvOutput.getText().toString(), sType);
                    Log.d("MAIN", "Start add bottle");
                    Bottle bottle = new Bottle(etSId.getText().toString(),
                            Integer.parseInt(etVolume.getText().toString()),
                            iType,
                            Integer.parseInt(etAlco.getText().toString()),
                            Integer.parseInt(etPeregon.getText().toString()));
                    //bottle.setTimeStamp(new java.sql.Date(Calendar.getInstance().getTime().getTime()));
                    //bottle.setDate(new java.sql.Date(Calendar.getInstance().getTime().getTime()));
                    String stDate = etDate.getText().toString();

                    try {
                        //SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd");
                        java.util.Date parsed = sdf.parse(stDate);
                        bottle.setDate(new java.sql.Date(parsed.getTime()));
                    } catch (Exception e) {
                        Log.d("MAIN", e.getMessage());
                    }

                    bottle.setSugar(Integer.parseInt(etSugar.getText().toString()));
                    bottle.setSource(source);
                    bottle.setDescription(etDescr.getText().toString());

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
