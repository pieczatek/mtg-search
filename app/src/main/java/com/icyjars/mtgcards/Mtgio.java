package com.icyjars.mtgcards;

import java.util.List;

public class Mtgio {

    private List<Card> cards;
    public List<Card> getCards(){ return cards; }

    public class Card{

        @Override
        public boolean equals(Object other){
            if (other instanceof String) {
                return this.getName().equals(other);
            }
            else {
                return super.equals(other);
            }
        }


        private String name;
        private String type;
        private int multiverseid;

        public String getName(){ return this.name; }
        public String getType(){ return this.type; }
        public int getMultiverseid(){ return this.multiverseid; }
        public void setMultiverseid(int multiverseid){ this.multiverseid = multiverseid; }
    }


}
