package ru.yamalinform.alkocalc44;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class viewReport extends AppCompatActivity {
    private ArrayList<HashMap<String, Object>> listReport;
    private SimpleAdapter adapterReport;
    private alkosql db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report_bottle);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        ListView lvReport = (ListView) findViewById(R.id.lvReport);
        TextView tvDescr = (TextView) findViewById(R.id.tvDescr);

        int bottleId = getIntent().getExtras().getInt("bottleId");
        db = new alkosql(getApplicationContext());
        Bottle bottle = db.getBottle(bottleId);
        toolbar.setTitle(bottle.getsId());
        tvDescr.setText(bottle.getDescription());

        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Запостить отчет???", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        listReport = new ArrayList<HashMap<String, Object>>();
        getReports(bottleId);

        adapterReport = new SimpleAdapter(
                viewReport.this, listReport,
                R.layout.list_report, new String[] { alkosql.KEY_REP_ALKACH,
                alkosql.KEY_REP_DATE,
                alkosql.KEY_REP_STARS,
                alkosql.KEY_REP_TEXT},
                new int[] { R.id.tvRepAlkach,
                        R.id.tvRepDate,
                        R.id.rbReport,
                        R.id.tvReport});
        ((SimpleAdapter) adapterReport).setViewBinder(new reportBinder());
        // updating listview

        lvReport.setAdapter(adapterReport);
        Log.d("VIEW_REP", "Size of listAdapter: " + String.valueOf(adapterReport.getCount()));
    }

    private void getReports(int bottleId) {
        Log.d("VIEW_REP", "BID: " + String.valueOf(bottleId));
        List<Report> reports = db.searchReports(bottleId);
        Log.d("VIEW_REP", "Size of Reports: " + String.valueOf(reports.size()));

        listReport.clear();
        HashMap<String, Object> hm;
        SimpleDateFormat sdf = new SimpleDateFormat("d.MM.yy", Locale.getDefault());

        for (int i = 0; i < reports.size(); i++) {
            hm = new HashMap<>();
            hm.put(alkosql.KEY_REP_ALKACH,reports.get(i).getAlkach());
            //Log.d("VIEW_REP", reports.get(i).getAlkach().toString());
            hm.put(alkosql.KEY_REP_DATE, reports.get(i).getDate().toString());
            hm.put(alkosql.KEY_REP_STARS,reports.get(i).getStars());
            hm.put(alkosql.KEY_REP_TEXT,reports.get(i).getReport());
            listReport.add(hm);
        }

    }
}

class reportBinder implements SimpleAdapter.ViewBinder {
    public boolean setViewValue(View view, Object data, String textRepresentation) {
        if(view.getId() == R.id.rbReport){
            int stars = (Integer) data;
            //float ratingValue = Float.parseFloat(stringval);
            RatingBar ratingBar = (RatingBar) view;
            ratingBar.setRating(stars);
            return true;
        }
        return false;
    }
}
