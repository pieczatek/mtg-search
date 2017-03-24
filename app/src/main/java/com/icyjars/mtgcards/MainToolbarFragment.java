package com.icyjars.mtgcards;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class MainToolbarFragment extends Fragment {

    private View mView;

    public MainToolbarFragment(){}

    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container,
                             final Bundle savedInstanceState){

        mView = inflater.inflate(R.layout.toolbar_main, container, false);

        return mView;
    }
}
