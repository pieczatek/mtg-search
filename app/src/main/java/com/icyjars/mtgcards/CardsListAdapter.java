package com.icyjars.mtgcards;

import android.app.Fragment;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.icyjars.mtgcards.Model.Mtgio;

import java.util.ArrayList;
import java.util.List;

public class CardsListAdapter extends RecyclerView.Adapter<CardsListAdapter.ViewHolder>{

    private OnListItemClickListener mListener;
    private final List<Mtgio.Card> mData;

    public CardsListAdapter() {
        super();
        mData = new ArrayList<>();
    }

    public void clearData(){
        mData.clear();
        notifyDataSetChanged();
    }

    public void addData(Mtgio mtgio){
        for (Mtgio.Card c : mtgio.getCards()) {
            mergeCard(c);
        }
        notifyDataSetChanged();
    }

    private void mergeCard(Mtgio.Card card) {

        synchronized (mData) {
            int index = mData.indexOf(card);

            if (index >= 0) {
                int id1 = mData.get(index).getMultiverseid();
                int id2 = card.getMultiverseid();
                mData.get(index).setMultiverseid(Math.max(id1, id2));
            } else {
                mData.add(card);
            }

            mData.notify();
        }
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

        Mtgio.Card card = mData.get(position);

        holder.cardNameTextView.setText(card.getName());
        holder.cardTypesTextView.setText(card.getType());
        holder.lastMultiverseId = card.getMultiverseid();

        String primaryType = card.getTypes().get(0).toLowerCase();
        Drawable listItemIconDrawable = mListener.getDrawableByName(primaryType);
        holder.cardTypeImageView.setImageDrawable(listItemIconDrawable);

        holder.mListener = mListener;
    }

    public interface OnListItemClickListener{
        void onListItemClick(String cardName, int multiverseid);
        Drawable getDrawableByName(String name);
    }


    @Override
    public int getItemCount() {
        return mData.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder implements
            View.OnClickListener{

        public TextView cardNameTextView;
        public TextView cardTypesTextView;
        public ImageView cardTypeImageView;
        public OnListItemClickListener mListener = null;
        public int lastMultiverseId = 0;

        public ViewHolder(View v) {
            super(v);
            cardNameTextView = (TextView) v.findViewById(R.id.card_name);
            cardTypesTextView = (TextView) v.findViewById(R.id.card_types);
            cardTypeImageView = (ImageView) v.findViewById(R.id.list_view_item_icon);
            v.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            String cardName = ((TextView)v.findViewById(R.id.card_name)).getText().toString();
            mListener.onListItemClick(cardName,lastMultiverseId);
        }
    }

}
