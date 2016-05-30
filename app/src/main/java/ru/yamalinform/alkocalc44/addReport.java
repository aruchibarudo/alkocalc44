package ru.yamalinform.alkocalc44;

import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RatingBar;

public class addReport extends AppCompatActivity {
    private int bottleId;
    private alkosql db;
    private float stars = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_report);

        final RatingBar rbReport = (RatingBar) findViewById(R.id.rbReport);
        final EditText etReport = (EditText) findViewById(R.id.etReport);
        final Button btnReportSave = (Button) findViewById(R.id.btnReportSave);

        bottleId = getIntent().getExtras().getInt("bottleId");
        db = new alkosql(getApplicationContext());

        rbReport.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float v, boolean b) {
                stars = v;
            }
        });

        btnReportSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(etReport.getText().length() > 3) {
                    Report report = new Report(bottleId);
                    report.setStars((int) stars);
                    report.setReport(etReport.getText().toString());
                    report.setAlkach("archyz@gmail.com");
                    db.addReport(report);
                    Snackbar.make(view, "Записано", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                    finish();
                }else{
                    Snackbar.make(view, "Маловато, продолжай еще", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                }
            }
        });

    }
}
