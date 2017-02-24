package com.tech.android;

import android.app.Activity;
import android.os.Bundle;
import android.view.Window;

import com.tech.android.treeview.bean.Node;
import com.tech.android.view.TechDetailRootView;

/**
 * Created by tianyang on 16/12/28.
 */
public class TechDetailActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);

        Node recentNode = (Node) getIntent().getSerializableExtra("node");
        setContentView(new TechDetailRootView(this,recentNode));
    }
}
