package com.binhnv.hdsplash.provider;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.readystatesoftware.sqliteasset.SQLiteAssetHelper;

/**
 * Created by sev_user on 3/22/2016.
 */
public class ImageDBOpenHelper extends SQLiteAssetHelper {

    public static final String DATABASE_NAME = "Application.db";
    private static final int DATABASE_VERSION = 2;


    public ImageDBOpenHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }


}
