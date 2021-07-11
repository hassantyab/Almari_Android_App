package com.fyp.hassan.almari.User_Management;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.fyp.hassan.almari.home_tabs.HomeActivity;
import com.fyp.hassan.almari.LoginClasses.UserSessionManager;
import com.fyp.hassan.almari.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class SignUp extends AppCompatActivity implements CallBack, View.OnClickListener {
    private Toolbar myToolbar;
    private Intent in;
    private EditText Et_fname, Et_lname, Et_email, Et_pass, Et_address, Et_num;
    private String fname, lname, email, pass, address;
    private Button signUp;
    private ProgressDialog pd;
    private UserSessionManager session;
    private RequestQueue requestQueue;
    private String url, number;
    private Spinner numSpin;
    private ArrayList<String> numlist;
    private RadioGroup rg;
    private RadioButton genderOption;
    private ImageView backbtn;
    String gender;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestQueue = Volley.newRequestQueue(this);
        setContentView(R.layout.activity_sign_up);
        myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        pd= new ProgressDialog(this);
        pd.setMessage("Please wait");

        //Data
        numlist = new ArrayList<>();
        numlist.add("92");
        rg = (RadioGroup) findViewById(R.id.Signup_rg_gender);
        numSpin = (Spinner) findViewById(R.id.SignUp_Spinner_number);
        Et_num = (EditText) findViewById(R.id.SignUP_Et_number);
        Et_fname = (EditText) findViewById(R.id.Et_FirstName);
        Et_lname = (EditText) findViewById(R.id.Et_LastName);
        Et_email = (EditText) findViewById(R.id.Et_Email);
        Et_pass = (EditText) findViewById(R.id.Et_Password);
        Et_address = (EditText) findViewById(R.id.Et_address);
        signUp = (Button) findViewById(R.id.Bt_SignUP);
        backbtn =(ImageView) findViewById(R.id.signUp_backbtn);
        session = new UserSessionManager(getApplicationContext());
        numSpin.setAdapter(new ArrayAdapter<>(this, R.layout.support_simple_spinner_dropdown_item, numlist));

        backbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        signUp.setOnClickListener(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_my_account, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

            case android.R.id.home:
                in = new Intent(this, HomeActivity.class);
                startActivity(in);
                return true;
            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                return super.onOptionsItemSelected(item);

        }
    }

    private void backgroundtask(String email, String pass, String fname, String lname, String address, String number) {
        JSONObject js = new JSONObject();
        int selected = rg.getCheckedRadioButtonId();
        genderOption = (RadioButton) findViewById(selected);
        gender = genderOption.getText().toString();

        try {
            js.put("firstName", fname);
            js.put("lastName", lname);
            js.put("email", email);
            js.put("password", pass);
            js.put("address", address);
            js.put("phoneNumber", number);
            js.put("gender",gender);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Log.d("signObject",js.toString());
        url = getApplicationContext().getString(R.string.ApiAddress) + "api/users";
        JsonObjectRequest jsonArrayRequest = new JsonObjectRequest(Request.Method.POST, url, js, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    processData(response);
                } catch (Exception e) {
                    e.printStackTrace();
                    pd.dismiss();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
                pd.dismiss();
            }
        });
        requestQueue.add(jsonArrayRequest);
    }


    private void makeSession(String userid, String token) {
            if(!userid.isEmpty() && !token.isEmpty()) {
                session.createUserLoginSession(userid, token);
                Intent i = new Intent(this, HomeActivity.class);
                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(i);
                finish();
            }
    }


    @Override
    public void processData(JSONObject data) {
        try {
        if (!data.toString().isEmpty()) {
                pd.dismiss();
                makeSession(data.getString("userId"),data.getString("token"));
        }
        else {
            pd.dismiss();
            Toast.makeText(this,data.toString(),Toast.LENGTH_SHORT).show();
        }
        } catch (Exception e) {
            e.printStackTrace();
            pd.dismiss();
        }
    }


    @Override
    public void onClick(View v) {
        if (v.getId() == signUp.getId()) {
            fname = Et_fname.getText().toString();
            lname = Et_lname.getText().toString();
            email = Et_email.getText().toString();
            pass = Et_pass.getText().toString();
            address = Et_address.getText().toString();
            numSpin.setSelection(0);
            number = numSpin.getSelectedItem().toString() + Et_num.getText().toString();
            Log.d("datanow" ,fname +"-"+ lname +"-"+ email +"-"+ pass +"-"+ address +"-"+ number);
            if (validateFields(email, pass, fname, lname, address, number)) {
                pd.show();
                backgroundtask(email, pass, fname, lname, address, number);
            }
        }

    }

    private boolean validateFields(String email, String pass, String fname, String lname, String Address, String number) {
        if (email.matches("^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,6}$") &&
                pass.length()>=6 &&
                fname.matches("([a-zA-Z]{3,10})+") &&
                lname.matches("([a-zA-Z]{3,10})+") &&
                number.matches("\\d{12}") &&
                Address.length()>1
                )
        {
            return true;
        }

        if (!email.matches("^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,6}$"))
            Et_email.setError("Please enter a valid email");

        if (pass.length()<6)
            Et_pass.setError("Please enter a valid password of length more than 6");

        if (!fname.matches("([a-zA-Z]{3,10})+"))
            Et_fname.setError("Please enter a valid first name");

        if (!lname.matches("([a-zA-Z]{3,10})+"))
            Et_lname.setError("Please enter a valid last name");

         if (Address.length()<=1)
           Et_address.setError("Please enter a valid address");

        if (!number.matches("\\d{12}"))
            Et_num.setError("Please enter valid number");

        return false;
    }
}
interface CallBack{

    void processData(JSONObject s);
}



