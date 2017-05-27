package com.icyjars.mtgcards;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
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

    private int SCREEN_WIDTH;
    private int SCREEN_HEIGHT;
    private int FOREGROUND_ALPHA_BASE = 0;
    private int FOREGROUND_ALPHA_POPUP = 140;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        SCREEN_HEIGHT = metrics.heightPixels;
        SCREEN_WIDTH = metrics.widthPixels;

        mainFrame = (FrameLayout) findViewById(R.id.main_app_frame_layout);
        mainFrame.getForeground().setAlpha(FOREGROUND_ALPHA_BASE);

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

            showPopUp(R.layout.popup_about);
            return true;

        } else if (id == R.id.action_image){

            View popupView = showPopUp(R.layout.popup_image);

            try {

                ImageView popupImageView = ((ImageView) popupView.findViewById(R.id.card_imageview));
                CardFragment currentCardFragment = (CardFragment) fragmentManager.findFragmentByTag("CARD");
                Bitmap bitmap = currentCardFragment.getCardBitmap(true);
                popupImageView.setImageBitmap(bitmap);

                float cardRatio = currentCardFragment.CARD_LENGTH_RATIO;
                float screenRatio = (float)SCREEN_HEIGHT / (float)SCREEN_WIDTH;

                int popupHeight, popupWidth;

                if(screenRatio > cardRatio){
                    popupWidth = SCREEN_WIDTH;
                    popupHeight = (int) (SCREEN_WIDTH * cardRatio);
                }
                else if (screenRatio < cardRatio){
                    popupHeight = SCREEN_HEIGHT;
                    popupWidth = (int)(SCREEN_HEIGHT / cardRatio);
                }
                else{
                    popupHeight = SCREEN_HEIGHT;
                    popupWidth = SCREEN_WIDTH;
                }

                float maxPopupScreenCover = 0.9f;

                popupImageView.setMaxHeight( (int)(popupHeight * maxPopupScreenCover) );
                popupImageView.setMaxWidth( (int)(popupWidth * maxPopupScreenCover) );

                popupImageView.setScaleType(ImageView.ScaleType.FIT_XY);

            }catch (NullPointerException e){
                System.out.println(e.toString());
            }

            return true;

        } else if (id == R.id.action_price){
            Toast.makeText(this,"price",Toast.LENGTH_SHORT).show();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private View showPopUp(int layID){

        View popupView = View.inflate(this,layID,null);
        PopupWindow popup = new PopupWindow(popupView, ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT,true);
        popup.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                mainFrame.getForeground().setAlpha(FOREGROUND_ALPHA_BASE);
            }
        });

        popup.showAtLocation(findViewById(R.id.main_linear_layout), Gravity.CENTER,0,0);
        mainFrame.getForeground().setAlpha(FOREGROUND_ALPHA_POPUP);

        return popupView;

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
