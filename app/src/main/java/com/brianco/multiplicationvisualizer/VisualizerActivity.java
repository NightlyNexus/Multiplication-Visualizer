package com.brianco.multiplicationvisualizer;

import android.app.Activity;
import android.os.Bundle;

public class VisualizerActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vis);
        if (savedInstanceState == null) {
            getFragmentManager().beginTransaction()
                    .add(R.id.container, new VisualizerFragment())
                    .commit();
        }
    }
}
