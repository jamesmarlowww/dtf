package com.dtfapp;

import android.app.Activity;
import android.content.pm.PackageInstaller;
import android.os.Bundle;
import android.service.textservice.SpellCheckerService;
import android.util.Log;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.FacebookActivity;
import com.facebook.GraphRequest;
import com.facebook.GraphRequestBatch;
import com.facebook.GraphResponse;
import com.facebook.HttpMethod;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by James on 5/23/2015.
 */
public class ListOfFriends extends Activity{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.list_friends);
        findFriends();

    }

    private void findFriends() {

        System.out.println("in here");
        GraphRequestBatch batch = new GraphRequestBatch(
                GraphRequest.newMyFriendsRequest(
                        AccessToken.getCurrentAccessToken(),
                        new GraphRequest.GraphJSONArrayCallback() {
                            @Override
                            public void onCompleted(
                                    JSONArray jsonArray,
                                    GraphResponse response) {
                                // Application code for users friends
                                System.out.println("getFriendsData onCompleted : jsonArray " + jsonArray);
                                System.out.println("getFriendsData onCompleted : response " + response);
                                try {
                                    JSONObject jsonObject = response.getJSONObject();
                                    System.out.println("getFriendsData onCompleted : jsonObject " + jsonObject);
                                    JSONObject summary = jsonObject.getJSONObject("summary");
                                    System.out.println("getFriendsData onCompleted : summary total_count - " + summary.getString("total_count"));
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        })

        );
        batch.addCallback(new GraphRequestBatch.Callback() {
            @Override
            public void onBatchCompleted(GraphRequestBatch graphRequests) {
                // Application code for when the batch finishes
                int x = graphRequests.size();
                Toast.makeText(getApplicationContext(), "Num of friends: "+x, Toast.LENGTH_LONG);


                
            }
        });
        batch.executeAsync();

        Bundle parameters = new Bundle();
        parameters.putString("fields", "id,name,link,picture");


    }


}
