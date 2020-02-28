package com.example.skye_stagram;

import android.app.Application;

import com.example.skye_stagram.models.Post;
import com.parse.Parse;
import com.parse.ParseObject;

public class ParseApplication extends Application
{
    @Override
    public void onCreate()
    {
        super.onCreate();

        // Must register data objects with Parse before calling initialize
        ParseObject.registerSubclass(Post.class);

        // configure settings based on heroku's settings
        // clientKey is not needed unless explicitly configured
        // any network interceptors must be added with the configuration builder given this syntax
        Parse.initialize(new Parse.Configuration.Builder(this)
        .applicationId("skye-stagram")      // APP_ID
        .clientKey("LovelyOrbiting")
        .server("https://skye-stagram.herokuapp.com/parse/").build());  // ensure the url uses https so the web traffic is encrypted

    }
}
