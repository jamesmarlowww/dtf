package io.downto.app;

import android.app.Activity;
import android.app.AlertDialog;
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


public class Settings extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_fragment);
    }

    public void fabBack(View v) {
        finish();
    }

    public void howTo(View v) {
        finish();
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
                prefs.edit().putString("loadingText", value).commit();

                finish();
                Intent i = new Intent(getApplicationContext(), ListFriends.class);
                startActivity(i);

            }
        });


        alert.show();
    }

}
