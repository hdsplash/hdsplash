package com.binhnv.hdsplash.network;

import android.util.Log;

import com.binhnv.hdsplash.CustomApplication;
import com.binhnv.hdsplash.models.MyImage;
import com.binhnv.hdsplash.other.MyDeserializer;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.squareup.okhttp.Cache;
import com.squareup.okhttp.OkHttpClient;

import java.io.File;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Iterator;

import retrofit.RequestInterceptor;
import retrofit.RestAdapter;
import retrofit.client.OkClient;
import retrofit.converter.GsonConverter;
import retrofit.http.GET;
import retrofit.http.Headers;
import retrofit.http.Query;
import rx.Observable;

/**
 * Created by sev_user on 1/27/2016.
 */
public class MyApi {
    public static final String ENDPOINT = "https://api.unsplash.com/";

    private final UnsplashService mWebService;

   // public static Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:ss").create(); //2015-01-18 15:48:56

    public MyApi() {
        Cache cache = null;
        OkHttpClient okHttpClient = null;

        try {
            File cacheDir = new File(CustomApplication.getContext().getCacheDir().getPath(), "pictures.json");
            Log.d("CacheFile", cacheDir.toString());
            cache = new Cache(cacheDir, 10 * 1024 * 1024);
            okHttpClient = new OkHttpClient();
            okHttpClient.setCache(cache);
        } catch (Exception e) {
        }

        Gson gson= new GsonBuilder()
                .registerTypeAdapter(MyImage.class, new MyDeserializer<MyImage>())
                .create();

        RestAdapter restAdapter = new RestAdapter.Builder()
                .setEndpoint(ENDPOINT)
                .setClient(new OkClient(okHttpClient))
                .setConverter(new GsonConverter(gson))
                .setRequestInterceptor(new RequestInterceptor() {
                    @Override
                    public void intercept(RequestFacade request) {
                        request.addHeader("Cache-Control", "public, max-age=" + 60 * 60 * 4);
                    }
                })
                .build();
        mWebService = restAdapter.create(UnsplashService.class);
    }


    public interface UnsplashService {
        //@GET("/pictures")
        // Observable<ImageList> listImages();
        @Headers("Authorization: Client-ID 3ad75d8070903e089b9a92e51805572584b1fae54b5fcb973794550656c8c092")
        @GET("/photos")
        Observable<ArrayList<MyImage>> getData(@Query("per_page") String per_page, @Query("page") String page);
    }

   /* public interface RandomUnsplashService {
        @GET("/random")
        Image random();
    }*/



    public Observable<ArrayList<MyImage>> fetchImages(String per_page, String page) {
        return mWebService.getData(per_page, page);
    }


    //keep the filtered array so we can reuse it later :D
   /* private ArrayList<MyImage> featured = null;

    public ArrayList<MyImage> filterFeatured(ArrayList<MyImage> images) {
        if (featured == null) {
            ArrayList<MyImage> list = new ArrayList<MyImage>(images);
            for (Iterator<MyImage> it = list.iterator(); it.hasNext(); ) {
                if (it.next().getFeatured() != 1) {
                    it.remove();
                }
            }
            featured = list;
        }
        return featured;
    }

    public static int countFeatured(ArrayList<MyImage> images) {
        int count = 0;
        for (MyImage image : images) {
            if (image.getFeatured() == 1) {
                count = count + 1;
            }
        }
        return count;
    }*/

    public ArrayList<MyImage> filterCategory(ArrayList<MyImage> images, int filter) {
        ArrayList<MyImage> list = new ArrayList<MyImage>(images);
        for (Iterator<MyImage> it = list.iterator(); it.hasNext(); ) {
            if ((it.next().getCategory() & filter) != filter) {
                it.remove();
            }
        }
        return list;
    }

    public ArrayList<MyImage> filterLoved(ArrayList<MyImage> images, int loved){
        ArrayList<MyImage> list = new ArrayList<MyImage>(images);
        for (Iterator<MyImage> it = list.iterator(); it.hasNext(); ) {
            if ((it.next().getLoved() & loved) != loved) {
                it.remove();
            }
        }
        return list;
    }

    public static int countCategory(ArrayList<MyImage> images, int filter) {
        int count = 0;
        for (MyImage image : images) {
            if ((image.getCategory() & filter) == filter) {
                count = count + 1;
            }
        }
        return count;
    }

    public static int countLoved(ArrayList<MyImage> images, int loved){
        int count = 0;
        for (MyImage image : images) {
            if ((image.getLoved() & loved) == loved) {
                count = count + 1;
            }
        }
        return count;
    }
}
