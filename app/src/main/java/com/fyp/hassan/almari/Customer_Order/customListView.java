package com.fyp.hassan.almari.Customer_Order;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.fyp.hassan.almari.R;

import java.util.ArrayList;

public class customListView extends ArrayAdapter<String>
{
    Context context;
    private ArrayList<String> order_Date_List,order_totalprice_List;
    private TextView date,price;

    public customListView(Activity context,ArrayList<String> dates, ArrayList<String> price )
    {
        super(context, R.layout.myorder_customlistview,dates);
        this.context= context;
        this.order_Date_List=dates;
        this.order_totalprice_List = price;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.myorder_customlistview, null, true);
        date =(TextView)view.findViewById(R.id.customList_date);
        price =(TextView)view.findViewById(R.id.customList_Tprice);

        date.setText(order_Date_List.get(position));
        price.setText("Rs: "+order_totalprice_List.get(position));
        return view;
    }
}
