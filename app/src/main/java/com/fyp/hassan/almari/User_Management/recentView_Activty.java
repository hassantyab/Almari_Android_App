package com.fyp.hassan.almari.User_Management;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
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
import com.fyp.hassan.almari.Customer_Order.MyOrdersListClass;
import com.fyp.hassan.almari.LoginClasses.Sign_In_Activity;
import com.fyp.hassan.almari.LoginClasses.UserSessionManager;
import com.fyp.hassan.almari.R;
import com.fyp.hassan.almari.Search_Activities.Search_Activity;
import com.fyp.hassan.almari.SingleProductAvtivity.Product;
import com.fyp.hassan.almari.home_tabs.HomeActivity;
import com.github.ybq.android.spinkit.style.Wave;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class recentView_Activty extends AppCompatActivity {

    RecyclerView recyclerView;
    recent_view_Adapter adapter;
    List<Product> productList;
    private RequestQueue requestQueue;
    private ProgressBar progressBar;
    private Wave wave;
    private Intent in;
    private UserSessionManager session;
    private TextView tv_cart;
    private Toolbar myToolbar;
    private ImageView backBtn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recentview__activty);
        recyclerView= (RecyclerView)findViewById(R.id.recent_view_recyclerView);
        myToolbar=(Toolbar) findViewById(R.id.recent_viewed_toolbar);
        backBtn=(ImageView) findViewById(R.id.recent_viewed_backbtn);
        setSupportActionBar(myToolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        progressBar =(ProgressBar)findViewById(R.id.recent_viewed_progressBar);
        wave = new Wave();
        progressBar.setIndeterminateDrawable(wave);
        session= new UserSessionManager(this);
        recyclerView.addItemDecoration(new decorator());
        requestQueue= Volley.newRequestQueue(this);
        productList =new ArrayList<>();
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        backGroundTask();
    }
    private void backGroundTask()
    {
        try {
            String url = this.getResources().getString(R.string.ApiAddress) + "api/user/recent";
            JSONObject jsonObject = new JSONObject();
            if(session.isUserLoggedIn())
            {
                jsonObject.put("userId",session.getUserId());
            }
            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, jsonObject, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    progressBar.setVisibility(View.GONE);
                    Log.i("srkar",response.toString());
                    processData(response);
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    error.printStackTrace();
                    progressBar.setVisibility(View.GONE);
                }
            });
            requestQueue.add(jsonObjectRequest);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }
    private void  processData(JSONObject data)
    {
        try {
            JSONArray jsonArray = data.getJSONArray("products");
            if (!jsonArray.toString().isEmpty()) {

                for (int i = 0; i < jsonArray.length(); i++) {
                    ArrayList<String> imgArray = new ArrayList<>();
                    JSONObject js = jsonArray.getJSONObject(i);
                    JSONArray on = js.getJSONArray("Images");
                    for (int j = 0; j < on.length(); j++) {
                        imgArray.add(on.getString(j));
                    }
                    Product p = new Product(js.getString("_id"), js.getString("Title"), js.getString("Description"), js.getString("Quantity"), js.getString("Price"), js.getString("BrandName"), js.getString("CategoryName"), js.getString("SubCategoryName"), imgArray,js.getString("AverageRating"));
                    productList.add(p);
                }
                if (!productList.isEmpty())
                {
                    adapter= new recent_view_Adapter(this,productList);
                    recyclerView.setAdapter(adapter);
                }
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.clear();
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return super.onCreateOptionsMenu(menu);
    }
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        menu.findItem(R.id.menu_Home).setVisible(true);
        menu.findItem(R.id.Myaccount).setVisible(false);
        menu.findItem(R.id.Submenu_Myaccount).setVisible(true);
        menu.findItem(R.id.cart_menu).setVisible(true);
        if(session.isUserLoggedIn())
        {
            setOnlineCartValue(menu);
            menu.findItem(R.id.SaveMenu).setVisible(true);
            menu.findItem(R.id.SignIn).setVisible(false);
            menu.findItem(R.id.Logout).setVisible(true);
        }
        else {
            setLocalCartValue(menu);
        }
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {


            case R.id.menu_Home:
                in = new Intent(this,HomeActivity.class);
                startActivity(in);
                finish();
                return true;
            case R.id.SearchOption:
                in = new Intent(this,Search_Activity.class);
                startActivity(in);
                return true;
            case R.id.MyOrders:
                in = new Intent(this, MyOrdersListClass.class);
                startActivity(in);
                return true;
            case R.id.cart_menu:
                in = new Intent(this,HomeActivity.class);
                in.putExtra("tab",2);
                startActivity(in);
                finish();
                return true;

            case R.id.SaveMenu:
                in = new Intent(this,HomeActivity.class);
                in.putExtra("tab",1);
                startActivity(in);
                return true;
            case R.id.SignIn:
                in = new Intent(this, Sign_In_Activity.class);
                in.putExtra("fav","sign");
                startActivityForResult(in,this.getResources().getInteger(R.integer.SignCode));
                return true;
            case R.id.Logout:
                session.logoutUser();
                Toast.makeText(this,"User is logged out",Toast.LENGTH_SHORT).show();
                return true;
            case R.id.Submenu_Myaccount:
                in = new Intent(this,MyAccount.class);
                startActivity(in);
                return true;
            default:
                return super.onOptionsItemSelected(item);

        }
    }


    private void setOnlineCartValue(final Menu menu)
    {
        try {
            String url = this.getResources().getString(R.string.ApiAddress) + "api/cart/items";
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("userId",session.getUserId());
            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, jsonObject, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response)
                {
                    try {
                        MenuItem menuItem = menu.findItem(R.id.cart_menu);
                        menuItem.setIcon(buildCounterDrawable(Integer.parseInt(response.getString("items")), R.drawable.cart_logo));

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
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
    private void setLocalCartValue(Menu menu)
    {
        MenuItem menuItem = menu.findItem(R.id.cart_menu);
        menuItem.setIcon(buildCounterDrawable(3,  R.drawable.cart_logo));
    }


    private Drawable buildCounterDrawable(int count, int backgroundImageId) {
        LayoutInflater inflater = LayoutInflater.from(this);
        View view = inflater.inflate(R.layout.cart_counter, null);
        view.setBackgroundResource(backgroundImageId);
        tv_cart = (TextView) view.findViewById(R.id.cart_logo_value);
        tv_cart.setText("" + count);
        view.measure(View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
                View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
        view.layout(0, 0, view.getMeasuredWidth(), view.getMeasuredHeight());
        view.setDrawingCacheEnabled(true);
        view.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);
        Bitmap bitmap = Bitmap.createBitmap(view.getDrawingCache());
        view.setDrawingCacheEnabled(false);

        return new BitmapDrawable(getResources(), bitmap);
    }

    public class decorator extends RecyclerView.ItemDecoration {

        @Override
        public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
            //int position = parent.getChildAdapterPosition(view); // item position
            outRect.bottom=5;
        }
    }

}
