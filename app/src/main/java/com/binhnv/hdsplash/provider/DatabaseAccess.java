package com.binhnv.hdsplash.provider;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteConstraintException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.binhnv.hdsplash.models.MyImage;

import java.util.ArrayList;
import java.util.Calendar;

/**
 * Created by sev_user on 3/22/2016.
 */
public class DatabaseAccess {
    private static final String TAG = "DatabaseAccess";
    private SQLiteOpenHelper dbHelper;
    private SQLiteDatabase db;
    private static DatabaseAccess instance;

    private ImageDBDao mImageDBDao;
    private DaoSession mDaoSession;

    public static DatabaseAccess getInstance(Context context){
        if(instance == null){
            instance = new DatabaseAccess(context);
        }
        return  instance;
    }


    private DatabaseAccess(Context context){
        this.dbHelper = new ImageDBOpenHelper(context);
        this.db = dbHelper.getWritableDatabase();

        DaoMaster daoMaster = new DaoMaster(db);
        mDaoSession = daoMaster.newSession();
        mImageDBDao = mDaoSession.getImageDBDao();
    }

    public void close(){
        if(db != null)
            db.close();
    }

    /*class Comparator implements java.util.Comparator<MyImage>{
        public int compare(MyImage A, MyImage B){


        }
    }*/

    public ArrayList<MyImage> getImagesFromDb(){
        ArrayList<MyImage> imageList = new ArrayList<>();
        Cursor cursor = db.query("IMAGE_DB", new String[]{"url", "width", "height", "author", "category", "loved", "modified_date"}, null, null, null, null, "modified_date" + " DESC");
        cursor.moveToFirst();
        while(!cursor.isAfterLast()){
            //Log.d("han.hanh", cursor.getString(3));
            imageList.add(new MyImage(cursor.getString(0), cursor.getInt(1), cursor.getInt(2), cursor.getString(3), cursor.getInt(4), cursor.getInt(5), cursor.getLong(6)));
//            Log.d("han.hanh", .gecursortString(3));
            cursor.moveToNext();
        }
        cursor.close();

        return imageList;
    }

    public void setLoved(String tableName, ContentValues cv, String where, String[] value){
        db.update(tableName, cv, where, value);
    }

    public void insertImage(ArrayList<MyImage> images){
        for(MyImage image : images){
            image.convertCategory();
            Calendar calendar = Calendar.getInstance();
            long currentTime = calendar.getTimeInMillis();

            ImageDB iDb = new ImageDB(null, image.getAuthor(), image.getCategory(), image.getColor(), /*image.getDate()*/0,
                    /*image.getDisplayDate()*/ 0 , /*image.getFeatured()*/ 0, image.getUrl() , image.getLoved(),
                    image.getWidth(), image.getHeight(),/*image.getModifiedDate()*/ currentTime, /*image.getPlatform()*/ "",
                    /*image.getThumbnail()*/ "", image.getUrl());
            try {
                mImageDBDao.insert(iDb);
            } catch (SQLiteConstraintException e){
                Log.d(TAG, "Duplicate image");
            }
        }
        //Log.d(TAG, mImageDBDao.count() + "");
    }
}
