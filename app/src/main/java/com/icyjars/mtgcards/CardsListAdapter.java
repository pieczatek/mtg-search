package com.icyjars.mtgcards;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class CardsListAdapter extends RecyclerView.Adapter<CardsListAdapter.ViewHolder>{

    private LightCardsListInfoContainer data;
    private OnListItemClickListener mListener;

    public CardsListAdapter() {

    }

    public void setListener(Fragment fragment){

        if (fragment instanceof OnListItemClickListener) {
            mListener = (OnListItemClickListener) fragment;
        } else {
            throw new RuntimeException(fragment.toString()
                    + " must implement OnListItemClickListener");
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent,int viewType) {

        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.card_list_item, parent, false);

        // set the view's size, margins, paddings and layout parameters
        //... TODO

        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        String cardName = data.getName(position);

        holder.cardNameTextView.setText(cardName);
        holder.cardTypesTextView.setText(data.getTypes(position));
        holder.lastMultiverseId = data.getLastMultiverseId(cardName);
        holder.mListener = mListener;
    }

    public void setContainer(LightCardsListInfoContainer container){
        data = container;
    }

    public interface OnListItemClickListener{
        void onListItemClick(String cardName, int multiverseid);
    }


    @Override
    public int getItemCount() {
        return data.getLength();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder implements
            View.OnClickListener{

        public TextView cardNameTextView;
        public TextView cardTypesTextView;
        public OnListItemClickListener mListener = null;
        public int lastMultiverseId = 0;

        public ViewHolder(View v) {
            super(v);
            cardNameTextView = (TextView) v.findViewById(R.id.card_name);
            cardTypesTextView = (TextView) v.findViewById(R.id.card_types);
            v.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            String cardName = ((TextView)v.findViewById(R.id.card_name)).getText().toString();
            mListener.onListItemClick(cardName,lastMultiverseId);
        }
    }

}
