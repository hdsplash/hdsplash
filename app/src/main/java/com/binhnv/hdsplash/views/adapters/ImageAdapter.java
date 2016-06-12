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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewPropertyAnimator;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.binhnv.hdsplash.CountCategory;
import com.binhnv.hdsplash.CustomApplication;
import com.binhnv.hdsplash.OnItemClickListener;
import com.binhnv.hdsplash.R;
import com.binhnv.hdsplash.models.MyImage;
import com.binhnv.hdsplash.other.PaletteTransformation;
import com.binhnv.hdsplash.provider.DatabaseAccess;
import com.mikepenz.iconics.view.IconicsImageView;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class ImageAdapter extends RecyclerView.Adapter<ImagesViewHolder> {

    private Context mContext;
    public CountCategory countCategory;
    public ArrayList<MyImage> mImages;

    private int mScreenWidth;

    private int mDefaultTextColor;
    private int mDefaultBackgroundColor;
    public int mStarred = 0;
    private DatabaseAccess databaseAccess;
    //private MainActivity activity;

    private OnItemClickListener onItemClickListener;

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public ImageAdapter(Activity countCategory) {
        //activity = MainActivity.getInstance();
        this.countCategory = (CountCategory) countCategory;
        mImages = new ArrayList<>();
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

    public void updateDataFromUrls(ArrayList<String> urls) {

    }

    @Override
    public ImagesViewHolder onCreateViewHolder(ViewGroup viewGroup, int position) {

        View rowView = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.item_image, viewGroup, false);

        //set the mContext
        this.mContext = viewGroup.getContext();
        databaseAccess = DatabaseAccess.getInstance(mContext);

        //get the colors
        mDefaultTextColor = mContext.getResources().getColor(R.color.text_without_palette);
        mDefaultBackgroundColor = mContext.getResources().getColor(R.color.image_without_palette);

        //get the screenWidth :D optimize everything :D
        mScreenWidth = mContext.getResources().getDisplayMetrics().widthPixels;

        return new ImagesViewHolder(rowView, onItemClickListener);
    }

    @Override
    public void onBindViewHolder(final ImagesViewHolder imagesViewHolder, final int position) {

        final MyImage currentImage = mImages.get(position);
        mStarred = currentImage.getLoved();
        imagesViewHolder.imageAuthor.setText(currentImage.getAuthor());
        // imagesViewHolder.imageDate.setText(currentImage.getReadableModified_Date());
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
        Picasso.with(mContext).load(mImages.get(position).getImageSrc(mScreenWidth))
                //.networkPolicy(isNetworkOnline() ? NetworkPolicy.NO_CACHE : NetworkPolicy.OFFLINE)
                .networkPolicy(NetworkPolicy.OFFLINE)
                .placeholder(R.drawable.gradient)
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
                                MyImage item = mImages.get(position);
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
                                //activity.setCategoryCount(mImages);
                            }
                        });
                    }

                    @Override
                    public void onError() {
                        Picasso.with(mContext).load(mImages.get(position).getImageSrc(mScreenWidth))
                                .placeholder(R.drawable.gradient)
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
                                                MyImage item = mImages.get(position);
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
        //image.width .... image.height
        //device.width ... device
        //int finalHeight = (int) (displaymetrics.widthPixels / (currentImage.getRatio() < 1 ? 1 : currentImage.getRatio()));
        int finalHeight = (int) (displaymetrics.widthPixels * currentImage.getHeight() / currentImage.getWidth());
        int layoutType= CustomApplication.sharedPreferences.getInt("LayoutType", 0);
        finalHeight= (layoutType ==0) ? finalHeight/2 : finalHeight ;
        imagesViewHolder.imageView.setMinimumHeight(finalHeight);

    }

    @Override
    public int getItemCount() {
        return mImages.size();
    }

    private void style(View view, int value) {
        view.setScaleX(value);
        view.setScaleY(value);
        view.setAlpha(value);
    }

}





class ImagesViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    protected final LinearLayout imageTextContainer;
    protected final ImageView imageView;
    protected final TextView imageAuthor;
   // protected final TextView imageDate;
    private final OnItemClickListener onItemClickListener;
    protected final RelativeLayout imageLoveContainer;
    protected final IconicsImageView imageLovedYes;
    protected final IconicsImageView imageLovedNo;

    public ImagesViewHolder(final View itemView, OnItemClickListener onItemClickListener) {

        super(itemView);
        this.onItemClickListener = onItemClickListener;

        imageTextContainer = (LinearLayout) itemView.findViewById(R.id.item_image_text_container);
        imageView = (ImageView) itemView.findViewById(R.id.item_image_img);
        imageAuthor = (TextView) itemView.findViewById(R.id.item_image_author);
        //imageDate = (TextView) itemView.findViewById(R.id.item_image_date);
        imageLoveContainer = (RelativeLayout) itemView.findViewById(R.id.item_image_loved_container);
        imageLovedYes = (IconicsImageView) itemView.findViewById(R.id.item_image_loved_yes);
        imageLovedNo = (IconicsImageView) itemView.findViewById(R.id.item_image_loved_no);

        imageView.setOnClickListener(this);
    }

    public void animateHeart(View imageLovedOn, View imageLovedOff, boolean on) {
        imageLovedOn.setVisibility(View.VISIBLE);
        imageLovedOff.setVisibility(View.VISIBLE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR1) {
            viewPropertyStartCompat(imageLovedOff.animate().scaleX(on ? 0 : 1).scaleY(on ? 0 : 1).alpha(on ? 0 : 1));
            viewPropertyStartCompat(imageLovedOn.animate().scaleX(on ? 1 : 0).scaleY(on ? 1 : 0).alpha(on ? 1 : 0));
        }
    }

    public static void viewPropertyStartCompat(ViewPropertyAnimator animator) {
        if (Build.VERSION.SDK_INT >= 14) {
            animator.start();
        }
    }

    @Override
    public void onClick(View v) {
        onItemClickListener.onClick(v, getPosition());
    }



}

