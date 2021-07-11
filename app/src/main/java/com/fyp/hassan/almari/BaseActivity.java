package com.fyp.hassan.almari;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ExpandableListView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.fyp.hassan.almari.Category_package.CategoryActivity;
import com.fyp.hassan.almari.home_tabs.XpandableListAdapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class BaseActivity extends AppCompatActivity implements ExpandableListView.OnChildClickListener {
    private DrawerLayout drawer;
    private ActionBarDrawerToggle actionBarDrawerToggle;
    private ExpandableListView expandableListView;
    private XpandableListAdapter Xadapter;
    private List<String> listDataHeader= new ArrayList<>();
    private HashMap<String,List<String>> listHash = new HashMap<>();
    private RequestQueue requestQueue;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.base_activity_layout);
        expandableListView = (ExpandableListView)findViewById(R.id.base_lvExp);
        drawer = (DrawerLayout) findViewById(R.id.base_drawer);
        drawer.setEnabled(false);
        requestQueue= Volley.newRequestQueue(this);
        backgroundTask();

    }

    @Override
    public void startActivity(Intent intent) {
        super.startActivity(intent);

    }

    void backgroundTask()
    {
        String url="http://smart-ecom.herokuapp.com/api/category/";
        JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET, url,  null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray jsonArray)
            {
                try {
                        setCategories(jsonArray);

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

    public void setCategories(JSONArray jsonArray) {

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

void checkup()
{

}

        private void initData(HashMap<String,ArrayList<String>> catList)
    {
        for (Map.Entry<String, ArrayList<String>> entry : catList.entrySet()) {
            listDataHeader.add(entry.getKey());
            listHash.put(entry.getKey(),entry.getValue());
        }
        Xadapter = new XpandableListAdapter(this,listDataHeader,listHash);
        expandableListView.setAdapter(Xadapter);
        expandableListView.setOnChildClickListener(this);
        drawer.setEnabled(true);
    }

    @Override
    public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
        {
            try {
                Intent in = new Intent(this, CategoryActivity.class);
                in.putExtra("Category", listDataHeader.get(groupPosition));
                in.putExtra("subCategory", listHash.get(listDataHeader.get(groupPosition)).get(childPosition));
                startActivity(in);
                finish();

            }catch(Exception e)
            {
                e.printStackTrace();
                return false;
            }
        }
        return true;
    }
}
