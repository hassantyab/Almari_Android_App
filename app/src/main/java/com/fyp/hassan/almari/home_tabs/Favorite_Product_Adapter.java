package com.fyp.hassan.almari.home_tabs;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
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

public class Favorite_Product_Adapter extends RecyclerView.Adapter<Favorite_Product_Adapter.MyViewHolder>
{
    Context mContext;
    List<Product> productList;
    private Button btn_rmv,btn_cancel;
    private View mView;
    private Favourite favourite;
    private RequestQueue requestQueue;
    private UserSessionManager session;



    public Favorite_Product_Adapter(Context context, List<Product> productList,Favourite object)
    {
        this.mContext =context;
        this.productList=productList;
        this.favourite=object;
        setHasStableIds(true);
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_favorite_recycle_card, parent, false);
        requestQueue= Volley.newRequestQueue(mContext);
        session= new UserSessionManager(mContext);
        return new Favorite_Product_Adapter.MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position)
    {
        final Product product = productList.get(position);
        holder.brandname.setText(product.getBrandName());
        holder.Product_title.setText(product.getTitle());
        holder.Product_price.setText("Rs: "+product.getPrice());
       try {
           RequestOptions options = new RequestOptions()
                   .centerCrop()
                   .priority(Priority.HIGH);

           new GlideImageLoader(holder.product_image,
                   holder.progressBar).load("https://s3.us-east-2.amazonaws.com/almari-2018/" + product.getImgs().get(0), options);

       // Glide.with(mContext).load("https://s3.us-east-2.amazonaws.com/almari-2018/" + favourite.productList.get(position).getImgs().get(0)).into(holder.product_image);


        holder.fav_buy_btn.setOnClickListener(new View.OnClickListener() {
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
        holder.fav_remove_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(v.getId()==holder.fav_remove_btn.getId())
                {
                    try
                    {
                        LayoutInflater inflater = LayoutInflater.from(mContext);
                        mView=inflater.inflate(R.layout.dialog_message,null,false);
                        btn_rmv=(Button)mView.findViewById(R.id.Btn_Dialog_Rmv);
                        btn_cancel=(Button)mView.findViewById(R.id.Btn_Dialog_Cancel);
                        final AlertDialog.Builder mBuilder = new AlertDialog.Builder(mContext);
                        mBuilder.setView(mView);
                        final AlertDialog dialog = mBuilder.create();
                        dialog.show();

                        btn_rmv.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                dialog.dismiss();
                                String url =mContext.getString(R.string.ApiAddress)+"api/user/favoriteProduct/" + session.getUserId() + "/" + productList.get(position).getId();
                                JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.PUT, url, null, new Response.Listener<JSONObject>() {
                                    @Override
                                    public void onResponse(JSONObject response) {
                                        proccessData(response);

                                    }
                                }, new Response.ErrorListener() {
                                    @Override
                                    public void onErrorResponse(VolleyError error) {
                                       error.printStackTrace();
                                    }
                                });
                                requestQueue.add(jsonObjectRequest);
                            }
                        });

                        btn_cancel.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                dialog.dismiss();
                            }
                        });

                    }
                    catch (Exception e)
                    {
                        e.printStackTrace();

                    }
                }
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
                   // Toast.makeText(mContext,response.toString(),Toast.LENGTH_SHORT).show();
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



    private void proccessData(JSONObject jsonObject)
    {
        favourite.adapter.notifyDataSetChanged();
        favourite.getFragmentManager().beginTransaction().detach(favourite).attach(favourite).commit();
    }




    @Override
    public int getItemCount() {
        return productList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        private TextView brandname,Product_title,Product_price;
        private ImageView product_image;
        private Button fav_buy_btn,fav_remove_btn;
        private ProgressBar progressBar;
        private View cardView;
        private MyViewHolder(View v) {
            super(v);

            brandname=(TextView)v.findViewById(R.id.favBrand);
            Product_title =(TextView)v.findViewById(R.id.fav_title);
            Product_price =(TextView)v.findViewById(R.id.fav_Price);
            product_image=(ImageView)v.findViewById(R.id.fav_imageView);
            fav_buy_btn=(Button)v.findViewById(R.id.fav_buybtn);
            fav_remove_btn=(Button)v.findViewById(R.id.fav_Delete_Button);
            progressBar=(ProgressBar)v.findViewById(R.id.fav_progressBar);
            cardView=v;
        }
    }
}
