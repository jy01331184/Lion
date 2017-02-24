package com.tech.android;

import android.app.Activity;
import android.os.Bundle;
import android.view.Window;

import com.tech.android.view.TechRootView;

/**
 * Created by tianyang on 16/9/29.
 */
public class TechResultActivity extends Activity{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(new TechRootView(this));
    }
}
