package com.fyp.hassan.almari.User_Management;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.fyp.hassan.almari.home_tabs.HomeActivity;
import com.fyp.hassan.almari.R;
import com.google.gson.JsonObject;

import org.json.JSONObject;


public class ForgetPassword extends AppCompatActivity {
    Toolbar myToolbar;
    Intent in;
    private EditText et_email, et_code,et_newPass;
    private LinearLayout email_layout, code_layout, password_layout;
    private Button bt_email, bt_code, bt_pass;
    private RequestQueue requestQueue;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forget_password);
        myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        requestQueue= Volley.newRequestQueue(this);

        email_layout=(LinearLayout)findViewById(R.id.layout_forgetEmail);
        code_layout=(LinearLayout)findViewById(R.id.layout_forgetCode);
        password_layout=(LinearLayout)findViewById(R.id.layout_forgetChangePass);

        et_email =(EditText)findViewById(R.id.et_forgetEmail);
        et_code =(EditText)findViewById(R.id.et_forgetEmailCode);
        et_newPass =(EditText)findViewById(R.id.et_forgetNewPass);

        bt_email=(Button)findViewById(R.id.Bt_forgetEmail);
        bt_code=(Button)findViewById(R.id.Bt_Forgetcode);
        bt_pass=(Button)findViewById(R.id.Bt_ChangePassword);

        setSupportActionBar(myToolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        bt_email.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(et_email.getText().length()>1)
                {

                    String url= ForgetPassword.this.getResources().getString(R.string.ApiAddress) + "api/user/forget";
                    JSONObject jsonObject = new JSONObject();
                    try
                    {
                        jsonObject.put("email",et_email.getText().toString());
                    }
                    catch (Exception e)
                    {
                        e.printStackTrace();
                    }
                    JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, jsonObject, new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            code_layout.setVisibility(View.VISIBLE);
                            email_layout.setVisibility(View.GONE);
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
        });

        bt_code.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(et_code.getText().length()>1)
                {
                    String url= ForgetPassword.this.getResources().getString(R.string.ApiAddress) + "api/user/verify_code";
                    JSONObject jsonObject = new JSONObject();
                    try
                    {
                        jsonObject.put("email",et_email.getText().toString());
                        jsonObject.put("resetCode",et_code.getText().toString());
                    }
                    catch (Exception e)
                    {
                        e.printStackTrace();
                    }
                    JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, jsonObject, new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {

                            Log.d("forResponse", response.toString());
                            code_layout.setVisibility(View.GONE);
                            password_layout.setVisibility(View.VISIBLE);

                        }
                    }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            error.printStackTrace();
                            Toast.makeText(ForgetPassword.this,"",Toast.LENGTH_SHORT).show();
                        }
                    });
                    requestQueue.add(jsonObjectRequest);
                }
            }
        });

        bt_pass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(et_newPass.getText().length()>=6)
                {
                    String url= ForgetPassword.this.getResources().getString(R.string.ApiAddress) + "api/user/reset_password";
                    JSONObject jsonObject = new JSONObject();
                    try
                    {
                        jsonObject.put("email",et_email.getText().toString());
                        jsonObject.put("password",et_newPass.getText().toString());
                    }
                    catch (Exception e)
                    {
                        e.printStackTrace();
                    }
                    JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, jsonObject, new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            Log.d("forResponse", response.toString());
                            Toast.makeText(ForgetPassword.this,"Password Changed",Toast.LENGTH_SHORT).show();
                            in = new Intent(ForgetPassword.this,HomeActivity.class);
                            startActivity(in);
                            finish();
                        }
                    }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            error.printStackTrace();
                            Toast.makeText(ForgetPassword.this,"Wrong code",Toast.LENGTH_SHORT).show();
                        }
                    });
                    requestQueue.add(jsonObjectRequest);
                }

            }
        });


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_my_account,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

            case android.R.id.home:
                in = new Intent(this,HomeActivity.class);
                startActivity(in);
                return true;
            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                return super.onOptionsItemSelected(item);

        }
    }
}
