package com.br.app;

import android.app.Application;
import android.text.TextUtils;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.Volley;
import com.br.utils.LruBitmapCache;
import com.br.utils.PrefManager;

/**
 * Created by techresult on 21/10/2014.
 *
 * This is a singleton class which extends Application and uses the Volley library to replace the regular Network connection in order to make some improvements
 *
 */
public class AppController extends Application {
    public static final String TAG = AppController.class.getSimpleName();

    private RequestQueue requestQueue;
    private ImageLoader imageLoader;
    LruBitmapCache lruBitmapCache;

    private static AppController mInstance;
    private PrefManager pref;

    @Override
    public void onCreate(){
        super.onCreate();
        mInstance = this;
        pref = new PrefManager(this);
    }

    public static synchronized AppController getInstance(){
        return mInstance;
    }

    public PrefManager getPrefManager(){
        if(pref == null){
            pref = new PrefManager(this);
        }

        return pref;
    }

    public RequestQueue getRequestQueue(){
        if(requestQueue == null){
            requestQueue = Volley.newRequestQueue(getApplicationContext()); //Volley usage
        }

        return requestQueue;
    }

    public ImageLoader getImageLoader(){
        getRequestQueue();
        if(imageLoader == null){
            getLruBitmapCache();
            imageLoader = new ImageLoader(this.requestQueue, lruBitmapCache);
        }

        return this.imageLoader;
    }

    public LruBitmapCache getLruBitmapCache(){
        if(lruBitmapCache == null){
            lruBitmapCache = new LruBitmapCache();
        }

        return this.lruBitmapCache;
    }

    public <T> void addToRequestQueue(Request<T> req, String tag){
        req.setTag(TextUtils.isEmpty(tag) ? TAG : tag);
        getRequestQueue().add(req);
    }

    public <T> void addToRequestQueue(Request<T> req){
        req.setTag(TAG);
        getRequestQueue().add(req);
    }

    public void cancelPendingRequests(Object tag){
        if(requestQueue != null){
            requestQueue.cancelAll(tag);
        }
    }
}
