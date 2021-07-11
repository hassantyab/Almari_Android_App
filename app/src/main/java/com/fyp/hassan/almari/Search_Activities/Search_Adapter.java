package com.fyp.hassan.almari.Search_Activities;

import android.content.Context;
import android.content.Intent;
import android.media.Image;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Priority;
import com.bumptech.glide.request.RequestOptions;
import com.fyp.hassan.almari.Glide_Package.GlideImageLoader;
import com.fyp.hassan.almari.R;
import com.fyp.hassan.almari.SingleProductAvtivity.Product;
import com.fyp.hassan.almari.SingleProductAvtivity.SingleProductActivity;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

public class Search_Adapter extends RecyclerView.Adapter<Search_Adapter.MyViewHolder>
{
    private Context mContext;
    private List<Product> productlist;


    public Search_Adapter(Context context, List<Product> productList)
    {
        this.mContext=context;
        this.productlist=productList;

    }


    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.search_adapter_layout, parent, false);

        return new MyViewHolder(itemView);

    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position)
    {
        try
        {
            holder.productTitle.setText(productlist.get(position).getTitle());
            holder.product_price.setText("Rs: " +productlist.get(position).getPrice());
            holder.brandName.setText(productlist.get(position).getBrandName());
            holder.cardView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        Intent in = new Intent(mContext, SingleProductActivity.class);
                        in.putExtra("Pid", productlist.get(position).getId());
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
           new GlideImageLoader(holder.productImage,holder.progressBar).load("https://s3.us-east-2.amazonaws.com/almari-2018/"+ productlist.get(position).getImgs().get(0),options);


        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

    }

    @Override
    public int getItemCount() {
        return productlist.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {
    private ImageView productImage;
    private TextView productTitle,product_price,brandName;
    private ProgressBar progressBar;
    private View cardView;


        public MyViewHolder(View itemView) {
            super(itemView);

            productImage=(ImageView)itemView.findViewById(R.id.Search_product_image);
            productTitle=(TextView)itemView.findViewById(R.id.Search_product_title);
            product_price=(TextView)itemView.findViewById(R.id.Search_product_price);
            progressBar =(ProgressBar)itemView.findViewById(R.id.Search_progressBar);
            brandName=(TextView)itemView.findViewById(R.id.Search_brandName);
            cardView=itemView;
        }
    }
}
