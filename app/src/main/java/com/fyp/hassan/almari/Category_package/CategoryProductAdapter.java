package com.fyp.hassan.almari.Category_package;


import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Priority;
import com.bumptech.glide.request.RequestOptions;
import com.fyp.hassan.almari.Glide_Package.GlideImageLoader;
import com.fyp.hassan.almari.LoginClasses.UserSessionManager;
import com.fyp.hassan.almari.R;
import com.fyp.hassan.almari.SingleProductAvtivity.Product;
import com.fyp.hassan.almari.SingleProductAvtivity.SingleProductActivity;

import java.util.List;

public class CategoryProductAdapter extends RecyclerView.Adapter <CategoryProductAdapter.MyViewHolder> {


    private Context mContext;
    private List<Product> productLists;
    boolean isSwitchView = true;
    private static final int LIST_ITEM = 0;
    private static final int GRID_ITEM = 1;

    public class MyViewHolder extends RecyclerView.ViewHolder {


        TextView t1;
        private TextView title, compny, price;
        private ImageView thumbnail;
        private RelativeLayout linearLayout ;
        public ProgressBar progressBar;
        private RatingBar ratingBar;
        private MyViewHolder(View view) {
            super(view);
            title = (TextView) view.findViewById(R.id.Category_p_title);
            compny = (TextView) view.findViewById(R.id.Category_p_Brand);
            price = (TextView) view.findViewById(R.id.Category_P_price);
            thumbnail = (ImageView) view.findViewById(R.id.Category_p_Image);
            linearLayout=(RelativeLayout)view.findViewById(R.id.linearLayoutCategory);
            progressBar=(ProgressBar)view.findViewById(R.id.Category_progress);
            ratingBar=(RatingBar)view.findViewById(R.id.Category_ratingBar);
        }
    }


    public CategoryProductAdapter(Context mContext, List<Product> albumList) {
        this.mContext = mContext;
        this.productLists = albumList;

    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView=null;
        if(viewType==LIST_ITEM) {
            itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.category_list_view, parent, false);
        }
        else
        {
            itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.category_card, parent, false);
        }


        return new MyViewHolder(itemView);
    }

    public boolean toggleItemViewType () {
        this.isSwitchView = !this.isSwitchView;
        return isSwitchView;
    }

    @Override
    public int getItemViewType(int position) {
        if (isSwitchView){
            return GRID_ITEM;
        }else{
            return LIST_ITEM ;
        }
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, int position) {

       final Product product = productLists.get(position);
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
                holder.progressBar).load("https://s3.us-east-2.amazonaws.com/almari-2018/"+ CategoryActivity.catList.get(position).getImgs().get(0),options);

           // Glide.with(mContext).load("https://s3.us-east-2.amazonaws.com/almari-2018/"+ CategoryActivity.catList.get(position).getImgs().get(position)).into(holder.thumbnail);
        }

        catch (Exception e)
        {
            e.printStackTrace();
        }
       holder.linearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Toast.makeText(mContext,"" + product.getId() ,Toast.LENGTH_SHORT).show();
               try {
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

    }



    @Override
    public int getItemCount() {
        return productLists.size();
    }
}