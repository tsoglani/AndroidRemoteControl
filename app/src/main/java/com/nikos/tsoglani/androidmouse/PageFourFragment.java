package com.nikos.tsoglani.androidmouse;

import android.app.Fragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

/**
 * Created by tsoglani on 15/8/2015.
 */
public class PageFourFragment extends Fragment {
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.close,container,false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Button shut_down= (Button) getActivity().findViewById(R.id.shut_down);
        Button sleep= (Button) getActivity().findViewById(R.id.sleep);
        Button restart= (Button) getActivity().findViewById(R.id.restart);
        shut_down.setOnClickListener(listener);
        restart.setOnClickListener(listener);
        sleep.setOnClickListener(listener);
    }

    View.OnClickListener listener= new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Button b= (Button) v;
            MouseUIActivity.ps.println(b.getText());
        }
    };
}
