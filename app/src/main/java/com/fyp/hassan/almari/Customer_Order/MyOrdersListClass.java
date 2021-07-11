package com.fyp.hassan.almari.Customer_Order;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.fyp.hassan.almari.LoginClasses.Sign_In_Activity;
import com.fyp.hassan.almari.LoginClasses.UserSessionManager;
import com.fyp.hassan.almari.Order_Activities.Order;
import com.fyp.hassan.almari.Order_Activities.OrderProduct;
import com.fyp.hassan.almari.R;
import com.fyp.hassan.almari.Search_Activities.Search_Activity;
import com.fyp.hassan.almari.User_Management.MyAccount;
import com.fyp.hassan.almari.User_Management.recentView_Activty;
import com.fyp.hassan.almari.home_tabs.HomeActivity;
import com.facebook.AccessToken;
import com.facebook.login.LoginManager;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MyOrdersListClass extends AppCompatActivity implements View.OnClickListener, AdapterView.OnItemClickListener {

    private ListView listView_order;
    private ImageView backBtn;
    private Toolbar myToolbar;
    private ArrayList<String> order_Date_List,order_totalprice_List;
    private customListView customListView;
    private Intent in ;
    private UserSessionManager session;
    private RequestQueue requestQueue;
    private List<Order> orderList;
    private List<OrderProduct> orderProductList;
    private LinearLayout layout;
    private ProgressBar progressBar;
    private View no_orderlayout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_orders);
        listView_order=(ListView)findViewById(R.id.myOrder_listview);
        backBtn=(ImageView)findViewById(R.id.myOrder_backBtn);
        myToolbar=(Toolbar)findViewById(R.id.myOrderActivity_toolbar);
        progressBar=(ProgressBar)findViewById(R.id.myorders_progressBar);
        layout=(LinearLayout)findViewById(R.id.myorders_layout);
        no_orderlayout=(View)findViewById(R.id.no_order_layout);
        requestQueue= Volley.newRequestQueue(this);
        session = new UserSessionManager(this);
        backBtn.setOnClickListener(this);
        setSupportActionBar(myToolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        order_Date_List=new ArrayList<>();
        order_totalprice_List =new ArrayList<>();
        listView_order.setOnItemClickListener(this);
        backGroundTask();


    }




    private void backGroundTask()
    {
        try {
            String url = this.getResources().getString(R.string.ApiAddress) + "api/order/myOrders/" + session.getUserId();
            JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, url, null, new Response.Listener<JSONArray>() {
                @Override
                public void onResponse(JSONArray response)
                {
                    Log.i("orderData",response.toString());
                    processData(response);

                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    error.printStackTrace();
                    if(error instanceof TimeoutError)
                    {
                        backGroundTask();
                    }
                }
            }){
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    Map<String, String> params = new HashMap<String, String>();
                    params.put("Content-Type", "application/json");
                    params.put("authorization", "bearer " + session.getUserToken());
                    return params;
                }
            };
            requestQueue.add(jsonArrayRequest);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }


    private void processData(JSONArray array)
    {
        try
        {
            orderList = new ArrayList<>();
            orderProductList =new ArrayList<>();
            if(array!=null) {
                for (int i = 0; i < array.length(); i++) {

                    JSONObject jsonObject = array.getJSONObject(i);
                    int totalprice=0;
                    JSONArray productArray =jsonObject.getJSONArray("products");
                    for(int j =0; j<productArray.length();j++)
                    {
                        JSONObject productObject= productArray.getJSONObject(j);
                        JSONObject ob=productObject.getJSONObject("productId");
                        totalprice =totalprice+Integer.parseInt(productObject.getString("quantity"))* Integer.parseInt(ob.getString("Price"));
                        OrderProduct orderProduct= new OrderProduct(ob.getString("_id"),productObject.getString("quantity"));
                        orderProductList.add(orderProduct);
                    }


                    order_Date_List.add(jsonObject.getString("orderDate").substring(0,10));
                    order_totalprice_List.add(""+totalprice);
                    Order order = new Order(jsonObject.getString("_id"),jsonObject.getString("paymentType"),orderProductList,jsonObject.getString("shippingAddress"));
                    orderList.add(order);
                }

                if(!orderProductList.isEmpty()) {
                    setData(order_Date_List, order_totalprice_List);
                    no_orderlayout.setVisibility(View.GONE);
                    layout.setVisibility(View.VISIBLE);
                    progressBar.setVisibility(View.GONE);
                }
                else
                    {
                        progressBar.setVisibility(View.GONE);
                        layout.setVisibility(View.GONE);
                        no_orderlayout.setVisibility(View.VISIBLE);
                    }
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    void setData(ArrayList<String> dates , ArrayList<String> totalPrices)
    {

        customListView = new customListView(this,dates,totalPrices);
        listView_order.setAdapter(customListView);
    }


    @Override
    public void onClick(View v) {

        if(v.getId()==backBtn.getId())
        {
            finish();
        }

    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id)
    {
        try {
            in = new Intent(this, OrderDetailsClass.class);
            in.putExtra("id", orderList.get(position).getId());
            startActivity(in);
        }
        catch (Exception e)
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
            case R.id.Myaccount:
                in = new Intent(this, MyAccount.class);
                startActivity(in);
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
