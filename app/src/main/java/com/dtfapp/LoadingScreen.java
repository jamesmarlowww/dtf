package com.dtfapp;

import android.app.Fragment;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.TextView;

/**
 * Created by James on 6/29/2015.
 */
public class LoadingScreen extends Fragment {
    ViewGroup mRootview;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.loading_spin_frag, container, false);
        TextView tv = (TextView) view.findViewById(R.id.penisText);

        rotateText(tv);
        return  view;
    }

    public void rotateText(TextView tv) {
        //rotate from the start of string
        RotateAnimation anim = new RotateAnimation(0f, 350f, 15f, 15f);
        anim.setInterpolator(new LinearInterpolator());
        anim.setRepeatCount(Animation.INFINITE);
        anim.setDuration(700);
        tv.startAnimation(anim);
    }




}
