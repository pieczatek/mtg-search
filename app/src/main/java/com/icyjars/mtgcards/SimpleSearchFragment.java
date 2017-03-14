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

import java.util.regex.Pattern;


public class SimpleSearchFragment extends Fragment {

    View view;
    Button searchButton;
    EditText cardNameTextView;
    private OnNewSearchRecordListener mListener;

    private SearchEngine searchEngine = SearchEngine.getInstance();

    public SimpleSearchFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container,
                             final Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_simple_search, container, false);

        cardNameTextView = (EditText) view.findViewById(R.id.simpleSearchEditText);
        searchButton = (Button) view.findViewById(R.id.simpleSearchButton);
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

                int resposneCode = searchEngine.executeQuery();

                for (int errorCode : SearchEngine.ERROR_CODES)
                    if(resposneCode == errorCode) {
                        mListener.onNewSearchRecord(resposneCode);
                        return;
                    }

                mListener.onNewSearchRecord(searchEngine.getContainer());

            }
        });

        return view;
    }

    public void setPopupWindow(PopupWindow popupWindow){
        int offx=(view.getWidth()-popupWindow.getWidth())/2;
        int offy=(-view.getHeight()-popupWindow.getHeight())/2;
        popupWindow.showAsDropDown(view,offx,offy);
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
