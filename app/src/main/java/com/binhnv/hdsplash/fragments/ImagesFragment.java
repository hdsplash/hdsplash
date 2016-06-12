package com.binhnv.hdsplash.fragments;

import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.PointF;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearSmoothScroller;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.util.SparseArray;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.binhnv.hdsplash.CustomApplication;
import com.binhnv.hdsplash.OnItemClickListener;
import com.binhnv.hdsplash.R;
import com.binhnv.hdsplash.activities.DetailActivity;
import com.binhnv.hdsplash.activities.MainActivity;
import com.binhnv.hdsplash.models.MyImage;
import com.binhnv.hdsplash.network.MyApi;
import com.binhnv.hdsplash.provider.DatabaseAccess;
import com.binhnv.hdsplash.views.adapters.ImageAdapter;

import java.util.ArrayList;
import java.util.Collections;

import rx.Observable;
import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func2;
import rx.schedulers.Schedulers;
import tr.xip.errorview.ErrorView;

public class ImagesFragment extends Fragment {

    private static final String TAG = "ImagesFragment";
    public static SparseArray<Bitmap> photoCache = new SparseArray<>(1);

    private MyApi mApi = new MyApi();
    private MyApi sApi = new MyApi();

    private ImageAdapter mImageAdapter;
    private ArrayList<MyImage> mImages;
    private ArrayList<MyImage> mCurrentImages;
    private RecyclerView mImageRecycler;
    private ProgressBar mImagesProgress;
    private ErrorView mImagesErrorView;
    private DatabaseAccess databaseAccess;
    private SwipeRefreshLayout mRefreshLayout;
    RecyclerView.LayoutManager layoutManager;
    GridLayoutManager gridLayoutManager;
    StaggeredGridLayoutManager staggeredGridLayoutManager;

    private FloatingActionButton fab;
    private int layoutType;
    private int imagesNum;
    private  int allFilter;
    private int currentFilter;

    //private Context mContext;
    private Toolbar toolbar;


    public void setToolbar(Toolbar toolbar) {
        this.toolbar = toolbar;
    }

    private class CustomGridLayoutManager extends GridLayoutManager{
        public CustomGridLayoutManager(Context context, int spanCount) {
            super(context, spanCount);
        }

        @Override
        public void smoothScrollToPosition(RecyclerView recyclerView, RecyclerView.State state, int position){
            RecyclerView.SmoothScroller smoothScroller = new TopSnappedSmoothScroller(recyclerView.getContext());
            smoothScroller.setTargetPosition(position);
            startSmoothScroll(smoothScroller);
        }

        private class TopSnappedSmoothScroller extends LinearSmoothScroller {
            public TopSnappedSmoothScroller(Context context) {
                super(context);

            }

            @Override
            public PointF computeScrollVectorForPosition(int targetPosition) {
                return CustomGridLayoutManager.this
                        .computeScrollVectorForPosition(targetPosition);
            }

            @Override
            protected int getVerticalSnapPreference() {
                return SNAP_TO_START;
            }
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        Log.d("frag", "onCreate");
        setHasOptionsMenu(true);
        databaseAccess = DatabaseAccess.getInstance(getActivity());
        imagesNum= databaseAccess.getImagesFromDb().size();
        currentFilter=  MainActivity.Category.ALL.id;
        //mImages = databaseAccess.getImagesFromDb();
        if (ImagesFragment.this.getActivity() instanceof MainActivity) {
            ((MainActivity) ImagesFragment.this.getActivity()).setOnFilterChangedListener(new MainActivity.OnFilterChangedListener() {
                @Override
                public void onFilterChanged(int filter) {
                    currentFilter=filter;
                    if (mImages != null) {
                        Log.d("frag", "onCreate - " + mImages.size());
                        if (filter == MainActivity.Category.ALL.id) {
                            //showAll(filter);
                            showCategory(filter);
                        } else if (filter == MainActivity.Category.FEATURED.id) {
                            showFeatured();
                        } else if (filter == MainActivity.Category.LOVED.id) {
                            showLoved();
                        } else {
                            showCategory(filter);
                        }
                    }
                }
            });
        }

        //showAll();
        staggeredGridLayoutManager= new StaggeredGridLayoutManager(2,StaggeredGridLayoutManager.VERTICAL);
        gridLayoutManager =  new CustomGridLayoutManager(getActivity(), 1);
        //gridLayoutManager = new GridLayoutManager(getActivity(), 1);

        // sharedPreferences= getActivity().getSharedPreferences(MyPREFERENCES,getActivity().MODE_PRIVATE);
        layoutType= CustomApplication.sharedPreferences.getInt(getString(R.string.layout_type), 0);
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.d("frag", "onCreateView");
        View rootView = inflater.inflate(R.layout.fragment_images, container, false);
        mImageRecycler = (RecyclerView) rootView.findViewById(R.id.fragment_last_images_recycler);
        fab = (FloatingActionButton)rootView.findViewById(R.id.fab);
        //mImagesProgress = (ProgressBar) rootView.findViewById(R.id.fragment_images_progress);
        //mImagesErrorView = (ErrorView) rootView.findViewById(R.id.fragment_images_error_view);
        mRefreshLayout = (SwipeRefreshLayout) rootView.findViewById(R.id.swipeRefreshLayout);
        mRefreshLayout.setColorSchemeColors(getResources().getColor(R.color.material_red));
        mRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                showAll(currentFilter);
                Log.d("DKM", "refresh " + currentFilter);
            }
        });

        //gridLayoutManager = new CustomGridLayoutManager(getActivity(), 1);
        layoutManager= (layoutType == 1) ? gridLayoutManager : staggeredGridLayoutManager;
        mImageRecycler.setLayoutManager(layoutManager);

        SpacesItemDecoration decoration = new SpacesItemDecoration(5);
        mImageRecycler.addItemDecoration(decoration);
       // mImageRecycler.setLayoutManager(gridLayoutManager);
        //mImageRecycler.addItemDecoration(new RecyclerInsetsDecoration(getActivity()));
        mImageRecycler.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return false;
            }
        });

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mImageRecycler.smoothScrollToPosition(0);
            }
        });


        mImageAdapter = new ImageAdapter(getActivity());
        mImageAdapter.setOnItemClickListener(recyclerRowClickListener);
        mImageRecycler.setAdapter(mImageAdapter);

        mImages = databaseAccess.getImagesFromDb();
        updateAdapter(mImages);
        //showAll(MainActivity.Category.ALL.id);
        showAll(currentFilter);

        mImageRecycler.setOnScrollListener(recyclerScrollListener);
        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d("frag", "onResume");
        if (mImages != null) {
            Log.d("frag", "onResume - " + mImages.size());
        }
    }

    private void showAll(int cate) {

        allFilter= cate;

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


    /*class LoadImageFromDb extends AsyncTask<Void, Void, ArrayList<MyImage>> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(ArrayList<MyImage> images) {
            mImagesProgress.setVisibility(View.GONE);
            mImageRecycler.setVisibility(View.VISIBLE);
            //mImagesErrorView.setVisibility(View.GONE);
            mImages = images;
            updateAdapter(mImages);
            Log.d(TAG, "onPostExcute");
        }

        @Override
        protected ArrayList<MyImage> doInBackground(Void... params) {
            //databaseAccess.open();
            return databaseAccess.getImagesFromDb();
        }
    }*/

    private void showFeatured() {

        //updateAdapter(mApi.filterFeatured(mImages));
    }

    private void showCategory(int category) {
        if( category == MainActivity.Category.ALL.id)
            updateAdapter(mImages);
        else
            updateAdapter(mApi.filterCategory(mImages, category));
    }

    private void showLoved() {
        updateAdapter(mApi.filterLoved(mImages, 1));
    }

    private Observer<ArrayList<MyImage>> observer = new Observer<ArrayList<MyImage>>() {
        @Override
        public void onNext(final ArrayList<MyImage> images) {
            databaseAccess.insertImage(images);
        }

        @Override
        public void onCompleted() {
            mImages = databaseAccess.getImagesFromDb();
            int imagesUpNum= mImages.size() - imagesNum;
            imagesNum= mImages.size();
            toolbar = (Toolbar) getActivity().findViewById(R.id.activity_main_toolbar);
            String text;

            if(imagesUpNum == 0){
                text="nothing new";
            } else {
                text= imagesUpNum + " new photo(s)";
                if(allFilter == MainActivity.Category.ALL.id )
                    updateAdapter(mImages);
                else
                    updateAdapter(mApi.filterCategory(mImages, allFilter));
                mImageRecycler.smoothScrollToPosition(0);
            }

            if (ImagesFragment.this.getActivity() instanceof MainActivity) {
                ((MainActivity) ImagesFragment.this.getActivity()).setCategoryCount(mImages);
            }
            mRefreshLayout.setRefreshing(false);
            Toast toast=  Toast.makeText(getActivity(), text, Toast.LENGTH_LONG);
            toast.setGravity(Gravity.TOP, 0, toolbar.getHeight()+20);
            toast.show();
        }

        @Override
        public void onError(final Throwable error) {
            mImages = databaseAccess.getImagesFromDb();
            if(allFilter == MainActivity.Category.ALL.id )
                updateAdapter(mImages);
            else
                updateAdapter(mApi.filterCategory(mImages, allFilter));

            Log.d(TAG, "message detail " + error.toString());
            if (ImagesFragment.this.getActivity() instanceof MainActivity) {
                ((MainActivity) ImagesFragment.this.getActivity()).setCategoryCount(mImages);
            }
            mRefreshLayout.setRefreshing(false);
        }
    };

    private OnItemClickListener recyclerRowClickListener = new OnItemClickListener() {

        @Override
        public void onClick(View v, int position) {

            MyImage selectedImage = mCurrentImages.get(position);

            Intent detailIntent = new Intent(getActivity(), DetailActivity.class);
            detailIntent.putExtra("position", position);
            detailIntent.putExtra("selected_image", selectedImage);

            if (selectedImage.getSwatch() != null) {
                detailIntent.putExtra("swatch_title_text_color", selectedImage.getSwatch().getTitleTextColor());
                detailIntent.putExtra("swatch_rgb", selectedImage.getSwatch().getRgb());
            }

            ImageView coverImage = (ImageView) v.findViewById(com.binhnv.hdsplash.R.id.item_image_img);
            if (coverImage == null) {
                coverImage = (ImageView) ((View) v.getParent()).findViewById(com.binhnv.hdsplash.R.id.item_image_img);
            }

            if (Build.VERSION.SDK_INT >= 21) {
                if (coverImage.getParent() != null) {
                    ((ViewGroup) coverImage.getParent()).setTransitionGroup(false);
                }
            }

            if (coverImage != null && coverImage.getDrawable() != null) {
                Bitmap bitmap = ((BitmapDrawable) coverImage.getDrawable()).getBitmap(); //ew
                if (bitmap != null && !bitmap.isRecycled()) {
                    photoCache.put(position, bitmap);

                    // Setup the transition to the detail activity
                    ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(getActivity(), coverImage, "cover");

                    startActivity(detailIntent, options.toBundle());
                }
            }
        }
    };

    private RecyclerView.OnScrollListener recyclerScrollListener = new RecyclerView.OnScrollListener() {
        public int lastDy;
        public boolean flag;

        @Override
        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {

            //mRefreshLayout.setEnabled(gridLayoutManager.findFirstCompletelyVisibleItemPosition() == 0);
            /*super.onScrolled(recyclerView, dx, dy);
            // toolbar = (Toolbar) getActivity().findViewById(R.id.activity_main_toolbar);

            if (toolbar == null)
                throw new IllegalStateException("BooksFragment has not a reference of the main toolbar");

            // Is scrolling up
            if (dy > 10) {

                if (!flag) {

                    showToolbar();
                    flag = true;
                }

                // is scrolling down
            } else if (dy < -10) {

                if (flag) {

                    hideToolbar();
                    flag = false;
                }
            }

            lastDy = dy;*/
        }
    };

    private void showToolbar() {

        toolbar.startAnimation(AnimationUtils.loadAnimation(getActivity(),
                R.anim.translate_up_off));
    }

    private void hideToolbar() {

        toolbar.startAnimation(AnimationUtils.loadAnimation(getActivity(),
                R.anim.translate_up_on));
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if(id == R.id.layout){
            layoutType= (layoutType ==1) ? 0 : 1 ;
            SharedPreferences.Editor editor = CustomApplication.sharedPreferences.edit();
            editor.putInt(getString(R.string.layout_type), layoutType).commit();
            layoutManager= (layoutType == 1) ? gridLayoutManager : staggeredGridLayoutManager;
            mImageRecycler.setLayoutManager(layoutManager);
        }

        if (id == com.binhnv.hdsplash.R.id.action_shuffle) {
            if (mImages != null) {
                //we don't want to shuffle the original list
                ArrayList<MyImage> shuffled = new ArrayList<MyImage>(mImages);
                Collections.shuffle(shuffled);
                mImageAdapter.updateData(shuffled);
                updateAdapter(shuffled);
            }
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * a small helper class to update the adapter
     *
     * @param images
     */
    private void updateAdapter(final ArrayList<MyImage> images) {
        mCurrentImages = images;
        mImageAdapter.updateData(mCurrentImages);
        //mImageRecycler.scrollToPosition(0);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Log.d("frag", "onDestroyView");
        if (mImages != null) {
            Log.d("frag", "onDestroyView - " + mImages.size());
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d("frag", "onDestroy");
    }

    public class SpacesItemDecoration extends RecyclerView.ItemDecoration {
        private final int mSpace;
        public SpacesItemDecoration(int space) {
            this.mSpace = space;
        }
        @Override
        public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
            outRect.left = mSpace;
            outRect.right = mSpace;
            outRect.bottom = mSpace;
            // Add top margin only for the first item to avoid double space between items
            if (parent.getChildAdapterPosition(view) == 0)
                outRect.top = mSpace;
        }
    }
}
