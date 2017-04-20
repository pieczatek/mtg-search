package com.icyjars.mtgcards;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.PopupWindow;
import android.widget.Toast;

import com.icyjars.mtgcards.Fragment.CardFragment;
import com.icyjars.mtgcards.Fragment.CardsListFragment;
import com.icyjars.mtgcards.Fragment.SimpleSearchFragment;
import com.icyjars.mtgcards.Model.Mtgio;

public class MainActivity extends AppCompatActivity implements
        CardsListFragment.OnListFragmentInteractionListener,
        SimpleSearchFragment.OnNewSearchRecordListener {

    private final FragmentManager fragmentManager = getFragmentManager();
    private FrameLayout mainFrame;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mainFrame = (FrameLayout) findViewById(R.id.main_app_frame_layout);
        mainFrame.getForeground().setAlpha(0);

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
        if (id == R.id.action_about) {

            View popupView = View.inflate(this,R.layout.popup_about,null);
            PopupWindow popup = new PopupWindow(popupView, ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT,true);
            popup.setOnDismissListener(new PopupWindow.OnDismissListener() {
                @Override
                public void onDismiss() {
                    mainFrame.getForeground().setAlpha(0);
                }
            });
            popup.showAtLocation(findViewById(R.id.main_linear_layout), Gravity.CENTER,0,0);
            mainFrame.getForeground().setAlpha(140);

            return true;

        } else if (id == R.id.action_image){
            Toast.makeText(this,"image",Toast.LENGTH_SHORT).show();
            return true;
        } else if (id == R.id.action_price){
            Toast.makeText(this,"price",Toast.LENGTH_SHORT).show();
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
    public void onNewSearchRecord(Mtgio mtgio, int percentProgress) {

        CardsListFragment fragment = (CardsListFragment) fragmentManager.findFragmentByTag("LIST");

        try {
            fragment.updateData(mtgio,percentProgress);
        }catch (NullPointerException e){
            System.out.println(e.toString());
        }

    }

    @Override
    public void onNewSearchRecord(int responseCode) {

        String message = String.valueOf(responseCode) + " internet connection error";
        Toast.makeText(this,message,Toast.LENGTH_SHORT).show();

    }

    @Override
    public void onNewSearchRecord(){

        CardsListFragment fragment = (CardsListFragment) fragmentManager.findFragmentByTag("LIST");

        if(fragment == null) {

            fragment = new CardsListFragment();

            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.add(R.id.main_linear_layout, fragment, "LIST");
            fragmentTransaction.addToBackStack(null);
            fragmentTransaction.commit();
            fragmentManager.executePendingTransactions();

        }

        fragment.updateData();

    }
}
