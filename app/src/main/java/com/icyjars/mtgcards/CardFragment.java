package com.icyjars.mtgcards;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.app.Fragment;
import android.text.Spannable;
import android.text.style.ImageSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.sql.Array;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.net.ssl.HttpsURLConnection;


public class CardFragment extends Fragment {

    private View mView = null;
    private String cardName;
    private int multiverseid = -1;
    private static String baseURL = "https://api.magicthegathering.io/v1/cards";

    private static final Spannable.Factory spannableFactory = Spannable.Factory.getInstance();

    JSONObject cardInfo = null;


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
        connect();
        fillTable();
    }

    private void fillTable(){

        //TableLayout tableLayout = (TableLayout)getActivity().findViewById(R.id.card_table);

        /*
        try {

            ImageView iv = (ImageView)getActivity().findViewById(R.id.cardImageView);
            String imageUrl = cardInfo.getString("imageUrl");
            InputStream is = (InputStream) new URL(imageUrl).getContent();
            Drawable d = Drawable.createFromStream(is, "card image " + cardName);
            iv.setImageDrawable(d);

            //((TableRow)iv.getParent()).setMinimumHeight(b.getHeight());

        } catch (JSONException | IOException e) {
            e.printStackTrace();
        }
        */

        fillTextView("name", R.id.cardName, true, "");
        fillTextView("type", R.id.cardTypes, true, "");
        fillTextView("text", R.id.cardText, true, "");
        fillTextView("manaCost", R.id.cardCmc, true, "");
        fillTextView("power", R.id.cardPower, true, "");
        fillTextView("toughness", R.id.cardPower, false, " / ");


    }

    private void fillTextView(String cardParam, int textViewID, boolean replaceText, String prefix){

        Spannable spannable = null;
        TextView tv = (TextView)getActivity().findViewById(textViewID);

        try{
            String s = cardInfo.getString(cardParam);
            if (s.length() > 0) {
                if(replaceText)
                    spannable = spannableFactory.newSpannable(prefix + s);
                else
                    spannable = spannableFactory.newSpannable(tv.getText() + prefix + s);
                addMTGSymbols(spannable, tv.getLineHeight());
                tv.setText(spannable);
            }
            else{
                tv.setVisibility(View.GONE);
            }
        } catch (JSONException e) {
            e.printStackTrace();
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

    private void connect(){

        String urlString = baseURL + "/" + String.valueOf(multiverseid);
        URL url;

        try {
            url = new URL(urlString);
        }catch (MalformedURLException mURLe){
            System.out.println(mURLe.toString());
            return;
        }

        HttpsURLConnection connection;

        try {
            connection = (HttpsURLConnection)url.openConnection();
            connection.setRequestMethod("GET");
            int responseCode = connection.getResponseCode();

            System.out.println("\nSending 'GET' request to URL : " + url);
            System.out.println("Response Code : " + responseCode);


            BufferedReader in = new BufferedReader(
                    new InputStreamReader(connection.getInputStream()));
            String inputLine;
            StringBuffer response = new StringBuffer();

            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();

            System.out.println(response.toString());

            cardInfo = (new JSONObject(response.toString())).getJSONObject("card");


        }catch (IOException ioe){
            System.out.println(ioe.toString());
        } catch (JSONException jsone) {
            System.out.println(jsone.toString());
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
