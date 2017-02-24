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
import android.graphics.Color;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.FrameLayout;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.tech.StackObject;
import com.tech.android.treeview.bean.Node;
import com.tech.android.treeview.bean.TechValue;
import com.tech.android.treeview.bean.TreeNodeBean;
import com.tech.android.treeview.utils.ColorUtils;
import com.tech.android.treeview.utils.SpanUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import lecho.lib.hellocharts.listener.DummyPieChartOnValueSelectListener;
import lecho.lib.hellocharts.model.Axis;
import lecho.lib.hellocharts.model.Column;
import lecho.lib.hellocharts.model.ColumnChartData;
import lecho.lib.hellocharts.model.PieChartData;
import lecho.lib.hellocharts.model.SliceValue;
import lecho.lib.hellocharts.model.SubcolumnValue;
import lecho.lib.hellocharts.view.ColumnChartView;
import lecho.lib.hellocharts.view.PieChartView;


public class DetailTreeListViewAdapter<T> extends TreeListViewAdapter<T> {

    private Node recentNode;

	public DetailTreeListViewAdapter(Context context, ListView tree, int defaultExpandLevel,Node recentNode) throws IllegalAccessException, IllegalArgumentException {
		super(context, tree, defaultExpandLevel);
        this.recentNode = recentNode;
	}

	@Override
	public View getConvertView(final Node node, final int position, View convertView, final ViewGroup parent) {
		
		ViewHolder holder = null;

        if (convertView==null) {
            HorizontalScrollView scrollView = new HorizontalScrollView(parent.getContext());
            scrollView.setDescendantFocusability(ViewGroup.FOCUS_BLOCK_DESCENDANTS);
            scrollView.setLayoutParams(new AbsListView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));

            LinearLayout linearLayout = new LinearLayout(parent.getContext());
            linearLayout.setLayoutParams(new FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            linearLayout.setOrientation(LinearLayout.VERTICAL);
            linearLayout.setGravity(Gravity.CENTER_VERTICAL);

            final TextView textView = new TextView(parent.getContext());
            textView.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            linearLayout.addView(textView);
            PieChartView pieChartView = new PieChartView(parent.getContext());
            pieChartView.setLayoutParams(new LinearLayout.LayoutParams(parent.getWidth(),1000));
            pieChartView.setOnValueTouchListener(new DummyPieChartOnValueSelectListener(){
                @Override
                public void onValueSelected(int arcIndex, SliceValue value) {
                    TechValue techValue = (TechValue) value;
                    Toast.makeText(parent.getContext(),techValue.toastStr,Toast.LENGTH_SHORT).show();
                }
            });

            ColumnChartView allPieChartView = new ColumnChartView(parent.getContext());
            allPieChartView.setLayoutParams(new LinearLayout.LayoutParams(parent.getWidth(),1000));
            allPieChartView.setPadding(100,0,100,0);
            linearLayout.addView(pieChartView);
            linearLayout.addView(allPieChartView);

            holder = new ViewHolder();
            scrollView.setHorizontalScrollBarEnabled(false);

            scrollView.addView(linearLayout);
			//holder.mIcon = (ImageView) convertView.findViewById(R.id.item_icon);
			holder.mText = textView;
            holder.chartView = pieChartView;
            holder.allChartView = allPieChartView;
            convertView = scrollView;
			convertView.setTag(holder);

		}else {
			holder = (ViewHolder) convertView.getTag();
		}

        if(node.getType() == TreeNodeBean.TYPE_CONTENT)
        {
            holder.mText.setTextColor(Color.WHITE);
            holder.mText.setTextSize(16);
        }
        else if(node.getType() == TreeNodeBean.TYPE_TOP_CONTENT)
        {
            holder.mText.setTextColor(Color.RED);
            holder.mText.setTextSize(16);
        }
        else
        {
            holder.mText.setTextColor(Color.WHITE);
            holder.mText.setTextSize(20);
        }

        int index = node.object.className.lastIndexOf(".");
        SpannableStringBuilder sb = new SpannableStringBuilder();

        if(index > 0){
            SpanUtil.append("package  :   ",sb,new ForegroundColorSpan(Color.RED));
            SpanUtil.append(node.object.className.substring(0,index)+"\n",sb,new ForegroundColorSpan(Color.WHITE));
        }

        SpanUtil.append("method   :   ",sb,new ForegroundColorSpan(Color.RED));
        SpanUtil.append(node.object.className.substring(Math.max(index+1,0))+"."+node.object.methodName+"\n",sb,new ForegroundColorSpan(Color.WHITE));

        SpanUtil.append("total create object  :   ",sb,new ForegroundColorSpan(ColorUtils.COLOR1));
        SpanUtil.append(node.object.getCreateObjectCount()+"\n",sb,new ForegroundColorSpan(Color.WHITE));

        SpanUtil.append("self create object :   ",sb,new ForegroundColorSpan(ColorUtils.COLOR1));
        SpanUtil.append(node.object.getSelfCreateCount()+"",sb,new ForegroundColorSpan(Color.WHITE));

        List<Map.Entry<String,Integer>> list = node.object.getSortedDetail();

        for (Map.Entry<String,Integer> entry : list) {
            SpanUtil.append("\n     detail:   ",sb,new ForegroundColorSpan(Color.parseColor("#9dc794")));
            SpanUtil.append(entry.getKey()+"="+entry.getValue(),sb,new ForegroundColorSpan(Color.parseColor("#9dc794")));
        }

        holder.chartView.setPieChartData(generateData(node.object));
        holder.allChartView.setZoomEnabled(false);

        StackObject p = node.object;
        Node temp = recentNode;

        while(!temp.isRoot()){
            temp = temp.getParent();
            p = temp.object;
        }

        holder.allChartView.setColumnChartData(generateDefaultData(node.object,p));
        holder.mText.setText(sb);

        return convertView;
	}

	private class ViewHolder{
		TextView mText;
        PieChartView chartView;
        ColumnChartView allChartView;
	}

    private PieChartData generateData(StackObject stackObject) {

        List<SliceValue> values = new ArrayList<SliceValue>();

        Map<String, Integer> map = stackObject.getObjectCreateRecord();

        Set<String> keys = map.keySet();
        for (String key : keys) {

            int index = key.lastIndexOf(".");
            TechValue sliceValue = new TechValue(map.get(key), ColorUtils.nextColor());
            sliceValue.setLabel( (index >0?key.substring(Math.max(index+1,0)):key) + ":"+(int)sliceValue.getValue());
            sliceValue.toastStr = key+":"+(int)sliceValue.getValue();
            values.add(sliceValue);
        }


        PieChartData data = new PieChartData(values);
        data.setHasLabels(true);
        data.setHasLabelsOnlyForSelected(false);
        data.setHasLabelsOutside(false);
        data.setHasCenterCircle(false);

        return data;
    }

    private ColumnChartData generateDefaultData(StackObject stackObject,StackObject parent) {

        // Column can have many subcolumns, here by default I use 1 subcolumn in each of 8 columns.
        List<Column> columns = new ArrayList<Column>();

        List<SubcolumnValue> values = new ArrayList<>();


        SubcolumnValue value = new SubcolumnValue(parent.getCreateObjectCount(), ColorUtils.nextColor());
        value.setLabel("total:"+(int)value.getValue());
        values.add(value);
        Column column = new Column(values);
        column.setHasLabels(true);
        column.setHasLabelsOnlyForSelected(false);
        columns.add(column);

        values = new ArrayList<>();
        value = new SubcolumnValue(stackObject.getSelfCreateCount(), ColorUtils.nextColor());
        value.setLabel("self:"+(int)value.getValue());
        values.add(value);
        column = new Column(values);
        column.setHasLabels(true);
        column.setHasLabelsOnlyForSelected(false);
        columns.add(column);

        values = new ArrayList<>();
        value = new SubcolumnValue(stackObject.getCreateObjectCount()-stackObject.getSelfCreateCount(), ColorUtils.nextColor());
        value.setLabel("children:"+(int)value.getValue());
        values.add(value);
        column = new Column(values);
        column.setHasLabels(true);
        column.setHasLabelsOnlyForSelected(false);
        columns.add(column);




        ColumnChartData data = new ColumnChartData(columns);
        Axis axisX = new Axis();
        Axis axisY = new Axis().setHasLines(true);
        data.setAxisXBottom(null);
        data.setAxisYLeft(axisY);
        return data;
    }
}
