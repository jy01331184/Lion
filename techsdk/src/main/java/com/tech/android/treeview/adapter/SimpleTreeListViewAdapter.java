/**
 * @Title: SimpleTreeListViewAdapter.java
 * @Package com.sloop.treeview.adapter
 * Copyright: Copyright (c) 2015
 * 
 * @author sloop
 * @date 2015年2月22日 上午2:01:06
 * @version V1.0
 */

package com.tech.android.treeview.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.tech.android.TechDetailActivity;
import com.tech.android.drawable.MinsDrawable;
import com.tech.android.drawable.PlusDrawable;
import com.tech.android.treeview.bean.Node;
import com.tech.android.treeview.bean.TreeNodeBean;


public class SimpleTreeListViewAdapter<T> extends TreeListViewAdapter<T> {

	public SimpleTreeListViewAdapter(Context context, ListView tree, int defaultExpandLevel) throws IllegalAccessException, IllegalArgumentException {
		super(context, tree, defaultExpandLevel);
	}

	@Override
	public View getConvertView(final Node node, final int position, View convertView, final ViewGroup parent) {
		
		ViewHolder holder = null;

        if (convertView==null) {
            HorizontalScrollView scrollView = new HorizontalScrollView(parent.getContext());
            scrollView.setDescendantFocusability(ViewGroup.FOCUS_BLOCK_DESCENDANTS);
            scrollView.setLayoutParams(new AbsListView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));

            LinearLayout linearLayout = new LinearLayout(parent.getContext());
            linearLayout.setOrientation(LinearLayout.HORIZONTAL);
            linearLayout.setGravity(Gravity.CENTER_VERTICAL);

            final ImageView imageView = new ImageView(parent.getContext());
            final TextView textView = new TextView(parent.getContext());
            textView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));

            imageView.setPadding(20,20,20,20);
            imageView.setLayoutParams(new LinearLayout.LayoutParams(100,100));
            imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Node node1 = (Node) imageView.getTag();
                    node1.isPlusOrMins = !node1.isPlusOrMins;
                    imageView.setImageDrawable(node1.isPlusOrMins?new PlusDrawable():new MinsDrawable());
                    textView.setText(node1.isPlusOrMins?node1.getName():node1.getFullName());
                }
            });
            linearLayout.addView(imageView);


            linearLayout.addView(textView);

            holder = new ViewHolder();
            scrollView.setHorizontalScrollBarEnabled(false);

            scrollView.addView(linearLayout);
			holder.mText = textView;
            holder.plus = imageView;
            convertView = scrollView;
			convertView.setTag(holder);

		}else {
			holder = (ViewHolder) convertView.getTag();
		}

        holder.plus.setTag(node);
        holder.plus.setImageDrawable(node.isPlusOrMins?new PlusDrawable():new MinsDrawable());

        if(node.getType() == TreeNodeBean.TYPE_CONTENT)
        {
            holder.plus.setVisibility(View.VISIBLE);
            holder.mText.setTextColor(Color.WHITE);
            holder.mText.setTextSize(16);
        }
        else if(node.getType() == TreeNodeBean.TYPE_TOP_CONTENT)
        {
            holder.plus.setVisibility(View.VISIBLE);
            holder.mText.setTextColor(Color.RED);
            holder.mText.setTextSize(16);
        }
        else
        {
            holder.plus.setVisibility(View.GONE);
            holder.mText.setTextColor(Color.WHITE);
            holder.mText.setTextSize(20);
        }


        holder.mText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                expandOrCollapse(position);
            }
        });

        holder.mText.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                Intent intent = new Intent(parent.getContext(), TechDetailActivity.class);
                intent.putExtra("node",node);
                parent.getContext().startActivity(intent);
                return true;
            }
        });

		holder.mText.setText(node.isPlusOrMins?node.getName():node.getFullName());
		return convertView;
	}

	private class ViewHolder{
		TextView mText;
        ImageView plus;
	}


}
