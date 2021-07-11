package com.fyp.hassan.almari.User_Management.feedback_package;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RatingBar;
import android.widget.TextView;

import com.fyp.hassan.almari.R;

import java.util.List;

public class review_adapter extends RecyclerView.Adapter<review_adapter.myReviewHolder>
{
    private List<reviewData> reviewList;
    private Context mContext;


    public review_adapter(Context context, List<reviewData> reviewlist)
    {
        this.mContext=context;
        this.reviewList =reviewlist;
    }

    @Override
    public myReviewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(mContext).inflate(R.layout.review_list,parent,false);
        return  new myReviewHolder(view);
    }

    @Override
    public void onBindViewHolder(myReviewHolder holder, int position)
    {
        try {
            final reviewData review = reviewList.get(position);
            holder.title.setText(review.getReviewTitle());
            holder.reviewString.setText(review.getReviewString());
            holder.date.setText("2018-09-11");
            holder.ratingBar.setRating((float) Integer.parseInt(review.getStars()));
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        return reviewList.size();
    }

    public  static  class  myReviewHolder extends RecyclerView.ViewHolder
    {
        private TextView title,reviewString,date;
        private RatingBar ratingBar;
        public myReviewHolder(View itemView) {
            super(itemView);
            title= (TextView)itemView.findViewById(R.id.reviewTitle);
            reviewString=(TextView)itemView.findViewById(R.id.reviewString);
            date=(TextView)itemView.findViewById(R.id.review_date);
            ratingBar=(RatingBar)itemView.findViewById(R.id.reviewRating);
        }
    }
}
