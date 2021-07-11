package com.fyp.hassan.almari;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.CountDownTimer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.transition.Fade;
import android.transition.Transition;
import android.transition.TransitionInflater;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.fyp.hassan.almari.home_tabs.HomeActivity;
import com.github.ybq.android.spinkit.style.Wave;

import org.json.JSONArray;
import org.json.JSONObject;

public class splashScreen extends AppCompatActivity  {
    private RequestQueue requestQueue;
    private Button retryBtn;
    private ProgressBar progressBar;
    int onStartCount=0;
    private Wave wave;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);
        requestQueue = Volley.newRequestQueue(this);
        progressBar=(ProgressBar)findViewById(R.id.splash_pb);
        wave =new Wave();
        retryBtn = (Button)findViewById(R.id.splash_retry);

        progressBar.setIndeterminateDrawable(wave);
        backgroundTask();
        retryBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                retryBtn.setVisibility(View.GONE);
                progressBar.setVisibility(View.VISIBLE);
                backgroundTask();
            }
        });

    }

    void backgroundTask()
    {
        String url="http://smart-ecom.herokuapp.com/api/category/";
        JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET, url,  null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray jsonArray)
            {
                try {
                    proData(jsonArray);
                }
                catch (Exception e)
                {
                    Toast.makeText(getApplicationContext(),"Please check internet connection",Toast.LENGTH_SHORT).show();
                    progressBar.setVisibility(View.GONE);
                    retryBtn.setVisibility(View.VISIBLE);
                    e.printStackTrace();

                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
                progressBar.setVisibility(View.GONE);
                retryBtn.setVisibility(View.VISIBLE);

            }
        });
        request.setRetryPolicy(new DefaultRetryPolicy(5000,DefaultRetryPolicy.DEFAULT_MAX_RETRIES,DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        requestQueue.add(request);

    }


    void proData(JSONArray data)
    {
        try
        {
            if(!data.toString().isEmpty()) {
                /*JSONArray js =new JSONArray("[{\"votes\":0,\"createdAt\":\"2018-09-12T06:34:21.509Z\",\"updatedAt\":\"2018-09-12T06:34:21.509Z\",\"_id\":\"5b98b36d701f485d0836894d\",\"answer\":\"hello world\"},{\"votes\":0,\"createdAt\":\"2018-09-11T08:35:42.646Z\",\"updatedAt\":\"2018-09-11T08:35:42.646Z\",\"_id\":\"5b977e5e5ca7f554a087993b\",\"answer\":\"i don't know.\"}]");
                for(int i = 0 ; i<js.length();i++)
                {
                    Toast.makeText(this,js.getJSONObject(i).getString("answer"),Toast.LENGTH_SHORT).show();
                }
                Log.i("data",js.toString());
                */
                Intent in = new Intent(splashScreen.this, HomeActivity.class);
                startActivity(in);
                this.overridePendingTransition(R.anim.fade_in,R.anim.fadeout);
                finish();

            }
            else
            {
                retryBtn.setVisibility(View.VISIBLE);
            }
        }
        catch (Exception e)
        {
            Toast.makeText(this,"Please check internet connection",Toast.LENGTH_SHORT).show();
            retryBtn.setVisibility(View.VISIBLE);
        }
    }
    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = null;
        if (connectivityManager != null) {
            activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        }
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }
}
