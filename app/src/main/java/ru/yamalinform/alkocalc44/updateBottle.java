package ru.yamalinform.alkocalc44;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import java.sql.Date;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Locale;

/**
 * Created by archy on 5/25/16.
 */

public class updateBottle  extends AppCompatActivity {
    protected int iType;
    protected String[] alkotype;
    protected int Id;
    private alkosql db;
    private Bottle bottle;

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
        //Cursor cursor = db.searchDict(db.TYPE_ALKO);
        db = new alkosql(getApplicationContext());
        alkotype = db.arrayDict(db.TYPE_ALKO);

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,android.R.layout.simple_spinner_item, alkotype);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        //Log.d("UPD_BOTTLE", String.valueOf(adapter.getCount()));

        spAlkotype.setAdapter(adapter);

        if(getIntent().getExtras() != null && getIntent().getExtras().size() == 1) {
            bottle = db.getBottle(getIntent().getExtras().getInt("bottleId"));
            etSId.setText(bottle.getsId());
            etAlco.setText(String.valueOf(bottle.getAlco()));
            etVolume.setText(String.valueOf(bottle.getVolume()));
            etPeregon.setText(String.valueOf(bottle.getPeregon()));
            etSugar.setText(String.valueOf(bottle.getSugar()));
            SimpleDateFormat sdf = new SimpleDateFormat("d.MM.yy", Locale.getDefault());
            etDate.setText(sdf.format(bottle.getDate()));
            etDescr.setText(bottle.getDescription());
            spAlkotype.setSelection(bottle.getType() - 1);
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
                    Log.d("UPD_BOTTLE", "Start update bottle");
                    bottle.setsId(etSId.getText().toString());
                    bottle.setVolume(Integer.parseInt(etVolume.getText().toString()));
                    bottle.setType(iType);
                    bottle.setAlco(Integer.parseInt(etAlco.getText().toString()));
                    bottle.setPeregon(Integer.parseInt(etPeregon.getText().toString()));
                    String stDate = etDate.getText().toString();

                    try {
                        java.util.Date parsed = sdf.parse(stDate);
                        bottle.setDate(new java.sql.Date(parsed.getTime()));
                    } catch (Exception e) {
                        Log.d("UPD_BOTTLE", e.getMessage());
                    }

                    bottle.setSugar(Integer.parseInt(etSugar.getText().toString()));
                    bottle.setDescription(etDescr.getText().toString());


                    //ArrayList<Bottle> bb = null;
                    int res = db.updateBottle(bottle);
                    if(res < 1) {
                        Toast.makeText(updateBottle.this, "Что-то не обновилось", Toast.LENGTH_SHORT).show();
                    }
                    Log.d("UPD_BOTTLE", "Updated bottle sId: " + bottle.getsId() + " count: " + String.valueOf(res));
                    Log.d("UPD_BOTTLE", "End update bottle");
                    finish();
                }
            }
        });
    }
}
