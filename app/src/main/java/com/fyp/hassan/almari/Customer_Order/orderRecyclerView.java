package com.fyp.hassan.almari.Customer_Order;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Priority;
import com.bumptech.glide.request.RequestOptions;
import com.fyp.hassan.almari.Glide_Package.GlideImageLoader;
import com.fyp.hassan.almari.R;
import com.fyp.hassan.almari.SingleProductAvtivity.Product;
import com.fyp.hassan.almari.SingleProductAvtivity.SingleProductActivity;

import java.util.List;

public class orderRecyclerView extends RecyclerView.Adapter<orderRecyclerView.MyViewHolder>
{
    List<Product> productList;
    Context mContext;
    String status;

    public orderRecyclerView(Context context, List<Product> pList,String status)
    {
        this.mContext=context;
        this.productList=pList;
        this.status = status;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.orderdetails_card, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position)
    {

        try {
            final Product product = productList.get(position);
            holder.brandName.setText(product.getBrandName());
            holder.title.setText(product.getTitle());
            holder.price.setText("Rs: "+product.getPrice());
            holder.quantity.setText("Quantity: " + product.getQuantity());
            holder.status.setText("product is " + status);
            RequestOptions options = new RequestOptions()
                    .centerCrop()
                    .priority(Priority.HIGH);

            new GlideImageLoader(holder.imageView,
                    holder.progressBar).load("https://s3.us-east-2.amazonaws.com/almari-2018/"+ product.getImgs().get(0),options);

            holder.cardView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent in = new Intent(mContext, SingleProductActivity.class);
                    in.putExtra("Pid", product.getId());
                    mContext.startActivity(in);
                }
            });


        }
        catch (Exception e)
        {
            e.printStackTrace();
        }


    }

    @Override
    public int getItemCount() {
        return productList.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder
    {
        private TextView brandName,title,price,quantity,status;
        private ImageView imageView;
        private ProgressBar progressBar;
        private View cardView;
        public MyViewHolder(View view) {
            super(view);
            progressBar=(ProgressBar)view.findViewById(R.id.orderCard_progressBar);
            imageView=(ImageView)view.findViewById(R.id.orderCard_image);
            brandName=(TextView)view.findViewById(R.id.orderCard_brand);
            title=(TextView)view.findViewById(R.id.orderCard_title);
            quantity=(TextView)view.findViewById(R.id.orderCard_Quantity);
            status=(TextView)view.findViewById(R.id.orderCard_status);
            price=(TextView)view.findViewById(R.id.orderCard_price);
            cardView=view;

        }
    }
}
