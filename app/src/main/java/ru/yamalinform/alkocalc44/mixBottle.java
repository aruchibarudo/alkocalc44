package ru.yamalinform.alkocalc44;

/**
 * Created by archy on 5/18/16.
 */
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.Locale;

public class mixBottle extends AppCompatActivity {

    private alkosql db;
    private int iVol1, iVol2, iGrad;
    private String sid1, sid2;
    private int id1, id2;
    private double dGrad;
    private int action;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mix_bottle);

        final SeekBar sbMix = (SeekBar) findViewById(R.id.seekBar);
        final TextView tvResGrad = (TextView) findViewById(R.id.tvResGrad);
        final EditText etGrad1 = (EditText) findViewById(R.id.etGrad1);
        final EditText etGrad2 = (EditText) findViewById(R.id.etGrad2);
        final TextView tvResVol1 = (TextView) findViewById(R.id.tvResVol1);
        final TextView tvResVol2 = (TextView) findViewById(R.id.tvResVol2);
        final EditText etMaxVolume = (EditText) findViewById(R.id.etMaxVolume);
        final TextView tvB1id = (TextView) findViewById(R.id.tvB1id);
        final TextView tvB2id = (TextView) findViewById(R.id.tvB2id);
        final Button btnOK = (Button) findViewById(R.id.btnMixOK);

        //sid1 = getIntent().getExtras().getString("sid1");
        //sid2 = getIntent().getExtras().getString("sid2");
        action = getIntent().getExtras().getInt("action");
        if(action == R.id.nav_mix) {
            btnOK.setEnabled(false);
        }else if(action == R.id.btnMixOK){
            id1 = getIntent().getExtras().getInt("id1");
            id2 = getIntent().getExtras().getInt("id2");
            db = new alkosql(getApplicationContext());

            final Bottle b1 = db.getBottle(id1);
            final Bottle b2 = db.getBottle(id2);
            Log.d("DO_MIX", "id1: " + b1.getsId());
            Log.d("DO_MIX", "id2: " + b2.getsId());
            tvB1id.setText(b1.getsId());
            tvB2id.setText(b2.getsId());
            etGrad1.setText(String.valueOf(b1.getAlco()));
            etGrad2.setText(String.valueOf(b2.getAlco()));

            btnOK.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    iGrad = (int) Math.round(dGrad);
                    Log.d("MIX_BOTTLE", String.valueOf(iGrad));
                    Bottle bMix = new Bottle();
                    bMix.setAlco(iGrad);
                    b1.setVolume(iVol1);
                    b2.setVolume(iVol2);
                    String source = bMix.makeCoupage(b1, b2);

                    Intent intent = new Intent(mixBottle.this, addBottle.class);
                    intent.putExtra("alco", String.valueOf(iGrad));
                    intent.putExtra("source", source);
                    intent.putExtra("volume", etMaxVolume.getText().toString());
                    startActivity(intent);
                }
            });

        }

        etGrad1.setOnFocusChangeListener(new EditText.OnFocusChangeListener() {

            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    int iPos = sbMix.getProgress();
                    int iMax = sbMix.getMax();
                    doMix(etGrad1.getText().toString(),
                            etGrad2.getText().toString(),
                            etMaxVolume.getText().toString(),
                            iMax, iPos);
                    tvResGrad.setText(String.format(Locale.ENGLISH,"%.1fº", dGrad));
                    tvResVol1.setText(String.valueOf(iVol1));
                    tvResVol2.setText(String.valueOf(iVol2));
                }
            }
        });

        etGrad2.setOnFocusChangeListener(new EditText.OnFocusChangeListener() {

            public void onFocusChange(View v, boolean hasFocus) {
                if(!hasFocus) {
                    int iPos = sbMix.getProgress();
                    int iMax = sbMix.getMax();
                    doMix(etGrad1.getText().toString(),
                            etGrad2.getText().toString(),
                            etMaxVolume.getText().toString(),
                            iMax, iPos);
                    tvResGrad.setText(String.format(Locale.ENGLISH,"%.1fº", dGrad));
                    tvResVol1.setText(String.valueOf(iVol1));
                    tvResVol2.setText(String.valueOf(iVol2));
                }
            }
        });

        etMaxVolume.setOnFocusChangeListener(new EditText.OnFocusChangeListener() {

            public void onFocusChange(View v, boolean hasFocus) {
                if(!hasFocus) {
                    int iPos = sbMix.getProgress();
                    int iMax = sbMix.getMax();
                    doMix(etGrad1.getText().toString(),
                            etGrad2.getText().toString(),
                            etMaxVolume.getText().toString(),
                            iMax, iPos);
                    tvResGrad.setText(String.format(Locale.ENGLISH,"%.1fº", dGrad));
                    tvResVol1.setText(String.valueOf(iVol1));
                    tvResVol2.setText(String.valueOf(iVol2));
                }
            }
        });

        sbMix.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int iPos, boolean fromUser) {
                int iMax = seekBar.getMax();
                doMix(etGrad1.getText().toString(),
                        etGrad2.getText().toString(),
                        etMaxVolume.getText().toString(),
                        iMax, iPos);
                tvResGrad.setText(String.format(Locale.ENGLISH,"%.1fº", dGrad));
                tvResVol1.setText(String.valueOf(iVol1));
                tvResVol2.setText(String.valueOf(iVol2));
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                int iPos = seekBar.getProgress();
                int iMax = seekBar.getMax();
                doMix(etGrad1.getText().toString(),
                        etGrad2.getText().toString(),
                        etMaxVolume.getText().toString(),
                        iMax, iPos);
                tvResGrad.setText(String.format(Locale.ENGLISH,"%.1fº", dGrad));
                tvResVol1.setText(String.valueOf(iVol1));
                tvResVol2.setText(String.valueOf(iVol2));

            }


            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }
        });


    }

    public void doMix(String sGr1, String sGr2, String sMaxVolume, int iMax, int iPos) {
        // TODO Сделать расчет сахара
        Toast toast = new Toast(getApplicationContext());
        if (sGr1.isEmpty() || sGr2.isEmpty() || sMaxVolume.isEmpty()) {
            toast.makeText(getApplicationContext(),"Поля не должны быть пустыми",Toast.LENGTH_LONG).show();
        }else{
            //toast.cancel();
            int iGr1 = Integer.valueOf(sGr1);
            int iGr2 = Integer.valueOf(sGr2);
            int iMaxVolume = Integer.valueOf(sMaxVolume);
            double iEd = iMaxVolume / iMax;
            double iVol1 = iPos * iMaxVolume / 100;
            double iVol2 = (iMax - iPos) * iMaxVolume / 100;
            double iVolAlco = iPos * iGr1 * iEd / 100 +
                    (iMax - iPos) * iGr2 * iEd / 100;
            ArrayList res = new ArrayList(3);
            this.iVol1 = (int) Math.round(iVol1);
            this.iVol2 = (int) Math.round(iVol2);
            this.dGrad = iVolAlco / iMaxVolume * 100;
        }

    }


}
