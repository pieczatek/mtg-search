package com.icyjars.mtgcards;

import java.util.ArrayList;
import java.util.LinkedHashMap;


public class LightCardsListInfoContainer{


    private class CardSubInfo{

        private ArrayList<Integer> cardNumbers;
        private String types;

        private CardSubInfo(String types, int cardNumber){

            cardNumbers = new ArrayList<>();
            this.types = types;
            this.cardNumbers.add(cardNumber);

        }

        private void addCardNumber(int cardNumber){

            // sorted min to max

            if (cardNumbers.size() == 0) {
                cardNumbers.add(cardNumber);
                return;
            }

            for(int i=0; i<cardNumbers.size(); i++){
                if(cardNumbers.get(i) > cardNumber){
                    cardNumbers.add(i,cardNumber);
                    return;
                }
            }

            cardNumbers.add(cardNumber);

        }

        private int getLastMultiverseId(){
            if (cardNumbers.size() == 0)
                return 0;
            return cardNumbers.get(cardNumbers.size()-1);
        }

        private String getTypes(){
            return types;
        }
    }

    private LinkedHashMap<String,CardSubInfo> dataSet;
    private ArrayList<String> mapPosition;

    LightCardsListInfoContainer(){
        dataSet = new LinkedHashMap<>();
        mapPosition = new ArrayList<>();
    }

    public void add(String name, String types, int cardNumber){
        if(dataSet.containsKey(name)){
            dataSet.get(name).addCardNumber(cardNumber);
        }
        else{
            dataSet.put(name,new CardSubInfo(types,cardNumber));
            mapPosition.add(name);
        }
    }

    public int getLength(){
        return dataSet.size();
    }

    public String getName(int index){
        return mapPosition.get(index);
    }

    public int getLastMultiverseId(String cardName){
        return dataSet.get(cardName).getLastMultiverseId();
    }

    public String getTypes(int index){
        String name = mapPosition.get(index);
        return dataSet.get(name).getTypes();
    }

}