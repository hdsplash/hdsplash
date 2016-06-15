package com.binhnv.hdsplash.views.adapters;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.support.v7.graphics.Palette;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.binhnv.hdsplash.AdNativeViewHolder;
import com.binhnv.hdsplash.AppAdViewHolder;
import com.binhnv.hdsplash.ImagesViewHolder;

import com.binhnv.hdsplash.AppInstallAdFetcher;
import com.binhnv.hdsplash.AppInstallAdPlacement;
import com.binhnv.hdsplash.CountCategory;
import com.binhnv.hdsplash.CustomApplication;
import com.binhnv.hdsplash.OnItemClickListener;
import com.binhnv.hdsplash.R;
import com.binhnv.hdsplash.models.MyImage;
import com.binhnv.hdsplash.other.PaletteTransformation;
import com.binhnv.hdsplash.provider.DatabaseAccess;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.formats.NativeAppInstallAdView;
import com.mikepenz.iconics.view.IconicsImageView;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class ImageAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private String TAG = "ImageAdapter";
    private Context mContext;
    public CountCategory countCategory;
    public ArrayList<MyImage> mImages;
    public ArrayList<Object> listData;

    private int mScreenWidth;
    private int layoutType;

    private int mDefaultTextColor;
    private int mDefaultBackgroundColor;
    public int mStarred = 0;
    private DatabaseAccess databaseAccess;
    //private MainActivity activity;

    private OnItemClickListener onItemClickListener;

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public ImageAdapter(Activity countCategory/*, List<Object> list*/) {
        this.countCategory = (CountCategory) countCategory;
        mContext = countCategory.getApplicationContext();
        mImages = new ArrayList<>();
        listData = new ArrayList<Object>();
    }

    public ImageAdapter(ArrayList<MyImage> images) {
        this.mImages = images;
    }

    public boolean isNetworkOnline() {
        boolean status=false;
        try{
            ConnectivityManager cm = (ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo netInfo = cm.getNetworkInfo(0);
            if (netInfo != null && netInfo.getState()==NetworkInfo.State.CONNECTED) {
                status= true;
            }else {
                netInfo = cm.getNetworkInfo(1);
                if(netInfo!=null && netInfo.getState()==NetworkInfo.State.CONNECTED)
                    status= true;
            }
        }catch(Exception e){
            e.printStackTrace();
            return false;
        }
        return status;

    }

    public void updateData(ArrayList<MyImage> images) {
        this.mImages = images;
        notifyDataSetChanged();
    }

    public void updateAll( ArrayList<Object> list){
        this.listData = list;
        notifyDataSetChanged();
    }


    public int getItemViewType(int position){
        Object object = listData.get(position);
        if(object != null){
            return object instanceof MyImage ? 0 : 1;
        }
        return 0;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {

        if (viewType == 0) {
            View rowView = LayoutInflater.from(viewGroup.getContext())
                    .inflate(R.layout.item_image, viewGroup, false);

            databaseAccess = DatabaseAccess.getInstance(mContext);

            //get the colors
            mDefaultTextColor = mContext.getResources().getColor(R.color.text_without_palette);
            mDefaultBackgroundColor = mContext.getResources().getColor(R.color.image_without_palette);

            //get the screenWidth :D optimize everything :D
            mScreenWidth = mContext.getResources().getDisplayMetrics().widthPixels;
            return new ImagesViewHolder(rowView, onItemClickListener);
        } else {
            //TODO: For future native ad advance
            //View rowView = LayoutInflater.from(viewGroup.getContext())
            //        .inflate(R.layout.ad_app_install, viewGroup, false);
            //return new AppAdViewHolder((NativeAppInstallAdView) rowView);
            View rowView = LayoutInflater.from(viewGroup.getContext())
                    .inflate(R.layout.ad_native_item, viewGroup, false);
            return new AdNativeViewHolder(rowView);
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder ViewHolder, final int position) {

        Object rowData = listData.get(position);
        if(rowData instanceof MyImage) {
            Log.d(TAG, "position = " + position + " ImageOnBindViewHolder");
            final MyImage currentImage = (MyImage)rowData;
            mStarred = currentImage.getLoved();
            final ImagesViewHolder imagesViewHolder = (ImagesViewHolder)ViewHolder;
            imagesViewHolder.imageAuthor.setText(currentImage.getAuthor());
            imagesViewHolder.imageView.setDrawingCacheEnabled(true);
            imagesViewHolder.imageView.setImageBitmap(null);
            style(imagesViewHolder.imageLovedYes, mStarred == 1 ? 1 : 0);
            style(imagesViewHolder.imageLovedNo, mStarred == 0 ? 1 : 0);
            //reset colors so we prevent crazy flashes :D
            //imagesViewHolder.imageAuthor.setTextColor(mDefaultTextColor);
            //imagesViewHolder.imageDate.setTextColor(mDefaultTextColor);
            //imagesViewHolder.imageTextContainer.setBackgroundColor(mDefaultBackgroundColor);

            //cancel any loading images on this view
            //Picasso.with(mContext).cancelRequest(imagesViewHolder.imageView);
            //load the image
            //Log.d("DKM", position + " - " + mImages.get(position).getImageSrc(mScreenWidth));
            Picasso.with(mContext).load(((MyImage)listData.get(position)).getImageSrc(mScreenWidth))
                    //.networkPolicy(isNetworkOnline() ? NetworkPolicy.NO_CACHE : NetworkPolicy.OFFLINE)
                    .networkPolicy(NetworkPolicy.OFFLINE)
                    .placeholder(R.drawable.gradient_1)
                    .transform(PaletteTransformation.instance())
                    .into(imagesViewHolder.imageView, new Callback.EmptyCallback() {

                        @Override
                        public void onSuccess() {
                            Bitmap bitmap = ((BitmapDrawable) imagesViewHolder.imageView.getDrawable()).getBitmap(); // Ew!

                            if (bitmap != null && !bitmap.isRecycled()) {
                                Palette palette = PaletteTransformation.getPalette(bitmap);

                                if (palette != null) {
                                    Palette.Swatch s = palette.getVibrantSwatch();
                                    if (s == null) {
                                        s = palette.getDarkVibrantSwatch();
                                    }
                                    if (s == null) {
                                        s = palette.getLightVibrantSwatch();
                                    }
                                    if (s == null) {
                                        s = palette.getMutedSwatch();
                                    }

                                    if (s != null && position >= 0 && position < mImages.size()) {
                                        if (mImages.get(position) != null) {
                                            mImages.get(position).setSwatch(s);
                                        }

                                        //imagesViewHolder.imageAuthor.setTextColor(s.getTitleTextColor());
                                        //imagesViewHolder.imageDate.setTextColor(s.getTitleTextColor());
                                        //Utils.animateViewColor(imagesViewHolder.imageTextContainer, mDefaultBackgroundColor, s.getRgb());
                                    }
                                }
                            }

                            // just delete the reference again.
                            bitmap = null;

                            if (Build.VERSION.SDK_INT >= 21) {
                                imagesViewHolder.imageView.setTransitionName("cover" + position);
                            }
                            imagesViewHolder.imageTextContainer.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    onItemClickListener.onClick(v, position);
                                }
                            });

                            imagesViewHolder.imageLoveContainer.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    MyImage item = (MyImage)listData.get(position);
                                    boolean mStarred = false;
                                    ContentValues cv = new ContentValues();
                                    String where = "URL=?";
                                    String[] value = {item.getUrl()};
                                    if (item.getLoved() == 1) {
                                        mStarred = false;
                                        item.withLoved(0);
                                        cv.put("loved", 0);
                                    } else if (item.getLoved() == 0) {
                                        mStarred = true;
                                        item.withLoved(1);
                                        cv.put("loved", 1);
                                    }

                                    databaseAccess.setLoved("IMAGE_DB", cv, where, value);
                                    imagesViewHolder.animateHeart(((ViewGroup) v).getChildAt(0), ((ViewGroup) v).getChildAt(1), mStarred);
                                    countCategory.updateCategory(mImages);
                                }
                            });
                        }

                        @Override
                        public void onError() {
                            Picasso.with(mContext).load(((MyImage)listData.get(position)).getImageSrc(mScreenWidth))
                                    .placeholder(R.drawable.gradient_1)
                                    .transform(PaletteTransformation.instance())
                                    .into(imagesViewHolder.imageView, new Callback.EmptyCallback() {
                                        @Override
                                        public void onSuccess() {
                                            Bitmap bitmap = ((BitmapDrawable) imagesViewHolder.imageView.getDrawable()).getBitmap(); // Ew!

                                            if (bitmap != null && !bitmap.isRecycled()) {
                                                Palette palette = PaletteTransformation.getPalette(bitmap);

                                                if (palette != null) {
                                                    Palette.Swatch s = palette.getVibrantSwatch();
                                                    if (s == null) {
                                                        s = palette.getDarkVibrantSwatch();
                                                    }
                                                    if (s == null) {
                                                        s = palette.getLightVibrantSwatch();
                                                    }
                                                    if (s == null) {
                                                        s = palette.getMutedSwatch();
                                                    }

                                                    if (s != null && position >= 0 && position < mImages.size()) {
                                                        if (mImages.get(position) != null) {
                                                            mImages.get(position).setSwatch(s);
                                                        }

                                                    }
                                                }
                                            }
                                            // just delete the reference again.
                                            bitmap = null;

                                            if (Build.VERSION.SDK_INT >= 21) {
                                                imagesViewHolder.imageView.setTransitionName("cover" + position);
                                            }
                                            imagesViewHolder.imageTextContainer.setOnClickListener(new View.OnClickListener() {
                                                @Override
                                                public void onClick(View v) {
                                                    onItemClickListener.onClick(v, position);
                                                }
                                            });

                                            imagesViewHolder.imageLoveContainer.setOnClickListener(new View.OnClickListener() {
                                                @Override
                                                public void onClick(View v) {
                                                    MyImage item = (MyImage)listData.get(position);
                                                    boolean mStarred = false;
                                                    ContentValues cv = new ContentValues();
                                                    String where = "URL=?";
                                                    String[] value = {item.getUrl()};
                                                    if (item.getLoved() == 1) {
                                                        mStarred = false;
                                                        item.withLoved(0);
                                                        cv.put("loved", 0);
                                                    } else if (item.getLoved() == 0) {
                                                        mStarred = true;
                                                        item.withLoved(1);
                                                        cv.put("loved", 1);
                                                    }

                                                    databaseAccess.setLoved("IMAGE_DB", cv, where, value);
                                                    imagesViewHolder.animateHeart(((ViewGroup) v).getChildAt(0), ((ViewGroup) v).getChildAt(1), mStarred);
                                                    //activity.setCategoryCount(mImages);
                                                    countCategory.updateCategory(mImages);
                                                }
                                            });
                                        }  // end onSuccess of onerror
                                    }); // end callback of onError
                        } // end onError
                    });  // end picasso


            //calculate height of the list-item so we don't have jumps in the view
            DisplayMetrics displaymetrics = mContext.getResources().getDisplayMetrics();
            int finalHeight =  0;
            try {
                finalHeight = (int) (displaymetrics.widthPixels * currentImage.getHeight() / currentImage.getWidth());
            } catch (Exception e){
                e.printStackTrace();
            }
            int layoutType = CustomApplication.sharedPreferences.getInt("LayoutType", 1);
            finalHeight = (layoutType == 0) ? finalHeight / 2 : finalHeight;
            imagesViewHolder.imageView.setMinimumHeight(finalHeight);
        } else if (rowData instanceof AppInstallAdPlacement){
            //TODO: For future native ad advance
            //AppAdViewHolder adViewHolder = (AppAdViewHolder)ViewHolder;
            //((AppInstallAdPlacement) rowData).mFetcher.loadAd(mContext, adViewHolder);
            layoutType= CustomApplication.sharedPreferences.getInt(mContext.getString(R.string.layout_type), 1);
            AdNativeViewHolder adNativeViewHolder = (AdNativeViewHolder)ViewHolder;
            adNativeViewHolder.adView.loadAd(new AdRequest.Builder().build());
            if(layoutType == 0){
                adNativeViewHolder.adView.setVisibility(View.GONE);
            } else if(layoutType == 1){
                adNativeViewHolder.adView.setVisibility(View.VISIBLE);
            }
        } else {
            throw new IllegalArgumentException(
                    String.format("Adapter can't handle getView() for list item of type %s",
                            rowData.getClass().getName()));
        }

    }

    @Override
    public int getItemCount() {
        return listData.size();
    }

    private void style(View view, int value) {
        view.setScaleX(value);
        view.setScaleY(value);
        view.setAlpha(value);
    }

}


