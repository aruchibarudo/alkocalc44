package ru.yamalinform.alkocalc44;

import android.app.SearchManager;
import android.content.Intent;
//import android.net.Uri;
import android.database.Cursor;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.widget.CursorAdapter;
import android.support.v4.widget.SimpleCursorAdapter;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

//import com.google.android.gms.appindexing.Action;
//import com.google.android.gms.appindexing.AppIndex;
//import com.google.android.gms.common.api.GoogleApiClient;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class MainActivity extends AppCompatActivity {

    private ArrayAdapter<String> listAdapter;
    private alkosql db;
    private List<Bottle> Bottles;
    private ArrayList<String> listBottles = new ArrayList<>();
    private ListView lvBottles;
    private String filter;
    private FloatingActionButton fab;
    private Menu menu;
    private SparseBooleanArray mixArray;
    private String[] alkotype;
    public Cursor cursorSugg;
    public CursorAdapter searchAdapter;



    final String LOG_TAG = "list_bottle";
    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    //private GoogleApiClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_bottle);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, addBottle.class);
                startActivity(intent);
                //Snackbar.make(view, "Добавить бутыльку", Snackbar.LENGTH_LONG).setAction("Action", null).show();
            }
        });
        db = new alkosql(getApplicationContext(), MainActivity.this);
        lvBottles = (ListView) findViewById(R.id.lvBottles);
        listAdapter = new ArrayAdapter<>(this, R.layout.list_view, listBottles);
        //alkotype = getResources().getStringArray(R.array.alkotype);

        lvBottles.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                Log.d(LOG_TAG, "itemClick: position = " + position + ", id = " + id);
                //Toast.makeText(getApplicationContext(), "itemClick: position = " + position + ", id = " + id, Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(MainActivity.this, viewBottle.class);
                intent.putExtra("filter", filter);
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
        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        //client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        this.menu = menu;
        getMenuInflater().inflate(R.menu.menu_list_bottles, menu);

        menu.getItem(2).setVisible(true);
        menu.getItem(3).setVisible(false);

        final MenuItem searchItem = menu.findItem(R.id.action_search);
        final SearchView searchView = (SearchView) searchItem.getActionView();




        //searchView.setQuery(filter,false);

        SearchManager searchManager = (SearchManager) getSystemService(getApplicationContext().SEARCH_SERVICE);
        if(null!=searchManager ) {
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
                searchView.setQuery(cursorSugg.getString(2), false);
                return false;
            }
        });

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {

            @Override
            public boolean onQueryTextSubmit(String query) {
                //Toast.makeText(getApplicationContext(), query, Toast.LENGTH_SHORT);
                if(query.length() > 2) {
                    filter = query;
                    getBottles(filter);
                }else{
                    Toast.makeText(getApplicationContext(), "Надо больше букв", Toast.LENGTH_SHORT).show();
                }

                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                String[] from = new String[] {db.KEY_DICT_VALUE};
                int[] to = new int[]{android.R.id.text1};

                //MainActivity.this.listAdapter.getFilter().filter(newText);
                cursorSugg = db.searchDict(db.TYPE_ALKO, newText);
                searchAdapter = new SimpleCursorAdapter(getApplicationContext(), android.R.layout.simple_list_item_1, cursorSugg,from,to,CursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER);
                searchView.setSuggestionsAdapter(searchAdapter);
                searchAdapter.notifyDataSetChanged();
                return false;
            }

        });

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // TODO Auto-generated method stub
        switch (item.getItemId()) {
            case R.id.mCoupage:

                listAdapter = new ArrayAdapter<>(this, R.layout.list_checked, listBottles);
                getBottles(filter);


                lvBottles.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    public void onItemClick(AdapterView<?> parent, View view,
                                            int position, long id) {
                        Log.d(LOG_TAG, "itemClick: position = " + position + ", id = " + id);
                        //Toast.makeText(getApplicationContext(), "itemClick: position = " + position + ", id = " + id, Toast.LENGTH_SHORT).show();
                        mixArray = lvBottles.getCheckedItemPositions();
                        Log.d("TO_MIX", mixArray.toString());
                        if(lvBottles.getCheckedItemCount() > 2) {
                            Toast.makeText(getApplicationContext(), "Нельзя купажировать больше двух", Toast.LENGTH_SHORT).show();
                            lvBottles.setItemChecked(position, false);
                        }
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
                        if(mixArray!=null && mixArray.size() == 2) {
                            Intent intent = new Intent(MainActivity.this, mixBottle.class);
                            for(int i=0; i<mixArray.size(); i++) {
                                Log.d("TO_MIX", "id" + String.valueOf(i+1) + ":" + String.valueOf(Bottles.get(mixArray.keyAt(i)).getId()));
                                intent.putExtra("id" + String.valueOf(i+1), Bottles.get(mixArray.keyAt(i)).getId());
                            }
                            //intent.putExtra("id1", )
                            startActivity(intent);
                        }else {
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
                        Intent intent = new Intent(MainActivity.this, addBottle.class);
                        startActivity(intent);
                        //Snackbar.make(view, "Добавить бутыльку", Snackbar.LENGTH_LONG).setAction("Action", null).show();
                    }
                });
                listAdapter = new ArrayAdapter<>(this, R.layout.list_view, listBottles);
                getBottles(filter);

                lvBottles.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    public void onItemClick(AdapterView<?> parent, View view,
                                            int position, long id) {
                        Log.d(LOG_TAG, "itemClick: position = " + position + ", id = " + id);
                        //Toast.makeText(getApplicationContext(), "itemClick: position = " + position + ", id = " + id, Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(MainActivity.this, viewBottle.class);
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
        }
        return super.onOptionsItemSelected(item);
    }

    private void getBottles(String filter) {
        Bottles = db.searchBottles(filter);
        Log.d("MAIN", "Size of Bottles: " + String.valueOf(Bottles.size()));

        listAdapter.clear();

        for (int i = 0; i < Bottles.size(); i++) {
            listAdapter.add(Bottles.get(i).getsId());
        }
        Log.d("MAIN", "Size of listAdapter: " + String.valueOf(listAdapter.getCount()));
        try {
            lvBottles.setAdapter(listAdapter);
        } catch (Exception e) {
            Log.e(LOG_TAG, e.toString());
        }
    }

    @Override
    public void onStart() {
        super.onStart();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        /*client.connect();
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "Main Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app URL is correct.
                Uri.parse("android-app://ru.yamalinform.alkocalc44/http/host/path")
        );
        AppIndex.AppIndexApi.start(client, viewAction);*/
    }

    @Override
    public void onStop() {
        super.onStop();
/*
        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "Main Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app URL is correct.
                Uri.parse("android-app://ru.yamalinform.alkocalc44/http/host/path")
        );
        AppIndex.AppIndexApi.end(client, viewAction);
        client.disconnect();*/
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d("MAIN", "onResume()");
        lvBottles.clearChoices();
        getBottles(filter);
    }
}
