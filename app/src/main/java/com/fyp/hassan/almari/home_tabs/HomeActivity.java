package com.fyp.hassan.almari.home_tabs;


import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ExpandableListView;
import android.widget.Toast;

import com.android.volley.TimeoutError;
import com.fyp.hassan.almari.User_Management.recentView_Activty;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.fyp.hassan.almari.Category_package.CategoryActivity;
import com.fyp.hassan.almari.Customer_Order.MyOrdersListClass;
import com.fyp.hassan.almari.LoginClasses.Sign_In_Activity;
import com.fyp.hassan.almari.LoginClasses.UserSessionManager;
import com.fyp.hassan.almari.User_Management.MyAccount;
import com.fyp.hassan.almari.R;
import com.fyp.hassan.almari.Search_Activities.Search_Activity;
import com.facebook.AccessToken;
import com.facebook.login.LoginManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class HomeActivity extends AppCompatActivity implements Home_tab.OnFragmentInteractionListener,CartPage.OnFragmentInteractionListener,Favourite.OnFragmentInteractionListener, ExpandableListView.OnChildClickListener,CallBack{


    private RecyclerView recyclerView;
    private Toolbar myToolbar;
    private TabLayout tb;
    CustomViewPager vp;
    HomePage_Tab_Adapter hta;
    private Intent in;
    private UserSessionManager session;
    ActionBar actionBar;
    private static final int REQ_CODE_LOGIN=200;
    private DrawerLayout drawer;
    private ActionBarDrawerToggle actionBarDrawerToggle;
    private ExpandableListView listView;
    private XpandableListAdapter listAdapter;
    private ArrayList<String> listDataHeader= new ArrayList<>();
    private HashMap<String,List<String>>listHash = new HashMap<>();
    private String url;
    private RequestQueue requestQueue;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
    try {
        requestQueue = Volley.newRequestQueue(this);
        //Toolbar
        session = new UserSessionManager(getApplicationContext());
        myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        tb = (TabLayout) findViewById(R.id.tablayout);

        tb.addTab(tb.newTab().setText("Home").setIcon(R.drawable.home_tab_logo));
        tb.addTab(tb.newTab().setText("Saved").setIcon(R.drawable.fav_tab_logo));
        tb.addTab(tb.newTab().setText("Cart").setIcon(R.drawable.cart_logo));
        tb.setTabGravity(TabLayout.GRAVITY_FILL);
        tb.setTabTextColors(getResources().getColor(R.color.normal), getResources().getColor(R.color.selected));
        actionBar = getSupportActionBar();

        vp = (CustomViewPager) findViewById(R.id.HomeViewPager);
        hta = new HomePage_Tab_Adapter(getSupportFragmentManager(), tb.getTabCount());
        vp.setAdapter(hta);
        vp.setOffscreenPageLimit(-2);

        in = getIntent();

        if (in.hasExtra("tab")) {
            int tabNum = in.getIntExtra("tab", 0);
            switch (tabNum) {
                case 1:
                     if(session.isUserLoggedIn()) {
                         tb.getTabAt(1).select();
                         vp.setCurrentItem(1);
                     }
                     else
                     {
                         tb.getTabAt(0).select();
                         vp.setCurrentItem(0);
                         Toast.makeText(this,"Login to see saved items",Toast.LENGTH_SHORT).show();
                     }
                    break;

                case 2:
                    tb.getTabAt(2).select();
                    vp.setCurrentItem(2);
                    break;
            }

        }


        //Drawer

        drawer = (DrawerLayout) findViewById(R.id.drawer);
        actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawer, myToolbar, R.string.drawer_open, R.string.drawer_close);
        drawer.setDrawerListener(actionBarDrawerToggle);

        listView = (ExpandableListView) findViewById(R.id.lvExp);


        vp.setOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tb));
        tb.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {

                try {
                    vp.getAdapter().notifyDataSetChanged();
                    switch (tab.getPosition()) {
                        case 0:
                            tb.getTabAt(0).select();
                            vp.setCurrentItem(0);

                            break;

                        case 1:
                            if (session.isUserLoggedIn()) {
                                vp.setCurrentItem(1);
                                break;
                            } else {
                                Intent in = new Intent(getApplicationContext(), Sign_In_Activity.class);
                                in.putExtra("fav", "class");
                                startActivityForResult(in, REQ_CODE_LOGIN);

                                break;
                            }
                        case 2: {
                            tb.getTabAt(2).select();
                            vp.setCurrentItem(2);
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
        setSupportActionBar(myToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setLogo(getDrawable(R.drawable.almari));
        backgroundTask();
    }
    catch (Exception e)
    {
        e.printStackTrace();
    }

    }



    void backgroundTask()
    {
        url="http://smart-ecom.herokuapp.com/api/category/";
        JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET, url, (JSONArray) null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray jsonArray)
            {
                try {
                    processData(jsonArray);

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
                    backgroundTask();
                }
            }
        });

        requestQueue.add(request);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==REQ_CODE_LOGIN&& resultCode==RESULT_OK)
        {
            tb.getTabAt(1).select();
            vp.setCurrentItem(1);
        }
        else
        {
            tb.getTabAt(0).select();
            vp.setCurrentItem(0);
        }

    }

    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        actionBarDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        actionBarDrawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main,menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        invalidateOptionsMenu();
        if(session.isUserLoggedIn()) {
            menu.findItem(R.id.SignIn).setVisible(false);
            menu.findItem(R.id.Logout).setVisible(true);
        }
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        try {

            switch (item.getItemId()) {
                case R.id.SignIn:
                    in = new Intent(this, Sign_In_Activity.class);
                    startActivity(in);
                    return true;
                case R.id.Logout:
                    session.logoutUser();
                    logoutApp();
                    vp.setCurrentItem(0);
                    tb.getTabAt(0).select();
                    Toast.makeText(this, "User is logged out", Toast.LENGTH_SHORT).show();
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
                case R.id.Myaccount:
                    in = new Intent(this, MyAccount.class);
                    startActivity(in);
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

                default:
                    return actionBarDrawerToggle.onOptionsItemSelected(item) || super.onOptionsItemSelected(item);

            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return false;
    }


    private void initData(HashMap<String,ArrayList<String>> catList)
    {
        for (Map.Entry<String, ArrayList<String>> entry : catList.entrySet()) {
            listDataHeader.add(entry.getKey());
            listHash.put(entry.getKey(),entry.getValue());
        }
        listAdapter = new XpandableListAdapter(this,listDataHeader,listHash);
        listView.setAdapter(listAdapter);
        listView.setOnChildClickListener(this);

    }
    @Override
    public void onFragmentInteraction(Uri uri) {
    }
    @Override
    public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {

        {
           try {
               Intent in = new Intent(this, CategoryActivity.class);
               in.putExtra("Category", listDataHeader.get(groupPosition));
               in.putExtra("subCategory", listHash.get(listDataHeader.get(groupPosition)).get(childPosition));
               startActivity(in);
               tb.getTabAt(0).select();
               vp.setCurrentItem(0);
               drawer.closeDrawer(Gravity.START);
           }catch(Exception e)
           {
               e.printStackTrace();
           }
        }

        return true;
    }

    @Override
    public void processData(JSONArray jsonArray) {

        if (jsonArray!=null) {

            HashMap<String,ArrayList<String>> catNameList= new HashMap<>();
            ArrayList<String> sublist= new ArrayList<>();

            try {
                for (int i = 0; i < jsonArray.length(); i++) {

                    JSONObject js = jsonArray.getJSONObject(i);
                    JSONArray on=js.getJSONArray("subCategory");
                    for (int j=0;j< on.length();j++)
                    {
                        String as = on.getString(j);
                        sublist.add(as);
                    }
                    catNameList.put(js.getString("categoryName"),sublist);
                    sublist = new ArrayList<>();
                }
                initData(catNameList);
            }

            catch (JSONException e) {
                e.printStackTrace();
            }

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

interface CallBack{

    void processData(JSONArray s);
}



