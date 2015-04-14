package ca.zevisert.groovesharkrefresh;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;
import android.util.LruCache;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.Volley;

import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

public class VolleyContainer {

    static final String API_URL = "https://api.grooveshark.com/ws/3.0/?sig=";
    static final String API_KEY = "droid_isert";
    static final String API_SECRET = "aac41bec4b8c2d68ebe6af7f9f213676";

    private static VolleyContainer instance;
    private RequestQueue mRequestQueue;
    private ImageLoader mImageLoader;

    public VolleyContainer(Context context) {
        mRequestQueue = Volley.newRequestQueue(context);
        mImageLoader = new ImageLoader(mRequestQueue, new ImageLoader.ImageCache() {
            private final LruCache<String, Bitmap> cache = new LruCache<String, Bitmap>(20);
            @Override
            public Bitmap getBitmap(String url) {
                return cache.get(url);
            }

            @Override
            public void putBitmap(String url, Bitmap bitmap) {
                cache.put(url, bitmap);
            }
        });
    }

    public static VolleyContainer getInstance(Context context){
        if (instance == null){
            instance = new VolleyContainer(context);
        }
        return instance;
    }

    private RequestQueue getRequestQueue(){
        return mRequestQueue;
    }

    public ImageLoader getImageLoader(){
        return mImageLoader;
    }

    public <T> void addToRequestQueue(Request<T> req){
        req.setTag("Grooveshark Refresh");
        getRequestQueue().add(req);
    }

    public static String getHmacMD5(String API_PAYLOAD){
        String sEncodedString = null;

        try {
            SecretKeySpec key = new SecretKeySpec((API_SECRET).getBytes("UTF-8"), "HmacMD5");
            Mac mac = Mac.getInstance("HmacMD5");
            mac.init(key);

            byte[] bytes = mac.doFinal(API_PAYLOAD.getBytes("UTF-8"));

            StringBuilder hash = new StringBuilder();

            for (int i = 0; i < bytes.length; i++){
                String hex = Integer.toHexString(0xFF & bytes[i]);
                if (hex.length() == 1){
                    hash.append('0');
                }
                hash.append(hex);
            }
            sEncodedString = hash.toString();
        }
        catch (UnsupportedEncodingException | NoSuchAlgorithmException | InvalidKeyException e) {
            Log.e("[GS REFRESH][ONLINE]", e.getMessage());
        }
        return sEncodedString;
    }

    public static String makePayload (String API_METHOD){
        return "{\"method\":\"" + API_METHOD + "\",\"header\":{\"wsKey\":\"" + API_KEY + "\"},\"parameters\":[]}";
    }

}
