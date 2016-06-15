package com.binhnv.hdsplash;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.FrameLayout;

import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.NativeExpressAdView;

/**
 * Created by sev_user on 6/15/2016.
 */
public class AdNativeViewHolder extends RecyclerView.ViewHolder {

    public NativeExpressAdView adView;
    public FrameLayout adContainer;

    public AdNativeViewHolder(View itemView){
        super(itemView);
        adContainer = (FrameLayout)itemView.findViewById(R.id.ad_container);
        adView = (NativeExpressAdView)itemView.findViewById(R.id.adView);
    }
}
