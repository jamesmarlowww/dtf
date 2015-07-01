package com.dtfapp;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.os.AsyncTask;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;

/**
 * Created by James on 6/30/2015.
 */
public class FriendListArrayAdapter extends ArrayAdapter<FriendInfo> {
    private int resourceID; // number in list. 0,1,2,3
    private Context context;
    private ArrayList<FriendInfo> friendList;


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

        if (rowView == null) {
            LayoutInflater inflater = ((Activity) context).getLayoutInflater();
            rowView = inflater.inflate(resourceID, parent, false);


            viewHolder.friendName = (TextView) rowView.findViewById(R.id.friendsName);
            viewHolder.friendPic = (ImageView) rowView.findViewById(R.id.friendsPic);
            rowView.setTag(viewHolder);

        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        viewHolder.friendName.setText(friendList.get(pos).getFriendName());

        viewHolder.friendPic = (ImageView) rowView.findViewById(R.id.friendsPic);
        String id = friendList.get(pos).getIdStr();
        viewHolder.friendPic.setTag(id);

        new BitmapImageDownload().execute(viewHolder.friendPic);




        return rowView;
    }


    private class BitmapImageDownload extends AsyncTask<ImageView, Void, Bitmap> {
        ImageView imageView;

        @Override
        protected Bitmap doInBackground(ImageView... params) {
            ImageView s = params[0];
            try {
                return getImage((String) imageView.getTag());
            } catch (NullPointerException e) {
                Log.e("NullPointerException", e.toString());
                return null;
            }
        }

        @Override
        protected void onPostExecute(Bitmap result) {
            if(result!= null)
                imageView.setImageBitmap(result);
        }


        private Bitmap getImage(String id) {
            Bitmap bitmap = null;
            try {
//                URL imgUrl = new URL("https://graph.facebook.com/" + id + "/picture/?type=small");
//                InputStream in = (InputStream) imgUrl.getContent();
//                bitmap = BitmapFactory.decodeStream(in);

                URL aURL = new URL("https://graph.facebook.com/" + id + "/picture/?type=small");
                URLConnection conn = aURL.openConnection();
                conn.connect();
                InputStream is = conn.getInputStream();
                BufferedInputStream bis = new BufferedInputStream(is);
                bitmap = BitmapFactory.decodeStream(bis);


            } catch (IOException e) {
                Log.e("Error", e.toString());
            }

            return bitmap;

        }


    }


    public static Bitmap getRoundedBitmap(Bitmap bitmap) {
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

    @Override
    public int getCount() {
        return friendList.size();
    }

}
