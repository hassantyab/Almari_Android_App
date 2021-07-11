package com.fyp.hassan.almari.SingleProductAvtivity;
import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.Priority;
import com.bumptech.glide.request.RequestOptions;
import com.fyp.hassan.almari.Glide_Package.GlideImageLoader;
import com.fyp.hassan.almari.R;

import java.util.List;


public class ProductListAdapter extends RecyclerView.Adapter <ProductListAdapter.MyViewHolder>  {
    private Context mContext;
    private List<Product> productLists;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView title, brandname,price;
        public ImageView image;
        public ProgressBar progressBar;
        private View cardView;

        public MyViewHolder(View view) {
            super(view);
            title = (TextView) view.findViewById(R.id.Category_p_title);
            brandname = (TextView) view.findViewById(R.id.Category_p_Brand);
            price = (TextView) view.findViewById(R.id.Category_P_price);
            image = (ImageView) view.findViewById(R.id.Category_p_Image);
            progressBar=(ProgressBar)view.findViewById(R.id.Category_progress);
            cardView=view;

        }
    }


    public ProductListAdapter(Context mContext, List<Product> albumList) {
        this.mContext = mContext;
        this.productLists = albumList;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.category_card, parent, false);

        return new MyViewHolder(itemView);
    }



    @Override
    public void onBindViewHolder(final MyViewHolder holder, int position) {
       try {
           final Product product = productLists.get(position);
           holder.title.setText(product.getTitle());
           holder.brandname.setText(product.getBrandName());
           holder.price.setText(product.getPrice());
           // loading album cover using Glide library
           holder.cardView.setOnClickListener(new View.OnClickListener() {
               @Override
               public void onClick(View view) {
                   try
                   {
                       Intent in = new Intent(mContext, SingleProductActivity.class);
                       in.putExtra("Pid", product.getId());
                       mContext.startActivity(in);
                   }
                   catch (Exception e)
                   {
                       e.printStackTrace();
                   }
               }
           });


           RequestOptions options = new RequestOptions()
                   .centerCrop()
                   .priority(Priority.HIGH);

           new GlideImageLoader(holder.image,
                   holder.progressBar).load("https://s3.us-east-2.amazonaws.com/almari-2018/" + product.getImgs().get(0), options);
       }
       catch (Exception e)
       {
           e.printStackTrace();
       }

    }



    @Override
    public int getItemCount() {
        return productLists.size();
    }
}
