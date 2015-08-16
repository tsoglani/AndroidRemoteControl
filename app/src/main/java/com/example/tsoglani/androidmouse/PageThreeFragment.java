package com.nikos.tsoglani.androidmouse;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

/**
 * Created by tsoglani on 17/7/2015.
 */
public class PageThreeFragment extends Fragment {
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {


        return inflater.inflate(com.nikos.tsoglani.androidmouse.R.layout.keyboard, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        TextView txt= (TextView) getActivity().findViewById(R.id.textScreen);
txt.setOnClickListener(new View.OnClickListener() {
    @Override
    public void onClick(View v) {
        EditText txt= (EditText) v;
        txt.setText("");
    }
});

    }
}
