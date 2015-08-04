package io.downto.app;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.facebook.login.LoginManager;


public class Settings extends Activity {

    private FragmentManager fragmentManager;
    private HowTo howTo;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_fragment);
    }

    public void fabBack(View v) {
        finish();
    }

    public void howTo(View v) {
        fragmentManager = getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        howTo = new HowTo();
        fragmentTransaction.replace(android.R.id.content, howTo);
        fragmentTransaction.commit();

    }

    public void goAway(View v) {
        fragmentManager.beginTransaction().remove(howTo).commit();
    }

    public void logOut(View v) {

        LoginManager.getInstance().logOut();

        SharedPreferences prefs = getSharedPreferences("io.downto.app", MODE_PRIVATE);
        prefs.edit().putString("uid", null).commit();

        Intent intent = new Intent(getApplicationContext(), StartScreen.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);



//        Intent i = new Intent(getApplicationContext(), ListFriends.class);
//        startActivity(i);





    }


    public void changeText(View v) {
        AlertDialog.Builder alert = new AlertDialog.Builder(this);

        final EditText edittext = new EditText(getApplicationContext());
        edittext.setTextColor(Color.BLACK);
        alert.setTitle("Loading text");
        alert.setMessage("\n");

        alert.setView(edittext);

        alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                //What ever you want to do with the value
                String value = edittext.getText().toString();

                SharedPreferences prefs = getSharedPreferences("io.downto.app", MODE_PRIVATE);
                if (value.length() == 0) {
                    Toast.makeText(getApplicationContext(), "Text feild is empty", Toast.LENGTH_LONG).show();

                } else {
                    prefs.edit().putString("loadingText", value).commit();
                    finish();
                    Intent i = new Intent(getApplicationContext(), ListFriends.class);
                    i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(i);
                }


            }
        });

        alert.show();
    }

}
