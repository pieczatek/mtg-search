package com.icyjars.mtgcards;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.PopupWindow;
import android.widget.ProgressBar;

public class MainActivity extends AppCompatActivity implements
        CardsListFragment.OnListFragmentInteractionListener,
        SimpleSearchFragment.OnNewSearchRecordListener {

    RecyclerView recyclerView;
    private final FragmentManager fragmentManager = getFragmentManager();
    private Fragment currentFragment = null;
    private Fragment currentToolbar = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        setContentView(R.layout.activity_main);
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        currentFragment = new SimpleSearchFragment();
        currentToolbar = new MainToolbarFragment();
        fragmentTransaction.replace(R.id.content_main, currentFragment);
        fragmentTransaction.replace(R.id.navigation_main, currentToolbar);
        fragmentTransaction.commit();

        /*
        currentToolbar = (Toolbar) findViewById(R.id.mainToolbar);
        setSupportActionBar(currentToolbar);
        */
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

        setNewFragment(new CardFragment(), new CardToolbarFragment());
        ((CardFragment)currentFragment).setCardName(cardName);
        ((CardFragment)currentFragment).setCardMultiverseId(multiverseid);
        this.fragmentManager.executePendingTransactions();

    }


    private void setNewFragment(Fragment newFragment, Fragment newToolbarFragment){

        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        currentFragment = newFragment;
        currentToolbar = newToolbarFragment;
        fragmentTransaction.replace(R.id.content_main, currentFragment);
        fragmentTransaction.replace(R.id.navigation_main, currentToolbar);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();

    }

    @Override
    public void onNewSearchRecord(LightCardsListInfoContainer container) {

        setNewFragment(new CardsListFragment(), new SearchToolbarFragment());
        this.fragmentManager.executePendingTransactions();

        recyclerView = (RecyclerView)((CardsListFragment)currentFragment).mView;
        CardsListAdapter adapter = new CardsListAdapter();
        adapter.setContainer(container);
        adapter.setListener(currentFragment);

        recyclerView.setAdapter(adapter);

    }

    @Override
    public void onNewSearchRecord(int resposneCode) {

        Button b = new Button(getApplicationContext());
        b.setText(String.valueOf(resposneCode));
        b.setWidth(150);
        b.setHeight(150);
        b.setBackgroundColor(Color.RED);

        final PopupWindow window = new PopupWindow(b,150,150);

        b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                window.dismiss();
            }
        });

        ((SimpleSearchFragment)currentFragment).setPopupWindow(window);
    }
}
