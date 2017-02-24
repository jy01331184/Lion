package com.tech.android.view;

import android.content.Context;
import android.graphics.Color;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.RelativeLayout;

import com.tech.android.treeview.adapter.DetailTreeListViewAdapter;
import com.tech.android.treeview.bean.Node;
import com.tech.android.treeview.bean.TreeNodeBean;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by tianyang on 16/12/28.
 */
public class TechDetailRootView extends RelativeLayout {

    private ListView techListView;
    private DetailTreeListViewAdapter<Node> mAdapter = null;


    public TechDetailRootView(Context context,Node recentNode) {
        super(context);
        setBackgroundColor(Color.BLACK);
        techListView = new ListView(context);

        LayoutParams params = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);

        techListView.setLayoutParams(params);

        addView(techListView);
        try {
            mAdapter = new DetailTreeListViewAdapter<Node>(getContext(), techListView, 0,recentNode);
            List<Node> nodes = new ArrayList<>();

            Node node = new Node(recentNode.object, "", "", TreeNodeBean.TYPE_TOP_CONTENT);
            node.objects = recentNode.object.getResult();
            node.setExpend(true);
            nodes.add(node);
            mAdapter.setNodes(nodes);
            techListView.setAdapter(mAdapter);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
