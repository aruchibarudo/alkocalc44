package ru.yamalinform.alkocalc44;

import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.support.annotation.LayoutRes;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

//import android.widget.Button;
//import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
//import android.widget.TextView;

import org.json.JSONException;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

public class viewBottle extends AppCompatActivity {

    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    private SectionsPagerAdapter mSectionsPagerAdapter;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    private ViewPager mViewPager;

    public static List<Bottle> Bottles;
    private alkosql db;
    private String filter;
    private int pos;
    private String order;
    private int bottleId;
    public static FloatingActionButton fab, fabReport;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_bottle);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        filter = getIntent().getExtras().getString("filter");
        pos = getIntent().getExtras().getInt("pos");
        order = getIntent().getExtras().getString("order");
        db = new alkosql(getApplicationContext());
        Bottles = db.searchBottles(filter, order);

        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);
        mViewPager.setCurrentItem(pos);


        fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Snackbar.make(view, "Клонировать бутыльку", Snackbar.LENGTH_LONG)
                //        .setAction("Action", null).show();

                Intent intent = new Intent(viewBottle.this, updateBottle.class);
                //intent.putExtra("filter", mSectionsPagerAdapter.getItem(pos).getView().findViewById(R.id.alco));
                bottleId = Bottles.get(mViewPager.getCurrentItem()).getId();
                intent.putExtra("bottleId", bottleId);
                startActivity(intent);
            }
        });

        fabReport = (FloatingActionButton) findViewById(R.id.fabReport);
        fabReport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                bottleId = Bottles.get(mViewPager.getCurrentItem()).getId();
                Snackbar.make(view, "Добавить репорт к " + bottleId, Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();

                Intent intent = new Intent(viewBottle.this, addReport.class);
                bottleId = Bottles.get(mViewPager.getCurrentItem()).getId();
                intent.putExtra("bottleId", bottleId);
                startActivity(intent);
            }
        });


    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_bottle, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private static final String ARG_SECTION_NUMBER = "section_number";
        private TextView etSId;
        private TextView etAlco;
        private TextView etVolume;
        private TextView etPeregon;
        private TextView etSugar;
        private TextView etDate;
        private TextView etDescr, tvSrc1, tvSrc2;
        private RatingBar rbStars;

        public PlaceholderFragment() {
        }

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static PlaceholderFragment newInstance(int sectionNumber) {
            PlaceholderFragment fragment = new PlaceholderFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }



        @Override
        public View onCreateView(final LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_bottle, container, false);
            //View ParentView = inflater.inflate(R.layout.activity_view_bottle, container, false);

            //TextView tvLabel = (TextView) rootView.findViewById(R.id.section_label);
            ImageView ivPic = (ImageView) rootView.findViewById(R.id.ivPic);
            ivPic.setImageResource(getImage(getArguments().getInt(ARG_SECTION_NUMBER)));
            final Bottle bottle = viewBottle.Bottles.get(getArguments().getInt(ARG_SECTION_NUMBER));
            //tvLabel.setText(bottle.getsId());

            //viewBottle.bottleId = bottle.getId() - 1;
            etSId = (TextView) rootView.findViewById(R.id.sId);
            etAlco = (TextView) rootView.findViewById(R.id.alco);
            etVolume = (TextView) rootView.findViewById(R.id.volume);
            etPeregon = (TextView) rootView.findViewById(R.id.peregon);
            etSugar = (TextView) rootView.findViewById(R.id.sugar);
            etDate = (TextView) rootView.findViewById(R.id.date);
            etDescr = (TextView) rootView.findViewById(R.id.etDescr);
            tvSrc1 = (TextView) rootView.findViewById(R.id.tvSrc1);
            tvSrc2 = (TextView) rootView.findViewById(R.id.tvSrc2);
            rbStars = (RatingBar) rootView.findViewById(R.id.rbStars);

            etSId.setText(bottle.getsId());
            etAlco.setText(String.valueOf(bottle.getAlco()));
            etVolume.setText(String.valueOf(bottle.getVolume()));
            etPeregon.setText(String.valueOf(bottle.getPeregon()));
            etSugar.setText(String.valueOf(bottle.getSugar()));
            SimpleDateFormat sdf = new SimpleDateFormat("d.MM.yy", Locale.getDefault());
            etDate.setText(sdf.format(bottle.getDate()));
            etDescr.setText(bottle.getDescription());
            rbStars.setRating(bottle.getStars());
            rbStars.setIsIndicator(true);
            //rbStars.setClickable(true);
            tvSrc1.setVisibility(View.INVISIBLE);
            tvSrc2.setVisibility(View.INVISIBLE);
            /*FloatingActionButton fabRep = (FloatingActionButton) container.findViewById(R.id.fabReport);

            if(bottle.getReport() > 0) {
                fabRep.setVisibility(View.VISIBLE);
            }else{
                fabRep.setVisibility(View.INVISIBLE);
            }*/

            rbStars.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View view, MotionEvent motionEvent) {
                    if(motionEvent.getAction() == MotionEvent.ACTION_UP) {
                        int reports = bottle.getReport();
                        if(reports > 0) {
                            Snackbar.make(view, "Смотреть репортов: " + reports, Snackbar.LENGTH_LONG)
                                    .setAction("Action", null).show();
                            Intent intent = new Intent(getActivity(), viewReport.class);
                            intent.putExtra("bottleId", bottle.getId());
                            startActivity(intent);
                        } else {
                            Snackbar.make(view, "Нет отзывов", Snackbar.LENGTH_LONG)
                                    .setAction("Action", null).show();
                        }
                    }
                    return true;
                }
            });

            try {
                if(bottle.getSource().length() == 2) {
                    alkosql db = new alkosql(getContext());
                    int src1 = bottle.getSource().getJSONObject(0).getInt("id");
                    String vol1 = bottle.getSource().getJSONObject(0).getString("volume");
                    Log.d("VIEW_BOTTLE", String.valueOf(src1));
                    Bottle b1 = db.getBottle(src1);
                    if (b1!=null){
                        tvSrc1.setText(b1.getsId() + ": " + vol1 + "мл");
                        tvSrc1.setVisibility(View.VISIBLE);
                    }else{
                        tvSrc1.setText("Исходник " + String.valueOf(src1) + " в базе не найден");
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                            tvSrc1.setTextAppearance(android.R.style.TextAppearance_Small);
                        }
                        tvSrc1.setTextColor(Color.BLUE);
                        tvSrc1.setVisibility(View.VISIBLE);
                    }
                    int src2 = bottle.getSource().getJSONObject(1).getInt("id");
                    String vol2 = bottle.getSource().getJSONObject(1).getString("volume");
                    Log.d("VIEW_BOTTLE", String.valueOf(src2));
                    Bottle b2 = db.getBottle(src2);
                    if (b2!=null){
                        tvSrc2.setText(b2.getsId() + ": " + vol2 + "мл");
                        tvSrc2.setVisibility(View.VISIBLE);
                    }else{
                        tvSrc2.setText("Исходник " + String.valueOf(src2) + " в базе не найден");
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                            tvSrc2.setTextAppearance(android.R.style.TextAppearance_Small);
                        }
                        tvSrc2.setTextColor(Color.BLUE);
                        tvSrc2.setVisibility(View.VISIBLE);
                    }
                }
            }catch (JSONException e) {
                Log.e("VIEW_BOTTLE", e.getMessage());
                e.printStackTrace();
            }

            tvSrc1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                }
            });


            return rootView;
        }

        public int getImage(int position) {
            switch (position % 2) {
                case 0:
                    return R.drawable.vino;
                case 1:
                    return R.drawable.vodka;
            }
            return R.drawable.alkocalc_icon;
        }
    }

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
            //this.setPrimaryItem(null,pos,PlaceholderFragment);
        }

        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            // Return a PlaceholderFragment (defined as a static inner class below).
            return PlaceholderFragment.newInstance(position);
        }

        @Override
        public int getCount() {
            return Bottles.size();
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return Bottles.get(position).getsId();
        }


    }
}
