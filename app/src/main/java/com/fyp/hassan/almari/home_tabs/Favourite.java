package com.fyp.hassan.almari.home_tabs;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkError;
import com.android.volley.NoConnectionError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.fyp.hassan.almari.LoginClasses.UserSessionManager;
import com.fyp.hassan.almari.R;
import com.fyp.hassan.almari.SingleProductAvtivity.Product;
import com.github.ybq.android.spinkit.style.Wave;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;


public class Favourite extends Fragment {
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private RequestQueue requestQueue;
    private String url;
    private View v;
    private ProgressBar progressBar;
    private LayoutInflater inflater;
    private Wave wave;
    private TabLayout tb;
    ViewGroup viewGroup;
    private UserSessionManager session;
    Favorite_Product_Adapter adapter;
    List<Product> productList;
    RecyclerView recyclerView;


    //xml element


    private OnFragmentInteractionListener mListener;

    public Favourite() {

    }

    public static Favourite newInstance(String param1, String param2) {
        Favourite fragment = new Favourite();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        session=new UserSessionManager(getContext());
        this.inflater=inflater;
        this.viewGroup=container;
        v = inflater.inflate(R.layout.fragment_favourite, container, false);
        wave = new Wave();
        progressBar=(ProgressBar)v.findViewById(R.id.fav_main_progressBar);
        progressBar.setIndeterminateDrawable(wave);
        recyclerView = (RecyclerView) v.findViewById(R.id.Fav_recycler);
        recyclerView.setHasFixedSize(true);
        recyclerView.getItemAnimator().setAddDuration(1000);
        recyclerView.getItemAnimator().setRemoveDuration(1000);
        tb=(TabLayout)getActivity().findViewById(R.id.tablayout);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        requestQueue = Volley.newRequestQueue(getContext());
        productList = new ArrayList<>();
        checkLogin(inflater,container);
        return v;
    }

   private void checkLogin(LayoutInflater inflater,ViewGroup container)
    {

        if(session.isUserLoggedIn() && isNetworkAvailable()) {
            productList.clear();
            getFavProduct();
        }
        else {
            if(!session.isUserLoggedIn() &&isNetworkAvailable())
            {
                progressBar.setVisibility(View.GONE);
                v=inflater.inflate(R.layout.fragment_empty_favorite, container, false);
            }
            else {
                progressBar.setVisibility(View.GONE);
                setViewLayout(R.layout.no_internet_connection);
            }
        }
    }

    private void getFavProduct()
    {
        try {

            if (session.isUserLoggedIn()) {
                url = this.getString(R.string.ApiAddress) + "api/user/favoriteProduct/" + session.getUserId();
                JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, url, null, new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        processData(response);

                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        error.printStackTrace();
                        if(error instanceof NoConnectionError) {
                            progressBar.setVisibility(View.GONE);
                            setViewLayout(R.layout.no_internet_connection);
                        }
                        else if(error instanceof TimeoutError)
                        {
                           getFavProduct();
                        }
                        else
                        {
                            progressBar.setVisibility(View.GONE);
                            setViewLayout(R.layout.internet_timeout);
                        }
                    }
                });
                requestQueue.add(jsonArrayRequest);
            }
        }
        catch(Exception e)
            {
                e.printStackTrace();
            }

    }


    private void processData(JSONArray data)
    {

            try {
                if (data != null) {
                    ArrayList<String> imgArray;
                    JSONObject js;
                    for (int i = 0; i < data.length(); i++) {
                        imgArray = new ArrayList<>();
                        js = data.getJSONObject(i);
                        JSONArray on = js.getJSONArray("Images");
                        for (int j = 0; j < on.length(); j++) {
                            imgArray.add(on.getString(j));
                        }
                        Product p = new Product(js.getString("_id"), js.getString("Title"), js.getString("Description"), js.getString("Quantity"), js.getString("Price"), js.getString("BrandName"), js.getString("CategoryName"), js.getString("SubCategoryName"), imgArray,js.getString("AverageRating"));
                        productList.add(p);
                    }
                    if (!productList.isEmpty()) {
                        progressBar.setVisibility(View.GONE);
                        adapter = new Favorite_Product_Adapter(getContext(), productList, Favourite.this);
                        recyclerView.setAdapter(adapter);
                        adapter.notifyDataSetChanged();
                    } else {
                        setViewLayout(R.layout.fragment_empty_favorite);
                        progressBar.setVisibility(View.GONE);
                    }
                }
                else
                {
                    setViewLayout(R.layout.fragment_empty_favorite);
                    progressBar.setVisibility(View.GONE);
                }
            }
            catch (Exception e)
            {
                e.printStackTrace();
                progressBar.setVisibility(View.GONE);
                setViewLayout(R.layout.fragment_empty_favorite);
            }
        }



    private void setViewLayout(int id){

        try {
            LayoutInflater inflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            v = inflater.inflate(id, viewGroup,false);
            ViewGroup rootView = (ViewGroup) getView();
            rootView.removeAllViews();
            rootView.addView(v);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }
    public void onButtonPressed(Uri uri) {
    }
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }
    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }
    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        try {
            if (isVisibleToUser) {

                tb.getTabAt(1).select();
                getFragmentManager().beginTransaction().detach(this).attach(this).commit();
            }
        } catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    private boolean isNetworkAvailable() {
       try {
           ConnectivityManager connectivityManager
                   = (ConnectivityManager) getContext().getSystemService(Context.CONNECTIVITY_SERVICE);
           if(connectivityManager!=null) {
               NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
               return activeNetworkInfo != null && activeNetworkInfo.isConnected() && activeNetworkInfo.isConnectedOrConnecting();
           }
       }
       catch (Exception e )
       {
           e.printStackTrace();
       }

       return false;
    }

    public interface OnFragmentInteractionListener {

        void onFragmentInteraction(Uri uri);
    }
}


