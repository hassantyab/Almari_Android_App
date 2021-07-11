package com.fyp.hassan.almari.SingleProductAvtivity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;


import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.fyp.hassan.almari.Customer_Order.MyOrdersListClass;
import com.fyp.hassan.almari.User_Management.MyAccount;
import com.fyp.hassan.almari.Search_Activities.Search_Activity;
import com.fyp.hassan.almari.User_Management.feedback_package.reviewData;
import com.fyp.hassan.almari.User_Management.feedback_package.review_adapter;
import com.fyp.hassan.almari.home_tabs.HomeActivity;
import com.fyp.hassan.almari.LoginClasses.UserSessionManager;
import com.fyp.hassan.almari.R;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class Product_user_reviews extends AppCompatActivity implements View.OnClickListener {

    private TextView productTitle,productPrice;
    private RatingBar ratingBar;
    private CheckBox full_review_Checkbox;
    private EditText username,reviewTitle,reviewText;
    private Button send_Review_Btn;
    private UserSessionManager session;
    private LinearLayout full_review_layout ;
    private ImageView backbtn;
    private Toolbar myToolbar;
    private Intent in;
    private String productID;
    private LinearLayout linearLayout;
    private RequestQueue requestQueue;
    private RecyclerView recyclerView;
    private List<reviewData> reviewList;
    private review_adapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_user_reviews);

        //Elements
        in = getIntent();
        requestQueue= Volley.newRequestQueue(this);
        linearLayout= (LinearLayout)findViewById(R.id.review_layout);
        recyclerView=(RecyclerView)findViewById(R.id.review_products_recyclerview);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        myToolbar =(Toolbar) findViewById(R.id.review_toolbar);
        productTitle = (TextView)findViewById(R.id.reviewProductTitle);
        productPrice = (TextView)findViewById(R.id.reviewProductPrice);
        ratingBar=(RatingBar)findViewById(R.id.review_ratingBar);
        full_review_Checkbox =(CheckBox)findViewById(R.id.reviewFullCheckbox);
        username =(EditText)findViewById(R.id.reviewerName);
        reviewTitle =(EditText)findViewById(R.id.reviewTitle);
        reviewText= (EditText)findViewById(R.id.reviewText);
        send_Review_Btn =(Button)findViewById(R.id.review_sendBtn);
        full_review_layout = (LinearLayout)findViewById(R.id.fullreviewLayout);
        backbtn=(ImageView)findViewById(R.id.review_back_btn);
        session = new UserSessionManager(this);
        setSupportActionBar(myToolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);


        backbtn.setOnClickListener(this);
        send_Review_Btn.setOnClickListener(this);

        full_review_Checkbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked)
                {
                    full_review_layout.setVisibility(View.VISIBLE);
                }
                else
                {
                    full_review_layout.setVisibility(View.GONE);
                }
            }
        });

        if (session.isUserLoggedIn())
        {
            try {
                if (in.hasExtra("pId")) {
                    productID = in.getStringExtra("pId");
                    setData(in.getStringExtra("pTitle"), in.getStringExtra("pPrice"), "");
                    setproductReviews(productID);
                    linearLayout.setVisibility(View.VISIBLE);
                }
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        }

    }

    private void setproductReviews(String productID) {
        try {
            String url = this.getResources().getString(R.string.ApiAddress) + "api/product/product-reviews";
            JSONObject object = new JSONObject();
            object.put("productId",productID);
            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, object, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                Log.i("productReviews",response.toString());
                processProductReviews(response);
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

    private void processProductReviews(JSONObject response) {
        try
        {
            JSONArray reviewArray= response.getJSONArray("reviews");
            reviewList =new ArrayList<>();
            if(!reviewArray.toString().isEmpty())
            {
                for(int i =0; i <reviewArray.length();i++)
                {
                    JSONObject jsonObject =reviewArray.getJSONObject(i);
                    reviewData review = new reviewData(jsonObject.getString("status"),jsonObject.getString("_id"),jsonObject.getString("reviewString"),jsonObject.getString("reviewTitle"),jsonObject.getString("numOfStars"),jsonObject.getString("userId"), jsonObject.getString("productId"));
                    reviewList.add(review);
                }
                if(!reviewList.isEmpty()) {
                    adapter = new review_adapter(this, reviewList);
                    recyclerView.setAdapter(adapter);
                    adapter.notifyDataSetChanged();
                }
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }


    void setData(String pTitle,String pPrice, String pRating)
    {
        productTitle.setText(pTitle);
        productPrice.setText("Rs: " + pPrice);
    }




    @Override
    public void onClick(View v) {
        if(send_Review_Btn.getId()==v.getId())
        {
            sendData();
        }

        if(v.getId()==backbtn.getId())
        {
            finish();
        }
    }


    void sendData()
    {
        if(validateFields())
        {
            try {
                String url = this.getString(R.string.ApiAddress) + "api/product/review";
                JSONObject jsonObject = new JSONObject();
                if(full_review_Checkbox.isChecked()) {

                    jsonObject.put("userId", session.getUserId());
                    jsonObject.put("productId", productID);
                    jsonObject.put("numOfStars", ""+(int) ratingBar.getRating());
                    jsonObject.put("reviewTitle", reviewTitle.getText());
                    jsonObject.put("reviewString", reviewText.getText());
                }
                else
                {

                }
                Log.i("jsonData", jsonObject.toString());
                JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, jsonObject, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                            processData(response);
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        error.printStackTrace();
                        Toast.makeText(getApplicationContext(),"review cancelled",Toast.LENGTH_SHORT).show();
                    }
                });

                requestQueue.add(jsonObjectRequest);
            }
            catch (Exception e)
            {
                e.printStackTrace();
                Toast.makeText(this,"review cancelled",Toast.LENGTH_SHORT).show();
            }
        }

    }

    void processData(JSONObject data)
    {
        if(!data.toString().isEmpty())
        {
            Log.i("review", data.toString());
            Intent n = new Intent();
            n.putExtra("review",true);
            setResult(RESULT_OK, n);
            finish();

        }


    }

    private boolean validateFields() {
        if (!username.getText().toString().isEmpty() &&
                !reviewText.getText().toString().isEmpty() &&
                !reviewTitle.getText().toString().isEmpty()
                ) {
            return true;
        }

        if (username.getText().toString().isEmpty())
            username.setError("Please enter valid name");

        if (reviewTitle.getText().toString().isEmpty())
            reviewTitle.setError("Please enter a valid title");

        if(reviewText.getText().toString().isEmpty())
            reviewText.setError("Please enter valid comment");

        return false;
    }
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        invalidateOptionsMenu();
        menu.findItem(R.id.Myaccount).setVisible(false);
        menu.findItem(R.id.cart_menu).setVisible(true);
        menu.findItem(R.id.menu_Home).setVisible(true);
        menu.findItem(R.id.Submenu_Myaccount).setVisible(true);
        menu.findItem(R.id.SaveMenu).setVisible(true);
        menu.findItem(R.id.SignIn).setVisible(false);
        menu.findItem(R.id.Logout).setVisible(false);
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main,menu);
        return true;
    }




    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        try {
            switch (item.getItemId()) {

                case R.id.cart_menu:
                    in = new Intent(this, HomeActivity.class);
                    in.putExtra("tab", 2);
                    startActivity(in);
                    return true;

                case R.id.menu_Home:
                    in = new Intent(this, HomeActivity.class);
                    startActivity(in);
                    return true;
                case R.id.Submenu_Myaccount:
                    in = new Intent(this, MyAccount.class);
                    startActivity(in);
                    return true;
                case R.id.SearchOption:
                    in = new Intent(this, Search_Activity.class);
                    startActivity(in);
                    return true;
                case R.id.MyOrders:
                    in = new Intent(this, MyOrdersListClass.class);
                    startActivity(in);
                    return true;
                default:
                    return super.onOptionsItemSelected(item);

            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return false;
    }
}



