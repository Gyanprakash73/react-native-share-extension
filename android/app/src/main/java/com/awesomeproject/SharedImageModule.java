package com.awesomeproject;

import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.Promise;
import com.facebook.react.bridge.WritableMap;
import com.facebook.react.bridge.Arguments;
import android.content.Intent;
import android.net.Uri;
import android.content.SharedPreferences;
import android.content.Context;

public class SharedImageModule extends ReactContextBaseJavaModule {
    private static ReactApplicationContext reactContext;
    private static final String PREFS_NAME = "SharedImagePrefs";
    private static final String KEY_SHARED_IMAGE_URI = "sharedImageUri";

    public SharedImageModule(ReactApplicationContext context) {
        super(context);
        reactContext = context;
    }

    @Override
    public String getName() {
        return "SharedImageModule";
    }

    public static void handleIntent(Intent intent) {
        if (intent.hasExtra(Intent.EXTRA_STREAM)) {
            Uri sharedImageUri = intent.getParcelableExtra(Intent.EXTRA_STREAM);
            if (sharedImageUri != null && reactContext != null) {
                saveSharedImageUri(sharedImageUri.toString());
            }
        }
    }

    private static void saveSharedImageUri(String uri) {
        SharedPreferences prefs = reactContext.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(KEY_SHARED_IMAGE_URI, uri);
        editor.apply();
    }

    private static String getStoredSharedImageUri() {
        SharedPreferences prefs = reactContext.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        return prefs.getString(KEY_SHARED_IMAGE_URI, null);
    }

    @ReactMethod
    public void getSharedImage(Promise promise) {
        WritableMap map = Arguments.createMap();
        String storedUri = getStoredSharedImageUri();

        if (storedUri != null) {
            map.putString("type", "image");
            map.putString("value", storedUri);
            promise.resolve(map);
        } else {
            promise.resolve(null);
        }
    }

    @ReactMethod
    public void clearSharedImage(Promise promise) {
        SharedPreferences prefs = reactContext.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.remove(KEY_SHARED_IMAGE_URI);
        editor.apply();
        promise.resolve(true);
    }
}
