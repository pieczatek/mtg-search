package com.icyjars.mtgcards;

import android.content.Context;
import android.graphics.Color;
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
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.net.ssl.HttpsURLConnection;


public class CardFragment extends Fragment {

    private View mView = null;
    private TableLayout tableLayout = null;
    private String cardName;
    private int multiverseid = -1;
    private static String baseURL = "https://api.magicthegathering.io/v1/cards";

    private static final Spannable.Factory spannableFactory = Spannable.Factory.getInstance();

    private String[] fields = {"name","type","text"};

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

        if (mView != null && tableLayout != null)
            return mView;

        mView = inflater.inflate(R.layout.fragment_card, container, false);
        tableLayout = (TableLayout)mView.findViewById(R.id.card_table);

        connect();
        fillTable();

        return mView;
    }

    private void fillTable(){

        try {

            ImageView iv = new ImageView(getActivity());
            String imageUrl = cardInfo.getString("imageUrl");

            InputStream is = (InputStream) new URL(imageUrl).getContent();
            Drawable d = Drawable.createFromStream(is, "card image " + cardName);
            iv.setImageDrawable(d);



            tableLayout.addView(iv);

        } catch (JSONException e) {
            e.printStackTrace();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }


        for (String field : fields){

            try {
                TextView tv = new TextView(getActivity());

                tv.setPadding(50,50,50,50);
                //tv.setBackgroundColor(Color.rgb(200,220,255));
                tv.setBackgroundColor(Color.BLACK);
                tv.setTextColor(Color.WHITE);

                Spannable spannable = spannableFactory.newSpannable(cardInfo.getString(field));
                addMTGSymbols(spannable,tv.getLineHeight());
                tv.setText(spannable);
                tableLayout.addView(tv);

            } catch (JSONException e) {
                e.printStackTrace();
            }

        }

    }

    private void addMTGSymbols(Spannable spannable, int lineHight){

        lineHight *= 0.9f;
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

            d.setBounds(0, 0, lineHight, lineHight);
            ImageSpan is = new ImageSpan(d,ImageSpan.ALIGN_BASELINE);
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
