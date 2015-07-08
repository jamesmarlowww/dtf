package com.dtfapp;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.GraphRequest;
import com.facebook.GraphRequestBatch;
import com.facebook.GraphResponse;
import com.parse.FindCallback;
import com.parse.LogInCallback;
import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SignUpCallback;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by James on 5/23/2015.
 */
public class ListFriends extends FragmentActivity {
    private int count;
    public ParseUser parseUser;
    private String myId;
    private ListView listViewFriends;
    private ArrayList<FriendInfo> friendsInfo = new ArrayList<FriendInfo>();
    private boolean hasFriends;
    private boolean youLike;
    private boolean youLove;
    FragmentManager fragmentManager;
    LoadingScreen loadingScreen;

    //rosid id 100000479237442

    /**
     * need to link to for image http://icons8.com
     *
     * @param savedInstanceState
     */


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.list_friends_frag);

        fragmentManager = getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        Configuration configInfo = getResources().getConfiguration();

        loadingScreen = new LoadingScreen();
        fragmentTransaction.replace(android.R.id.content, loadingScreen);
        fragmentTransaction.commit();

        Parse.initialize(this, "LD9q9E8DBtXQGetITICyXpW9yVVR3ZSEscEvKDfW", "CCTYV5PIHxQ7HG3avmXBl8fu62XKrBWRdEC8xvAg");


        //follow this method to log into parse then get fb frends
        getUserId();



        View v = (View) findViewById(R.id.background);
        v.getBackground().setAlpha(20);


        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                // runs after 3 seconds
                fragmentManager.beginTransaction().remove(loadingScreen).commit();

            }
        }, 3000);

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
                                        final String s = jsonArray.getJSONObject(i).getString("name");
                                        final String id = jsonArray.getJSONObject(i).getString("id");

//                                       // set up our query for the Book object
                                        ParseQuery likedQuery = ParseQuery.getQuery("liked");
                                        ParseQuery lovedQuery = ParseQuery.getQuery("loved");

                                        setYouLike(false);
                                        setYouLove(false);
                                        likedQuery.findInBackground(new FindCallback<ParseObject>() {
                                            public void done(List<ParseObject> list, ParseException e) {
                                                if(list.contains(id))
                                                    setYouLike(true);
                                            }
                                        });
                                        lovedQuery.findInBackground(new FindCallback<ParseObject>() {
                                            public void done(List<ParseObject> list, ParseException e) {
                                                if(list.contains(id))
                                                    setYouLike(true);
                                            }
                                        });

                                        friendsInfo.add(new FriendInfo(s, id, isYouLike(), isYouLove(), false, false));



                                    } catch (Exception e) {
                                        e.printStackTrace();
                                        Log.e("error", e.toString());
                                    }
                                }
                                displayFriends();

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

    public void logInParse(String uid) throws ParseException {
        ParseUser user = new ParseUser();
        user.setUsername(uid);
        user.setPassword(uid);
//        user.setEmail("email@example.com");

        parseUser = user;
        user.logInInBackground(uid, uid, new LogInCallback() {
            public void done(ParseUser user, ParseException e) {
                if (e == null && user != null) {
                    makeToast("Login in successfull", Toast.LENGTH_LONG);

                    //finds the facebook friends
                    user.add("liked", "100000479237442");

                    findFriends();





                } else {

                    siginUpToParse(user);
                }
            }
        });


    }

    private void siginUpToParse(ParseUser user) {
        user.signUpInBackground(new SignUpCallback() {
            @Override
            public void done(com.parse.ParseException e) {
                if (e == null) {
                    // Hooray! Let them use the app now.

                } else {
                    // Sign up didn't succeed. Look at the ParseException
                    // to figure out what went wrong
                    makeToast("Something went wrong. Try checking your internet", Toast.LENGTH_LONG);

                }
            }

        });
    }


    private void getUserId() {
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
//                            String s = object.getString("age_range");

                            //follow this method
                            logInParse(id);

                        } catch (JSONException e) {
                            e.printStackTrace();
                            makeToast("something went wrong", Toast.LENGTH_LONG);

                        } catch (ParseException e) {
                            e.printStackTrace();
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


    public void setMyId(String myId) {
        this.myId = myId;
    }

    public void makeToast(String s, int len) {
        Toast.makeText(getApplicationContext(), s, len).show();
    }

    public void setHasFriends(boolean hasFriends) {
        this.hasFriends = hasFriends;
    }

    public boolean isHasFriends() {
        return hasFriends;
    }

    public void setYouLike(boolean youLike) {
        this.youLike = youLike;
    }

    public boolean isYouLike() {
        return youLike;
    }

    public boolean isYouLove() {
        return youLove;
    }

    public void setYouLove(boolean youLove) {
        this.youLove = youLove;
    }
}
