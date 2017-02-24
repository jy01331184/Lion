/**
 * @Title: TreeListViewAdapter.java
 * @Package com.sloop.treeview.utils.adapter
 * Copyright: Copyright (c) 2015
 * 
 * @author sloop
 * @date 2015年2月22日 上午1:16:25
 * @version V1.0
 */

package com.tech.android.treeview.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;

import com.tech.android.treeview.bean.Node;
import com.tech.android.treeview.utils.TreeHelper;

import java.util.ArrayList;
import java.util.List;


/**
 * @ClassName: TreeListViewAdapter
 * @author sloop
 * @date 2015年2月22日 上午1:16:25
 */

public abstract class TreeListViewAdapter<T> extends BaseAdapter {

	protected Context mContext;				//上下文
	protected List<Node> mAllNodes = new ArrayList<>();			//所有节点
	protected List<Node> mVisibleNodes = new ArrayList<>();		//显示的节点
	protected LayoutInflater mInflater;		//页面填充器
	protected ListView mTree;					//展示用的ListView
	protected int expandLevel;

	public TreeListViewAdapter(Context context,ListView tree, int defaultExpandLevel) {
		mContext = context;
		mInflater = LayoutInflater.from(context);
        expandLevel = defaultExpandLevel;
		mTree = tree;
	}

    public void setNodes(List<Node> datas) {
        try
        {
            mAllNodes = TreeHelper.getSortedNodes(datas, expandLevel);
            mVisibleNodes = TreeHelper.fliterVisibleNodes(mAllNodes);
        }catch (Throwable e)
        {
            e.printStackTrace();
        }
    }

    /**
	 * 点击收缩或者展开
	 * @Title: expandOrCollapse
	 * @param position 
	 */
	protected void expandOrCollapse(int position) {
		Node node = mVisibleNodes.get(position);
		if (node!=null) {
            if(!node.lazyLoad){
                node.lazyLoad = true;
                node.loop();
                System.out.println("loop:"+node.getChildren().size());
                mAllNodes.addAll(mAllNodes.indexOf(node)+1,node.getChildren());

            }

			if (node.isLeaf()) {
				return;
			}
			node.setExpend(!node.isExpend());
			mVisibleNodes = TreeHelper.fliterVisibleNodes(mAllNodes);
			notifyDataSetChanged();	//刷新
		}
	}
	

	/**
	 * @Override
	 * Title: getCount
	 * @return 
	 */
	@Override
	public int getCount() {
		return mVisibleNodes.size();
	}

	/**
	 * @Override
	 * Title: getItem
	 * @param position
	 * @return 
	 */
	@Override
	public Object getItem(int position) {
		return mVisibleNodes.get(position);
	}

	/**
	 * @Override
	 * Title: getItemId
	 * @param position
	 * @return 
	 */
	@Override
	public long getItemId(int position) {
		return position;
	}

	/**
	 * @Override
	 * Title: getView
	 * @param position
	 * @param convertView
	 * @param parent
	 * @return 
	 */
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		Node node = mVisibleNodes.get(position);
		convertView = getConvertView(node, position, convertView, parent);
		convertView.setPadding(node.getLevel()*40, 10, 10, 10);	//设置padding内边距
		return convertView;
	}

	/**
	 * 提供给用户的自定义条目的方式
	 * @Title: getConvertView
	 * @param node
	 * @param position
	 * @param convertView
	 * @param parent
	 * @return View
	 */
	public abstract View getConvertView(Node node, int position, View convertView, ViewGroup parent);
}
