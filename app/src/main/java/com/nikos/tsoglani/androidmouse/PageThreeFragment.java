package com.nikos.tsoglani.androidmouse;

import android.app.Fragment;
import android.os.Bundle;
import android.view.KeyEvent;
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
        EditText txt= (EditText) getActivity().findViewById(R.id.textScreen);
        txt.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if ((event.getAction() == KeyEvent.ACTION_DOWN) &&
                        (keyCode == KeyEvent.KEYCODE_ENTER)) {
                    // Perform action on key press
                    EditText txtView = (EditText) v;
                    MouseUIActivity.ps.println("keyboard Word:" + txtView.getText().toString());
                    MouseUIActivity.ps.flush();
                    txtView.setText("");
                    return true;
                }
                return false;
            }
        });

    }
}
