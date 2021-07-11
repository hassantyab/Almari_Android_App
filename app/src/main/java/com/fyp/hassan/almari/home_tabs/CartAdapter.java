package com.fyp.hassan.almari.home_tabs;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Spinner;
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
import com.google.gson.JsonObject;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import static android.database.sqlite.SQLiteDatabase.openOrCreateDatabase;

public class CartAdapter extends RecyclerView.Adapter <CartAdapter.MyViewHolder>  {

    private Context mContext;
    private List<Product> productLists;
    private CartPage object;
    private SQLiteDatabase db;
    private Button btn_rmv,btn_cancel;
    private View mView;
    private int add= 0;
    private ArrayAdapter<String> ad;
    private ArrayList<String> values = new ArrayList<String>(){};
    private Cursor cursor;
    private String productQuantity;
    private UserSessionManager session;
    private RequestQueue requestQueue;

    public CartAdapter(Context mContext, List<Product> cartList, CartPage obj) {
        this.mContext = mContext;
        this.productLists = cartList;
        setHasStableIds(true);
        this.object=obj;
        values.add("1");
        values.add("2");
        values.add("3");
        values.add("4");
        values.add("5");
        values.add("6");
        values.add("7");
        values.add("8");
        values.add("9");
        values.add("10");
    }

    @Override
    public CartAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.cart, parent, false);
        session= new UserSessionManager(mContext);
        requestQueue= Volley.newRequestQueue(mContext);
        return new CartAdapter.MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int possition) {
       try {

           final Product product = productLists.get(possition);
           holder.title.setText(product.getTitle());
           holder.brandname.setText(product.getBrandName());
           holder.price.setText(product.getPrice());
           ad = new ArrayAdapter<>(mContext, R.layout.support_simple_spinner_dropdown_item, values);
           holder.quantity.setAdapter(ad);

           if (!session.isUserLoggedIn())
           {
               db = mContext.openOrCreateDatabase("CartDatabase", mContext.MODE_PRIVATE, null);
               cursor = db.rawQuery("select * from CartTable where ProductID=='" + product.getId() + "';", null);
               if (cursor.moveToFirst()) {
                   productQuantity = cursor.getString(cursor.getColumnIndex("Quantity"));
                   holder.quantity.setSelection(values.indexOf(productQuantity),false);
               }
           }
           else {
               holder.quantity.setSelection(values.indexOf(product.getQuantity()),false);
           }



           holder.cardView.setOnClickListener(new View.OnClickListener() {
               @Override
               public void onClick(View v)
               {
                try {
                    Intent in = new Intent(mContext, SingleProductActivity.class);
                    in.putExtra("Pid", product.getId());
                    mContext.startActivity(in);
                    //object.vp.setCurrentItem(0);
                    //object.tb.getTabAt(0).select();
                }catch (Exception e)
                {
                    e.printStackTrace();
                }
               }
           });

           holder.quantity.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
               @Override
               public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {


                       if (parent.getId() == holder.quantity.getId())
                       {

                           if (!session.isUserLoggedIn()) {
                               ContentValues cv = new ContentValues();
                               cv.put("Quantity", Integer.parseInt(holder.quantity.getSelectedItem().toString()));
                               db.update("CartTable", cv, "ProductID=='" + productLists.get(possition).getId() + "';", null);
                               object.getFragmentManager().beginTransaction().detach(object).attach(object).commit();


                           }
                           else {

                               object.cart_layout.setVisibility(View.GONE);
                               object.progressBar.setVisibility(View.VISIBLE);
                               update_product_Quantity(product.getId(), holder.quantity.getSelectedItem().toString());

                           }
                       }

               }

               @Override
               public void onNothingSelected(AdapterView<?> parent) {

               }
           });


           holder.btn_delete.setOnClickListener(new View.OnClickListener() {
               @Override
               public void onClick(View v) {
                   if (v.getId() == holder.btn_delete.getId()) {
                           LayoutInflater inflater = LayoutInflater.from(mContext);
                           mView = inflater.inflate(R.layout.dialog_message, null, false);
                           btn_rmv = (Button) mView.findViewById(R.id.Btn_Dialog_Rmv);
                           btn_cancel = (Button) mView.findViewById(R.id.Btn_Dialog_Cancel);
                           final AlertDialog.Builder mBuilder = new AlertDialog.Builder(mContext);
                           mBuilder.setView(mView);
                           final AlertDialog dialog = mBuilder.create();
                           dialog.show();
                           btn_rmv.setOnClickListener(new View.OnClickListener() {
                               @Override
                               public void onClick(View v) {

                                   if (!session.isUserLoggedIn()) {
                                       db.delete("CartTable", "ProductID=?", new String[]{product.getId()});
                                       productLists.remove(productLists.get(possition));
                                       object.recyclerView.getAdapter().notifyDataSetChanged();
                                       object.getFragmentManager().beginTransaction().detach(object).attach(object).commit();
                                       dialog.dismiss();
                                   }
                                   else
                                   {
                                       dialog.dismiss();
                                       remove_product_online(product.getId());
                                   }
                               }
                           });
                           btn_cancel.setOnClickListener(new View.OnClickListener() {
                               @Override
                               public void onClick(View v) {
                                   dialog.dismiss();
                               }
                           });
                   }
               }
           });

           RequestOptions options = new RequestOptions()
                   .centerCrop()
                   .priority(Priority.HIGH);

           new GlideImageLoader(holder.image,
                   holder.progressBar).load("https://s3.us-east-2.amazonaws.com/almari-2018/" + product.getImgs().get(0), options);


           // loading album cover using Glide library
           //Glide.with(mContext).load("https://s3.us-east-2.amazonaws.com/almari-2018/" + object.Cartimglist.get(possition)).into(holder.image);
       }
       catch (Exception e)
       {
           e.printStackTrace();
       }
    }



    private  void update_product_Quantity(String pid,String quantity)
    {
        try
        {
            String url = mContext.getResources().getString(R.string.ApiAddress) +"api/cart/quantity";
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("userId" , session.getUserId());
            jsonObject.put("productId",pid);
            jsonObject.put("quantity",quantity);

            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, jsonObject, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    Toast.makeText(mContext,"Product quantity updated",Toast.LENGTH_SHORT).show();
                    object.getFragmentManager().beginTransaction().detach(object).attach(object).commit();
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    error.printStackTrace();
                    object.cart_layout.setVisibility(View.VISIBLE);
                    object.progressBar.setVisibility(View.GONE);
                }
            }

            );
            requestQueue.add(jsonObjectRequest);
        }
        catch (Exception e )
        {
            e.printStackTrace();
        }

    }

    private void remove_product_online(String pid)
    {
        try {

            object.cart_layout.setVisibility(View.GONE);
            object.progressBar.setVisibility(View.VISIBLE);

            String url = mContext.getResources().getString(R.string.ApiAddress) + "api/cart/del";
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("userId",session.getUserId());
            jsonObject.put("productId",pid);

            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, jsonObject, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response)
                {
                    Toast.makeText(mContext,"Product removed",Toast.LENGTH_SHORT).show();
                    object.getFragmentManager().beginTransaction().detach(object).attach(object).commit();
                }
            },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error)
                        {
                              error.printStackTrace();
                            object.cart_layout.setVisibility(View.VISIBLE);
                            object.progressBar.setVisibility(View.GONE);
                        }
                    }
            );
            requestQueue.add(jsonObjectRequest);
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



    public class MyViewHolder extends RecyclerView.ViewHolder {
        private TextView title, brandname,price;
        private ImageView image;
        public Button btn_delete;
        private LinearLayout linearLayout;
        private Spinner quantity;
        private ProgressBar progressBar;
        private View cardView;
        private MyViewHolder(View view) {
            super(view);
            title = (TextView) view.findViewById(R.id.textViewTitle);
            quantity=(Spinner)view.findViewById(R.id.Cart_Quantity);
            brandname = (TextView) view.findViewById(R.id.textViewShortDesc);
            price = (TextView) view.findViewById(R.id.textViewPrice);
            image = (ImageView) view.findViewById(R.id.Cart_imageView);
            btn_delete=(Button)view.findViewById(R.id.Cart_Delete_Button);
            progressBar=(ProgressBar)view.findViewById(R.id.Cart_progressBar);
            cardView=view;


        }
    }
}
