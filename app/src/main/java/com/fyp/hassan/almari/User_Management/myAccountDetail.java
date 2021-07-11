package com.fyp.hassan.almari.User_Management;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.fyp.hassan.almari.home_tabs.HomeActivity;
import com.fyp.hassan.almari.LoginClasses.UserSessionManager;
import com.fyp.hassan.almari.R;
import com.fyp.hassan.almari.Search_Activities.Search_Activity;
import com.github.ybq.android.spinkit.style.Wave;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class myAccountDetail extends AppCompatActivity implements View.OnClickListener {

    private Toolbar myToolbar;
    private EditText Et_fname,Et_lname,Et_email,Et_address,Et_num,Et_currentPass,Et_newPass;
    private String fname,lname,email,pass,address,number,url,gender;
    private Button savebtn,changePassbtn;
    private ProgressDialog pd;
    private UserSessionManager session;
    private RequestQueue requestQueue;
    private Spinner numSpin;
    private ArrayList<String> numslist;
    private RadioGroup rg;
    private RadioButton option;
    private ImageView backbtn;
    private ArrayAdapter<String> Spinneradapter;
    private ProgressBar progressBar;
    private ScrollView scrollView;
    private LinearLayout password_Layout;
    private Wave wave;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_detail);
        myToolbar=(Toolbar)findViewById(R.id.Account_toolbar);
        progressBar=(ProgressBar)findViewById(R.id.Account_progressBar);
        scrollView=(ScrollView)findViewById(R.id.Account_layout);
        //Element
        numSpin=(Spinner)findViewById(R.id.Account_Spinner_number);
        password_Layout =(LinearLayout)findViewById(R.id.passwordLayout);
        Et_num=(EditText)findViewById(R.id.Account_Et_number);
        Et_fname=(EditText)findViewById(R.id.Account_FirstName);
        Et_lname=(EditText)findViewById(R.id.Account_lastName);
        Et_email=(EditText)findViewById(R.id.Account_Email);
        Et_address=(EditText)findViewById(R.id.Account_address);
        Et_currentPass=(EditText)findViewById(R.id.CurrentPass);
        Et_newPass=(EditText)findViewById(R.id.NewPass);
        backbtn=(ImageView) findViewById(R.id.Account_backbtn);
        rg=(RadioGroup)findViewById(R.id.Account_RadioGroup_Gender);
        savebtn=(Button)findViewById(R.id.Account_savebtn);
        changePassbtn=(Button)findViewById(R.id.ChangePassbtn);

        wave=new Wave();
        progressBar.setIndeterminateDrawable(wave);
        backbtn.setOnClickListener(this);
        savebtn.setOnClickListener(this);
        changePassbtn.setOnClickListener(this);

        //setup

        requestQueue= Volley.newRequestQueue(this);
        session = new UserSessionManager(getApplicationContext());numslist= new ArrayList<>();
        numslist.add("92");
        Spinneradapter = new ArrayAdapter<>(this,R.layout.support_simple_spinner_dropdown_item,numslist);
        numSpin.setAdapter(Spinneradapter);
        numSpin.setSelection(Spinneradapter.getPosition("92"));


        setUpelement();
        setSupportActionBar(myToolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        invalidateOptionsMenu();
        menu.findItem(R.id.Myaccount).setVisible(false);
        menu.findItem(R.id.cart_menu).setVisible(true);
        menu.findItem(R.id.menu_Home).setVisible(true);
        if(session.isUserLoggedIn()) {
            menu.findItem(R.id.SignIn).setVisible(false);
            menu.findItem(R.id.Logout).setVisible(true);
        }
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.SearchOption:
                Intent in = new Intent(this, Search_Activity.class);
                startActivity(in);
                return true;
            case R.id.cart_menu:

                return true;

            case R.id.menu_Home:
                in = new Intent(this, HomeActivity.class);
                startActivity(in);
                finish();


            case R.id.Logout:
                return true;

            case R.id.RecentViews:
                return true;
            case R.id.MyOrders:
                return true;
            default:

                return super.onOptionsItemSelected(item);

        }
    }




    @Override
    public void onClick(View v)
    {
        if (savebtn.getId()==v.getId())
        {
            updateDetails();

        }
        if (changePassbtn.getId()==v.getId())
        {

            if(Et_currentPass.getText().toString().length()>=6 &&Et_newPass.getText().toString().length()>=6)
            {
                updatePassword();
            }
            else
            {
                if (Et_currentPass.getText().length()<6)
                    Et_currentPass.setError("Please enter a valid current passord");

                if (Et_newPass.getText().length()<6)
                    Et_newPass.setError("Please enter a valid password of length more than 6");

            }

        }
        if(backbtn.getId()==v.getId())
        {
            finish();
        }

    }

    private void updateDetails()
    {
        try {


            int selected = rg.getCheckedRadioButtonId();
            option = (RadioButton) findViewById(selected);
            gender = option.getText().toString();
            String num = numSpin.getSelectedItem().toString() + Et_num.getText().toString();


            if (validateFields(Et_email.getText().toString(), Et_fname.getText().toString(), Et_lname.getText().toString(), Et_address.getText().toString(), num))
            {
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("_id",session.getUserId());
                jsonObject.put("firstName", Et_fname.getText().toString());
                jsonObject.put("lastName", Et_lname.getText().toString());
                jsonObject.put("email", Et_email.getText().toString());
                jsonObject.put("address", Et_address.getText().toString());
                jsonObject.put("phoneNumber", num);
                jsonObject.put("gender",gender);


                String url = this.getString(R.string.ApiAddress) + "api/user/update";
                JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, jsonObject, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        showToast("Your profile has been updated",getApplicationContext());
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        error.printStackTrace();
                        if(error instanceof TimeoutError)
                        {
                            updateDetails();
                        }
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

    private void setUpelement() {
        try {
            if (session.isUserLoggedIn()) {
            String userid=session.getUserId();
            url = this.getString(R.string.ApiAddress) + "api/user/"+userid;

                JsonObjectRequest   jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response)
                    {
                        Log.i("userDetails",response.toString());
                        processData(response);
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        error.printStackTrace();
                        if(error instanceof TimeoutError)
                        {
                            setUpelement();
                        }
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

    private void updatePassword()
    {
        try
        {
           String url=this.getString(R.string.ApiAddress) + "api/user/updatePass";
           JSONObject js = new JSONObject();
           js.put("_id",session.getUserId());
           js.put("oldPassword",Et_currentPass.getText().toString());
           js.put("newPassword",Et_newPass.getText().toString());
           JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, js, new Response.Listener<JSONObject>() {
               @Override
               public void onResponse(JSONObject response)
               {

                   UpdatePassRespone(response);
               }
           }, new Response.ErrorListener() {
               @Override
               public void onErrorResponse(VolleyError error) {
                    error.printStackTrace();
                   if(error instanceof TimeoutError)
                   {
                       updatePassword();
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

    private void  UpdatePassRespone(JSONObject data)
    {
        try {
            if(data.getString("message").equalsIgnoreCase("Password Updated"))
            {
                showToast("Password Updated",this);
            }
        }
        catch (JSONException e)
        {
            e.printStackTrace();
        }
    }

    private void processData(JSONObject data)
    {
        if (data!=null)
        {
            try
            {
                String num="";
                if(data.has("phoneNumber"))
                {
                     num= data.getString("phoneNumber").substring(2,12);
                }

                String gender=data.getString("gender");
                numSpin.setSelection(Spinneradapter.getPosition("92"));
                if(gender.equalsIgnoreCase("Female"))
                {
                    rg.check(R.id.Account_female);
                }
                else
                {
                    rg.check(R.id.Account_male);
                }
                Et_fname.setText(data.getString("firstName"));
                Et_lname.setText(data.getString("lastName"));
                if(data.has("address")) {
                    Et_address.setText(data.getString("address"));
                }
                else {
                    Et_address.setText("");
                }
                if(data.has("facebookId"))
                {
                    password_Layout.setVisibility(View.GONE);
                }
                Et_email.setText(data.getString("email"));
                Et_num.setText(num);
                scrollView.setVisibility(View.VISIBLE);
                progressBar.setVisibility(View.GONE);
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }

        }
    }

    private boolean validateFields(String email, String fname , String lname,String Address,String number)
    {
        //pass.matches("^(?=.*[a-zA-Z])(?=.*\\d*)([a-zA-Z]|[a-zA-Z\\d]){6,10}$")
        if(email.matches("^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,6}$") &&
                fname.matches("([a-zA-Z]{3,10})+") &&
                lname.matches("([a-zA-Z]{3,10})+") &&
                 number.matches("\\d{12}")
                && Address.length()>1
                )


        {
            return true;
        }

        if (!email.matches("^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,6}$"))
            Et_email.setError("Please enter a valid email");

      //  if (!pass.matches("^(?=.*[a-zA-Z])(?=.*\\d*)([a-zA-Z]|[a-zA-Z\\d]){6,10}$"))
        //    Et_pass.setError("Please enter a valid password");

        if (!fname.matches("([a-zA-Z]{3,10})+"))
            Et_fname.setError("Please enter a valid first name between 3 to 10 letters");

        if (!lname.matches("([a-zA-Z]{3,10})+"))
            Et_lname.setError("Please enter a valid last name between 3 to 10 letters");

        if (Address.length()<=1)
           Et_address.setError("Please enter a valid address");

        if(!number.matches("\\d{12}"))
            Et_num.setError("Please enter valid number");

        return false;
    }
    private void showToast( String msg, Context context) {
        try {
            LayoutInflater inflater = getLayoutInflater();
            View layout = inflater.inflate(R.layout.custom_toast_layout,
                    (ViewGroup) findViewById(R.id.Toast_Layout));
            TextView text = (TextView) layout.findViewById(R.id.toastTextView);
            text.setText(msg);
            Toast toast = new Toast(context);
            int y = myToolbar.getHeight();
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
}
