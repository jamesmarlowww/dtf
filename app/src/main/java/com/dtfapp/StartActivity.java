package com.dtfapp;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;

import org.json.JSONException;
import org.json.JSONObject;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

// try remove java security and using 2nd one


public class StartActivity extends Activity {

    private Button fbbutton;

    // Creating Facebook CallbackManager Value
    public static CallbackManager callbackmanager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
       // if(checkLogin()) {
//            Intent i = new Intent(this, ListOfFriends.class);
//            startActivity(i);

       // } else {

            super.onCreate(savedInstanceState);

            // Initialize SDK before setContentView(Layout ID)
            FacebookSdk.sdkInitialize(getApplicationContext());

            setContentView(R.layout.activity_main);

            // Initialize layout button
            fbbutton = (Button) findViewById(R.id.login_button);

            fbbutton.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    // Call private method
                    onFblogin();
                }
            });
   //     }
        if(checkLogin()) Toast.makeText(getApplicationContext(), "yup logged int", Toast.LENGTH_SHORT).show();

    }

    // Private method to handle Facebook login and callback
    private void onFblogin()
    {
        callbackmanager = CallbackManager.Factory.create();

        // Set permissions
        LoginManager.getInstance().logInWithReadPermissions(this, Arrays.asList("email", "user_photos", "public_profile"));

        LoginManager.getInstance().registerCallback(callbackmanager,
                new FacebookCallback<LoginResult>() {
                    @Override
                    public void onSuccess(LoginResult loginResult) {

                        System.out.println("Success");
                        GraphRequest.newMeRequest(
                                loginResult.getAccessToken(), new GraphRequest.GraphJSONObjectCallback() {
                                    @Override
                                    public void onCompleted(JSONObject json, GraphResponse response) {
                                        if (response.getError() != null) {
                                            // handle error
                                            System.out.println("ERROR");
                                        } else {
                                            System.out.println("Success");
                                            try {

                                                String jsonresult = String.valueOf(json);
                                                System.out.println("JSON Result" + jsonresult);

                                                String str_email = json.getString("email");
                                                String str_id = json.getString("id");
                                                String str_firstname = json.getString("first_name");
                                                String str_lastname = json.getString("last_name");

                                            } catch (JSONException e) {
                                                e.printStackTrace();
                                            }
                                        }
                                    }

                                }).executeAsync();

                    }

                    @Override
                    public void onCancel() {
                        Log.d("on Cancel", "On cancel");
                    }

                    @Override
                    public void onError(FacebookException error) {
                        Log.d("on error ", error.toString());
                    }
                });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        callbackmanager.onActivityResult(requestCode, resultCode, data);
    }


    public boolean checkLogin() {
        boolean loggedIn = false;
        if (AccessToken.getCurrentAccessToken() != null)
            loggedIn = true;

        return loggedIn;
    }


    public void checkLogin(View v) {

        boolean loggedIn = false;
        if (AccessToken.getCurrentAccessToken() != null)
            loggedIn = true;

        if (loggedIn) {
            Toast.makeText(getApplicationContext(), "logged in nigger", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(getApplicationContext(), "ah rats", Toast.LENGTH_SHORT).show();
        }
    }

    public void getHashKey() {
        try {
            PackageInfo info = getPackageManager().getPackageInfo(getPackageName(), PackageManager.GET_SIGNATURES);

            for (Signature signature : info.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                Log.d("KeyHash:", Base64.encodeToString(md.digest(), Base64.DEFAULT));
            }

        } catch (PackageManager.NameNotFoundException e) {
            Log.e("name not found", e.toString());
        } catch (NoSuchAlgorithmException e) {
            Log.e("no such an algorithm", e.toString());
        }
    }
}
