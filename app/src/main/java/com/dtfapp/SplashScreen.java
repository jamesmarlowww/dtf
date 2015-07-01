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

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

// try remove java security and using 2nd one


public class SplashScreen extends Activity {

    private Button fbbutton;

    // Creating Facebook CallbackManager Value
    public static CallbackManager callbackmanager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        FacebookSdk.sdkInitialize(getApplicationContext());

        setContentView(R.layout.activity_main);

        if (checkLogin()) {
            Intent i = new Intent(SplashScreen.this, ListOfFriends.class);
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


        TextView tv = (TextView) findViewById(R.id.penis);
        rotateText(tv);
//
//        final Activity activity = this;
//        final View content = activity.findViewById(android.R.id.content).getRootView();
//        if (content.getWidth() > 0) {
//            Bitmap image = BlurBuilder.blur(content);
//            getWindow().setBackgroundDrawable(new BitmapDrawable(activity.getResources(), image));
//        } else {
//            content.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
//                @Override
//                public void onGlobalLayout() {
//                    Bitmap image = BlurBuilder.blur(content);
//                    getWindow().setBackgroundDrawable(new BitmapDrawable(activity.getResources(), image));
//                }
//            });
//        }

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

        callbackmanager = CallbackManager.Factory.create();

       // Set permissions
        LoginManager.getInstance().logInWithReadPermissions(this, Arrays.asList("email", "user_friends", "public_profile"));

        LoginManager.getInstance().registerCallback(callbackmanager,
                new FacebookCallback<LoginResult>() {
                    @Override
                    public void onSuccess(LoginResult loginResult) {

                        Intent i = new Intent(SplashScreen.this, ListOfFriends.class);
//                        finish();
                        startActivity(i);
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
        AccessToken t = AccessToken.getCurrentAccessToken();
        if (AccessToken.getCurrentAccessToken() != null)
            loggedIn = true;

        return AccessToken.getCurrentAccessToken()!= null;
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
