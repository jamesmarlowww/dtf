package com.dtfapp;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.media.Image;
import android.os.AsyncTask;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.HttpMethod;
import com.facebook.Profile;
import com.facebook.login.widget.ProfilePictureView;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;

/**
 * Created by James on 6/30/2015.
 */
public class FriendListArrayAdapter extends ArrayAdapter<FriendInfo> {
    private int resourceID = 0; // number in list. 0,1,2,3
    private Context context;
    private ArrayList<FriendInfo> friendList = new ArrayList<FriendInfo>();

    public FriendListArrayAdapter(Context context, int resource, ArrayList<FriendInfo> friendList) {
        super(context, resource, friendList);
        this.context = context;
        resourceID = resource;
        this.friendList = friendList;

    }

    @Override
    public View getView(int pos, View convertView, ViewGroup parent) {
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

                }
            });

            viewHolder.heart.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.d("It works", "inside heart");

                }
            });

            viewHolder.tick2.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.d("It works", "inside tick");

                }
            });

            viewHolder.heart2.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.d("It works", "inside heart");

                }
            });


        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        viewHolder.friendName.setText(friendList.get(pos).getFriendName());
        new DownloadImage().execute(new MyTaskParams(friendList.get(pos).getId(), viewHolder.friendPic));

        if(friendList.get(pos).isYouTick()) {
            viewHolder.tick.setColorFilter(Color.parseColor("#76c720"));
            if(friendList.get(pos).isTheyTick()) {
                viewHolder.tick2.setColorFilter(Color.parseColor("#76c720"));
                viewHolder.tick2.setVisibility(View.VISIBLE);
            }
        }
        if(friendList.get(pos).isYouHeart()) {
            viewHolder.heart.setColorFilter(Color.parseColor("#D32F2F"));
            if(friendList.get(pos).isTheyHeart()) {
                viewHolder.heart2.setColorFilter(Color.parseColor("#D32F2F"));
                viewHolder.heart2.setVisibility(View.VISIBLE);
            }
        }

        return rowView;
    }

    private class MyTaskParams {
        String id;
        ImageView imageView;

        MyTaskParams(String id, ImageView imageView) {
            this.id = id;
            this.imageView = imageView;

        }
    }

    private class DownloadImage extends AsyncTask<MyTaskParams, Void, Void> {
        ImageView imageView;
        Bitmap bitmap;

        @Override
        protected Void doInBackground(MyTaskParams... params) {
            MyTaskParams im = params[0];
            String id = im.id;
            imageView = im.imageView;
            try {
                URL imageURL = new URL("https://graph.facebook.com/" + id + "/picture?type=large");
                InputStream inputStream = (InputStream) imageURL.getContent();
                bitmap = BitmapFactory.decodeStream(inputStream);


            } catch(MalformedURLException e) {
                e.printStackTrace();
            } catch(IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
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

    class ViewHolder{
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
