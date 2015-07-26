package io.downto.app;

import android.app.Fragment;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

/**
 * Created by james on 7/26/2015.
 */
public class HowTo extends Fragment {

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.how_to, container,false);

        ImageView imageView = (ImageView) view.findViewById(R.id.h1);
        imageView.setColorFilter(Color.parseColor("#D32F2F"));
        ImageView imageView2 = (ImageView) view.findViewById(R.id.h2);
        imageView2.setColorFilter(Color.parseColor("#D32F2F"));
        ImageView imageView3 = (ImageView) view.findViewById(R.id.h3);
        imageView3.setColorFilter(Color.parseColor("#D32F2F"));



        return view;
    }


}
