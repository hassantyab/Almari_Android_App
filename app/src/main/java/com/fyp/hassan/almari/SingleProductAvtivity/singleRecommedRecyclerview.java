package com.fyp.hassan.almari.SingleProductAvtivity;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Priority;
import com.bumptech.glide.request.RequestOptions;
import com.fyp.hassan.almari.Glide_Package.GlideImageLoader;
import com.fyp.hassan.almari.R;

import java.util.List;

public class singleRecommedRecyclerview extends RecyclerView.Adapter<singleRecommedRecyclerview.myViewholder>
{
    private Context context;
    private List<Product> productList;

    public singleRecommedRecyclerview(Context context , List<Product> productList)
    {
        this.context=context;
        this.productList=productList;
    }

    @Override
    public myViewholder onCreateViewHolder(ViewGroup parent, int viewType) {

        View itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.most_viewed_layout, parent, false);
        return new myViewholder(itemView);
    }

    @Override
    public void onBindViewHolder(myViewholder holder, int position) {
        final Product product = productList.get(position);
        try
        {
            holder.title.setText(product.getTitle());
            holder.compny.setText(product.getBrandName());
            holder.price.setText("Rs: "+product.getPrice());
            holder.ratingBar.setRating(Float.parseFloat(product.getNumOfStars()));
            RequestOptions options = new RequestOptions()
                    .centerCrop()
                    .priority(Priority.HIGH);

            new GlideImageLoader(holder.thumbnail,
                    holder.progressBar).load("https://s3.us-east-2.amazonaws.com/almari-2018/"+ product.getImgs().get(0),options);

            // Glide.with(mContext).load("https://s3.us-east-2.amazonaws.com/almari-2018/"+ CategoryActivity.catList.get(position).getImgs().get(position)).into(holder.thumbnail);
        }

        catch (Exception e)
        {
            e.printStackTrace();
        }
        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Toast.makeText(mContext,"" + product.getId() ,Toast.LENGTH_SHORT).show();
                try {
                    Intent in = new Intent(context, SingleProductActivity.class);
                    in.putExtra("Pid", product.getId());
                    context.startActivity(in);
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }

            }
        });

    }

    @Override
    public int getItemCount() {
        return productList.size();
    }

    public class myViewholder extends RecyclerView.ViewHolder
    {
        private TextView title, compny, price;
        private ImageView thumbnail;
        public ProgressBar progressBar;
        private RatingBar ratingBar;
        private View cardView;
        public myViewholder(View view) {
            super(view);
            title = (TextView) view.findViewById(R.id.Category_p_title);
            compny = (TextView) view.findViewById(R.id.Category_p_Brand);
            price = (TextView) view.findViewById(R.id.Category_P_price);
            thumbnail = (ImageView) view.findViewById(R.id.Category_p_Image);
            cardView=view;
            progressBar=(ProgressBar)view.findViewById(R.id.Category_progress);
            ratingBar=(RatingBar)view.findViewById(R.id.Category_ratingBar);
        }
    }

}
