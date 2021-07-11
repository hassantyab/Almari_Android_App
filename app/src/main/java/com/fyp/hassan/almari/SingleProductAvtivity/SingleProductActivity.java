package com.fyp.hassan.almari.SingleProductAvtivity;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TableLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NoConnectionError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.fyp.hassan.almari.BaseActivity;
import com.fyp.hassan.almari.Customer_Order.MyOrdersListClass;
import com.fyp.hassan.almari.LoginClasses.Sign_In_Activity;
import com.fyp.hassan.almari.User_Management.MyAccount;
import com.fyp.hassan.almari.Search_Activities.Search_Activity;
import com.fyp.hassan.almari.User_Management.recentView_Activty;
import com.fyp.hassan.almari.home_tabs.HomeActivity;
import com.fyp.hassan.almari.LoginClasses.UserSessionManager;
import com.fyp.hassan.almari.R;
import com.facebook.AccessToken;
import com.facebook.login.LoginManager;
import com.github.ybq.android.spinkit.style.Wave;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class SingleProductActivity extends BaseActivity implements View.OnClickListener, CallBack {

    private Toolbar myToolbar;
    private ViewPager viewPager;
    private Button buyNow;
    private TextView tv_descripion, tv_company, tv_price, tv_product,toastTextView,reviewBtn,tv_cart,tv_quantityText;
    private RecyclerView recyclerView;
    private singleRecommedRecyclerview adapter;
    public static List<Product> productLists,recommendList;
    private List<attribute_class> attribute_list;
    private Product currentProduct;
    private Intent in ;
    private ProgressDialog pd;
    private String productId="";
    private String url,favProductId="";
    private RequestQueue requestQueue;
    private ImageView backbtn, favbtn;
    private UserSessionManager session;
    private Cursor cursor,cr;
    private RatingBar ratingBar;
    private SQLiteDatabase db;
    private RelativeLayout layout,no_internet_layout;;
    private final int REQ_CODE_REVIEW=001;
    private ProgressBar progressBar;
    private Wave wave;
    private int OnlineQuantityCounter=0;
    private FrameLayout contentFrameLayout;
    private Boolean favorited=false;
    private TableLayout table;
    private Boolean isSaved=false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_single_product);
        contentFrameLayout = (FrameLayout) findViewById(R.id.category_frameLayout);
        getLayoutInflater().inflate(R.layout.activity_single_product, contentFrameLayout);
        initializeElements();

        recyclerView.setLayoutManager(new LinearLayoutManager(this,LinearLayoutManager.HORIZONTAL,false));
        recyclerView.addItemDecoration(new decorator());
        recyclerView.setAdapter(adapter);
        recyclerView.setHasFixedSize(true);

        buyNow=(Button)findViewById(R.id.Bt_Buynow);

        buyNow.setOnClickListener(this);
        backbtn.setOnClickListener(this);
        favbtn.setOnClickListener(this);
        reviewBtn.setOnClickListener(this);

        requestQueue = Volley.newRequestQueue(this);


        if(in.hasExtra("Pid"))
        {
            productId=in.getStringExtra("Pid");
            if(isNetworkAvailable())
            {
                backgroundtask();
            }
            else
            {
                layout.setVisibility(View.GONE);
                progressBar.setVisibility(View.GONE);
                no_internet_layout.setVisibility(View.VISIBLE);
            }
        }


        setSupportActionBar(myToolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
    }


    void setRecommendedProducts(List <Product> rlist)
    {
        try {
            adapter = new singleRecommedRecyclerview(SingleProductActivity.this, rlist);
            recyclerView.setAdapter(adapter);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

    }

    private  void  initializeElements()
    {
        layout=(RelativeLayout)findViewById(R.id.single_layout);
        no_internet_layout=(RelativeLayout)findViewById(R.id.no_internet_layout);
        tv_descripion= (TextView) findViewById(R.id.Ptv_details);
        tv_company= (TextView) findViewById(R.id.Ptv_company);
        tv_price= (TextView)findViewById(R.id.Ptv_price);
        tv_product=(TextView) findViewById(R.id.Ptv_productTitle);
        tv_quantityText = (TextView)findViewById(R.id.productTotalRatings);
        recyclerView = (RecyclerView) findViewById(R.id.P_recycler_view);
        backbtn=(ImageView)findViewById(R.id.Single_back_btn);
        favbtn=(ImageView)findViewById(R.id.single_fav_button);
        table = (TableLayout)findViewById(R.id.single_Attribute_table);
        progressBar=(ProgressBar) findViewById(R.id.singleProduct_progressBar);
        wave = new Wave();
        progressBar.setIndeterminateDrawable(wave);
        myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        viewPager = (ViewPager) findViewById(R.id.ProductImageviewPager);
        ratingBar =(RatingBar)findViewById(R.id.ProductRatingBar);
        reviewBtn=(TextView)findViewById(R.id.productTotalRatings);


        in =getIntent();
        session = new UserSessionManager(this);
        productLists =  new ArrayList<>();
        recommendList =new ArrayList<>();
        attribute_list = new ArrayList<>();

    }
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {

        menu.findItem(R.id.Myaccount).setVisible(false);
        menu.findItem(R.id.cart_menu).setVisible(true);
        menu.findItem(R.id.menu_Home).setVisible(true);
        menu.findItem(R.id.Submenu_Myaccount).setVisible(true);

        if(session.isUserLoggedIn()) {
            setOnlineCartValue(menu);
            menu.findItem(R.id.SaveMenu).setVisible(true);
            menu.findItem(R.id.SignIn).setVisible(false);
            menu.findItem(R.id.Logout).setVisible(true);
        }
        else
        {
            setLocalCartValue(menu);
        }
        return super.onPrepareOptionsMenu(menu);
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
            getMenuInflater().inflate(R.menu.menu_main, menu);
            MenuItem menuItem = menu.findItem(R.id.cart_menu);
            menuItem.setIcon(buildCounterDrawable(3,  R.drawable.cart_logo));

        return super.onCreateOptionsMenu(menu);
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
              case R.id.SignIn:
                  in = new Intent(this, Sign_In_Activity.class);
                  in.putExtra("fav","sign");
                  startActivityForResult(in,this.getResources().getInteger(R.integer.SignCode));
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
              case R.id.RecentViews:
                  if (session.isUserLoggedIn()) {
                      in= new Intent(this,recentView_Activty.class);
                      startActivity(in);
                      return true;
                  } else {
                      Toast.makeText(this, "Please login", Toast.LENGTH_SHORT).show();
                  }
                  return true;
              case R.id.MyOrders:
                  if (session.isUserLoggedIn()) {
                      in = new Intent(this, MyOrdersListClass.class);
                      startActivity(in);
                      return true;
                  } else {
                      Toast.makeText(this, "Please login", Toast.LENGTH_SHORT).show();
                      return true;
                  }
              case R.id.Logout:
                  session.logoutUser();
                  logoutApp();
                  showToast("User Logged out",this);
                  return true;
              case R.id.SaveMenu:
                  in = new Intent(this,HomeActivity.class);
                  in.putExtra("tab",1);
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
    private void backgroundtask()
    {
        try {
            url = this.getString(R.string.ApiAddress) + "api/products/single";
            JSONObject jsonObject = new JSONObject();
            if(session.isUserLoggedIn())
            {
               jsonObject.put("userId",session.getUserId());
            }
            jsonObject.put("productId",productId);

            JsonObjectRequest jsonArrayRequest = new JsonObjectRequest(Request.Method.POST, url, jsonObject, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                        processData(response, false);
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    error.printStackTrace();
                    if(error instanceof TimeoutError)
                    {
                        backgroundtask();
                    }
//                    layout.setVisibility(View.GONE);
//                    progressBar.setVisibility(View.GONE);
//                    no_internet_layout.setVisibility(View.VISIBLE);
                }
            });
            requestQueue.add(jsonArrayRequest);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }
    @Override
    public void onClick(View v)
    {
        try {
                if (v.getId() == buyNow.getId()) {
                    if (session.isUserLoggedIn()) {
                        OnlineQuantityCounter++;
                        if (Integer.parseInt(currentProduct.getQuantity()) > OnlineQuantityCounter &&OnlineQuantityCounter<=10)
                            storeToOnline();
                    } else {
                        storeToLocally();
                    }
                }
                if (v.getId() == backbtn.getId()) {
                    this.finish();
                }

                if (v.getId() == favbtn.getId()) {
                    saveProduct();
                }

                if (v.getId() == reviewBtn.getId()) {
                    if (session.isUserLoggedIn()) {

                        in = new Intent(this, Product_user_reviews.class);
                        in.putExtra("pId", currentProduct.getId());
                        in.putExtra("pTitle", currentProduct.getTitle());
                        in.putExtra("pPrice", currentProduct.getPrice());
                        startActivityForResult(in, REQ_CODE_REVIEW);
                    }

                }
        }
           catch (Exception e )
                {
                    e.printStackTrace();
                }
    }
    private void storeToOnline()
    {
        try {
                String url = this.getResources().getString(R.string.ApiAddress) + "api/cart/insert";
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("userId",session.getUserId());
                jsonObject.put("productId",productId);
                JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, jsonObject, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.i("cartItem",response.toString());
                        showToast("Product added to cart",SingleProductActivity.this);
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        showToast("Product wasn't added to cart",SingleProductActivity.this);
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
    private void  storeToLocally()
    {
        try {

            db = openOrCreateDatabase("CartDatabase", MODE_PRIVATE, null);
            db.execSQL("create table if not exists CartTable ( ProductID varchar(30), ProductTitle varchar(20),CompanyName varchar(30) ,ProductDescription varchar(150),Price varchar(20), Image varchar(200),Quantity varchar(40) );");
            ContentValues cv = new ContentValues();
            cursor = db.rawQuery("select * from CartTable where ProductID=='" + productId + "';", null);

            if (cursor.getCount() == 0)
            {
                currentProduct.setQuantity("" + 1);
                db.execSQL("insert into CartTable(ProductID,ProductTitle,CompanyName,ProductDescription,Price,Image,Quantity) values (" + "'" + currentProduct.getId() + "'" + "," + "'" + currentProduct.getTitle() + "'" + "," + "'" + currentProduct.getBrandName() + "'" + "," + "'" + " " + "'" + "," + "'" + currentProduct.getPrice() + "'" + "," + "'" + currentProduct.getImgs().get(0) + "'" + "," + "'" + currentProduct.getQuantity() + "'" + ");");
                showToast("Product added to cart",this);
            }
            else
            {
                cursor.moveToFirst();
                if (Integer.parseInt(cursor.getString(cursor.getColumnIndex("Quantity"))) < 10) {
                    int a = Integer.parseInt(cursor.getString(cursor.getColumnIndex("Quantity")));
                    a++;
                    cv.put("Quantity", "" + a);
                    db.update("CartTable", cv, "ProductID=='" + currentProduct.getId() + "';", null);
                    cr = db.rawQuery("select * from CartTable where ProductID=='" + productId + "';", null);
                    cr.moveToFirst();
                    showToast("Product added to cart", this);
                }
                else
                {
                    showToast("Maximum limit of buying this product has reached ",this);
                }
            }
            db.close();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==REQ_CODE_REVIEW &&resultCode==RESULT_OK)
        {
            showToast("Product has been rated",this);
        }
    }

    @Override
    public void processData(JSONObject data , Boolean fav) {

        ArrayList<String> imgArray = new ArrayList<>();

            try {

                JSONObject js = data.getJSONObject("Product");
                JSONArray on = js.getJSONArray("Images");
                JSONArray attribute = js.getJSONArray("Attributes");

                for (int j = 0; j < on.length(); j++) {
                    imgArray.add(on.getString(j));
                }

                for (int z = 0; z < attribute.length(); z++) {
                    JSONObject jsonObject = attribute.getJSONObject(z);

                    String key = jsonObject.keys().next();
                    attribute_list.add(new attribute_class(key, jsonObject.get(key).toString()));

                }
                String description = "";
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    description = Html.fromHtml(js.getString("Description"), Html.FROM_HTML_MODE_COMPACT).toString();
                } else {
                    description = Html.fromHtml(js.getString("Description")).toString();
                }

                //   Log.i("mydata"," " +attribute.toString());
                Product p = new Product(js.getString("_id"), js.getString("Title"), description, js.getString("Quantity"), js.getString("Price"), js.getString("BrandName"), js.getString("CategoryName"), js.getString("SubCategoryName"), imgArray, js.getString("AverageRating"));
                productLists.add(p);
                setAttribute(attribute_list);
                currentProduct = productLists.get(0);

                //filter_heart
                if (data.has("fav")) {
                    JSONArray favArray = data.getJSONArray("fav");
                    Log.d("favArray", favArray.toString());
                    for (int i = 0; i < favArray.length(); i++) {
                        if (currentProduct.getId().equals(favArray.getString(i))) {
                            isSaved = true;
                            favProductId = favArray.getString(i);
                            Log.d("favObject", favArray.getString(i));
                        }
                    }
                    if (isSaved) {
                        DrawableCompat.setTint(favbtn.getDrawable(), ContextCompat.getColor(this, R.color.colorPrimary));
                    }
                }
                if (Double.parseDouble(p.getNumOfStars()) > 0) {
                    tv_quantityText.setText("Rate it");
                }
                tv_descripion.setText(currentProduct.getDescription());
                tv_company.setText(currentProduct.getBrandName());
                tv_price.setText("Rs: " + currentProduct.getPrice());
                tv_product.setText(currentProduct.getTitle());
                ratingBar.setRating(Float.parseFloat(currentProduct.getNumOfStars()));

                JSONArray prodArray = data.getJSONArray("prods");
                List<Product> recommendList = new ArrayList<>();
                for (int i = 0; i < prodArray.length(); i++)
                {
                    JSONObject jsonO = prodArray.getJSONObject(i);
                    JSONArray ja = jsonO.getJSONArray("Images");
                    ArrayList<String> imgss= new ArrayList<>();
                    imgss.add(ja.getString(0));
                    Product pro = new Product(jsonO.getString("_id"), jsonO.getString("Title"), description, jsonO.getString("Quantity"), jsonO.getString("Price"), jsonO.getString("BrandName"), jsonO.getString("CategoryName"), jsonO.getString("SubCategoryName"), imgss, jsonO.getString("AverageRating"));
                    recommendList.add(pro);
                }
                setRecommendedProducts(recommendList);
                layout.setVisibility(View.VISIBLE);
                progressBar.setVisibility(View.GONE);
                recyclerView.setAdapter(adapter);
                ViewPageAdapter v = new ViewPageAdapter(this,productLists);
                viewPager.setAdapter(v);
            }
            catch (JSONException e)
            {
                e.printStackTrace();
            }
    }

    private void setAttribute(List<attribute_class> list)
    {
       try
       {
           for (int i = 0; i < list.size(); i++)
           {
               table.addView(tableChild(list,i), i);
           }
       }
       catch (Exception e)
       {
           e.printStackTrace();
       }
    }

    private View tableChild(List<attribute_class> list,int i)
    {
        View v;
        v = LayoutInflater.from(this).inflate(R.layout.tablerow1, null, false);

        TextView name = (TextView) v.findViewById(R.id.row1_name);
        name.setText("" + list.get(i).getName());
        TextView value = (TextView) v.findViewById(R.id.row1_value);
        value.setText("" + list.get(i).getValue());
        return v;
    }

    private void saveProduct()
    {
        try
        {
            if(session.isUserLoggedIn()) {
                if(isSaved)
                {
                    url =this.getString(R.string.ApiAddress)+"api/user/favoriteProduct/" + session.getUserId() + "/" + favProductId;
                    JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.PUT, url, null, new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            isSaved=false;
                            DrawableCompat.setTint(favbtn.getDrawable(),ContextCompat.getColor(SingleProductActivity.this,R.color.grey));
                            showToast("Product removed from favorite items",SingleProductActivity.this);

                        }
                    }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            error.printStackTrace();
                        }
                    });
                    requestQueue.add(jsonObjectRequest);
                }

                else {
                     url = this.getString(R.string.ApiAddress) + "api/user/favoriteProduct";
                    JSONObject js = new JSONObject();
                    js.put("userId", session.getUserId());
                    js.put("productId", productLists.get(0).getId());

                    JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, js, new Response.Listener<JSONObject>() {

                        @Override
                        public void onResponse(JSONObject response) {

                                isSaved=true;
                                DrawableCompat.setTint(favbtn.getDrawable(),ContextCompat.getColor(SingleProductActivity.this,R.color.colorPrimary));
                                showToast("Product added to favorite items",SingleProductActivity.this);
                        }
                    }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            error.printStackTrace();
                        }
                    });
                    requestQueue.add(jsonObjectRequest);
                }


            }
            else
            {
                showToast("Please login to favorite porduct",this);
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }
    @Override
    public void onBackPressed() {

        finish();

    }
    private void showToast( String msg, Context context) {
        try {
            LayoutInflater inflater = getLayoutInflater();
            View layout = inflater.inflate(R.layout.custom_toast_layout,
                    (ViewGroup) findViewById(R.id.Toast_Layout));
            TextView text = (TextView) layout.findViewById(R.id.toastTextView);
            text.setText(msg);
            Toast toast = new Toast(context);
            int y = getSupportActionBar().getHeight();
            toast.setGravity(Gravity.TOP | Gravity.FILL_HORIZONTAL, 0, y);
            toast.setDuration(Toast.LENGTH_SHORT);
            toast.setView(layout);
            toast.show();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

    }

    private void setViewLayout(int id){

        try {
            contentFrameLayout.removeAllViews();
            getLayoutInflater().inflate(id, contentFrameLayout);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }
    private boolean isNetworkAvailable() {
        try {
            ConnectivityManager connectivityManager
                    = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
            if (connectivityManager != null) {
                NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
                return activeNetworkInfo != null && activeNetworkInfo.isConnected() && activeNetworkInfo.isConnectedOrConnecting();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public class decorator extends RecyclerView.ItemDecoration {
        @Override
        public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
            outRect.left=10;
        }
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


interface CallBack{

    void processData(JSONObject s , Boolean fav);
}