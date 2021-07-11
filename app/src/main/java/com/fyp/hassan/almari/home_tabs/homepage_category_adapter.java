package com.fyp.hassan.almari.home_tabs;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

public class homepage_category_adapter extends RecyclerView.Adapter<homepage_category_adapter.myCategoryHolder>
{


    @Override
    public myCategoryHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return null;
    }

    @Override
    public void onBindViewHolder(myCategoryHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return 0;
    }

    public static class myCategoryHolder extends RecyclerView.ViewHolder
    {

        public myCategoryHolder(View itemView) {
            super(itemView);
        }
    }

}
