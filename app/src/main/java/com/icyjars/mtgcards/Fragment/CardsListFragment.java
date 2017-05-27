package com.icyjars.mtgcards.Fragment;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.app.Fragment;
import android.support.v7.view.menu.MenuBuilder;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.icyjars.mtgcards.CardsListAdapter;
import com.icyjars.mtgcards.Model.Mtgio;
import com.icyjars.mtgcards.R;


public class CardsListFragment extends Fragment
implements CardsListAdapter.OnListItemClickListener {

    private OnListFragmentInteractionListener mListener;
    private View mView = null;

    private CardsListAdapter adapter;
    private ProgressBar searchProgressBar;

    private int MAX_PROGRESS = 100;

    private class SimpleDividerItemDecoration extends RecyclerView.ItemDecoration {
        private Drawable mDivider;

        public SimpleDividerItemDecoration(Context context) {
            mDivider = context.getResources().getDrawable(R.drawable.divider);
        }

        @Override
        public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
            super.getItemOffsets(outRect, view, parent, state);

            if (parent.getChildAdapterPosition(view) == 0) {
                return;
            }

            outRect.top = mDivider.getIntrinsicHeight();
        }

        @Override
        public void onDraw(Canvas canvas, RecyclerView parent, RecyclerView.State state) {
            int dividerLeft = parent.getPaddingLeft();
            int dividerRight = parent.getWidth() - parent.getPaddingRight();

            int childCount = parent.getChildCount();
            for (int i = 0; i < childCount - 1; i++) {
                View child = parent.getChildAt(i);

                RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) child.getLayoutParams();

                int dividerTop = child.getBottom() + params.bottomMargin;
                int dividerBottom = dividerTop + mDivider.getIntrinsicHeight();

                mDivider.setBounds(dividerLeft, dividerTop, dividerRight, dividerBottom);
                mDivider.draw(canvas);
            }
        }
    }

    public CardsListFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (mView != null)
            return mView;

        mView = inflater.inflate(R.layout.fragment_card_list, container, false);
        adapter = new CardsListAdapter();


        adapter.setListener(this);

        RecyclerView recyclerView = (RecyclerView) mView.findViewById(R.id.list);
        recyclerView.addItemDecoration(new SimpleDividerItemDecoration(getActivity()));
        recyclerView.setAdapter(adapter);

        searchProgressBar = (ProgressBar) mView.findViewById(R.id.simpleSearchProgressBar);

        searchProgressBar.setMax(MAX_PROGRESS);
        searchProgressBar.setProgress(0);

        return mView;
    }

    public void updateData(Mtgio mtgio, int percentProgress){

        searchProgressBar.setVisibility(View.VISIBLE);
        adapter.addData(mtgio);
        int progress = (int)Math.ceil((double) percentProgress * (double) MAX_PROGRESS / 100.0);
        searchProgressBar.incrementProgressBy(progress);
        if(searchProgressBar.getProgress() >= MAX_PROGRESS)
            searchProgressBar.setVisibility(View.INVISIBLE);
    }

    public void updateData(){

        adapter.clearData();
        searchProgressBar.setProgress(0);
        searchProgressBar.setVisibility(View.INVISIBLE);

    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnListFragmentInteractionListener) {
            mListener = (OnListFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnListFragmentInteractionListener");
        }
    }


    public int getResId(String name){
        Resources resources = getResources();
        final int resourceId = resources.getIdentifier(name, "drawable", getActivity().getPackageName());
        return resourceId;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        if (activity instanceof OnListFragmentInteractionListener) {
            mListener = (OnListFragmentInteractionListener) activity;
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

    @Override
    public void onListItemClick(String cardName, int multiverseid) {
        mListener.onListFragmentInteraction(cardName,multiverseid);
    }

    @Override
    public Drawable getDrawableByName(String name){

        Resources resources = getActivity().getResources();
        int id = resources.getIdentifier(name,"drawable",getActivity().getPackageName());
        return resources.getDrawable(id);

    }


    public interface OnListFragmentInteractionListener {
        void onListFragmentInteraction(String cardName, int multiverseid);
    }
}
