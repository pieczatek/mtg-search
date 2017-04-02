package com.icyjars.mtgcards.Fragment;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.app.Fragment;
import android.support.annotation.NonNull;
import android.text.Spannable;
import android.text.style.ImageSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.icyjars.mtgcards.Service.MtgioService;
import com.icyjars.mtgcards.Model.MtgioSingleCard;
import com.icyjars.mtgcards.R;
import com.icyjars.mtgcards.Service.ServiceFactory;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import retrofit2.Call;
import retrofit2.Response;


public class CardFragment extends Fragment {

    private View mView = null;
    private String cardName;
    private int multiverseid = -1;
    private MtgioSingleCard.Card card;

    private static final Spannable.Factory spannableFactory = Spannable.Factory.getInstance();


    public CardFragment(){}

    public void setCardName(String cardName){
        this.cardName = cardName;
    }

    public void setCardMultiverseId(int id){
        this.multiverseid = id;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        if (mView != null)
            return mView;

        mView = inflater.inflate(R.layout.fragment_card, container, false);

        return mView;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState){
        super.onViewCreated(view,savedInstanceState);

        MtgioService service = ServiceFactory.createRetrofitService(MtgioService.class,MtgioService.SERVICE_ENDPOINT);
        Call<MtgioSingleCard> call = service.getSingleCard(multiverseid);
        Response<MtgioSingleCard> response;

        try {

            response = call.execute();
            fillTable(response.body().getCard());

        } catch (IOException e) {

            Toast.makeText(getActivity(),"failed on internet connection with " + cardName,Toast.LENGTH_SHORT).show();
            e.printStackTrace();

        }

    }

    private void fillTable(MtgioSingleCard.Card c){

        this.card = c;

        fillTextView(card.getName(), R.id.cardName, true, "");
        fillTextView(card.getType(), R.id.cardTypes, true, "");
        fillTextView(card.getText(), R.id.cardText, true, "");
        fillTextView(card.getManaCost(), R.id.cardCmc, true, "");
        fillTextView(card.getPower(), R.id.cardPower, true, "");
        fillTextView(card.getToughness(), R.id.cardPower, false, " / ");


    }

    private void fillTextView(String cardParam, int textViewID, boolean replaceText, String prefix){

        TextView tv = (TextView)getActivity().findViewById(textViewID);

        if (cardParam != null){
            Spannable spannable;
            if(replaceText)
                spannable = spannableFactory.newSpannable(prefix + cardParam);
            else
                spannable = spannableFactory.newSpannable(tv.getText() + prefix + cardParam);
            addMTGSymbols(spannable, tv.getLineHeight());
            tv.setText(spannable);
        }
        else{
            tv.setVisibility(View.GONE);
        }

    }

    private void addMTGSymbols(Spannable spannable, int lineHight){

        float lineOffset = lineHight*0.15f;

        System.out.println("MATCHER: " + spannable);

        Pattern pattern = Pattern.compile("\\{(" +
                "[a-zA-Z]|" +               // B,G,U,R,W,E,T,Q etc
                "[0-9]{1,3}|" +             // 0-999
                "[a-zA-Z_0-9]/[a-zA-Z]|" +  // {G/W} {2/R} {U/R} etc
                "∞)\\}");                   // special

        Matcher matcher = pattern.matcher(spannable);
        while(matcher.find()){
            System.out.println("FOUND: " + matcher.group());


            String resourcesSymbolName = matcher.group();
            resourcesSymbolName = resourcesSymbolName.replaceAll("[^a-zA-Z_0-9]","").toLowerCase();

            Drawable d;

            try {
                int resID = getResources().getIdentifier(resourcesSymbolName,"drawable",getActivity().getPackageName());
                d = getResources().getDrawable(resID);
            }catch (Exception e){
                d = getResources().getDrawable(R.drawable.x);
            }

            int off = Math.round(lineOffset);
            d.setBounds(0, off, lineHight - off, lineHight);

            //TODO: align center
            ImageSpan is = new ImageSpan(d,ImageSpan.ALIGN_BOTTOM);
            spannable.setSpan(is,matcher.start(),matcher.end(),Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        }

    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

}
