package io.downto.app;

import android.app.Application;

import com.parse.Parse;

/**
 * Created by james on 7/27/2015.
 */
public class App extends Application {

    //called in manifest.
    //only runs when first started. best way with android app lifecycle
    @Override
    public void onCreate() {
        super.onCreate();
        Parse.initialize(this, "LD9q9E8DBtXQGetITICyXpW9yVVR3ZSEscEvKDfW", "CCTYV5PIHxQ7HG3avmXBl8fu62XKrBWRdEC8xvAg");
    }
}


