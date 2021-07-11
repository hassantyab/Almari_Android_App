package com.fyp.hassan.almari.User_Management;

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
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Priority;
import com.bumptech.glide.request.RequestOptions;
import com.fyp.hassan.almari.Glide_Package.GlideImageLoader;
import com.fyp.hassan.almari.LoginClasses.UserSessionManager;
import com.fyp.hassan.almari.R;
import com.fyp.hassan.almari.SingleProductAvtivity.Product;
import com.fyp.hassan.almari.SingleProductAvtivity.SingleProductActivity;

import org.json.JSONObject;

import java.util.List;

public class recent_view_Adapter extends RecyclerView.Adapter<recent_view_Adapter.MyViewHolder>
{
    List<Product> productList;
    Context mContext;
    private UserSessionManager session;
    private RequestQueue  requestQueue;

    public recent_view_Adapter(Context context , List<Product> productList)
    {
        this.mContext=context;
        this.productList=productList;
        session=new UserSessionManager(mContext);
        requestQueue = Volley.newRequestQueue(mContext);
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.recenty_viewed_adapter_layout, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position)
    {
        try {
            final Product product = productList.get(position);
            holder.brandName.setText(product.getBrandName());
            holder.title.setText(product.getTitle());
            holder.price.setText("Rs:" + product.getPrice());
            RequestOptions options = new RequestOptions()
                    .centerCrop()
                    .priority(Priority.HIGH);

            new GlideImageLoader(holder.imageView,
                    holder.progressBar).load("https://s3.us-east-2.amazonaws.com/almari-2018/" + product.getImgs().get(0), options);

            holder.buyBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    storeToOnline(product.getId());
                }
            });
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

    private void storeToOnline(String id)
    {
        try {
            String url = mContext.getResources().getString(R.string.ApiAddress) + "api/cart/insert";
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("userId",session.getUserId());
            jsonObject.put("productId",id);
            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, jsonObject, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    Toast.makeText(mContext,response.toString(),Toast.LENGTH_SHORT).show();
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Toast.makeText(mContext,"Product not added to cart",Toast.LENGTH_SHORT).show();
                    error.printStackTrace();
                }
            });
            requestQueue.add(jsonObjectRequest);

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
        private TextView brandName,title,price;
        private ImageView imageView;
        private Button buyBtn;
        private ProgressBar progressBar;
        private View cardView;

        public MyViewHolder(View itemView) {
            super(itemView);
        brandName=(TextView)itemView.findViewById(R.id.recent_viewed_brandName);
        title=(TextView)itemView.findViewById(R.id.recent_viewed_title);
        price=(TextView)itemView.findViewById(R.id.recent_viewed_price);
        imageView=(ImageView)itemView.findViewById(R.id.recent_viewed_image);
        buyBtn= (Button)itemView.findViewById(R.id.recent_viewed_btn);
        progressBar=(ProgressBar)itemView.findViewById(R.id.recent_viewed_prgressBar);
        cardView =itemView;
        }
    }
}
