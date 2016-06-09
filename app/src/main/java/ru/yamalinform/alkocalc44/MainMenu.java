package ru.yamalinform.alkocalc44;

import android.app.SearchManager;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.CursorAdapter;
import android.support.v4.widget.SimpleCursorAdapter;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.CheckedTextView;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class MainMenu extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private SimpleAdapter listAdapter;
    private alkosql db;
    private List<Bottle> Bottles;
    private ArrayList<HashMap<String, Object>> listBottles = new ArrayList<>();
    private ListView lvBottles;
    private String filter;
    private String order = "b.date";
    private FloatingActionButton fab;
    private Menu menu;
    private SparseBooleanArray mixArray;
    private String[] alkotype;
    public Cursor cursorSugg;
    public CursorAdapter searchAdapter;

    final String LOG_TAG = "list_bottle";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainMenu.this, addBottle.class);
                startActivity(intent);
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        db = new alkosql(getApplicationContext(), MainMenu.this);
        lvBottles = (ListView) findViewById(R.id.lvBottles);
        //listAdapter = new ArrayAdapter<>(this, R.layout.list_view, listBottles);
        listAdapter = new SimpleAdapter(
                MainMenu.this, listBottles,
                R.layout.list_bottles, new String[]{alkosql.KEY_SID,
                alkosql.KEY_DATE,
                alkosql.KEY_REP_STARS},
                new int[]{R.id.tvSid,
                        R.id.tvDate,
                        R.id.rbReport});
        listAdapter.setViewBinder(new adapterBinder());
        //alkotype = getResources().getStringArray(R.array.alkotype);
        lvBottles.setAdapter(listAdapter);
        listAdapter.notifyDataSetChanged();

        lvBottles.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                Log.d(LOG_TAG, "itemClick: position = " + position + ", id = " + id);
                //Toast.makeText(getApplicationContext(), "itemClick: position = " + position + ", id = " + id, Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(MainMenu.this, viewBottle.class);
                intent.putExtra("filter", filter);
                intent.putExtra("order", order);
                intent.putExtra("pos", position);
                startActivity(intent);
            }
        });

        lvBottles.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> parent, View view,
                                       int position, long id) {
                Log.d(LOG_TAG, "itemSelect: position = " + position + ", id = " + id);
            }

            public void onNothingSelected(AdapterView<?> parent) {
                Log.d(LOG_TAG, "itemSelect: nothing");
            }
        });

    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        this.menu = menu;
        getMenuInflater().inflate(R.menu.menu_list_bottles, menu);

        menu.getItem(2).setVisible(true);
        menu.getItem(3).setVisible(false);

        final MenuItem searchItem = menu.findItem(R.id.action_search);
        final SearchView searchView = (SearchView) searchItem.getActionView();
        SearchManager searchManager = (SearchManager) getSystemService(getApplicationContext().SEARCH_SERVICE);
        if (null != searchManager) {
            searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        }
        searchView.setIconifiedByDefault(false);

        searchView.setOnSuggestionListener(new SearchView.OnSuggestionListener() {

            @Override
            public boolean onSuggestionSelect(int position) {
                return false;
            }

            @Override
            public boolean onSuggestionClick(int position) {
                cursorSugg.moveToPosition(position);
                searchView.setQuery(cursorSugg.getString(2), true);
                return false;
            }
        });

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {

            @Override
            public boolean onQueryTextSubmit(String query) {
                //Toast.makeText(getApplicationContext(), query, Toast.LENGTH_SHORT);
                if (query.length() > 2) {
                    filter = query;
                    getBottles(filter);
                    listAdapter.notifyDataSetChanged();
                } else {
                    Toast.makeText(getApplicationContext(), "Надо больше букв", Toast.LENGTH_SHORT).show();
                }

                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                String[] from = new String[]{db.KEY_DICT_VALUE};
                int[] to = new int[]{android.R.id.text1};

                //MainActivity.this.listAdapter.getFilter().filter(newText);
                cursorSugg = db.searchDict(db.TYPE_ALKO, newText);
                searchAdapter = new SimpleCursorAdapter(getApplicationContext(), android.R.layout.simple_list_item_1, cursorSugg, from, to, CursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER);
                searchView.setSuggestionsAdapter(searchAdapter);
                searchAdapter.notifyDataSetChanged();
                return false;
            }

        });


        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        switch (item.getItemId()) {
            case R.id.mCoupage:

                //listAdapter = new ArrayAdapter<>(this, R.layout.list_checked, listBottles);
                listAdapter = new SimpleAdapter(
                        MainMenu.this, listBottles,
                        R.layout.list_bottles_check, new String[]{alkosql.KEY_SID,
                        alkosql.KEY_DATE,
                        alkosql.KEY_REP_STARS},
                        new int[]{R.id.tvSid,
                                R.id.tvDate,
                                R.id.rbReport});
                ((SimpleAdapter) listAdapter).setViewBinder(new adapterBinder());
                getBottles(filter);
                lvBottles.setAdapter(listAdapter);
                listAdapter.notifyDataSetChanged();


                lvBottles.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    public void onItemClick(AdapterView<?> parent, View view,
                                            int position, long id) {
                        Log.d(LOG_TAG, "itemClick: position = " + position + ", id = " + id);
                        //Toast.makeText(getApplicationContext(), "itemClick: position = " + position + ", id = " + id, Toast.LENGTH_SHORT).show();

                        if (lvBottles.getCheckedItemCount() > 2) {
                            Toast.makeText(getApplicationContext(), "Нельзя купажировать больше двух", Toast.LENGTH_SHORT).show();
                            lvBottles.setItemChecked(position, false);
                        }else{

                            //CheckedTextView cbMix = (CheckedTextView) lvBottles.getSelectedView();
                            if(lvBottles.isItemChecked(position)) {
                                CheckedTextView v = (CheckedTextView) lvBottles.getChildAt(position).findViewById(R.id.img);
                                v.setChecked(true);
                            }else{
                                CheckedTextView v = (CheckedTextView) lvBottles.getChildAt(position).findViewById(R.id.img);
                                v.setChecked(false);
                            }
                        }
                        mixArray = lvBottles.getCheckedItemPositions();
                        if (mixArray != null) {
                            for (int i = 0; i < mixArray.size(); i++) {
                                if(!mixArray.valueAt(i)) {
                                    mixArray.delete(mixArray.keyAt(i));
                                }
                            }
                        }
                        Log.d("TO_MIX", mixArray.toString());

                        /*Intent intent = new Intent(MainActivity.this, viewBottle.class);
                        intent.putExtra("filter", filter);
                        intent.putExtra("pos", position);
                        startActivity(intent);*/
                    }
                });

                fab.setImageResource(android.R.drawable.ic_menu_delete);
                fab.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        if(mixArray.size() == 2) {
                            Intent intent = new Intent(MainMenu.this, mixBottle.class);
                            for (int i = 0; i < mixArray.size(); i++) {
                                if(mixArray.valueAt(i)) {
                                    Log.d("TO_MIX", "id" + String.valueOf(i+1) + ":" + String.valueOf(Bottles.get(mixArray.keyAt(i)).getId()));
                                    intent.putExtra("id" + String.valueOf(i+1), Bottles.get(mixArray.keyAt(i)).getId());
                                }
                            }
                            startActivity(intent);
                        } else {
                            Toast.makeText(getApplicationContext(), "Минимум две бутылки", Toast.LENGTH_LONG).show();
                        }

                    }
                });

                //menu.clear();
                menu.getItem(2).setVisible(false);
                menu.getItem(3).setVisible(true);
                break;
            case R.id.mCancel:
                lvBottles.clearChoices();
                fab.setImageResource(android.R.drawable.ic_menu_add);
                fab.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(MainMenu.this, addBottle.class);
                        startActivity(intent);
                        //Snackbar.make(view, "Добавить бутыльку", Snackbar.LENGTH_LONG).setAction("Action", null).show();
                    }
                });
                //listAdapter = new ArrayAdapter<>(this, R.layout.list_view, listBottles);
                listAdapter = new SimpleAdapter(
                        MainMenu.this, listBottles,
                        R.layout.list_bottles, new String[]{alkosql.KEY_SID,
                        alkosql.KEY_DATE,
                        alkosql.KEY_REP_STARS},
                        new int[]{R.id.tvSid,
                                R.id.tvDate,
                                R.id.rbReport});
                ((SimpleAdapter) listAdapter).setViewBinder(new adapterBinder());
                getBottles(filter);
                lvBottles.setAdapter(listAdapter);
                listAdapter.notifyDataSetChanged();

                lvBottles.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    public void onItemClick(AdapterView<?> parent, View view,
                                            int position, long id) {
                        Log.d(LOG_TAG, "itemClick: position = " + position + ", id = " + id);
                        //Toast.makeText(getApplicationContext(), "itemClick: position = " + position + ", id = " + id, Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(MainMenu.this, viewBottle.class);
                        intent.putExtra("filter", filter);
                        intent.putExtra("pos", position);
                        startActivity(intent);
                    }
                });

                //menu.clear();
                menu.getItem(2).setVisible(true);
                menu.getItem(3).setVisible(false);
                break;
            case R.id.mSearchCancel:
                filter = "";
                getBottles(filter);
                listAdapter.notifyDataSetChanged();
                break;
            case R.id.mOrderByStars:
                if (item.isChecked()) item.setChecked(false);
                else item.setChecked(true);
                order = db.KEY_REP_STARS;
                getBottles(filter);
                listAdapter.notifyDataSetChanged();
                break;
            case R.id.mOrderByDate:
                if (item.isChecked()) item.setChecked(false);
                else item.setChecked(true);
                order = "b." + db.KEY_DATE;
                getBottles(filter);
                listAdapter.notifyDataSetChanged();
                break;
            case R.id.mOrderByType:
                if (item.isChecked()) item.setChecked(false);
                else item.setChecked(true);
                order = "d." + db.KEY_DICT_VALUE;
                getBottles(filter);
                listAdapter.notifyDataSetChanged();
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        // TODO Реализовать: Смешивалку без бутылей, Експорт то xml/csv,
        // TODO Настройки (изменение словарей/параметров автовыбора типа алкоголя),
        // TODO Добавление локейшена, Активити логина в алкосеть
        int id = item.getItemId();

        if (id == R.id.nav_camera) {
            // Handle the camera action
        } else if (id == R.id.nav_gallery) {

        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void getBottles(String filter) {
        Bottles = db.searchBottles(filter, this.order);
        Log.d("MAIN", "Size of Bottles: " + String.valueOf(Bottles.size()));

        listBottles.clear();
        HashMap<String, Object> hm;
        SimpleDateFormat sdf = new SimpleDateFormat("d.MM.yy", Locale.getDefault());

        for (int i = 0; i < Bottles.size(); i++) {
            hm = new HashMap<>();
            hm.put(alkosql.KEY_SID, Bottles.get(i).getsId());
            //Log.d("VIEW_REP", reports.get(i).getAlkach().toString());
            hm.put(alkosql.KEY_DATE, Bottles.get(i).getDate().toString());
            hm.put(alkosql.KEY_REP_STARS, Bottles.get(i).getStars());
            //hm.put(alkosql.KEY_REP_TEXT,reports.get(i).getReport());
            listBottles.add(hm);
        }
    }

    private void getBottles(String filter, String order) {
        Bottles = db.searchBottles(filter, order);
        Log.d("MAIN", "Size of Bottles: " + String.valueOf(Bottles.size()));

        listBottles.clear();
        HashMap<String, Object> hm;
        SimpleDateFormat sdf = new SimpleDateFormat("d.MM.yy", Locale.getDefault());

        for (int i = 0; i < Bottles.size(); i++) {
            hm = new HashMap<>();
            hm.put(alkosql.KEY_SID, Bottles.get(i).getsId());
            //Log.d("VIEW_REP", reports.get(i).getAlkach().toString());
            hm.put(alkosql.KEY_DATE, Bottles.get(i).getDate().toString());
            hm.put(alkosql.KEY_REP_STARS, Bottles.get(i).getStars());
            //hm.put(alkosql.KEY_REP_TEXT,reports.get(i).getReport());
            listBottles.add(hm);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d("MAIN", "onResume()");
        //lvBottles.clearChoices();
        getBottles(filter);
        listAdapter.notifyDataSetChanged();
    }

}

