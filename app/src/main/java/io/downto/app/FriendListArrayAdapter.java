package io.downto.app;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.os.AsyncTask;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.parse.FindCallback;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by James on 6/30/2015.
 */
public class FriendListArrayAdapter extends ArrayAdapter<FriendInfo> {
    private int resourceID = 0; // number in list. 0,1,2,3
    private Context context;
    private String myId;
    private ArrayList<FriendInfo> friendList = new ArrayList<FriendInfo>();
    private int screenWidth;


    public FriendListArrayAdapter(Context context, int resource, ArrayList<FriendInfo> friendList, String myId, int screenWidth) {
        super(context, resource, friendList);
        this.context = context;
        resourceID = resource;
        this.myId = myId;
        this.friendList = friendList;
        this.screenWidth = screenWidth;

    }

    @Override
    public View getView(final int pos, View convertView, ViewGroup parent) {
        View rowView = convertView;
        ViewHolder viewHolder = new ViewHolder();
        int rowType = getItemViewType(pos);

        if (rowView == null) {
            LayoutInflater inflater = ((Activity) context).getLayoutInflater();
            rowView = inflater.inflate(resourceID, parent, false);

            viewHolder.friendName = (TextView) rowView.findViewById(R.id.friendsName);
            viewHolder.friendPic = (ImageView) rowView.findViewById(R.id.friendsPic);
            viewHolder.tick = (ImageView) rowView.findViewById(R.id.tick);
            viewHolder.heart = (ImageView) rowView.findViewById(R.id.heart);
            viewHolder.tick2 = (ImageView) rowView.findViewById(R.id.tick2);
            viewHolder.heart2 = (ImageView) rowView.findViewById(R.id.heart2);

            viewHolder.tick.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.d("It works", "inside tick");
                    relationshipExists(friendList.get(pos).getId(), true, false);


                }
            });

            viewHolder.heart.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.d("It works", "inside heart");
                    relationshipExists(friendList.get(pos).getId(), true, true);


                }
            });

            viewHolder.tick2.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.d("It works", "inside tick");
                    relationshipExists(friendList.get(pos).getId(), true, false);

                }
            });

            viewHolder.heart2.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.d("It works", "inside heart");
                    relationshipExists(friendList.get(pos).getId(), true, true);

//                    
                }
            });


        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        viewHolder.friendName.setText(friendList.get(pos).getFriendName());

        DisplayMetrics display = new DisplayMetrics();


        //calls the aysnc task. So the main thread doesn't have to wait for the internet download of pic
        new DownloadImage().execute(new MyTaskParams(friendList.get(pos).getId(), viewHolder.friendPic, screenWidth));

        //sets the color of the icon.
        //adds the duplicate icon if both friends like/love
        if (friendList.get(pos).isYouTick()) {
            viewHolder.tick.setColorFilter(Color.parseColor("#76c720"));
            if (friendList.get(pos).isTheyTick()) {
                viewHolder.tick2.setColorFilter(Color.parseColor("#76c720"));
                viewHolder.tick2.setVisibility(View.VISIBLE);
            }
        }
        if (friendList.get(pos).isYouHeart()) {
            viewHolder.heart.setColorFilter(Color.parseColor("#D32F2F"));
            if (friendList.get(pos).isTheyHeart()) {
                viewHolder.heart2.setColorFilter(Color.parseColor("#D32F2F"));
                viewHolder.heart2.setVisibility(View.VISIBLE);
            }
        }

        return rowView;
    }

    private class MyTaskParams {
        String id;
        ImageView imageView;
        int screenSize;

        MyTaskParams(String id, ImageView imageView, int screenSize) {
            this.id = id;
            this.imageView = imageView;
            this.screenSize = screenSize;

        }
    }

    /**
     * If the relationship exists delete the row
     * if the new relationship is exactly the same. Only delete the row (i.e if heart is hightlighted, the same press on it removes it)
     * else remove the old row and add the new relationship
     *
     * @return
     */
    private boolean relationshipExists(final String friend_id, final boolean liked, final boolean loved) {
        final boolean[] result = {false};

        ParseQuery<ParseObject> query = ParseQuery.getQuery("relationship");
        //gets all rows where id = myId
        query.whereEqualTo("my_id", myId);
        query.whereEqualTo("id_of_friend", friend_id);
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, com.parse.ParseException e) {
                if (e == null) {
                    for (ParseObject ob : objects) {
                        if (ob.getBoolean("liked") && liked && !loved && !ob.getBoolean("loved")) {
                            result[0] = true;

                        } else if (loved && ob.getBoolean("loved") && liked && ob.getBoolean("liked")) {
                            result[0] = true;
                        }
                        ob.deleteInBackground();
                    }

                    if (!result[0]) {
                        addRelationship(friend_id, liked, loved);
                    }
                } else if (objects.size() > 1) {
                } else {
                }

            }
        });

        restartActivity();

        return result[0];
    }

    private void restartActivity() {
        Intent myIntent = new Intent(getContext(), ListFriends.class);
        ((Activity) getContext()).finish();
        getContext().startActivity(myIntent);
    }


    public void addRelationship(String id_of_friend, boolean liked, boolean loved) {
        ParseObject relationship = new ParseObject("relationship");
        relationship.put("my_id", myId);
        relationship.put("id_of_friend", id_of_friend);
        relationship.put("liked", liked);
        relationship.put("loved", loved);
        relationship.saveInBackground();
    }

    private class DownloadImage extends AsyncTask<MyTaskParams, Void, Void> {
        ImageView imageView;
        Bitmap bitmap;
        int screenSize;

        @Override
        protected Void doInBackground(MyTaskParams... params) {
            MyTaskParams im = params[0];
            String id = im.id;
            imageView = im.imageView;
            screenSize = im.screenSize;
            try {
                URL imageURL = new URL("https://graph.facebook.com/" + id + "/picture?type=large");
                InputStream inputStream = (InputStream) imageURL.getContent();
                bitmap = BitmapFactory.decodeStream(inputStream);


            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            bitmap = Bitmap.createScaledBitmap(bitmap, screenSize/6, screenSize/6, false);

            bitmap = getCroppedBitmap(bitmap);
            imageView.setImageBitmap(getCroppedBitmap(bitmap));
        }

        //more rectangle images are distorted. So not ideal, but is slightly larger
        public Bitmap getRoundedBitmap(Bitmap bitmap) {
            Bitmap output = Bitmap.createBitmap(bitmap.getWidth(), bitmap
                    .getHeight(), Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(output);

            final int color = 0xff424242;
            final Paint paint = new Paint();
            final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
            final RectF rectF = new RectF(rect);

            paint.setAntiAlias(true);
            canvas.drawARGB(0, 0, 0, 0);
            paint.setColor(color);
            canvas.drawOval(rectF, paint);

            paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
            canvas.drawBitmap(bitmap, rect, rect, paint);
            return output;
        }

        //is a circle, slightly smaller
        public Bitmap getCroppedBitmap(Bitmap bitmap) {
            Bitmap output = Bitmap.createBitmap(bitmap.getWidth(),
                    bitmap.getHeight(), Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(output);

            final int color = 0xff424242;
            final Paint paint = new Paint();
            final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());

            paint.setAntiAlias(true);
            canvas.drawARGB(0, 0, 0, 0);
            paint.setColor(color);
            canvas.drawCircle(bitmap.getWidth() / 2, bitmap.getHeight() / 2,
                    bitmap.getWidth() / 2, paint);
            paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
            canvas.drawBitmap(bitmap, rect, rect, paint);
            return output;
        }
    }

    class ViewHolder {
        ImageView friendPic;
        TextView friendName;
        ImageView tick;
        ImageView heart;
        ImageView tick2;
        ImageView heart2;

    }


    @Override
    public int getCount() {
        return friendList.size();
    }

}
