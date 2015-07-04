package com.dtfapp;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.GraphRequest;
import com.facebook.GraphRequestBatch;
import com.facebook.GraphResponse;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

/**
 * Created by James on 5/23/2015.
 */
public class ListOfFriends extends FragmentActivity {
    private int count;
    private ListView listViewFriends;
    private ArrayList<FriendInfo> friendsInfo = new ArrayList<FriendInfo>();
    private boolean hasFriends;
    CallbackManager callbackManager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.list_friends);
        findFriends();

        getUserInfo();

    }


    public boolean findFriends() {
        GraphRequestBatch batch = new GraphRequestBatch(
                GraphRequest.newMyFriendsRequest(
                        AccessToken.getCurrentAccessToken(),
                        new GraphRequest.GraphJSONArrayCallback() {
                            @Override
                            public void onCompleted(
                                    JSONArray jsonArray,
                                    GraphResponse response) {
                                // Application code for users friends

                                if (jsonArray.length() > 0) setHasFriends(true);
                                else setHasFriends(false);

                                for (int i = 0; i < jsonArray.length(); i++) {
                                    try {
                                        String s = jsonArray.getJSONObject(i).getString("name");
                                        int id = jsonArray.getJSONObject(i).getInt("id");

                                        friendsInfo.add(new FriendInfo(s, id, false, false));


                                    } catch (Exception e) {
                                        e.printStackTrace();
                                        Toast.makeText(getApplicationContext(), e.toString(), Toast.LENGTH_LONG).show();
                                        Log.e("error", e.toString());
                                    }
                                }
                                displayFriends();

                                String s = null;
                                try {
                                    s = jsonArray.getJSONObject(1).toString();
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                                Toast.makeText(getApplicationContext(), s, Toast.LENGTH_SHORT).show();

                            }
                        })

        );
        batch.addCallback(new GraphRequestBatch.Callback() {
            @Override
            public void onBatchCompleted(GraphRequestBatch graphRequests) {
                // Application code for when the batch finishes


            }
        });

        batch.executeAsync();
        String query = "SELECT uid, name, pic, pic_small, pic_big FROM user WHERE uid IN (SELECT uid2 FROM friend WHERE uid1 = me())";

        Bundle params = new Bundle();
        params.putString("method", "fql.query");
        params.putString("query", query);


        return isHasFriends();

    }

    public Bitmap getDpImage(int id) throws MalformedURLException {

        Bitmap bitmap = null;
        try {
            URL imgUrl = new URL("https://graph.facebook.com/" + Integer.toString(id) + "/picture/?type=small"); //removed
            InputStream in = (InputStream) imgUrl.getContent();
            bitmap = BitmapFactory.decodeStream(in);
        } catch (IOException e) {
            Log.e("Error", e.toString());
        }
        return bitmap;
    }




    private void getUserInfo() {

        GraphRequest request = GraphRequest.newMeRequest(
                AccessToken.getCurrentAccessToken(),
                new GraphRequest.GraphJSONObjectCallback() {
                    @Override
                    public void onCompleted(
                            JSONObject object,
                            GraphResponse response) {
                        // Application code

                        try {
                            String id = object.getString("id");
                            String s = object.getString("age_range");
                            Toast.makeText(getApplicationContext(), s , Toast.LENGTH_LONG).show();

                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(getApplicationContext(), e.toString(), Toast.LENGTH_LONG).show();


                        }
                    }
                });
        Bundle parameters = new Bundle();
        parameters.putString("fields", "id,name, age_range");
        request.setParameters(parameters);
        request.executeAsync();

    }

    public void displayFriends() {

        listViewFriends = (ListView) findViewById(R.id.listFriends);
        FriendListArrayAdapter friendListAdapter = new FriendListArrayAdapter(this, R.layout.row, friendsInfo);
        listViewFriends.setAdapter(friendListAdapter);

        listViewFriends.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parentAdapter, View view, int position,
                                    long id) {
                //code for onClick item.

            }

        });
    }


    public void showHideFrgament(final Fragment fragment) {

        FragmentTransaction ft = getFragmentManager().beginTransaction();
        ft.setCustomAnimations(android.R.animator.fade_in,
                android.R.animator.fade_out);

        if (fragment.isHidden()) {
            ft.show(fragment);
            Log.d("hidden", "Show");
        } else {
            ft.hide(fragment);
            Log.d("Shown", "Hide");
        }
        ft.commit();

    }

    public void setHasFriends(boolean hasFriends) {
        this.hasFriends = hasFriends;
    }

    public boolean isHasFriends() {
        return hasFriends;
    }


}
