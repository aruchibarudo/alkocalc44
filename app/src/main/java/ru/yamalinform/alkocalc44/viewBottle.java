package ru.yamalinform.alkocalc44;

import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

//import android.widget.Button;
//import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
//import android.widget.TextView;

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
    private int bottleId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_bottle);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        filter = getIntent().getExtras().getString("filter");
        pos = getIntent().getExtras().getInt("pos");
        db = new alkosql(getApplicationContext());
        Bottles = db.searchBottles(filter);

        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);
        mViewPager.setCurrentItem(pos);


        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Клонировать бутыльку", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();

                Intent intent = new Intent(viewBottle.this, updateBottle.class);
                //intent.putExtra("filter", mSectionsPagerAdapter.getItem(pos).getView().findViewById(R.id.alco));
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
        private TextView etDescr;

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
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_bottle, container, false);
            //TextView tvLabel = (TextView) rootView.findViewById(R.id.section_label);
            ImageView ivPic = (ImageView) rootView.findViewById(R.id.ivPic);
            ivPic.setImageResource(getImage(getArguments().getInt(ARG_SECTION_NUMBER)));
            Bottle bottle = viewBottle.Bottles.get(getArguments().getInt(ARG_SECTION_NUMBER));
            //tvLabel.setText(bottle.getsId());

            //viewBottle.bottleId = bottle.getId() - 1;
            etSId = (TextView) rootView.findViewById(R.id.sId);
            etAlco = (TextView) rootView.findViewById(R.id.alco);
            etVolume = (TextView) rootView.findViewById(R.id.volume);
            etPeregon = (TextView) rootView.findViewById(R.id.peregon);
            etSugar = (TextView) rootView.findViewById(R.id.sugar);
            etDate = (TextView) rootView.findViewById(R.id.date);
            etDescr = (TextView) rootView.findViewById(R.id.etDescr);

            etSId.setText(bottle.getsId());
            etAlco.setText(String.valueOf(bottle.getAlco()));
            etVolume.setText(String.valueOf(bottle.getVolume()));
            etPeregon.setText(String.valueOf(bottle.getPeregon()));
            etSugar.setText(String.valueOf(bottle.getSugar()));
            SimpleDateFormat sdf = new SimpleDateFormat("d.MM.yy", Locale.getDefault());
            etDate.setText(sdf.format(bottle.getDate()));
            etDescr.setText(bottle.getDescription());


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
