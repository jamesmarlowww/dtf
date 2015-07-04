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


        } else {
            viewHolder = (ViewHolder) convertView.getTag();

        }
        viewHolder.friendName.setText(friendList.get(pos).getFriendName());


//        ((ProfilePictureView) rowView.findViewById(R.id.profilePicture)).setProfileId(
//                Profile.getCurrentProfile().getId()
//        );

//        String id = friendList.get(pos).getIdStr();
//        ((ProfilePictureView) rowView.findViewById(R.id.profilePicture)).setProfileId(id);


        new DownloadImage().execute(new MyTaskParams(friendList.get(pos).getId(), viewHolder.friendPic));



        return rowView;
    }

    private class MyTaskParams {
        int id;
        ImageView imageView;

        MyTaskParams(int id, ImageView imageView) {
            this.id = id;
            this.imageView = imageView;

        }
    }

    private class DownloadImage extends AsyncTask<MyTaskParams, Void, Void> {

        @Override
        protected Void doInBackground(MyTaskParams... params) {
            MyTaskParams im = params[0];
            int id = im.id;
            ImageView imageView = im.imageView;

            try {
//                URL url = null;
//                InputStream is = null;
//                url = new URL("http://graph.facebook.com/" + id + "/picture?type=large");
//                is = url.openConnection().getInputStream();
//                Bitmap image;
//                image = BitmapFactory.decodeStream(is);
//                imageView.setImageBitmap(image);

//                URL imgUrl = new URL ("https://graph.facebook.com/me/picture?type=normal&method=GET&access_token=" );
//                        //("http://graph.facebook.com/"+ id + "/picture?type=small");
//
//                InputStream in = (InputStream) imgUrl.getContent();
//                Bitmap bitmap = BitmapFactory.decodeStream(in);

                URL imageURL = new URL("https://graph.facebook.com/" + id + "/picture");
                InputStream inputStream = (InputStream) imageURL.getContent();
                Bitmap bitmap = BitmapFactory.decodeStream(inputStream);



                imageView.setImageBitmap(bitmap);

            } catch(MalformedURLException e) {
                e.printStackTrace();
            } catch(IOException e) {
                e.printStackTrace();
            }
            return null;

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
