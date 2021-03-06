package io.downto.app;

import android.app.AlertDialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Point;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.Display;
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
    private Fragment howTo;

    //rosid id 100000479237442

    /**
     * need to link to for image http://icons8.com
     *
     * @param savedInstanceState
     */


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.list_friends);

        fragmentManager = getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        Configuration configInfo = getResources().getConfiguration();
        loadingScreen = new LoadingScreen();
        fragmentTransaction.replace(android.R.id.content, loadingScreen);
        fragmentTransaction.commit();


        //this block starts the loading process.
        SharedPreferences prefs = getSharedPreferences("io.downto.app", MODE_PRIVATE);
        if (prefs.getString("uid", null) == null) {
            getUserId();
        } else {
            myId = prefs.getString("uid", null);
            getPersonalAndFriendsSavedRelationship();
        }


        //makes the background transparent
        View v = (View) findViewById(R.id.background);
        v.getBackground().setAlpha(20);


        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                // runs after 3 seconds
                fragmentManager.beginTransaction().remove(loadingScreen).commit();
            }

        }, 3000);


        if(!isNetworkConnected())
            makeToast("Make sure that you are connected to the internet", Toast.LENGTH_LONG);


    }

    private void popUp() {
        new AlertDialog.Builder(this)
                .setMessage("If you want to sleep with someone press on the bed" +
                        "\n\nIf you want more press the heart")
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // continue with delete
                    }
                })
                .show();
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

                                if (jsonArray.length() >= 0) {
                                    setHasFriends(true);

                                }
                                else {
                                    setHasFriends(false);
                                    makeToast("You're setting trends mate, you don't have any facebook friends using this app. Only friends using this app will appear", Toast.LENGTH_LONG);
                                    makeToast("You're setting trends mate, you don't have any facebook friends using this app. Only friends using this app will appear", Toast.LENGTH_LONG);
                                    makeToast("You're setting trends mate, you don't have any facebook friends using this app. Only friends using this app will appear", Toast.LENGTH_LONG);
                                    makeToast("You're setting trends mate, you don't have any facebook friends using this app. Only friends using this app will appear", Toast.LENGTH_LONG);
                                    makeToast("You're setting trends mate, you don't have any facebook friends using this app. Only friends using this app will appear", Toast.LENGTH_LONG);
                                    makeToast("You're setting trends mate, you don't have any facebook friends using this app. Only friends using this app will appear", Toast.LENGTH_LONG);
                                    makeToast("You're setting trends mate, you don't have any facebook friends using this app. Only friends using this app will appear", Toast.LENGTH_LONG);
                                    makeToast("You're setting trends mate, you don't have any facebook friends using this app. Only friends using this app will appear", Toast.LENGTH_LONG);
                                    makeToast("You're setting trends mate, you don't have any facebook friends using this app. Only friends using this app will appear", Toast.LENGTH_LONG);
                                    makeToast("You're setting trends mate, you don't have any facebook friends using this app. Only friends using this app will appear", Toast.LENGTH_LONG);
                                }


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

    /**
     * This method gets the all the saved relationships
     * between logged in person and friends
     * Every person liked/loved is added to myArray then put into myRelationshipArrays
     * Every friend that has liked/loved is added to friendsArray then into myFriendsRelationshipArrays
     * <p/>
     * Parse compound query is used instead of using two separate queries
     */
    private void getPersonalAndFriendsSavedRelationship() {
        final ArrayHolder myArray = new ArrayHolder(new ArrayList(), new ArrayList());
        final ArrayHolder friendsArray = new ArrayHolder(new ArrayList(), new ArrayList());

        ParseQuery<ParseObject> my = ParseQuery.getQuery("relationship");
        my.whereEqualTo("my_id", myId);

        ParseQuery<ParseObject> friends = ParseQuery.getQuery("relationship");
        friends.whereEqualTo("id_of_friend", myId);


        List<ParseQuery<ParseObject>> queries = new ArrayList<ParseQuery<ParseObject>>();
        queries.add(my);
        queries.add(friends);

        ParseQuery<ParseObject> mainQuery = ParseQuery.or(queries);
        mainQuery.findInBackground(new FindCallback<ParseObject>() {
            public void done(List<ParseObject> objects, ParseException e) {
                if (e == null) {
                    for (ParseObject j : objects) {
                        boolean liked = j.getBoolean("liked");
                        boolean loved = j.getBoolean("loved");
                        String my_id = j.getString("my_id");
                        String id_of_friend = j.getString("id_of_friend");
                        if (j.getString("id_of_friend").equals(myId)) {
                            if (liked)
                                friendsArray.liked.add(my_id);

                            if (loved)
                                friendsArray.loved.add(my_id);
                        }

                        if (j.getString("my_id").equals(myId)) {
                            if (liked)
                                myArray.liked.add(id_of_friend);

                            if (loved)
                                myArray.loved.add(id_of_friend);
                        }
                    }


                    myFriendsRelationShipArray = friendsArray;
                    myRelationshipArrays = myArray;
                    findFriends();
                } else {
                    makeToast("Something went wrong, check you are connect to the internet", Toast.LENGTH_LONG);
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

                    getPersonalAndFriendsSavedRelationship();

                } else {
//                    signUpToParse(user);
//                    findFriends();
                    getPersonalAndFriendsSavedRelationship();
                }

            }
        });
    }


    private void getUserId() {
        GraphRequestAsyncTask request = GraphRequest.newMeRequest(AccessToken.getCurrentAccessToken(), new GraphRequest.GraphJSONObjectCallback() {
            @Override
            public void onCompleted(JSONObject user, GraphResponse response) {
                if (user != null) {
                    try {
                        myId = user.getString("id");

                        SharedPreferences prefs = getSharedPreferences("io.downto.app", MODE_PRIVATE);
                        prefs.edit().putString("uid", myId).commit();

                        getPersonalAndFriendsSavedRelationship();


                    } catch (JSONException e) {
                        e.printStackTrace();
                        makeToast(e.toString(), Toast.LENGTH_LONG);
                    }
                }
            }
        }).executeAsync();
    }


    public void displayFriends() {
        int screenWidth = 0;
        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        screenWidth = size.x;


        listViewFriends = (ListView) findViewById(R.id.listFriends);
        FriendListArrayAdapter friendListAdapter = new FriendListArrayAdapter(this, R.layout.row, friendsInfo, myId, screenWidth);
        listViewFriends.setAdapter(friendListAdapter);

        listViewFriends.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parentAdapter, View view, int position,
                                    long id) {
                //code for onClick item.

            }

        });

        if (!hasFriends) {
            makeToast("You don't have any friends using Down to. Only friends using the app will appear ", Toast.LENGTH_LONG);
            makeToast("You don't have any friends using Down to. Only friends using the app will appear ", Toast.LENGTH_LONG);
            makeToast("You don't have any friends using Down to. Only friends using the app will appear ", Toast.LENGTH_LONG);
        }

        SharedPreferences prefs = getSharedPreferences("io.downto.app", MODE_PRIVATE);

        if (prefs.getBoolean("firstrun", true)) {
            prefs.edit().putBoolean("firstrun", false).commit();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            howTo = new HowTo();
            fragmentTransaction.replace(android.R.id.content, howTo);
            fragmentTransaction.commit();
        }

    }

    private boolean isNetworkConnected() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo ni = cm.getActiveNetworkInfo();
        if (ni == null) {
            // There are no active networks.
            return false;
        } else
            return true;
    }


    public void fabPressed(View v) {
        Intent i = new Intent(this, Settings.class);
        startActivity(i);
    }


    private void signUpToParse(ParseUser user) {
        user.signUpInBackground(new SignUpCallback() {
            @Override
            public void done(com.parse.ParseException e) {
                if (e == null) {
                } else {
                }
            }
        });
    }

    public void goAway(View v) {
        fragmentManager.beginTransaction().remove(howTo).commit();
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

    public void setYouLove(boolean youLove) {
        this.youLove = youLove;
    }
}
