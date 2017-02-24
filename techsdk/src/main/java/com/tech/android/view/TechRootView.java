package com.tech.android.view;

import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.text.InputType;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.tech.StackObject;
import com.tech.TechManager;
import com.tech.android.treeview.adapter.SimpleTreeListViewAdapter;
import com.tech.android.treeview.bean.Node;
import com.tech.android.treeview.bean.TreeNodeBean;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Set;

/**
 * Created by tianyang on 16/9/29.
 */
public class TechRootView extends RelativeLayout {

    private ListView techListView;
    private static ArrayList<Node> data = new ArrayList<Node>();
    private SimpleTreeListViewAdapter<Node> mAdapter = null;
    private static int minCreateCount = 0;
    public static TechManager.Order order = TechManager.Order.ORDER_DATE;


    public TechRootView(Context context) {
        super(context);
        setBackgroundColor(Color.BLACK);
        setFocusable(true);
        setFocusableInTouchMode(true);
        final Button refreshButton = new Button(context);
        refreshButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                initData();
                mAdapter.setNodes(data);
                mAdapter.notifyDataSetChanged();
            }
        });
        refreshButton.setLayoutParams(generateDefaultLayoutParams());
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            refreshButton.setId(View.generateViewId());
        }
        refreshButton.setTextColor(Color.WHITE);
        refreshButton.setText("更新");
        addView(refreshButton);

        Button clearButton = new Button(context);
        clearButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                TechManager.getInstance().resetAll();
                refreshButton.performClick();
            }
        });
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            clearButton.setId(View.generateViewId());
        }
        clearButton.setTextColor(Color.WHITE);
        clearButton.setText("清除");

        LayoutParams params = (LayoutParams) generateDefaultLayoutParams();
        params.addRule(RIGHT_OF,refreshButton.getId());
        clearButton.setLayoutParams(params);
        addView(clearButton);

        final Button sortButton = new Button(context);
        sortButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {

                if(sortButton.getText().toString().equals("数量⬆")){
                    sortButton.setText("时间⬇");
                    order = TechManager.Order.ORDER_COUNT;
                    refreshButton.performClick();
                }else{
                    sortButton.setText("数量⬆");
                    order = TechManager.Order.ORDER_DATE;
                    refreshButton.performClick();
                }
            }
        });
        sortButton.setTextColor(Color.WHITE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            sortButton.setId(View.generateViewId());
        }
        sortButton.setText(order == TechManager.Order.ORDER_DATE?"数量⬆":"时间⬇");


        LayoutParams params2 = (LayoutParams) generateDefaultLayoutParams();
        params2.addRule(RIGHT_OF,clearButton.getId());
        sortButton.setLayoutParams(params2);
        addView(sortButton);

        final EditText editText = new EditText(context);
        editText.setInputType(InputType.TYPE_CLASS_NUMBER);
        LayoutParams params1 = (LayoutParams) generateDefaultLayoutParams();
        params1.addRule(RIGHT_OF,sortButton.getId());
        editText.setLayoutParams(params1);
        editText.setMinWidth(200);
        editText.setFocusable(false);
        editText.setFocusableInTouchMode(true);
        editText.setText(minCreateCount+"");
        editText.setImeOptions(EditorInfo.IME_ACTION_DONE);
        editText.setTextColor(Color.WHITE);
        editText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if(actionId == EditorInfo.IME_ACTION_DONE || actionId == EditorInfo.IME_ACTION_NEXT){
                    Integer integer = 0;
                    if(!TextUtils.isEmpty(editText.getText().toString())){
                        integer = Integer.parseInt(editText.getText().toString());
                    }
                    if(integer != minCreateCount){
                        minCreateCount = integer;
                        refreshButton.performClick();
                    }
                }
                return false;
            }
        });

        addView(editText);

        techListView = new ListView(context);

        params = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        params.addRule(BELOW,refreshButton.getId());
        techListView.setLayoutParams(params);
        //techListView.setResults(TechManager.getResults());
        addView(techListView);
        if(data.isEmpty())
            initData();

        try {
            mAdapter = new SimpleTreeListViewAdapter<Node>(getContext(), techListView, 0);
            mAdapter.setNodes(data);
            techListView.setAdapter(mAdapter);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void initData()
    {
        data.clear();
        Set<TechManager> managers = TechManager.getResults(order);

        ArrayList<TechManager> temp = new ArrayList<TechManager>();
        for (TechManager manager : managers) {
            temp.add(manager);
        }

        Collections.sort(temp, new Comparator<TechManager>() {
            @Override
            public int compare(TechManager lhs, TechManager rhs) {
                return lhs.threadName.compareTo(rhs.threadName);
            }
        });


        for (TechManager manager : temp) {
            Collections.sort(manager.results, new Comparator<StackObject>() {
                @Override
                public int compare(StackObject lhs, StackObject rhs) {
                    if(order == TechManager.Order.ORDER_DATE){
                        return lhs.date.compareTo(rhs.date) ;
                    }
                    else{

                        return -lhs.getCreateObjectCount() + rhs.getCreateObjectCount();
                    }
                }
            });

            Node threadNode = new Node(null,"THREAD - ".concat(manager.threadName),"",TreeNodeBean.TYPE_THREAD);
            data.add(threadNode);
            boolean hasContent = false;
            for (StackObject obj : manager.results) {
                //System.out.println(obj.className+".."+obj.methodName+":"+obj.getResult().size()+":"+obj.getObjectCreateRecord().size());
                if(!obj.hasContent() || obj.getCreateObjectCount() < minCreateCount)
                {
                    continue;
                }
                Node node = new Node(obj,obj.toNodeString(),obj.toDetailString(),TreeNodeBean.TYPE_TOP_CONTENT);
                node.objects = obj.getResult();
                hasContent = true;
                data.add(node);
            }
            if(!hasContent)
                data.remove(threadNode);
        }

        Node threadNode = null;
        List<Node> deleteNodes = new ArrayList<>();
        for (Node node : data) {
            if(threadNode != null && node.getType() ==TreeNodeBean.TYPE_THREAD ){
                deleteNodes.add(threadNode);
            }

            if(node.getType() == TreeNodeBean.TYPE_THREAD){
                threadNode = node;
            }else{
                threadNode = null;
            }
        }

        if(threadNode!=null){
            deleteNodes.add(threadNode);
        }
        data.removeAll(deleteNodes);
    }

}
