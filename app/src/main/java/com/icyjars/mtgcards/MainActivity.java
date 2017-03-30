package com.icyjars.mtgcards;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity implements
        CardsListFragment.OnListFragmentInteractionListener,
        SimpleSearchFragment.OnNewSearchRecordListener {

    private RecyclerView recyclerView = null;
    private final FragmentManager fragmentManager = getFragmentManager();
    private CardsListAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        setContentView(R.layout.activity_main);
        adapter = new CardsListAdapter();

        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.add(R.id.main_linear_layout, new SimpleSearchFragment(), "SEARCH");
        fragmentTransaction.commit();
        fragmentManager.executePendingTransactions();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
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

    @Override
    public void onListFragmentInteraction(String cardName, int multiverseid) {

        CardFragment fr = new CardFragment();
        fr.setCardName(cardName);
        fr.setCardMultiverseId(multiverseid);

        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.remove(fragmentManager.findFragmentByTag("SEARCH"));
        fragmentTransaction.remove(fragmentManager.findFragmentByTag("LIST"));
        fragmentTransaction.add(R.id.main_linear_layout,fr,"CARD");
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
        fragmentManager.executePendingTransactions();

    }

    @Override
    public CardsListAdapter onNewSearchRecord() {

        setRecyclerView();
        adapter.clearData();
        return adapter;

    }

    private void setRecyclerView(){

        recyclerView = (RecyclerView) findViewById(R.id.list);

        if (recyclerView == null){

            adapter.clearData();

            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            CardsListFragment fr = new CardsListFragment();
            fragmentTransaction.add(R.id.main_linear_layout, fr, "LIST");
            fragmentTransaction.addToBackStack(null);
            fragmentTransaction.commit();
            fragmentManager.executePendingTransactions();

            recyclerView = (RecyclerView) findViewById(R.id.list);
            recyclerView.setAdapter(adapter);
            adapter.setListener(fr);

        }

    }

    @Override
    public void onNewSearchRecord(int resposneCode) {

        String message = String.valueOf(resposneCode) + " internet connection error";
        Toast.makeText(this,message,Toast.LENGTH_SHORT).show();

    }
}
