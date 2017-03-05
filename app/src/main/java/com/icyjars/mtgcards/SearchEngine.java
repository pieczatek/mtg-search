package com.icyjars.mtgcards;

import android.support.annotation.Nullable;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.net.ssl.HttpsURLConnection;


// Singleton class
public class SearchEngine {

    public static enum LOGIC {
        OR, AND, NOT
    }

    public static int[] ERROR_CODES = {
        -1, 400,  403, 404, 500, 503
    };

    private static SearchEngine mInstance = null;

    private static String baseURL = "https://api.magicthegathering.io/v1/cards";
    private JSONObject jsonObject = null;
    private LightCardsListInfoContainer container = null;
    private String cardName = null;
    private boolean withParameters = false;
    private int totalCount = Integer.MAX_VALUE;
    private int savedCount = 0;

    private SearchEngine(){

    }

    public static SearchEngine getInstance(){
        if (mInstance == null){
            mInstance = new SearchEngine();
        }
        return mInstance;
    }

    public int executeQuery(){

        container = new LightCardsListInfoContainer();

        String url = baseURL;
        if(withParameters)
            url += "?" + "name=" + cardName;

        int page = 1;
        int responseCode = ERROR_CODES[0];

        // TODO replace multiple connection in loop with one connection before loop
        while(savedCount < totalCount) {

            String nextUrl;

            if (withParameters)
                nextUrl = url+"&page="+page;
            else
                nextUrl = url+"?page="+page;

            responseCode = JSONObjectFromURL(nextUrl);

            for (int errorCode : ERROR_CODES)
                if (responseCode == errorCode)
                    return responseCode;


            try {
                JSONObjectToLightCardsListInfoContainer();
            } catch (JSONException e) {
                System.out.print(e.toString());
            }

        };

        return responseCode;
    }

    public void clearQuery(){
        withParameters = false;
        cardName = null;
        totalCount = Integer.MAX_VALUE;
        savedCount = 0;
    }

    public void setName(String name){
        withParameters = true;
        name = name.replace(" ", "+");
        cardName = name;
    }

    public LightCardsListInfoContainer getContainer(){
        return container;
    }

    @Nullable
    private int JSONObjectFromURL(String urlString) {

        //urlString = "https://api.magicthegathering.io/v1/cards"; //DEBUG

        int responseCode = ERROR_CODES[0];
        URL url;
        try {
            url = new URL(urlString);
        }catch (MalformedURLException mURLe){
            System.out.println(mURLe.toString());
            return responseCode;
        }

        HttpsURLConnection connection;

        try {
            connection = (HttpsURLConnection)url.openConnection();
            connection.setRequestMethod("GET");
            responseCode = connection.getResponseCode();

            System.out.println("\nSending 'GET' request to URL : " + url);
            System.out.println("Response Code : " + responseCode);

            String resPageSize = connection.getHeaderField("Page-Size");
            String resCount = connection.getHeaderField("Count");
            String resTotalCount = connection.getHeaderField("Total-Count");

            BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String inputLine;
            StringBuffer response = new StringBuffer();

            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();

            System.out.println(response.toString());

            this.jsonObject = new JSONObject(response.toString());

            jsonObject.accumulate("Page-Size", resPageSize);
            jsonObject.accumulate("Count", resCount);
            jsonObject.accumulate("Total-Count", resTotalCount);

            totalCount = Integer.valueOf(resTotalCount);
            savedCount += Integer.valueOf(resCount);


        }catch (IOException ioe){
            System.out.println(ioe.toString());
        } catch (JSONException jsone) {
            System.out.println(jsone.toString());
        }

        return responseCode;
    }

    private void JSONObjectToLightCardsListInfoContainer() throws JSONException {

        JSONArray jsonArrayCards = jsonObject.getJSONArray("cards");

        for(int i=0; i<jsonArrayCards.length(); i++){
            try{

                String name = jsonArrayCards.getJSONObject(i).getString("name");
                String types = jsonArrayCards.getJSONObject(i).getString("type");
                String cardNumberString = jsonArrayCards.getJSONObject(i).getString("multiverseid");

                container.add(name,types,Integer.valueOf(cardNumberString));

            }catch(JSONException e){
                System.out.print(e.toString() + "\nat jsonArrayCards index " + String.valueOf(i));
            }
        }

    }

}
