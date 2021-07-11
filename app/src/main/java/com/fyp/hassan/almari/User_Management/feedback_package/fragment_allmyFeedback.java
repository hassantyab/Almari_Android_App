package com.fyp.hassan.almari.User_Management.feedback_package;


import android.graphics.Rect;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.fyp.hassan.almari.LoginClasses.UserSessionManager;
import com.fyp.hassan.almari.R;
import com.github.ybq.android.spinkit.style.Wave;
import com.google.gson.JsonObject;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;


public class fragment_allmyFeedback extends Fragment {

    private RecyclerView recyclerView;
    private RequestQueue requestQueue;
    private UserSessionManager session;
    private List<reviewData> reviewList;
    private review_adapter adapter;
    private ProgressBar progressBar;
    private Wave wave;
    private View no_ratings_layout;
    public fragment_allmyFeedback() {
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        View view=inflater.inflate(R.layout.fragment_all_my_feedback, container, false);
        initialiseItem(view);
        no_ratings_layout=(View)view.findViewById(R.id.no_ratings_layout);
        request_My_Feedback();
        return  view;
    }

    private void initialiseItem(View v)
    {
        recyclerView =(RecyclerView)v.findViewById(R.id.myAllFeedback_recyclerView);
        progressBar=(ProgressBar)v.findViewById(R.id.myAllFeedback_progressBar);
        wave=new Wave();
        progressBar.setIndeterminateDrawable(wave);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setHasFixedSize(true);
       // recyclerView.addItemDecoration( new decorator());
        requestQueue= Volley.newRequestQueue(getContext());
        session = new UserSessionManager(getContext());
    }

    private void request_My_Feedback()
    {
        try {
            String url = getContext().getResources().getString(R.string.ApiAddress) + "api/product/user-reviews";
            JSONObject jsonObject = new JSONObject();
            if(session.isUserLoggedIn())
            {
                jsonObject.put("userId",session.getUserId());
            }
            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url,jsonObject , new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response)
                {
                    recyclerView.setVisibility(View.VISIBLE);
                    progressBar.setVisibility(View.GONE);
                    no_ratings_layout.setVisibility(View.GONE);
                    Log.i("myFeed", response.toString());
                    processData(response);
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    error.printStackTrace();
                    progressBar.setVisibility(View.GONE);
                    if(error instanceof TimeoutError)
                    {
                        request_My_Feedback();
                    }
                }
            });
            requestQueue.add(jsonObjectRequest);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }


    private void processData(JSONObject Object)
    {
        try
        {
            JSONArray jsonArray = Object.getJSONArray("reviews");
            reviewList =new ArrayList<>();
            if(!jsonArray.toString().isEmpty())
            {
                for(int i = 0; i<jsonArray.length();i++)
                {
                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    reviewData review = new reviewData(jsonObject.getString("status"),jsonObject.getString("_id"),jsonObject.getString("reviewString"),jsonObject.getString("reviewTitle"),jsonObject.getString("numOfStars"),jsonObject.getString("userId"), jsonObject.getString("productId"));
                    reviewList.add(review);
                }
                if(!reviewList.isEmpty()) {
                    adapter = new review_adapter(getContext(), reviewList);
                    recyclerView.setAdapter(adapter);
                    adapter.notifyDataSetChanged();
                }
                else
                {
                    recyclerView.setVisibility(View.GONE);
                    progressBar.setVisibility(View.GONE);
                    no_ratings_layout.setVisibility(View.VISIBLE);
                }
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }
    private class decorator extends RecyclerView.ItemDecoration {

        @Override
        public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
            int position = parent.getChildAdapterPosition(view); // item position

        }
    }



}
