package com.fyp.hassan.almari.Category_package;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;

import com.fyp.hassan.almari.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class filterAdapter extends BaseExpandableListAdapter
{
    private Context mContext;
    private ArrayList<String> nameList;
    private HashMap<String,ArrayList<String>> hashMap;
    HashMap<String,String> filterValues = new HashMap<>();
    private CategoryActivity categoryActivity;
    public filterAdapter(ArrayList<String> nameList,HashMap<String,ArrayList<String>> hashMap, Context context, CategoryActivity categoryActivity )
    {
        this.mContext=context;
        this.hashMap=hashMap;
        this.nameList=nameList;
        categoryActivity=categoryActivity;
    }

    @Override
    public int getGroupCount() {
        return nameList.size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return hashMap.get(nameList.get(groupPosition)).size();
    }

    @Override
    public Object getGroup(int groupPosition) {
        return nameList.get(groupPosition);
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return hashMap.get(nameList.get(groupPosition)).get(childPosition);
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View view, ViewGroup parent) {
        String headerTitle = (String)getGroup(groupPosition);
        if(view == null)
        {
            LayoutInflater inflater = (LayoutInflater)this.mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.filter_adapter_layout,null);
        }
        TextView tagName = (TextView)view.findViewById(R.id.tag_name);
        tagName.setText(headerTitle);
        return view;
    }

    @Override
    public View getChildView(final int groupPosition, final int childPosition, final boolean isLastChild,  View view, final ViewGroup parent) {
        final String childText = (String)getChild(groupPosition,childPosition);
        if(view == null)
        {
            LayoutInflater inflater = (LayoutInflater)this.mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.filter_childview_adapter,null);
        }

        TextView childTv = (TextView)view.findViewById(R.id.filter_childTv);
        childTv.setText(childText);
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
             try {

                 ViewGroup vg = (ViewGroup) parent.getChildAt(groupPosition);
                 TextView tagValue = (TextView)vg.findViewById(R.id.tag_moreBtn);
                 tagValue.setText(childText);
                 if(!filterValues.containsKey(nameList.get(groupPosition)))
                 {
                    filterValues.put(nameList.get(groupPosition),childText);
                 }
                 else
                 {
                     filterValues.remove(nameList.get(groupPosition));
                     filterValues.put(nameList.get(groupPosition),childText);
                 }
                 notifyDataSetChanged();
             }
             catch (Exception e)
             {
                 e.printStackTrace();
             }
            }
        });



        return view;
    }



    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }
}
