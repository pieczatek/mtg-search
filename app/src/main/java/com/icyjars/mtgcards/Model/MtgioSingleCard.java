package com.icyjars.mtgcards.Model;

import java.util.List;

public class MtgioSingleCard {

    private Card card;
    public Card getCard(){ return card; }

    public class Card{

        private String name;
        private String manaCost;
        private String type;
        private String power;
        private String toughness;
        private String text;
        private int multiverseid;
        private String imageUrl;

        public String getName(){ return this.name; };
        public String getManaCost(){ return this.manaCost; };
        public String getType(){ return this.type; };
        public String getPower(){ return this.power; };
        public String getToughness(){ return this.toughness; };
        public String getText(){ return this.text; };
        public int getMultiverseid(){ return this.multiverseid; };
        public String getImageUrl(){ return this.imageUrl; };

        /*
        private int cmc;
        private List<String> colors;
        private List<String> colorIdentity;
        private List<String> supertypes;
        private List<String> types;
        private List<String> subtypes;
        private String rarity;
        private String set;
        private String setName;
        private String artist;
        private String number;
        private String layout;
        //rulings
        //printings
        //legalities
        */

    }
}
