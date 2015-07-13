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
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.Button;
import android.widget.TextView;
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
import com.facebook.login.widget.LoginButton;

import org.json.JSONException;
import org.json.JSONObject;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;


public class StartScreen extends Activity {

    private Button fbbutton;
    private boolean is18;


    // Creating Facebook CallbackManager Value
    public static CallbackManager callbackmanager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        FacebookSdk.sdkInitialize(getApplicationContext());

        setContentView(R.layout.start_screen);

        if (checkLogin()) {
            Intent i = new Intent(StartScreen.this, ListFriends.class);
//            finish();
            startActivity(i);
        }

        fbbutton = (LoginButton) findViewById(R.id.login_button);
        fbbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Call private method
                onFblogin();
            }
        });


//        TextView tv = (TextView) findViewById(R.id.penis);
//        rotateText(tv);


    }

    public void rotateText(TextView tv) {
        //rotate from the start of string
        RotateAnimation anim = new RotateAnimation(0f, 350f, 15f, 15f);
        //rotate from center
//        RotateAnimation anim = new RotateAnimation(0f, 350f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);

        anim.setInterpolator(new LinearInterpolator());
        anim.setRepeatCount(Animation.INFINITE);
        anim.setDuration(700);
        tv.startAnimation(anim);
    }


    // Private method to handle Facebook login and callback
    private void onFblogin() {


        // Set permissions

        List<String> permissions = new ArrayList<String>();
        permissions.add("public_profile");
        permissions.add("user_friends");


        LoginManager.getInstance().logInWithReadPermissions(this, permissions);


        callbackmanager = CallbackManager.Factory.create();

        LoginManager.getInstance().registerCallback(callbackmanager,
                new FacebookCallback<LoginResult>() {
                    @Override
                    public void onSuccess(LoginResult loginResult) {


                        Intent i = new Intent(StartScreen.this, ListFriends.class);
//                        finish();
                        startActivity(i);

//                            Toast.makeText(getApplicationContext(), "You need to be 18 or older to use this app", Toast.LENGTH_LONG).show();
//                            LoginManager.getInstance().logOut();


                    }

                    @Override
                    public void onCancel() {
                        Log.d("on Cancel", "On cancel");
                        Toast.makeText(getApplicationContext(), "hmm something went wrong. Check your internet", Toast.LENGTH_LONG).show();

                    }

                    @Override
                    public void onError(FacebookException error) {
                        Log.d("on error ", error.toString());

                        Toast.makeText(getApplicationContext(), "ERRRRROR: "+error.toString(), Toast.LENGTH_LONG).show();
                    }
                });
    }


    private boolean checkOver18() {
        GraphRequest request = GraphRequest.newMeRequest(
                AccessToken.getCurrentAccessToken(),
                new GraphRequest.GraphJSONObjectCallback() {
                    @Override
                    public void onCompleted(
                            JSONObject object,
                            GraphResponse response) {
                        try {
                            String id = object.getString("id");
                            String s = object.getString("age_range");

                            if (s.equals("{\"min\":21}") || s.equals("{\"min\":18}")) {
                                setIs18(true);
                            } else setIs18(false);

                        } catch (JSONException e) {
                            e.printStackTrace();

                        }
                    }
                });
        Bundle parameters = new Bundle();
        parameters.putString("fields", "id,name, age_range");
        request.setParameters(parameters);
        request.executeAsync();

        return is18;
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        callbackmanager.onActivityResult(requestCode, resultCode, data);
    }


    public boolean checkLogin() {
        boolean loggedIn = false;
        AccessToken t = AccessToken.getCurrentAccessToken();
        if (AccessToken.getCurrentAccessToken() != null)
            loggedIn = true;

        return AccessToken.getCurrentAccessToken() != null;
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

    public void setIs18(boolean is18) {
        this.is18 = is18;
    }
}
