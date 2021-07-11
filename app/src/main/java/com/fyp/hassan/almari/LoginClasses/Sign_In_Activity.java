package com.fyp.hassan.almari.LoginClasses;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.fyp.hassan.almari.Customer_Order.cartData;
import com.fyp.hassan.almari.SingleProductAvtivity.Product;
import com.fyp.hassan.almari.User_Management.ForgetPassword;
import com.fyp.hassan.almari.home_tabs.HomeActivity;
import com.fyp.hassan.almari.R;
import com.fyp.hassan.almari.User_Management.SignUp;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.Profile;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.Scopes;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Scope;
import com.google.android.gms.plus.Plus;
import com.google.android.gms.plus.model.people.Person;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Sign_In_Activity extends AppCompatActivity implements View.OnClickListener, GoogleApiClient.OnConnectionFailedListener {
    private Toolbar myToolbar;
    private TextView tv_createAccount, tv_forgetPassword, Uemail, Upass;
    private Intent in;
    private String email, password;
    private UserSessionManager session;
    private Button btn_login;
    private RequestQueue requestQueue;
    private String url;
    private SignInButton google_Sign_in;
    private GoogleApiClient googleApiClient;
    private static final int REQ_CODE=9001;
    private Person person;
    private ImageView backbtn;
    private LoginButton fbloginBtn;
    private CallbackManager callbackManager;
    private ArrayList<cartData> productData ;
    private SQLiteDatabase db;
    private Intent i;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(this);
        setContentView(R.layout.fragment_sign__in);

        //toolbar
        myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);


        //Elements
        backbtn=(ImageView)findViewById(R.id.Login_Backbtn);
        fbloginBtn =(LoginButton)findViewById(R.id.Facebooklogin_button);
        fbloginBtn.setReadPermissions(Arrays.asList("public_profile","email"));
        google_Sign_in=(SignInButton)findViewById(R.id.Google_signin);
        callbackManager=CallbackManager.Factory.create();
        requestQueue = Volley.newRequestQueue(this);
        session = new UserSessionManager(getApplicationContext());
        tv_createAccount = (TextView) findViewById(R.id.tv_createAccount);
        tv_forgetPassword = (TextView) findViewById(R.id.tv_forgetPassword);
        Uemail = (TextView) findViewById(R.id.Et_Uemail);
        Upass = (TextView) findViewById(R.id.ET_Upassword);
        btn_login = (Button) findViewById(R.id.btn_login);
        productData = new ArrayList<>();

        GoogleSignInOptions googleSignInOptions = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestEmail().requestScopes(new Scope(Scopes.PLUS_LOGIN)).build();
        googleApiClient=new GoogleApiClient.Builder(this).enableAutoManage(this,this).addApi(Auth.GOOGLE_SIGN_IN_API,googleSignInOptions).addApi(Plus.API).build();

        fbloginBtn.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {

                GraphRequest request = GraphRequest.newMeRequest(
                        loginResult.getAccessToken(),
                        new GraphRequest.GraphJSONObjectCallback() {
                            @Override
                            public void onCompleted(JSONObject object, GraphResponse response) {

                                Log.i("LoginActivity", object.toString());
                                try {

                                    facebookLogin(object);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }});
                Bundle parameters = new Bundle();
                parameters.putString("fields", "id,email,first_name,last_name");
                request.setParameters(parameters);
                request.executeAsync();
        }

            @Override
            public void onCancel() {
                Toast.makeText(getApplicationContext(),"Login cancel",Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onError(FacebookException error) {
                Toast.makeText(getApplicationContext(),"Error occurred",Toast.LENGTH_SHORT).show();
            }
        });
        tv_createAccount.setOnClickListener(this);
        tv_forgetPassword.setOnClickListener(this);
        btn_login.setOnClickListener(this);
        backbtn.setOnClickListener(this);
        google_Sign_in.setOnClickListener(this);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

    }

    private void backgroundtask(String email, String password) {
        JSONObject js = new JSONObject();
        try {
            js.put("email", email);
            js.put("password", password);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        url = this.getString(R.string.ApiAddress) + "api/login";
        JsonObjectRequest jsonArrayRequest = new JsonObjectRequest(Request.Method.POST, url, js, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
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
                Toast.makeText(getApplicationContext(), "Username/Password is incorrect", Toast.LENGTH_LONG).show();
            }
        });
        requestQueue.add(jsonArrayRequest);
    }

    private void facebookLogin(JSONObject jsonObject)
    {
        try {
            String url = this.getResources().getString(R.string.ApiAddress) + "api/users/fbauth";
            Log.i("myobject", jsonObject.toString());
            jsonObject.put("gender", "Male");
            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, jsonObject, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    Log.i("fab", response.toString());
                    processData(response);

                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    error.printStackTrace();
                    Toast.makeText(getApplicationContext(), "Facebook error", Toast.LENGTH_LONG).show();
                }
            });
            requestQueue.add(jsonObjectRequest);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main2, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.SettingS1:
                Intent in = new Intent(this, HomeActivity.class);
                startActivity(in);

                return true;

            case R.id.SettingS2:
                // User chose the "Favorite" action, mark the current item
                // as a favorite...
                return true;

            case android.R.id.home:
                in = new Intent(this, HomeActivity.class);
                startActivity(in);

            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                return super.onOptionsItemSelected(item);

        }
    }


    @Override
    public void onClick(View v) {
        if (v.getId() == tv_createAccount.getId()) {
            in = new Intent(this, SignUp.class);
            startActivity(in);
        }

        if (v.getId() == tv_forgetPassword.getId()) {
            in = new Intent(this, ForgetPassword.class);
            startActivity(in);

        }

        if (v.getId() == btn_login.getId()) {
            email = Uemail.getText().toString();
            password = Upass.getText().toString();


            // Validate if username, password is filled
            if (validateFields(email, password)) {
                backgroundtask(email, password);
            } else {

                // user didn't entered username or password
                Toast.makeText(getApplicationContext(), "Please enter valid information", Toast.LENGTH_LONG).show();
            }


        }

       if(v.getId()==backbtn.getId())
       {
           finish();
       }
    }

    private void Googlesignin()
    {
        Intent intt = Auth.GoogleSignInApi.getSignInIntent(googleApiClient);
        startActivityForResult(intt,REQ_CODE);

    }

    private void result(GoogleSignInResult result)
    {
        if(result.isSuccess()) {

            try {
                GoogleSignInAccount account = result.getSignInAccount();
                String name = account.getDisplayName();
                String email = account.getEmail();
                String token = account.getIdToken();
                String userid = account.getId();
                String n = account.getGivenName();
                String n2 = account.getFamilyName();
                // int i = person.getGender();
                Toast.makeText(this, "name =" + name + "email= " + email + "\n user id=" + userid + "\n given name= " + n + " family name= " + n2, Toast.LENGTH_SHORT).show();
            }catch (Exception e)
            {
                e.printStackTrace();
            }
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode==REQ_CODE)
        {
              GoogleSignInResult googleSignInResult= Auth.GoogleSignInApi.getSignInResultFromIntent(data);
               person = Plus.PeopleApi.getCurrentPerson(googleApiClient);
               result(googleSignInResult);

        }
        callbackManager.onActivityResult(requestCode,resultCode,data);
    }


    private void GoogleSignOut()
    {
        Auth.GoogleSignInApi.signOut(googleApiClient);
    }

    private boolean validateFields(String email, String pass) {
        if (email.matches("^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,6}$") &&
                pass.length()>=6) {
            return true;
        }

        if (!email.matches("^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,6}$"))
            Uemail.setError("Please enter a valid email");

        if (pass.length()<6)
            Upass.setError("Please enter a valid password of lenght more than 6");

        return false;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        setResult(RESULT_CANCELED);
        finish();
    }

    private void makeSession(String userId, String token) {
        if (!token.isEmpty()&&!userId.isEmpty()) {
            i = new Intent(getApplicationContext(), HomeActivity.class);
            session.createUserLoginSession( "" + userId  , token);
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            sendLocalCartToApi();
        } else {
            // username / password doesn't match
            Toast.makeText(getApplicationContext(), "Username/Password is incorrect", Toast.LENGTH_LONG).show();

        }
    }


    void sendLocalCartToApi()
    {
        try {
            String url = this.getResources().getString(R.string.ApiAddress) + "api/cart/add";
            db = openOrCreateDatabase("CartDatabase", MODE_PRIVATE, null);
            String tableName= "CartTable";
            Cursor cursorTable = db.rawQuery("select DISTINCT tbl_name from sqlite_master where tbl_name = '"+tableName+"'", null);
            if(cursorTable!=null && cursorTable.getCount()>0)
            {
                Cursor cursor = db.rawQuery("select * from CartTable", null);
                if (cursor.getCount() != 0) {
                    cursor.moveToFirst();
                    do {

                        productData.add(new cartData(
                                cursor.getString(cursor.getColumnIndex("ProductID")),
                                cursor.getString(cursor.getColumnIndex("Quantity"))
                        ));
                    } while (cursor.moveToNext());

                    JSONArray jsonArray = convertData(productData);
                    if (jsonArray.length() != 0) {
                        JSONObject jsonObject = new JSONObject();
                        jsonObject.put("userId", session.getUserId());
                        jsonObject.put("cart", jsonArray);
                        Log.i("cartList", jsonObject.toString());
                        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, jsonObject, new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {
                                Log.i("cartResponce", response.toString());
                                String tableName= "CartTable";
                                db.execSQL("delete from "+ tableName);
                                goBack();
                            }
                        }, new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                error.printStackTrace();
                            }
                        });
                        requestQueue.add(jsonObjectRequest);
                    }
                } else {
                    goBack();
                }
            }
            else {
                goBack();
            }


        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    private JSONArray convertData(ArrayList<cartData> list)
    {
        JSONArray jsonArray = new JSONArray();
        try {

            for (int i = 0; i < list.size(); i++) {
                JSONObject jb = new JSONObject();
                jb.put("productId", list.get(i).getProductId());
                jb.put("quantity",list.get(i).getQuantity());
                jsonArray.put(jb);
            }

            return jsonArray;
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return jsonArray;
    }
    private void processData(JSONObject data) {
            try {
                 makeSession(data.getString("userId"),data.getString("token"));
            } catch (Exception e) {
                e.printStackTrace();
            }
    }

    void goBack()
    {
        Intent req = getIntent();
        if (req.hasExtra("fav")) {
            Intent in = new Intent();
            setResult(RESULT_OK, in);
        } else {
            startActivity(i);
        }
        db.close();
        finish();
    }
    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }
}

