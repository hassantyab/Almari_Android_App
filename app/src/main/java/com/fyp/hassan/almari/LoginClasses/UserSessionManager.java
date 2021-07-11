package com.fyp.hassan.almari.LoginClasses;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

import com.fyp.hassan.almari.User_Management.SignUp;


import java.util.HashMap;

public class UserSessionManager {
	
	// Shared Preferences reference
	private SharedPreferences pref;
	
	// Editor reference for Shared preferences
	private Editor editor;
	
	// Context
	private Context _context;
	private int PRIVATE_MODE = 0;
	private static final String PREFER_NAME = "AndroidExamplePref";
	
	// All Shared Preferences Keys
	private static final String IS_USER_LOGIN = "IsUserLoggedIn";
	private static final String KEY_token = "name";
	private static final String KEY_EMAIL = "email";
	private static final String KEY_TokenValue = "token";
	private static final String KEY_ADDRESS="Address";

	// Constructor
	public UserSessionManager(Context context){
		this._context = context;
		pref = _context.getSharedPreferences(PREFER_NAME, PRIVATE_MODE);
		editor = pref.edit();
	}
	
	//Create login session
	public void createUserLoginSession( String email, String token_value){
		editor.putBoolean(IS_USER_LOGIN, true);
		editor.putString(KEY_EMAIL, email);
		editor.putString(KEY_TokenValue,token_value);
		editor.commit();
	}	

	public boolean checkLogin(){
		// Check login status
		if(!this.isUserLoggedIn()){
			
			// user is not logged in redirect him to Login Activity
			Intent i = new Intent(_context, Sign_In_Activity.class);
			
			// Closing all the Activities from stack
			i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			
			// Add new Flag to start new Activity
			i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			
			// Staring Login Activity
			_context.startActivity(i);
			
			return true;
		}
		return false;
	}

	public HashMap<String, String> getUserDetails(){
		

		HashMap<String, String> user = new HashMap<String, String>();
		user.put(KEY_EMAIL, pref.getString(KEY_EMAIL, null));
		return user;
	}

	public String getUserId()
	{
		return pref.getString(KEY_EMAIL,null);
	}

	public String getUserToken()
	{
		return pref.getString(KEY_TokenValue,null);
	}

	public void logoutUser(){

		editor.clear();
		editor.commit();

		// After logout redirect user to Login Activity
		Intent i = new Intent(_context, SignUp.class);
		// Closing all the Activities
		i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		// Add new Flag to start new Activity
		i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		


	}
	
	public  void setAddress(String address)
	{
		editor.putBoolean("UserAddress",true);
		editor.putString(KEY_ADDRESS,address);
		editor.commit();
	}

	public String getAddress()
	{
		return pref.getString(KEY_ADDRESS,"No address");
	}

	public boolean isAddressStore()
	{
		return pref.getBoolean("UserAddress",false);
	}

	public boolean isUserLoggedIn(){
		return pref.getBoolean(IS_USER_LOGIN, false);
	}
}
