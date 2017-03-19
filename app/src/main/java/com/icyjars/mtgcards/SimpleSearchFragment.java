package com.icyjars.mtgcards;

import android.app.Activity;
import android.content.Context;
import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.PopupWindow;
import android.os.Handler;
import android.widget.ProgressBar;

import java.util.regex.Pattern;


public class SimpleSearchFragment extends Fragment {

    private View mView;
    private Button searchButton;
    private EditText cardNameTextView;

    private OnNewSearchRecordListener mListener;
    private SearchEngine searchEngine = SearchEngine.getInstance();
    private Handler mainThreadHandler;
    private ProgressBar searchProgressBar;

    int resposneCode = -666;

    public SimpleSearchFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container,
                             final Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mView = inflater.inflate(R.layout.fragment_simple_search, container, false);

        cardNameTextView = (EditText) mView.findViewById(R.id.simpleSearchEditText);
        searchButton = (Button) mView.findViewById(R.id.simpleSearchButton);
        mainThreadHandler = new Handler(getActivity().getMainLooper());
        searchProgressBar = (ProgressBar) mView.findViewById(R.id.simpleSearchProgressBar);
        searchProgressBar.setVisibility(View.INVISIBLE);

        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String cardName = cardNameTextView.getText().toString();

                if (Pattern.compile(SearchEngine.stringIllegalValidator).matcher(cardName).find()){
                    cardNameTextView.setError("Special characters are not supported yet. Use only A-Z, a-z or 0-9 characters!");
                    return;
                }

                cardName = cardName.replaceAll("^\\s+","").replaceAll("\\s+$","").replaceAll("\\s+"," ");
                cardNameTextView.setText(cardName);
                if (cardName.length()<3){
                    cardNameTextView.setError("Type at least 3 characters!");
                    return;
                }

                searchEngine.clearQuery();
                searchEngine.setName(cardName);
                searchProgressBar.setMax(100);
                searchProgressBar.setProgress(0);
                searchProgressBar.setVisibility(View.VISIBLE);

                Thread searchingThread = new Thread(new Runnable() {

                    Runnable callbackMainThread = new Runnable() {
                        @Override
                        public void run() {
                            finishedSearching();
                        }
                    };

                    public void run() {
                        // TODO: change way to update progressbar (MVC) - don't pass View element to Controller
                        resposneCode = searchEngine.executeQuery(searchProgressBar);
                        mainThreadHandler.post(callbackMainThread);
                    }

                });

                searchingThread.start();

            }
        });

        return mView;
    }

    private void finishedSearching(){

        searchProgressBar.setVisibility(View.INVISIBLE);

        for (int errorCode : SearchEngine.ERROR_CODES)
            if(resposneCode == errorCode) {
                mListener.onNewSearchRecord(resposneCode);
                return;
            }

        mListener.onNewSearchRecord(searchEngine.getContainer());

    }

    public void setPopupWindow(PopupWindow popupWindow){
        int offx=(mView.getWidth()-popupWindow.getWidth())/2;
        int offy=(-mView.getHeight()-popupWindow.getHeight())/2;
        popupWindow.showAsDropDown(mView,offx,offy);
    }

    public interface OnNewSearchRecordListener{
        void onNewSearchRecord(LightCardsListInfoContainer container);
        void onNewSearchRecord(int responseCode);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnNewSearchRecordListener) {
            mListener = (OnNewSearchRecordListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnListFragmentInteractionListener");
        }
    }

    @Override
    public void onAttach(Activity activity) {

        /*
            For API less than 23
         */

        super.onAttach(activity);

        if (activity instanceof OnNewSearchRecordListener) {
            mListener = (OnNewSearchRecordListener) activity;
        } else {
            throw new RuntimeException(activity.toString()
                    + " must implement OnListFragmentInteractionListener");
        }

    }


    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }
}
