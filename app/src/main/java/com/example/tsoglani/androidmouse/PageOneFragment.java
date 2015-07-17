package com.example.tsoglani.androidmouse;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

/**
 * Created by tsoglani on 26/6/2015.
 */
public class PageOneFragment extends Fragment {


    View.OnTouchListener onTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            FrameLayout fl = (FrameLayout) v;

            MouseActivity.ps.println("x:" + ((int) (5000-5000.0 * event.getY() / fl.getHeight())) + "@@" + "y:" + + ((int) (5000-5000.0 * event.getY() / fl.getHeight())) + "@@" + "z:" + ((int) (5000.0 * event.getX() / fl.getWidth())));
            return true;
        }
    };

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {


        return inflater.inflate(R.layout.fragment_one, container, false);
    }
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        FrameLayout fl=(FrameLayout)getActivity().findViewById(R.id.mousepad);
        fl.setOnTouchListener(onTouchListener);

    }
}
