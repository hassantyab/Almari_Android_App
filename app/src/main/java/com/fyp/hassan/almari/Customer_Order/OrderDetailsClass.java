package com.fyp.hassan.almari.Customer_Order;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Header;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.fyp.hassan.almari.LoginClasses.Sign_In_Activity;
import com.fyp.hassan.almari.LoginClasses.UserSessionManager;
import com.fyp.hassan.almari.R;
import com.fyp.hassan.almari.Search_Activities.Search_Activity;
import com.fyp.hassan.almari.SingleProductAvtivity.Product;
import com.fyp.hassan.almari.User_Management.MyAccount;
import com.fyp.hassan.almari.User_Management.recentView_Activty;
import com.fyp.hassan.almari.home_tabs.HomeActivity;
import com.facebook.AccessToken;
import com.facebook.login.LoginManager;
import com.github.ybq.android.spinkit.style.Wave;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class OrderDetailsClass extends AppCompatActivity {

    private TextView numofItem,totalPrice,paymentType,customerAddress,orderDate,orderId,orderStatus;
    private RecyclerView recyclerView;
    private ImageView backbtn;
    private orderRecyclerView adapter;
    List<Product> productList;
    private Intent in;
    private UserSessionManager session;
    private RequestQueue requestQueue;
    private ScrollView result_layout;
    private ProgressBar progressBar;
    private Wave wave;
    private Toolbar toolbar;
    private Button cancelOrderBtn;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_details);
        try {
            session = new UserSessionManager(this);
            toolbar = (Toolbar) findViewById(R.id.orderdetails_toolbar);
            numofItem = (TextView) findViewById(R.id.orderdetails_numOfitem);
            totalPrice = (TextView) findViewById(R.id.orderdetails_totalprice);
            paymentType = (TextView) findViewById(R.id.orderdetails_paymenttype);
            customerAddress = (TextView) findViewById(R.id.orderdetails_address);
            orderDate = (TextView) findViewById(R.id.orderdetails_date);
            orderStatus=(TextView)findViewById(R.id.order_status);
            orderId = (TextView) findViewById(R.id.orderdetails_id);
            backbtn = (ImageView) findViewById(R.id.orderdetails_backBtn);
            recyclerView = (RecyclerView) findViewById(R.id.orderdetails_recyclerView);
            recyclerView.setLayoutManager(new LinearLayoutManager(this));
            recyclerView.setHasFixedSize(true);
            result_layout = (ScrollView) findViewById(R.id.myorderDetails_layout);
            cancelOrderBtn = (Button) findViewById(R.id.cancel_order_Btn);
            progressBar = (ProgressBar) findViewById(R.id.myorderDetails_progressbar);
            wave = new Wave();
            progressBar.setIndeterminateDrawable(wave);

            cancelOrderBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String url=OrderDetailsClass.this.getResources().getString(R.string.ApiAddress)+"api/order/cancel";
                    JSONObject jsonObject = new JSONObject();
                    if (in.hasExtra("id")) {
                        try {
                            jsonObject.put("orderId", in.getStringExtra("id"));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                    JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, jsonObject, new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                                try {
                                    Toast.makeText(OrderDetailsClass.this, "Order has been cancelled", Toast.LENGTH_SHORT).show();
                                    Log.d("orderCancel", response.toString());
                                    Intent in = new Intent(OrderDetailsClass.this, MyOrdersListClass.class);
                                    startActivity(in);
                                    finish();
                                }
                                catch (Exception e)
                                {
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
            });
            requestQueue = Volley.newRequestQueue(this);
            backbtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    finish();
                }
            });
            setSupportActionBar(toolbar);
            getSupportActionBar().setDisplayShowTitleEnabled(false);

            in = getIntent();
            if (in.hasExtra("id")) {
                backgroundTask(in.getStringExtra("id"));
            }

        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

    }

    private void backgroundTask(final String id)
    {
        try {
            String url = this.getResources().getString(R.string.ApiAddress) + "api/order-details";
            JSONObject jsonObject = new JSONObject();
            if(session.isUserLoggedIn())
            {
                jsonObject.put("orderId",id);
            }

            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, jsonObject, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response)
                {

                    Log.i("singleOrder",response.toString());
                    processData(response);
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    error.printStackTrace();
                    if(error instanceof TimeoutError)
                    {
                        backgroundTask(id);
                    }
                }
            })
            {
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    Map<String, String> params = new HashMap<String, String>();
                    params.put("Content-Type", "application/json");
                    params.put("authorization", "bearer " + session.getUserToken());
                    Log.i("singleOrder",session.getUserToken());
                    return params;
                }
            }
            ;
            requestQueue.add(jsonObjectRequest);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

}
    private void processData(JSONObject object)
    {
        try
        {
            if(!object.toString().isEmpty())
            {
                JSONArray productsArray = object.getJSONArray("products");//all products
                int totalprice =0;
                int noOfitem=0;
                productList = new ArrayList<>();
                for(int i = 0;i<productsArray.length();i++)
                {
                    noOfitem++;
                    JSONObject product=productsArray.getJSONObject(i);//single product object
                    JSONObject productDetail = product.getJSONObject("productId");//product details
                    totalprice =totalprice+Integer.parseInt(product.getString("quantity"))* Integer.parseInt(productDetail.getString("Price"));
                    ArrayList<String> imgs = new ArrayList<>();
                    JSONArray imagesList = productDetail.getJSONArray("Images");
                    for (int j=0;j<imagesList.length();j++)
                    {
                        imgs.add(imagesList.getString(j));
                    }
                    productList.add(new Product(
                            productDetail.getString("_id"),
                            productDetail.getString("Title"),
                            productDetail.getString("Description"),
                            product.getString("quantity"),
                            productDetail.getString("Price"),
                            productDetail.getString("BrandName"),
                            productDetail.getString("CategoryName"),
                            productDetail.getString("SubCategoryName"),
                            imgs,
                            productDetail.getString("AverageRating")
                    ));
                }

                try {
                    if (object.getString("status").equals("Placed")) {
                       // Toast.makeText(this,"yes",Toast.LENGTH_SHORT).show();
                        cancelOrderBtn.setVisibility(View.VISIBLE);
                    }
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }
                setData(""+noOfitem,""+ totalprice,object.getString("payment"),object.getString("shippingAddress"),object.getString("orderId"),object.getString("Date").substring(0,10),productList,object.getString("status"));
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }
    private void  setData(String noOfItem,String totalprice,String paymenttype,String address,String Id, String Date,List<Product> productList,String status)
    {
        try {
            numofItem.setText(noOfItem + " Items");
            totalPrice.setText("Rs: " + totalprice);
            orderStatus.setText(""+ status);
            paymentType.setText(paymenttype);
            customerAddress.setText(address);
            orderDate.setText(Date);
            orderId.setText(Id);
            adapter = new orderRecyclerView(this, productList,status);
            recyclerView.setAdapter(adapter);
            result_layout.setVisibility(View.VISIBLE);
            progressBar.setVisibility(View.GONE);
        }catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        menu.clear();
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return super.onCreateOptionsMenu(menu);
    }
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        menu.findItem(R.id.menu_Home).setVisible(true);
        menu.findItem(R.id.Myaccount).setVisible(true);
        menu.findItem(R.id.Submenu_Myaccount).setVisible(false);
        menu.findItem(R.id.cart_menu).setVisible(false);
        menu.findItem(R.id.Logout).setVisible(false);
        menu.findItem(R.id.SignIn).setVisible(false);
        menu.findItem(R.id.MyOrders).setVisible(false);
        if(session.isUserLoggedIn())
        {
            menu.findItem(R.id.SaveMenu).setVisible(true);
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
            case R.id.Myaccount:
                in = new Intent(this, MyAccount.class);
                startActivity(in);
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
            case R.id.Logout:
                logoutApp();
                session.logoutUser();
                in = new Intent(this,HomeActivity.class);
                startActivity(in);
                Toast.makeText(this,"User is logged out",Toast.LENGTH_SHORT).show();
                finish();
                return true;
            case R.id.RecentViews:
                if (session.isUserLoggedIn()) {
                    in= new Intent(this,recentView_Activty.class);
                    startActivity(in);
                    return true;
                } else {
                    Toast.makeText(this, "Please login", Toast.LENGTH_SHORT).show();
                    return false;
                }
            default:
                return super.onOptionsItemSelected(item);

        }
    }
    private void logoutApp()
    {
        try {
            // Logout from Facebook
            if (AccessToken.getCurrentAccessToken() != null) {
                LoginManager.getInstance().logOut();
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

}
