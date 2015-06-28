package com.dtfapp;

import android.app.Activity;
import android.os.Bundle;
import android.text.Layout;
import android.view.View;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.GraphRequest;
import com.facebook.GraphRequestBatch;
import com.facebook.GraphResponse;
import com.facebook.internal.Utility;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by James on 5/23/2015.
 */
public class ListOfFriends extends Activity{
    private int count;
    ArrayAdapter<String> listAdapter;
    private ListView listViewFriends;
    private ArrayList<String> friends = new ArrayList<String>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.list_friends);
        findFriends();
        for (int i = 0; i < 5; i++) {
            friends.add("ian ");
        }
        displayFriends(friends);

    }

    public void addItems(View v, String s) {
        friends.add(s);
    }

    public ArrayList findFriends() {
        GraphRequestBatch batch = new GraphRequestBatch(
                GraphRequest.newMyFriendsRequest(
                        AccessToken.getCurrentAccessToken(),
                        new GraphRequest.GraphJSONArrayCallback() {
                            @Override
                            public void onCompleted(
                                    JSONArray jsonArray,
                                    GraphResponse response) {
                                // Application code for users friends

                                Toast.makeText(getApplicationContext(), "Num of friends: "+jsonArray.length(), Toast.LENGTH_LONG).show();

                                try {
                                    int id;
                                    String name;
                                    for (int i = 0; i < jsonArray.length(); i++) {
                                        JSONObject row = jsonArray.getJSONObject(i);
                                        id = row.getInt("id");
                                        name = row.getString("name");
                                        Toast.makeText(getApplicationContext(), id+""+name, Toast.LENGTH_LONG).show();
                                    }
                                } catch (JSONException e) {

                                }
                                System.out.println("getFriendsData onCompleted : jsonArray " + jsonArray);

                                Toast.makeText(getApplicationContext(), jsonArray.toString() ,Toast.LENGTH_LONG).show();

                                System.out.println("getFriendsData onCompleted : response " + response);

                                Toast.makeText(getApplicationContext(), response.toString() ,Toast.LENGTH_LONG).show();






//                                try {
//                                    JSONObject jsonObject = response.getJSONObject();
//                                    System.out.println("getFriendsData onCompleted : jsonObject " + jsonObject);
//                                    JSONObject summary = jsonObject.getJSONObject("summary");
//                                    System.out.println("getFriendsData onCompleted : summary total_count - " + summary.getString("total_count"));
//                                } catch (Exception e) {
//                                    e.printStackTrace();
//                                }
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

        Bundle parameters = new Bundle();
        parameters.putString("fields", "id,name,link,picture");



        return friends;
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
                            String name = object.getString("name");

                            Toast.makeText(getApplicationContext(),name+"\n", Toast.LENGTH_LONG).show();
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(getApplicationContext(),e.toString(), Toast.LENGTH_LONG).show();


                        }
                    }
                });
        Bundle parameters = new Bundle();
        parameters.putString("fields", "id,name,link");
        request.setParameters(parameters);
        request.executeAsync();



    }

    public void displayFriends(ArrayList a) {


        // Find the ListView resource.
        listViewFriends = (ListView) findViewById(R.id.listFriends);

        // Create ArrayAdapter using the planet list.
        listAdapter = new ArrayAdapter<String>(this, R.layout.row, a);

        // Set the ArrayAdapter as the ListView's adapter.
        listViewFriends.setAdapter(listAdapter);


        //Implement on click method ... when user clicks on a name ... present with option to remove friend.
        listViewFriends.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parentAdapter, View view, int position,
                                    long id) {
                // We know the View is a TextView so we can cast it
                TextView clickedView = (TextView) view;
//                removeFriendPopUp(clickedView.getText().toString());

            }

        });
    }


}
