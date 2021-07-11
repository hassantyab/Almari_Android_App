package com.fyp.hassan.almari.Category_package;

import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

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
import com.fyp.hassan.almari.LoginClasses.UserSessionManager;
import com.fyp.hassan.almari.SingleProductAvtivity.attribute_class;
import com.fyp.hassan.almari.User_Management.MyAccount;
import com.fyp.hassan.almari.R;
import com.fyp.hassan.almari.Search_Activities.Search_Activity;
import com.fyp.hassan.almari.SingleProductAvtivity.Product;
import com.fyp.hassan.almari.User_Management.recentView_Activty;
import com.fyp.hassan.almari.home_tabs.HomeActivity;
import com.facebook.AccessToken;
import com.facebook.login.LoginManager;
import com.github.ybq.android.spinkit.style.Wave;


import org.florescu.android.rangeseekbar.RangeSeekBar;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

public class CategoryActivity extends BaseActivity implements CallBack, View.OnClickListener {

    private int lastExpandedPosition=-1;
    private RecyclerView recyclerView;
    ExpandableListView filterExpList;
    private CategoryProductAdapter adapter;
    public static List<Product> catList,ratingSortList;
    private ProgressBar progressBar;
    private Intent in ;
    private Toolbar myToolbar;
    private String catname, subcatname;
    private String url;
    private RequestQueue requestQueue;
    private ImageView backbtn,gridOption;
    private UserSessionManager session;
    private Wave wave;
    private LinearLayout empty_layout;
    private FrameLayout contentFrameLayout;
    private TextView filter_option,tv_cart,tv_catName,activity_title,sortByBtn;
    private Menu menu;
    private View requestTime;
    private List<filterData> filterList;
    private List<attribute_class> attribute_List;
    private filterAdapter filterAdapter;
    private ArrayList<String> attributeNameList;
    private Button filter_doneBtn,filter_resetBtn;
    private int max=9999999,min=0;
    private EditText et_min,et_max;
    private  LinearLayout filter_layout;
    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle actionBarDrawerToggle;
    private double[] arr;
    private PopupWindow popupWindow;
    private View popUpView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
       try {
           //setContentView(R.layout.activity_category);
            contentFrameLayout = (FrameLayout) findViewById(R.id.category_frameLayout);
            getLayoutInflater().inflate(R.layout.activity_category, contentFrameLayout);
            initializeLayoutItems();

           in = getIntent();
           if(in.hasExtra("Category")) {
               catname = in.getStringExtra("Category");
               subcatname = in.getStringExtra("subCategory");
               tv_catName.setText(subcatname);
               activity_title.setText(catname);
               setOnlineFilters();
               backgroundtask();
           }
           else if(in.hasExtra("sid"))
           {
               setSalesData(in.getStringExtra("sid"));
                filter_layout.setVisibility(View.GONE);
               activity_title.setText("Sales items");
               tv_catName.setText("Sales");
           }
           else if(in.hasExtra("searchResults"))
           {
               setSearchRsults(in.getStringExtra("searchResults"));
               filter_layout.setVisibility(View.GONE);
               activity_title.setText("Search Results");
               tv_catName.setText("Search Results");
           }


           filter_option.setOnClickListener(new View.OnClickListener() {
               @Override
               public void onClick(View v) {
                   try {
                       if(drawerLayout.isDrawerOpen(Gravity.END))
                       {
                           drawerLayout.closeDrawer(Gravity.END);
                       }
                       else
                       {
                           drawerLayout.openDrawer(Gravity.END);
                       }
                   } catch (Exception e)
                   {
                       e.printStackTrace();
                   }
               }
           });

           gridOption.setOnClickListener(new View.OnClickListener() {
               @Override
               public void onClick(View v)
               { try {
                       Boolean isSwitched = adapter.toggleItemViewType();
                       recyclerView.setLayoutManager(isSwitched ?  new GridLayoutManager(CategoryActivity.this, 2): new LinearLayoutManager(CategoryActivity.this)  );
                       adapter.notifyDataSetChanged();
                       if(isSwitched)
                       { gridOption.setImageDrawable(getDrawable(R.drawable.list_view_grid));
                       }
                       else
                       { gridOption.setImageDrawable(getDrawable(R.drawable.grid_of_2));
                       }
                   }
                   catch (Exception e )
                   {
                       e.printStackTrace(); }
               }
           });

           backbtn.setOnClickListener(new View.OnClickListener() {
               @Override
               public void onClick(View v) {
                   finish();
               }
           });
           setSupportActionBar(myToolbar);
           getSupportActionBar().setDisplayShowTitleEnabled(false);

       }
       catch (Exception e)
       {
           e.printStackTrace();
       }
    }


    private void initializeLayoutItems()
    {
        try {
            activity_title = (TextView) findViewById(R.id.Category_heading);
            drawerLayout = (DrawerLayout) findViewById(R.id.category_drawer);
            requestQueue = Volley.newRequestQueue(this);
            backbtn = (ImageView) findViewById(R.id.Cat_back_btn);
            filter_option = (TextView) findViewById(R.id.category_filtersOption);
            tv_catName = (TextView) findViewById(R.id.filter_catName);
            myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
            empty_layout = (LinearLayout) findViewById(R.id.category_empty_layout);
            progressBar = (ProgressBar) findViewById(R.id.cat_main_progressBar);
            gridOption = (ImageView) findViewById(R.id.category_grid_picture);
            requestTime = (View) findViewById(R.id.timeout_layout);
            filter_doneBtn = (Button) findViewById(R.id.filter_doneBtn);
            filter_resetBtn= (Button) findViewById(R.id.filter_resetBtn);
            filterExpList = (ExpandableListView) findViewById(R.id.filter_expList);
            et_max = (EditText) findViewById(R.id.filter_Et_max);
            filter_layout = (LinearLayout) findViewById(R.id.filter_layout);
            et_min = (EditText) findViewById(R.id.filter_Et_min);
            sortByBtn =(TextView) findViewById(R.id.category_sortBy);
            wave = new Wave();
            progressBar.setIndeterminateDrawable(wave);
            session = new UserSessionManager(this);
            catList = new ArrayList<>();
            recyclerView = (RecyclerView) findViewById(R.id.Rv_categoryActivity);
            RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(this, 2);
            recyclerView.setLayoutManager(mLayoutManager);
            recyclerView.addItemDecoration(new GridSpacingItemDecoration());
            recyclerView.getItemAnimator().setAddDuration(1000);
            recyclerView.setAdapter(adapter);


            filter_doneBtn.setOnClickListener(this);
            filter_resetBtn.setOnClickListener(this);
            sortByBtn.setOnClickListener(this);


        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    private void setSearchRsults(String query)
    {
        try {
            String url = this.getResources().getString(R.string.ApiAddress) + "api/search";
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("text",query);
            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, jsonObject, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    Log.i("searchResult", response.toString());
                    processSearchResult(response);
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
            progressBar.setVisibility(View.GONE);
        }
    }


    private void processSearchResult(JSONObject jsonObject)
    {
        try {
            ArrayList<String> imgList;
            catList = new ArrayList<>();
            JSONObject topProduct = jsonObject.getJSONObject("TopProduct");
            JSONArray allResult = jsonObject.getJSONArray("allResults");

            for (int i = 0; i < allResult.length(); i++) {
                imgList = new ArrayList<>();
                JSONObject js = allResult.getJSONObject(i);
                imgList.add(js.getJSONArray("Images").get(0).toString());
                catList.add(new Product(
                        js.getString("_id"),
                        js.getString("Title"),
                        js.getString("Description"),
                        js.getString("Quantity"),
                        js.getString("Price"),
                        js.getString("BrandName"),
                        js.getString("CategoryName"),
                        js.getString("SubCategoryName"),
                        imgList,
                        js.getString("AverageRating")

                ));

            }
            if (!catList.isEmpty()) {
                adapter = new CategoryProductAdapter(this, catList);
                progressBar.setVisibility(View.GONE);
                recyclerView.setAdapter(adapter);
            }
            else
            {
                recyclerView.setVisibility(View.GONE);
                empty_layout.setVisibility(View.VISIBLE);
                progressBar.setVisibility(View.GONE);
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    private  void setSalesData(String id)
    {
        try {
            String url = this.getResources().getString(R.string.ApiAddress) + "api/sales";
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("id",id);
            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, jsonObject, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    Log.i("Allsales",response.toString());
                    processSales(response);
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

    private void processSales(JSONObject object)
    {
        try
        {
            if(!object.toString().isEmpty())
            {
                JSONArray productArray= object.getJSONArray("products");
                for(int i =0;i<productArray.length();i++) {

                    JSONObject products= productArray.getJSONObject(i);
                    JSONObject product = products.getJSONObject("productId");
                    ArrayList<String> imgs = new ArrayList<>();
                    JSONArray imagesList = product.getJSONArray("Images");
                    for (int j = 0; j < imagesList.length(); j++) {
                        imgs.add(imagesList.getString(j));
                    }
                    catList.add(new Product(
                            product.getString("_id"),
                            product.getString("Title"),
                            product.getString("Description"),
                            product.getString("Quantity"),
                            product.getString("Price"),
                            product.getString("BrandName"),
                            product.getString("CategoryName"),
                            product.getString("SubCategoryName"),
                            imgs,
                            product.getString("AverageRating")
                    ));
                }
                if(catList.isEmpty())
                {
                    recyclerView.setVisibility(View.GONE);
                    empty_layout.setVisibility(View.VISIBLE);
                    progressBar.setVisibility(View.GONE);
                }
                else {
                    adapter = new CategoryProductAdapter(this, catList);
                    progressBar.setVisibility(View.GONE);
                    recyclerView.setAdapter(adapter);
                }
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }


    private void backgroundtask()
    {
        try {
            catList.clear();
            url = this.getString(R.string.ApiAddress) + "api/products/" + catname + "/" + subcatname;

            JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, url, null, new Response.Listener<JSONArray>() {
                @Override
                public void onResponse(JSONArray response) {
                    try {
                        processData(response);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    error.printStackTrace();
                    if(error instanceof NoConnectionError) {
                        progressBar.setVisibility(View.GONE);
                        requestTime.setVisibility(View.VISIBLE);
                    }
                    else if(error instanceof TimeoutError)
                    {
                        backgroundtask();
                    }
                    else
                    {
                        progressBar.setVisibility(View.GONE);
                        requestTime.setVisibility(View.VISIBLE);
                        //setViewLayout(R.layout.internet_timeout);
                    }
                }
            });
            requestQueue.add(jsonArrayRequest);

        }
        catch (Exception e)
        {
            e.printStackTrace();
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
                logoutApp();
                session.logoutUser();
                Toast.makeText(this,"User is logged out",Toast.LENGTH_SHORT).show();
                return true;
            case R.id.Submenu_Myaccount:
                in = new Intent(this,MyAccount.class);
                startActivity(in);
                return true;
            case R.id.RecentSearches:
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
    @Override
    public void processData(JSONArray data)
    {

        if (data!=null) {

            try {

                for (int i = 0; i < data.length(); i++) {
                    JSONObject js = data.getJSONObject(i);
                    JSONArray on=js.getJSONArray("Images");
                    ArrayList<String> imgArray = new ArrayList<>();
                    for(int j =0; j<on.length();j++) {
                        imgArray.add(on.getString(j));
                    }
                    Product p = new Product(js.getString("_id"),js.getString("Title"),js.getString("Description"),js.getString("Quantity"),js.getString("Price"),js.getString("BrandName"),js.getString("CategoryName"),js.getString("SubCategoryName"),imgArray,js.getString("AverageRating"));
                    catList.add(p);
                }
               // setFilters( attributeNameList,filterHashList);


            } catch (JSONException e) {
                e.printStackTrace();
            }

            if(catList.isEmpty())
            {
                recyclerView.setVisibility(View.GONE);
                empty_layout.setVisibility(View.VISIBLE);
                progressBar.setVisibility(View.GONE);
            }
            else {
                adapter = new CategoryProductAdapter(this, catList);
                progressBar.setVisibility(View.GONE);
                recyclerView.setAdapter(adapter);
            }
        }
    }

    private void setOnlineFilters()
    {
        try {
            String url = this.getResources().getString(R.string.ApiAddress) + "api/category/" + catname;
            JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, url, null, new Response.Listener<JSONArray>() {
                @Override
                public void onResponse(JSONArray response) {
                   try {
                       attributeNameList = new ArrayList<>();
                       HashMap<String,ArrayList<String>> filterHashList= new HashMap<>();;
                       for (int i = 0; i < response.length(); i++) {
                           ArrayList<String> valueList=new ArrayList<>();
                           valueList.add("Any");
                           JSONObject jsonObject = response.getJSONObject(i);
                           attributeNameList.add(jsonObject.getString("attribute"));
                           JSONArray jsonArray = jsonObject.getJSONArray("value");
                           for(int j =0; j<jsonArray.length();j++)
                           {
                                valueList.add(jsonArray.getString(j));
                           }
                           filterHashList.put(jsonObject.getString("attribute"),valueList);
                       }

                       setFilters(attributeNameList,filterHashList);
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
                    if(error instanceof TimeoutError)
                    {
                        setOnlineFilters();
                    }
                }
            });
            requestQueue.add(jsonArrayRequest);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

    }

    private void setFilters(ArrayList<String> nameList,HashMap<String,ArrayList<String >> arrayListHashMap) {
        try {
            filterAdapter = new filterAdapter(nameList,arrayListHashMap,this,CategoryActivity.this);
            filterExpList.setAdapter(filterAdapter);
            filterExpList.setOnGroupExpandListener(new ExpandableListView.OnGroupExpandListener() {
                @Override
                public void onGroupExpand(int groupPosition) {
                    if (lastExpandedPosition != -1
                            && groupPosition != lastExpandedPosition) {
                        filterExpList.collapseGroup(lastExpandedPosition);
                    }
                    lastExpandedPosition = groupPosition;
                }
            });
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onClick(View v) {
        try {
            if (v.getId() == filter_doneBtn.getId()) {
                drawerLayout.closeDrawer(Gravity.END);
                getFilterResult();
            }

            if(sortByBtn.getId()==v.getId())
            {
                popUp();
                //sortBy();
            }
            if(filter_resetBtn.getId()==v.getId())
            {

                drawerLayout.closeDrawer(Gravity.END);
                setOnlineFilters();
                backgroundtask();
            }

        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }


    private void popUp()
    {
        try {
            popUpView = getLayoutInflater().inflate(R.layout.filter_popup, null); // inflating popup layout
            popupWindow = new PopupWindow(popUpView, LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT, true); // Creation of popup
            popupWindow.setAnimationStyle(android.R.style.Animation_Dialog);
            popupWindow.showAsDropDown(sortByBtn, 0, 0); // Displaying popup
            ArrayList<String> arrayList = new ArrayList<>();
            arrayList.add("Low-High price");
            arrayList.add("High-Low price");
            arrayList.add("Low-High ratings");
            arrayList.add("High-Low ratings");
            ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,R.layout.simple_textview,arrayList);
            ListView listView = (ListView) popUpView.findViewById(R.id.lv_sort);
            listView.setAdapter(adapter);
            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                    switch (i)
                    {
                        case 0:
                                sortByPrice_low_high();
                                popupWindow.dismiss();
                                break;
                        case 1:
                                sortByPrice_high_low();
                                popupWindow.dismiss();
                                break;
                        case 2:
                                sortByRating_low_high();
                                popupWindow.dismiss();
                                break;
                        case 3:
                                sortByRating_high_low();
                                popupWindow.dismiss();
                                break;
                    }
                }
            });
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

    }
    private void getFilterResult()
    {
       try {
           HashMap<String, String> filterHashmap = filterAdapter.filterValues;
           //Toast.makeText(this, filterHashmap.toString(), Toast.LENGTH_SHORT).show();
           String url = this.getResources().getString(R.string.ApiAddress) + "api/products/filter";
           JSONObject jsonObject = new JSONObject();
           JSONArray jsonArray = new JSONArray();
           for (String name : filterHashmap.keySet()) {
               String key = name;
               String value = filterHashmap.get(name);
               if (!value.equals("Any")) {
                   jsonArray.put(new JSONObject().put(key, value));
               }
           }
           Log.i("filterarray", jsonArray.toString());
           if (checkPrice() && jsonArray.length()>0) {
               jsonObject.put("subCat", subcatname);
               jsonObject.put("attributes", jsonArray);
               jsonObject.put("maxPrice", max);
               jsonObject.put("minPrice", min);
               Log.i("filterHash", jsonObject.toString());
               recyclerView.setVisibility(View.GONE);
               progressBar.setVisibility(View.VISIBLE);
               JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, jsonObject, new Response.Listener<JSONObject>() {
                   @Override
                   public void onResponse(JSONObject response) {
                       Log.i("filterresult", response.toString());
                       processFilterResult(response);
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
       catch (Exception e)
       {
           e.printStackTrace();
       }
    }

    private void sortByPrice_low_high()
    {
        if(!catList.isEmpty())
        {
            ratingSortList = catList;
            try {
                arr = new double[catList.size()];
                for (int i = 0; i < catList.size(); i++) {

                    arr[i]=Double.parseDouble(catList.get(i).getNumOfStars());
                }
                Collections.sort(ratingSortList,Product.priceComparator_asc);
                adapter= new CategoryProductAdapter(this,ratingSortList);
                recyclerView.setAdapter(adapter);
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        }


    }

    private void sortByPrice_high_low() {
        if (!catList.isEmpty()) {
            ratingSortList = catList;
            try {
                arr = new double[catList.size()];
                for (int i = 0; i < catList.size(); i++) {

                    arr[i] = Double.parseDouble(catList.get(i).getNumOfStars());
                }
                Collections.sort(ratingSortList, Product.priceComparator_desc);
//                for (int i = 0; i < ratingSortList.size(); i++) {
//                    Log.d("newlist", ratingSortList.get(i).getNumOfStars());
//                }
                adapter = new CategoryProductAdapter(this, ratingSortList);
                recyclerView.setAdapter(adapter);


            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void sortByRating_low_high()
    {
        if(!catList.isEmpty())
        {
            ratingSortList = catList;
            try {
                arr = new double[catList.size()];
                StringBuilder stringBuilder = new StringBuilder();
                for (int i = 0; i < catList.size(); i++) {

                    arr[i]=Double.parseDouble(catList.get(i).getNumOfStars());
                }
                Collections.sort(ratingSortList,Product.ratingComparator_asc);
                for (int i =0; i<ratingSortList.size();i++)
                {
                    Log.d("newlist", ratingSortList.get(i).getNumOfStars());
                }
                adapter= new CategoryProductAdapter(this,ratingSortList);
                recyclerView.setAdapter(adapter);


            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        }


    }

    private void sortByRating_high_low() {
        if (!catList.isEmpty()) {
            ratingSortList = catList;
            try {
                arr = new double[catList.size()];
                for (int i = 0; i < catList.size(); i++) {

                    arr[i] = Double.parseDouble(catList.get(i).getNumOfStars());
                }
                Collections.sort(ratingSortList, Product.ratingComparator_desc);
                for (int i = 0; i < ratingSortList.size(); i++) {
                    Log.d("newlist", ratingSortList.get(i).getNumOfStars());
                }
                adapter = new CategoryProductAdapter(this, ratingSortList);
                recyclerView.setAdapter(adapter);


            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private boolean checkPrice()
    {
        min = Integer.parseInt(et_min.getText().toString());
        max = Integer.parseInt(et_max.getText().toString());
        if(min>max)
        {
            et_min.setError("Enter valid minimum price");
            return false;
        }
        else
        {
            return true;
        }
    }

    private void processFilterResult(JSONObject response)
    {
        try
        {
            JSONArray jsonArray = response.getJSONArray("prod");
            catList.clear();
            if(jsonArray.length()>0)
            {
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject js = jsonArray.getJSONObject(i);
                    JSONArray on=js.getJSONArray("Images");
                    ArrayList<String> imgArray = new ArrayList<>();
                    for(int j =0; j<on.length();j++) {
                        imgArray.add(on.getString(j));
                    }
                    Product p = new Product(js.getString("_id"),js.getString("Title"),js.getString("Description"),js.getString("Quantity"),js.getString("Price"),js.getString("BrandName"),js.getString("CategoryName"),js.getString("SubCategoryName"),imgArray,js.getString("AverageRating"));
                    catList.add(p);
                }
               // drawerLayout.closeDrawer(Gravity.END);
                //filter_layout.setGravity(Gravity.END);
        }

            if(catList.isEmpty())
            {
                recyclerView.setVisibility(View.GONE);
                empty_layout.setVisibility(View.VISIBLE);
                progressBar.setVisibility(View.GONE);
            }
            else {
                adapter = new CategoryProductAdapter(this, catList);
                progressBar.setVisibility(View.GONE);
                recyclerView.setVisibility(View.VISIBLE);
                recyclerView.setAdapter(adapter);
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    public class GridSpacingItemDecoration extends RecyclerView.ItemDecoration {

        @Override
        public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state)
        {
            outRect.left=5;
            outRect.right=5;
            outRect.top=5;
            outRect.bottom=5;

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

}
interface CallBack{

    void processData(JSONArray s);
}