package com.binhnv.hdsplash;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import com.binhnv.hdsplash.models.MyImage;
import com.binhnv.hdsplash.network.MyApi;
import com.binhnv.hdsplash.provider.DatabaseAccess;

import java.util.ArrayList;

import rx.Observable;
import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func2;
import rx.schedulers.Schedulers;

/**
 * Created by sev_user on 6/11/2016.
 */
public class AlarmReceiver extends BroadcastReceiver {

    private String TAG = "AlarmReceiver";
    private DatabaseAccess databaseAccess;
    private MyApi mApi;
    private MyApi sApi;

    private Context mContext;

    @Override
    public void onReceive(Context context, Intent intent) {
        mContext = context;
        databaseAccess = DatabaseAccess.getInstance(mContext);
        mApi = new MyApi();
        sApi = new MyApi();
        Log.d(TAG, "onReceiver");
        Observable<ArrayList<MyImage>> mOb = mApi.fetchImages("30", "1").cache().subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread());
        Observable<ArrayList<MyImage>> sOb = sApi.fetchImages("30", "2").cache().subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread());
        Observable.zip(mOb, sOb, new Func2<ArrayList<MyImage>, ArrayList<MyImage>, ArrayList<MyImage>>() {
            @Override
            public ArrayList<MyImage> call(ArrayList<MyImage> myImages, ArrayList<MyImage> myImages2) {
                ArrayList<MyImage> zip = new ArrayList<MyImage>();
                for (MyImage img : myImages) {
                    zip.add(img);
                }
                for (MyImage img : myImages2) {
                    zip.add(img);
                }
                Log.d("frag zip", String.valueOf(myImages.size() + myImages2.size()));
                return zip;
            }
        }).subscribe(observer);

    }

    private Observer<ArrayList<MyImage>> observer = new Observer<ArrayList<MyImage>>() {
        @Override
        public void onNext(final ArrayList<MyImage> images) {
            Log.d(TAG, "onNext");
            databaseAccess.insertImage(images);
        }

        @Override
        public void onCompleted() {

        }

        @Override
        public void onError(final Throwable error) {

        }
    };
}
