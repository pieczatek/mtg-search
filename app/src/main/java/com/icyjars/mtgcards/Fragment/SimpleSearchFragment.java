package com.icyjars.mtgcards.Fragment;

import android.app.Activity;
import android.content.Context;
import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;

import com.icyjars.mtgcards.CardsListAdapter;
import com.icyjars.mtgcards.Model.Mtgio;
import com.icyjars.mtgcards.Service.MtgioService;
import com.icyjars.mtgcards.R;
import com.icyjars.mtgcards.Service.ServiceFactory;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class SimpleSearchFragment extends Fragment {

    private EditText cardNameTextView;

    private OnNewSearchRecordListener mListener;

    private int pageSize;
    private int totalCount;
    private int totalPages;

    private int[] ERROR_CODES = {
            400,  403, 404, 500, 503
    };

    private String stringIllegalValidator = "[^A-Za-z0-9 ]";

    /*

        TODO: check all characters occurs in MtG card names
        chcecked: space :'-()/,!._?&
        url dangerous: ?&
        "[^A-Za-z0-9\\:\\'\\-\\(\\)\\/\\,\\!\\.\\_ ]"

        TODO: check: not all cards are found with special characters, different records compared to gatherer.wizards, example: ' (one character "'")
        Some special characters breaks url's, ex. '?&
        ex. https://api.magicthegathering.io/v1/cards?name='&page=1

    */


    public SimpleSearchFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container,
                             final Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View mView = inflater.inflate(R.layout.fragment_simple_search, container, false);

        cardNameTextView = (EditText) mView.findViewById(R.id.simpleSearchEditText);

        Button searchButton = (Button) mView.findViewById(R.id.simpleSearchButton);
        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                InputMethodManager imm =  (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);

                mListener.onNewSearchRecord();

                String cardName = cardNameTextView.getText().toString();

                if (Pattern.compile(stringIllegalValidator).matcher(cardName).find()){
                    cardNameTextView.setError("Special characters are not supported yet. Use only A-Z, a-z or 0-9 characters!");
                    return;
                }

                cardName = cardName.replaceAll("^\\s+","").replaceAll("\\s+$","").replaceAll("\\s+"," ");
                cardNameTextView.setText(cardName);
                if (cardName.length()<3){
                    cardNameTextView.setError("Type at least 3 characters!");
                    return;
                }

                MtgioService service = ServiceFactory.createRetrofitService(MtgioService.class,MtgioService.SERVICE_ENDPOINT);
                Map<String,String>params = new HashMap<>();
                params.put("name",cardName);

                int page = 1;
                params.put("page", String.valueOf(page));

                Call<Mtgio> call = service.getCards(params);
                Response<Mtgio> response;

                try {

                    // synchro for first call
                    response = call.execute();

                    pageSize = Integer.valueOf(response.headers().get("Page-Size"));
                    totalCount = Integer.valueOf(response.headers().get("Total-Count"));
                    totalPages = (int)Math.ceil((double) totalCount/(double)pageSize);
                    int percentProgress = (int)Math.ceil(100.0 / (double) totalPages);

                    mListener.onNewSearchRecord(response.body(),percentProgress);


                    // async for call 2..n
                    while(page < totalPages) {

                        page++;
                        params.put("page", String.valueOf(page));

                        call = service.getCards(params);
                        call.enqueue(new Callback<Mtgio>() {
                            @Override
                            public void onResponse(Call<Mtgio> call, Response<Mtgio> response) {

                                int percentProgress = (int)Math.ceil(100.0 / (double) totalPages);
                                mListener.onNewSearchRecord(response.body(),percentProgress);

                            }

                            @Override
                            public void onFailure(Call<Mtgio> call, Throwable t) {

                            }
                        });

                    }

                }catch (IOException | NumberFormatException | NullPointerException e){
                    System.out.println(e.toString());
                }
            }
        });

        return mView;
    }

    private void finishedSearching(int resposneCode){

        //searchProgressBar.setVisibility(View.INVISIBLE);

        for (int errorCode : ERROR_CODES)
            if(resposneCode == errorCode) {
                mListener.onNewSearchRecord(resposneCode);
                return;
            }

        //mListener.onNewSearchRecord(searchEngine.getContainer());

    }

    public interface OnNewSearchRecordListener{
        void onNewSearchRecord(Mtgio mtgio, int percentProgress);
        void onNewSearchRecord(int responseCode);
        void onNewSearchRecord();
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
