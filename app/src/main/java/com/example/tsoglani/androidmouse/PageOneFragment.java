package com.nikos.tsoglani.androidmouse;

import android.app.Fragment;
import android.graphics.Point;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;

/**
 * Created by tsoglani on 26/6/2015.
 */
public class PageOneFragment extends Fragment {
    private Point point1;
    private long curTime;
    private final int doubleTouchDelay = 50;
    private final int doubleTouchDistance = 15;
    View.OnTouchListener onTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            try {


                ImageView fl = (ImageView) v;
                MouseUIActivity.ps.println("x:" + ((int) (5000 - 5000.0 * event.getY() / fl.getHeight())) + "@@" + "y:" + +((int) (5000 - 5000.0 * event.getY() / fl.getHeight())) + "@@" + "z:" + ((int) (5000.0 * event.getX() / fl.getWidth())));
                if (MotionEvent.ACTION_DOWN == event.getAction()) {
                    if (point1 == null) {
                        point1 = new Point((int) event.getX(), (int) event.getY());
                        curTime = System.currentTimeMillis();
                    } else {
                        if ((curTime - System.currentTimeMillis() <= doubleTouchDelay) && Math.abs((int) event.getX()) - point1.x < doubleTouchDistance && Math.abs(((int) event.getY()) - point1.y) < doubleTouchDistance) {
                            MouseUIActivity.ps.println("LEFT_CLICK");
                            MouseUIActivity.ps.println("LEFT_CLICK");
                        }
                        point1 = null;

                    }
                }


                MouseUIActivity.ps.flush();
            } catch (Exception e) {
                e.printStackTrace();
            }
            return true;
        }
    };

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {


        return inflater.inflate(com.nikos.tsoglani.androidmouse.R.layout.fragment_one, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Button lc=(Button)getActivity().findViewById(R.id.lc);
        lc.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (MotionEvent.ACTION_UP==event.getAction()) {
                    MouseUIActivity.ps.println("LEFT_CLICK_UP");
                    MouseUIActivity.ps.flush();

                }else if (MotionEvent.ACTION_DOWN==event.getAction()) {
                    MouseUIActivity.ps.println("LEFT_CLICK_DOWN");
                    MouseUIActivity.ps.flush();
                }
                return false;
            }
        });

        Button scrollUp=(Button)getActivity().findViewById(R.id.scroll_up);
        Button scrollDown=(Button)getActivity().findViewById(R.id.scroll_down);
        scrollUp.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                MouseUIActivity.ps.println("SCROLL_UP");
                MouseUIActivity.ps.flush();
                return true;
            }
        });

        scrollDown.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                MouseUIActivity.ps.println("SCROLL_DOWN");
                MouseUIActivity.ps.flush();
                return true;
            }
        });

        ImageView fl = (ImageView) getActivity().findViewById(R.id.mousepad_screen);
        fl.setOnTouchListener(onTouchListener);

    }


}
