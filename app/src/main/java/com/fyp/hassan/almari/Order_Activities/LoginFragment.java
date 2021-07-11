package com.fyp.hassan.almari.Order_Activities;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.fyp.hassan.almari.Customer_Order.cartData;
import com.fyp.hassan.almari.home_tabs.CustomViewPager;
import com.fyp.hassan.almari.LoginClasses.UserSessionManager;
import com.fyp.hassan.almari.R;
import com.fyp.hassan.almari.User_Management.ForgetPassword;
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
import com.google.android.gms.plus.model.people.Person;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import static android.app.Activity.RESULT_OK;
import static android.content.Context.MODE_PRIVATE;


public class LoginFragment extends Fragment implements View.OnClickListener {
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private String mParam1;
    private String mParam2;

    //social account sign in
    private LoginButton fbloginBtn;
    private CallbackManager callbackManager;
    private static final int REQ_CODE=9001;
    private Person person;
    private RequestQueue requestQueue;
    private TextView tv_createAccount, tv_forgetPassword, Uemail, Upass;
    private Intent in;
    private String email, password;
    private  UserSessionManager session;
    private Button btn_login;
    private ProgressDialog pd;
    private SQLiteDatabase db;
    private ArrayList<cartData> productData ;
    private OnFragmentInteractionListener mListener;

    public LoginFragment()
    {

    }


    public static LoginFragment newInstance(String param1, String param2) {
        LoginFragment fragment = new LoginFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(getContext());
        session = new UserSessionManager(getActivity().getApplicationContext());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View v=inflater.inflate(R.layout.fragment_login, container, false);

        requestQueue= Volley.newRequestQueue(getContext());
        productData = new ArrayList<>();
        tv_createAccount = (TextView) v.findViewById(R.id.Frag_createAccount);
        tv_forgetPassword = (TextView) v.findViewById(R.id.Frag_forgetPassword);
        Uemail = (TextView) v.findViewById(R.id.Frag_Uemail);
        Upass = (TextView) v.findViewById(R.id.Frag_Upassword);
        btn_login = (Button) v.findViewById(R.id.Frag_login);
        fbloginBtn= (LoginButton)v.findViewById(R.id.Frag_Fblogin_button);
        callbackManager=CallbackManager.Factory.create();




        fbloginBtn.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {

                GraphRequest request = GraphRequest.newMeRequest(
                        loginResult.getAccessToken(),
                        new GraphRequest.GraphJSONObjectCallback() {
                            @Override
                            public void onCompleted(JSONObject object, GraphResponse response) {


                                Toast.makeText(getContext(),"Facebook response = \n" + response.toString(),Toast.LENGTH_SHORT).show();
                                Log.v("LoginActivity", response.toString());

                                // Application code
                                try {
                                    Log.i("Response",response.toString());

                                    String email = response.getJSONObject().getString("email");
                                    String firstName = response.getJSONObject().getString("first_name");
                                    String lastName = response.getJSONObject().getString("last_name");
//                                  String gender = response.getJSONObject().getString("gender");



                                    Profile profile = Profile.getCurrentProfile();
                                    String id = profile.getId();
                                    String link = profile.getLinkUri().toString();
                                    Log.i("Link",link);
                                    if (Profile.getCurrentProfile()!=null)
                                    {
                                        Log.i("Login", "ProfilePic" + Profile.getCurrentProfile().getProfilePictureUri(200, 200));
                                    }

                                    Log.i("Login" + "Email", email);
                                    Log.i("Login"+ "FirstName", firstName);
                                    Log.i("Login" + "LastName", lastName);
                                    //Log.i("Login" + "Gender", gender);

                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }

                            }
                        });
                Bundle parameters = new Bundle();
                parameters.putString("fields", "id,email,first_name,last_name,gender");
                request.setParameters(parameters);
                request.executeAsync();
            }

            @Override
            public void onCancel() {
                Toast.makeText(getContext(),"Login cancel",Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onError(FacebookException error) {
                error.printStackTrace();
            }
        });
        tv_createAccount.setOnClickListener(this);
        tv_forgetPassword.setOnClickListener(this);
        btn_login.setOnClickListener(this);


        return v;
    }

    private void backgroundtask(String email, String password) {
        JSONObject js = new JSONObject();
        try {
            js.put("email", email);
            js.put("password", password);
        } catch (JSONException e) {
            e.printStackTrace();
        }

       String  url = this.getString(R.string.ApiAddress) + "api/login";
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
            }
        });
        requestQueue.add(jsonArrayRequest);
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
    public void onClick(View v) {
        try {
            if (v.getId() == tv_createAccount.getId()) {
                in = new Intent(getContext(), SignUp.class);
                startActivity(in);

            }

            if (v.getId() == tv_forgetPassword.getId()) {
                in = new Intent(getContext(), ForgetPassword.class);
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
                    Toast.makeText(getContext(), "Please enter valid information", Toast.LENGTH_LONG).show();
                }

            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    private boolean validateFields(String email, String pass)
    {
        if(email.matches("^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,6}$") &&
                pass.matches("^(?=.*[a-zA-Z])(?=.*\\d*)([a-zA-Z]|[a-zA-Z\\d]){6,10}$"))
        {
            return true;
        }

        if (!email.matches("^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,6}$"))
            Uemail.setError("Please enter a valid email");

        if (!pass.matches("^(?=.*[a-zA-Z])(?=.*\\d*)([a-zA-Z]|[a-zA-Z\\d]){6,10}$"))
            Upass.setError("Please enter a valid password");

        return false;
    }



    private void makeSession(String Id, String token) {

            session.createUserLoginSession( Id, token);
            sendLocalCartToApi();



    }

    private void processData(JSONObject data) {
        try {
            if (!data.getString("userId").isEmpty()) {
                    makeSession(data.getString("userId"), data.getString("token"));
            }
        }
        catch (Exception e )
        {
            e.printStackTrace();
            pd.dismiss();
        }
    }

    void sendLocalCartToApi()
    {
        try {
            String url = this.getResources().getString(R.string.ApiAddress) + "api/cart/add";
            db = getContext().openOrCreateDatabase("CartDatabase", MODE_PRIVATE, null);
            String tableName= "CartTable";
            Cursor cursorTable = db.rawQuery("select DISTINCT tbl_name from sqlite_master where tbl_name = '"+tableName+"'", null);
            if(cursorTable!=null && cursorTable.getCount()>0)
            {
                Cursor cursor = db.rawQuery("select * from CartTable", null);
                ArrayList<String> img = new ArrayList<>();
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
                        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, jsonObject, new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {
                               // Toast.makeText(getContext(), response.toString(), Toast.LENGTH_SHORT).show();
                                Log.i("cartFargResponce", response.toString());
                                String tableName= "CartTable";
                                db.execSQL("delete from "+ tableName);
                                done();
                            }
                        }, new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                error.printStackTrace();
                                done();
                            }
                        });
                        requestQueue.add(jsonObjectRequest);
                    }
                }
            }

        }
        catch (Exception e)
        {
            e.printStackTrace();
            done();
        }
    }

    private void done()
    {
        CustomViewPager vp=(CustomViewPager) getActivity().findViewById(R.id.CheckoutViewPager);
        vp.setCurrentItem(1);
        db.close();
    }

    public interface OnFragmentInteractionListener
    {

        void onFragmentInteraction(Uri uri);
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

}

