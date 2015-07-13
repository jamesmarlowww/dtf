package com.dtfapp;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.content.SharedPreferences;
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
import com.facebook.GraphRequestAsyncTask;
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
    private ArrayHolder myRelationshipArrays;
    private ArrayHolder myFriendsRelationShipArray;

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





        //follow this method to log into parse then get fb frends
        getUserId();


        //makes the backround transparent
        View v = (View) findViewById(R.id.background);
        v.getBackground().setAlpha(20);


        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                // runs after 3 seconds
                fragmentManager.beginTransaction().remove(loadingScreen).commit();
            }
        }, 3000);

        final String PREFS_NAME = "MyPrefsFile";

        SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);

        if (settings.getBoolean("my_first_time", true)) {
            //the app is being launched for first time, do something
            Log.d("Comments", "First time");

            // first time task

            HowToScreen hts = new HowToScreen();
            fragmentTransaction.replace(android.R.id.content, hts);
            fragmentTransaction.commit();

            // record the fact that the app has been started at least once
            settings.edit().putBoolean("my_first_time", false).commit();
        }
    }

    public void addRelationship(String id, boolean liked, boolean loved) {
        ParseObject relationship = new ParseObject("relationship");
        relationship.put("id_of_friend", id);
        relationship.put("liked", liked);
        relationship.put("loved", loved);
        relationship.saveInBackground();
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
                                        final String name = jsonArray.getJSONObject(i).getString("name");
                                        final String id = jsonArray.getJSONObject(i).getString("id");

                                        friendsInfo.add(new FriendInfo(name, id,
                                                myRelationshipArrays.liked.contains(id),
                                                myRelationshipArrays.loved.contains(id),
                                                myFriendsRelationShipArray.liked.contains(id),
                                                myFriendsRelationShipArray.loved.contains(id)));


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

    //either gets the liked or loved friends based off boolean
    private void getMyRelationship() {
        final ArrayHolder multiArray = new ArrayHolder(new ArrayList(), new ArrayList());
        ParseQuery<ParseObject> query = ParseQuery.getQuery("relationship");
        //gets all rows where id = myId
        query.whereEqualTo("my_id", myId);
        query.findInBackground(new FindCallback<ParseObject>() {

            @Override
            public void done(List<ParseObject> objects, com.parse.ParseException e) {
                if (e == null) {
                    for (ParseObject j : objects) {
                        boolean liked = j.getBoolean("liked");
                        boolean loved = j.getBoolean("loved");
                        String str = j.getString("id_of_friend");

                        if (liked)
                            multiArray.liked.add(str);

                        if (loved)
                            multiArray.loved.add(str);
                    }


                    myRelationshipArrays = multiArray;
                    getFriendsRelationship();


                } else if (objects.size() > 1) {
                    makeToast("repeated friend in db", Toast.LENGTH_LONG);
                } else {
                    makeToast(e.toString(), Toast.LENGTH_LONG);
                }

            }
        });
    }


    private void getFriendsRelationship() {

        final ArrayHolder multiArray = new ArrayHolder(new ArrayList(), new ArrayList());

        ParseQuery<ParseObject> query = ParseQuery.getQuery("relationship");
        //gets all rows where id_of_friend = myId
        query.whereEqualTo("id_of_friend", myId);
        query.findInBackground(new FindCallback<ParseObject>() {

            @Override
            public void done(List<ParseObject> objects, com.parse.ParseException e) {
                if (e == null) {
                    for (ParseObject j : objects) {
                        boolean liked = j.getBoolean("liked");
                        boolean loved = j.getBoolean("loved");
                        String str = j.getString("my_id");

                        if (liked)
                            multiArray.liked.add(str);

                        if (loved)
                            multiArray.loved.add(str);

                    }

                    myFriendsRelationShipArray = multiArray;
                    findFriends();

                } else if (objects.size() > 1) {
                    makeToast("repeated friend in db", Toast.LENGTH_LONG);
                } else {
                    makeToast(e.toString(), Toast.LENGTH_LONG);
                }

            }
        });
    }

    private void getFriendsList(String user) {
        ParseQuery<ParseObject> query = ParseQuery.getQuery("friendship");
        query.whereEqualTo("user1", user);

        query.findInBackground(new FindCallback<ParseObject>() {

            @Override
            public void done(List<ParseObject> objects, com.parse.ParseException e) {
                if (e == null) {
                    for (ParseObject j : objects) {
//                        friendsList.add(j.getString("user2"));
                    }

                } else {
//                    debugPopUp("e.getMessage(): ", e.getMessage().toString());
                }

            }
        });
    }

    public void logInParse(String uid) throws ParseException {
        ParseUser user = new ParseUser();
        user.setUsername(uid);
        user.setPassword(uid);
        user.setEmail("nah ");

        parseUser = user;
        user.logInInBackground(uid, uid, new LogInCallback() {
            public void done(ParseUser user, ParseException e) {
                if (e == null && user != null) {

                    //finds the facebook friends
//                    findFriends();

                    getMyRelationship();

                } else {
//                    signUpToParse(user);
//                    findFriends();
                    getMyRelationship();
                }

            }
        });
    }


    private void signUpToParse(ParseUser user) {
//        user.signUpInBackground(new SignUpCallback() {
//            @Override
//            public void done(com.parse.ParseException e) {
//                if (e == null) {
//                    // Hooray! Let them use the app now.
//
//                    //need to log in here
//                    findFriends();
//
//                } else {
//                    // Sign up didn't succeed. Look at the ParseException
//                    // to figure out what went wrong
//
//                }
//            }
//
//        });
    }


    private void getUserId() {
        GraphRequestAsyncTask request = GraphRequest.newMeRequest(AccessToken.getCurrentAccessToken(), new GraphRequest.GraphJSONObjectCallback() {
            @Override
            public void onCompleted(JSONObject user, GraphResponse response) {
                if (user != null) {
                    try {
                        myId = user.getString("id");

                        logInParse(myId);


                    } catch (JSONException e) {
                        e.printStackTrace();
                    } catch (ParseException e) {
                        makeToast(e.toString(), Toast.LENGTH_LONG);
                        e.printStackTrace();
                    }
                }
            }
        }).executeAsync();
    }

    public void displayFriends() {

        listViewFriends = (ListView) findViewById(R.id.listFriends);
        FriendListArrayAdapter friendListAdapter = new FriendListArrayAdapter(this, R.layout.row, friendsInfo, myId);
        listViewFriends.setAdapter(friendListAdapter);

        listViewFriends.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parentAdapter, View view, int position,
                                    long id) {
                //code for onClick item.

            }

        });
    }

    private class ArrayHolder {
        public ArrayList liked;
        public ArrayList loved;

        public ArrayHolder(ArrayList liked, ArrayList loved) {
            this.liked = liked;
            this.loved = loved;
        }
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

    public void restartActivity() {
        Intent i = getIntent();
        finish();
        startActivity(i);
    }

    public void setYouLove(boolean youLove) {
        this.youLove = youLove;
    }
}
