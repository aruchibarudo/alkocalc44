package ru.yamalinform.alkocalc44;

/**
 * Created by archy on 5/18/16.
 */
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Locale;

public class mixBottle extends AppCompatActivity {

    private alkosql db;

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

        String id1 = getIntent().getExtras().getString("id1");
        String id2 = getIntent().getExtras().getString("id2");
        db = new alkosql(getApplicationContext());
        Log.d("DO_MIX", "id1: " + id1);
        Log.d("DO_MIX", "id2: " + id2);
        Bottle b1 = db.getBottle(id1);
        Bottle b2 = db.getBottle(id2);

        etGrad1.setText(String.valueOf(b1.getAlco()));
        etGrad2.setText(String.valueOf(b2.getAlco()));

        etGrad1.setOnFocusChangeListener(new EditText.OnFocusChangeListener() {

            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    int iPos = sbMix.getProgress();
                    int iMax = sbMix.getMax();
                    ArrayList res = doMix(etGrad1.getText().toString(),
                            etGrad2.getText().toString(),
                            etMaxVolume.getText().toString(),
                            iMax, iPos);
                    if (res != null) {
                        tvResGrad.setText(res.get(0).toString());
                        tvResVol1.setText(res.get(1).toString());
                        tvResVol2.setText(res.get(2).toString());
                    }
                }
            }
        });

        etGrad2.setOnFocusChangeListener(new EditText.OnFocusChangeListener() {

            public void onFocusChange(View v, boolean hasFocus) {
                if(!hasFocus) {
                    int iPos = sbMix.getProgress();
                    int iMax = sbMix.getMax();
                    ArrayList res = doMix(etGrad1.getText().toString(),
                            etGrad2.getText().toString(),
                            etMaxVolume.getText().toString(),
                            iMax, iPos);
                    if(res != null) {
                        tvResGrad.setText(res.get(0).toString());
                        tvResVol1.setText(res.get(1).toString());
                        tvResVol2.setText(res.get(2).toString());
                    }
                }
            }
        });

        etMaxVolume.setOnFocusChangeListener(new EditText.OnFocusChangeListener() {

            public void onFocusChange(View v, boolean hasFocus) {
                if(!hasFocus) {
                    int iPos = sbMix.getProgress();
                    int iMax = sbMix.getMax();
                    ArrayList res = doMix(etGrad1.getText().toString(),
                            etGrad2.getText().toString(),
                            etMaxVolume.getText().toString(),
                            iMax, iPos);
                    if(res != null) {
                        tvResGrad.setText(res.get(0).toString());
                        tvResVol1.setText(res.get(1).toString());
                        tvResVol2.setText(res.get(2).toString());
                    }
                }
            }
        });

        sbMix.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int iPos, boolean fromUser) {
                int iMax = seekBar.getMax();
                ArrayList res = doMix(etGrad1.getText().toString(),
                        etGrad2.getText().toString(),
                        etMaxVolume.getText().toString(),
                        iMax, iPos);
                if(res != null) {
                    tvResGrad.setText(res.get(0).toString());
                    tvResVol1.setText(res.get(1).toString());
                    tvResVol2.setText(res.get(2).toString());
                }
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                int iPos = seekBar.getProgress();
                int iMax = seekBar.getMax();
                ArrayList res = doMix(etGrad1.getText().toString(),
                        etGrad2.getText().toString(),
                        etMaxVolume.getText().toString(),
                        iMax, iPos);
                if(res != null) {
                    tvResGrad.setText(res.get(0).toString());
                    tvResVol1.setText(res.get(1).toString());
                    tvResVol2.setText(res.get(2).toString());
                }

            }


            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }
        });


    }

    public ArrayList doMix(String sGr1, String sGr2, String sMaxVolume, int iMax, int iPos) {
        Toast toast = new Toast(getApplicationContext());
        if (sGr1.isEmpty() || sGr2.isEmpty() || sMaxVolume.isEmpty()) {
            toast.makeText(getApplicationContext(),"Поля не должны быть пустыми",Toast.LENGTH_LONG).show();
            return null;
        }else{
            toast.cancel();
            int iGr1 = Integer.valueOf(sGr1);
            int iGr2 = Integer.valueOf(sGr2);
            int iMaxVolume = Integer.valueOf(sMaxVolume);
            double iEd = iMaxVolume / iMax;
            double iVol1 = iPos * iMaxVolume / 100;
            double iVol2 = (iMax - iPos) * iMaxVolume / 100;
            double iVolAlco = iPos * iGr1 * iEd / 100 +
                    (iMax - iPos) * iGr2 * iEd / 100;
            ArrayList res = new ArrayList(3);
            res.add(0,String.format(Locale.ENGLISH,"%.1fº", iVolAlco / iMaxVolume * 100));
            res.add(1,String.valueOf((int) Math.round(iVol1)));
            res.add(2,String.valueOf((int) Math.round(iVol2)));
            return res;
        }

    }


}
