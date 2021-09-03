package com.agodademo;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.OvalShape;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomSheetBehavior;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.PagerSnapHelper;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SnapHelper;
import android.text.SpannableStringBuilder;
import android.text.style.StyleSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.agodademo.adapter.CustomInfoWindow;
import com.agodademo.adapter.ListAdapter;
import com.agodademo.interfaces.RecyclerClickListener;
import com.agodademo.model.SampleModel;
import com.agodademo.utils.LogUtil;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.maps.android.ui.IconGenerator;
import com.google.maps.android.ui.SquareTextView;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static android.graphics.Typeface.BOLD;
import static android.graphics.Typeface.ITALIC;
import static android.text.Spanned.SPAN_EXCLUSIVE_EXCLUSIVE;

public class MapActivity extends AppCompatActivity implements RecyclerClickListener, OnMapReadyCallback, GoogleMap.OnMapClickListener,
        GoogleMap.OnMarkerClickListener {

    private static final String TAG = MapActivity.class.getSimpleName();

    @BindView(R.id.btnList)
    Button btnList;
    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;
    @BindView(R.id.nestedScrollViewBottomSheet)
    NestedScrollView nestedScrollViewBottomSheet;
    @BindView(R.id.txtFilter)
    TextView txtFilter;
    @BindView(R.id.llBottomFilter)
    LinearLayout llBottomFilter;
    @BindView(R.id.llMapList)
    LinearLayout llMapList;
    @BindView(R.id.recyclerViewMap)
    RecyclerView recyclerViewMap;


    private GoogleMap mMap;
    private LinearLayoutManager mLayoutManager = null;
    private LinearLayoutManager mLayoutManagerMap = null;
    private ArrayList<SampleModel> arrayList = null;
    private ListAdapter adapter;
    private BottomSheetBehavior bottomSheetBehavior;
    private boolean doNotMoveCameraToCenterMarker = true;
    private Marker previousMarker = null;
    private float mDensity;
    private ShapeDrawable mColoredCircleBackground;
    private int overallXScroll = 0, firstItemPosition = 0;
    private int previousMarkerPosition = -1;
    ArrayList<Marker> arrayListMarker = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);
        ButterKnife.bind(this);

        initializeViews();
    }

    private void initializeViews() {

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        //Set Header
//        tbTxtTitle.setText("Map");

        mDensity = getResources().getDisplayMetrics().density;
        initBottomSheet();
        setDummyList(1);

    }

    @OnClick({R.id.btnList, R.id.llBottomFilter})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btnList:
                break;
            case R.id.llBottomFilter:

                if (txtFilter.getText().toString().equals("Filter") || txtFilter.getText().toString().equals("Filter | Map")) {

                    AnimateSlideDown(R.id.nestedScrollViewBottomSheet);
                    nestedScrollViewBottomSheet.setVisibility(View.GONE);
                    txtFilter.setText("Filter | List");

                    AnimateSlideUp(R.id.llBottomFilter);
                    AnimateSlideUp(R.id.llMapList);
                    llMapList.setVisibility(View.VISIBLE);
                } else {

                    setBottomSheetCollapsed();
                /*Animation bottomUp = AnimationUtils.loadAnimation(MapActivity.this, R.anim.slide_up);
                ViewGroup hiddenPanel = (ViewGroup) findViewById(R.id.nestedScrollViewBottomSheet);
                hiddenPanel.startAnimation(bottomUp);
                hiddenPanel.setVisibility(View.VISIBLE);*/
                    AnimateSlideUp(R.id.nestedScrollViewBottomSheet);
                    AnimateSlideDown(R.id.llBottomFilter);
                    AnimateSlideDown(R.id.llMapList);
//                AnimateSlideDown(R.id.llMapList, false);
//                llBottomFilter.animate().translationY(105f);
                    nestedScrollViewBottomSheet.setVisibility(View.VISIBLE);
//                llMapList.setVisibility(View.GONE);
                    txtFilter.setText("Filter");
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            llMapList.setVisibility(View.GONE);
                        }
                    }, 200);


                    Log.e(TAG, ":: call :: view.getRotationY(): " + view.getRotationY() + ":::view.getRotationX():" + view.getRotationX() + ":::view.getBottom():" + view.getBottom());
                    TranslateAnimation animate = new TranslateAnimation(0, 0, 0, view.getRotationY());
                    animate.setDuration(2000);
                    animate.setFillAfter(true);
                    view.startAnimation(animate);
//        view.setVisibility(View.GONE);
                }
                break;
        }
    }

    private void initBottomSheet() {
        llMapList.setVisibility(View.GONE);
        bottomSheetBehavior = BottomSheetBehavior.from(nestedScrollViewBottomSheet);
//        setBottomSheetExpanded();

        bottomSheetBehavior.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View view, int newState) {
                switch (newState) {
                    case BottomSheetBehavior.STATE_HIDDEN:
                        Log.e(TAG, ":: STATE_HIDDEN :: " + BottomSheetBehavior.STATE_HIDDEN);
                        break;
                    case BottomSheetBehavior.STATE_EXPANDED: {
                        Log.e(TAG, ":: STATE_EXPANDED :: " + BottomSheetBehavior.STATE_EXPANDED);
//                        txtFilter.setText("Filter | Map");
                    }
                    break;
                    case BottomSheetBehavior.STATE_COLLAPSED: {
                        Log.e(TAG, ":: STATE_COLLAPSED :: " + BottomSheetBehavior.STATE_COLLAPSED);
//                        txtFilter.setText("Filter");
                    }
                    break;
                    case BottomSheetBehavior.STATE_DRAGGING:
                        Log.e(TAG, ":: STATE_DRAGGING :: " + BottomSheetBehavior.STATE_DRAGGING);
                        break;
                    case BottomSheetBehavior.STATE_SETTLING:
                        Log.e(TAG, ":: STATE_SETTLING :: " + BottomSheetBehavior.STATE_SETTLING);
                        break;
                }
            }

            @Override
            public void onSlide(@NonNull View view, float slideOffset) {
                Log.e(TAG, ":: onSlide - slideOffset:: " + slideOffset);

                if (slideOffset >= 1) {
                    txtFilter.setText("Filter | Map");
                } else {
                    txtFilter.setText("Filter");
                }

            }
        });
    }

    private void setDummyList(int type) {

        arrayList = new ArrayList<>();
        SampleModel model = new SampleModel();
        model.setName("Deer Circle");
        model.setRatings("3");
        model.setYear("");
        model.setCategory("Sports");
        model.setArea("Ahmedabad");
        model.setLatitude(23.012727);
        model.setLongitude(72.514979);
        arrayList.add(model);

        model = new SampleModel();
        model.setName("Domino's Pizza");
        model.setRatings("3");
        model.setYear("");
        model.setCategory("Sports");
        model.setArea("Ahmedabad");
        model.setLatitude(23.011937);
        model.setLongitude(72.511135);
        arrayList.add(model);

        model = new SampleModel();
        model.setName("Jay Bhavani");
        model.setRatings("3");
        model.setYear("");
        model.setCategory("Sports");
        model.setArea("Ahmedabad");
        model.setLatitude(23.012658);
        model.setLongitude(72.518045);
        arrayList.add(model);

        model = new SampleModel();
        model.setName("HP Petrol Pump");
        model.setRatings("3");
        model.setYear("");
        model.setCategory("Sports");
        model.setArea("Ahmedabad");
        model.setLatitude(23.017432);
        model.setLongitude(72.518650);
        arrayList.add(model);

        model = new SampleModel();
        model.setName("Corporate Road");
        model.setRatings("4");
        model.setYear("");
        model.setCategory("Sports");
        model.setArea("Ahmedabad");
        model.setLatitude(23.009147);
        model.setLongitude(72.506799);
        arrayList.add(model);

        model = new SampleModel();
        model.setName("Pizza Hut");
        model.setRatings("5");
        model.setYear("");
        model.setCategory("Sports");
        model.setArea("Ahmedabad");
        model.setLatitude(23.012208);
        model.setLongitude(72.510861);
        arrayList.add(model);


//        if (type == 1)
        setUsersListAdapter(arrayList);
//        else
        setMapListAdapter(arrayList);
    }


    private void setUsersListAdapter(ArrayList<SampleModel> arrayList) {

        adapter = new ListAdapter(MapActivity.this, arrayList);

        mLayoutManager = new LinearLayoutManager(this);
        mLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setNestedScrollingEnabled(false);
        recyclerView.setHasFixedSize(true);

        //Cache
        recyclerView.setItemViewCacheSize(20);
        recyclerView.setDrawingCacheEnabled(true);
        recyclerView.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);

        adapter.setClickListener(this);
        recyclerView.setAdapter(adapter);
    }


    private void setMapListAdapter(final ArrayList<SampleModel> arrayList) {

        adapter = new ListAdapter(MapActivity.this, arrayList);

        mLayoutManager = new LinearLayoutManager(this);
        mLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        recyclerViewMap.setLayoutManager(mLayoutManager);
        recyclerViewMap.setNestedScrollingEnabled(false);
        recyclerViewMap.setHasFixedSize(true);

        //Cache
        recyclerViewMap.setItemViewCacheSize(20);
        recyclerViewMap.setDrawingCacheEnabled(true);
        recyclerViewMap.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);

        adapter.setClickListener(this);
        recyclerViewMap.setAdapter(adapter);

        SnapHelper snapHelper = new PagerSnapHelper();
        snapHelper.attachToRecyclerView(recyclerViewMap);

        recyclerViewMap.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);

                LogUtil.e(TAG, "===========onScrollStateChanged===========");
                LogUtil.e(TAG, "findFirstVisibleItemPosition  = " + mLayoutManager.findFirstVisibleItemPosition() + ":::" + mLayoutManager.findFirstCompletelyVisibleItemPosition());

                int position = mLayoutManager.findFirstCompletelyVisibleItemPosition();

                if (position >= 0) {
//                    LogUtil.e(TAG, "getLatitude  = " + adapter.getList().get(mLayoutManager.findFirstCompletelyVisibleItemPosition()).getLatitude());

                    LatLng latLng = new LatLng(adapter.getList().get(mLayoutManager.findFirstCompletelyVisibleItemPosition()).getLatitude(),
                            adapter.getList().get(mLayoutManager.findFirstCompletelyVisibleItemPosition()).getLongitude());
                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 16));

                    Marker marker = arrayListMarker.get(mLayoutManager.findFirstCompletelyVisibleItemPosition());
                    showVisibleMarkers(marker, arrayList, false);
                }
            }

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                LogUtil.e(TAG, "===========onScrolled===========");
            }
        });

        /*recyclerViewMap.setOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                overallXScroll = overallXScroll + dx;
//                LogUtil.i(TAG,"overall X  = " + overallXScroll);

                View firstVisibleItem = mLayoutManager.findViewByPosition(mLayoutManager.findFirstVisibleItemPosition());

                int leftScrollXCalculated = 0;
                if (firstItemPosition == 0){
                    //if first item, get width of headerview (getLeft() < 0, that's why I Use Math.abs())
                    leftScrollXCalculated = Math.abs(firstVisibleItem.getLeft());
                }
                else{

                    //X-Position = Gap to the right + Number of cells * width - cell offset of current first visible item
                    //(mHeaderItemWidth includes already width of one cell, that's why I have to subtract it again)
                    leftScrollXCalculated = (720 - 720) + firstItemPosition  * 720 + firstVisibleItem.getLeft();
                }

//                LogUtil.i(TAG,"calculated X to left = " + leftScrollXCalculated + "::::firstVisibleItem = "+firstVisibleItem.getLeft());

            }
        });*/
    }

    @Override
    public void onClickEvent(View view, int position) {

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;


        // Add a marker in Ahmedabad, and move the camera.
        /*LatLng latLng = new LatLng(23.012727, 72.514979);
        mMap.addMarker(new MarkerOptions().position(latLng).title("Deer Circle").snippet("0"));

        latLng = new LatLng(23.011937, 72.511135);
        mMap.addMarker(new MarkerOptions().position(latLng).title("Domino's Pizza").snippet("1"));

        latLng = new LatLng(23.012658, 72.518045);
        mMap.addMarker(new MarkerOptions().position(latLng).title("Jay Bhavani").snippet("2"));

        latLng = new LatLng(23.017432, 72.518650);
        mMap.addMarker(new MarkerOptions().position(latLng).title("HP Petrol Pump").snippet("3"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
        mMap.animateCamera(CameraUpdateFactory.zoomTo(16));*/

        setMarkers(arrayList);

        mMap.setOnMapClickListener(this);
        mMap.setOnMarkerClickListener(this);
    }

    @Override
    public void onMapClick(LatLng latLng) {

        Log.e(TAG, ":: call :: setBottomSheetCollapsed.. : " + bottomSheetBehavior.getState());
//        setBottomSheetCollapsed();

        /*new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                setBottomSheetCollapsed();
            }
        }, 200);*/

        /*Animation bottomUp = AnimationUtils.loadAnimation(MapActivity.this, R.anim.slide_down);
        ViewGroup hiddenPanel = (ViewGroup) findViewById(R.id.nestedScrollViewBottomSheet);
        hiddenPanel.startAnimation(bottomUp);*/

        if (txtFilter.getText().toString().equals("Filter") || txtFilter.getText().toString().equals("Filter | Map")) {

            AnimateSlideDown(R.id.nestedScrollViewBottomSheet);
            nestedScrollViewBottomSheet.setVisibility(View.GONE);
            txtFilter.setText("Filter | List");

            AnimateSlideUp(R.id.llBottomFilter);
            AnimateSlideUp(R.id.llMapList);
            llMapList.setVisibility(View.VISIBLE);
        } else {

        }

        LatLngBounds.Builder builder = new LatLngBounds.Builder();

//the include method will calculate the min and max bound.
        builder.include(arrayListMarker.get(0).getPosition());
        builder.include(arrayListMarker.get(1).getPosition());
        builder.include(arrayListMarker.get(2).getPosition());
        builder.include(arrayListMarker.get(3).getPosition());

        LatLngBounds bounds = builder.build();

        int width = getResources().getDisplayMetrics().widthPixels;
        int height = getResources().getDisplayMetrics().heightPixels;
        int padding = (int) (width * 0.10);

        CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, width, height, padding);

        mMap.animateCamera(cu);

        Log.e(TAG, ":: LatLngBounds : width:" + width + ":: height:" + height + ":: padding:" + padding);

        // Google logo padding from bottom
        mMap.setPadding(0, 0, 0, (int) getResources().getDimension(R.dimen._100sdp));
//        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
        /*if (bottomSheetBehavior.getState() != BottomSheetBehavior.STATE_COLLAPSED) {
//            nestedScrollViewBottomSheet.setVisibility(View.VISIBLE);
            bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
            Log.e(TAG, ":: call :: setBottomSheetCollapsed..");
        }*/
//        if(bottomSheetBehavior.getState() == BottomSheetBehavior.STATE_COLLAPSED) {
////            bottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
//
//        }
    }

    @Override
    public boolean onMarkerClick(Marker marker) {

        Log.e(TAG, ":: onMarkerClick :: " + marker.getSnippet() + ":: pos ::" + marker.getPosition());
        // First option
        /*marker.showInfoWindow();
        recyclerViewMap.scrollToPosition(Integer.parseInt(marker.getSnippet()));*/

        // Second option
        /*IconGenerator iconGenerator = new IconGenerator(MapActivity.this);

        LayoutInflater myInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        if (previousMarker != null) {
            View view = myInflater.inflate(R.layout.layout_default_info_window, null, false);
            iconGenerator.setContentView(view);

            TextView textView = view.findViewById(R.id.txtName);
            textView.setText("Test" + 1);

            Bitmap bmp1 = iconGenerator.makeIcon("Default");
            previousMarker.setIcon(BitmapDescriptorFactory.fromBitmap(bmp1));
        }

//        LayoutInflater myInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = myInflater.inflate(R.layout.layout_pressed_info_window, null, false);

        iconGenerator.setContentView(view);
//        iconGenerator.setStyle(IconGenerator.STYLE_RED);

        TextView textView = view.findViewById(R.id.txtName);
        textView.setText("Test" + 1);


        // If set background "null" then it takes background from layout file
//        iconGenerator.setBackground(null);
//        iconGenerator.setTextAppearance(R.style.iconGenText);
        Bitmap bmp = iconGenerator.makeIcon("Test" + 1);
        marker.setIcon(BitmapDescriptorFactory.fromBitmap(bmp));
        previousMarker = marker;*/

        showVisibleMarkers(marker, arrayList, true);

        return doNotMoveCameraToCenterMarker;
    }

    private LayerDrawable makeClusterBackground() {
        mColoredCircleBackground = new ShapeDrawable(new OvalShape());
        ShapeDrawable outline = new ShapeDrawable(new OvalShape());
        outline.getPaint().setColor(0x80ffffff); // Transparent white.
        LayerDrawable background = new LayerDrawable(new Drawable[]{outline, mColoredCircleBackground});
        int strokeWidth = (int) (mDensity * 3);
        background.setLayerInset(1, strokeWidth, strokeWidth, strokeWidth, strokeWidth);
        return background;
    }

    private SquareTextView makeSquareTextView(Context context) {
        SquareTextView squareTextView = new SquareTextView(context);
        ViewGroup.LayoutParams layoutParams = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        squareTextView.setLayoutParams(layoutParams);
        squareTextView.setId(R.id.text);
        int twelveDpi = (int) (12 * mDensity);
        squareTextView.setPadding(twelveDpi, twelveDpi, twelveDpi, twelveDpi);
        return squareTextView;
    }


    private void setBottomSheetExpanded() {
        if (bottomSheetBehavior.getState() != BottomSheetBehavior.STATE_EXPANDED) {
//            llAddToPost.setVisibility(View.GONE);
            bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
//            LogUtil.e(TAG, ":: call :: setBottomSheetExpanded..");
        }
    }


    private void setBottomSheetCollapsed() {
//        bottomSheetBehavior.setHideable(true);
//        bottomSheetBehavior.setPeekHeight(0);
        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);

//        if (bottomSheetBehavior.getState() != BottomSheetBehavior.STATE_COLLAPSED) {
//            llAddToPost.setVisibility(View.VISIBLE);
//            bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
//            LogUtil.e(TAG, ":: call :: setBottomSheetCollapsed..");
//        }
    }

    /*void startSettlingAnimation(View child, int state) {
        int top;
        if (state == BottomSheetBehavior.STATE_COLLAPSED) {
            top = mCollapsedOffset;
        } else if (state == BottomSheetBehavior.STATE_HALF_EXPANDED) {
            top = mHalfExpandedOffset;
        } else if (state == BottomSheetBehavior.STATE_EXPANDED) {
            top = getExpandedOffset();
        } else if (mHideable && state == BottomSheetBehavior.STATE_HIDDEN) {
            top = mParentHeight;
        } else {
            throw new IllegalArgumentException("Illegal state argument: " + state);
        }
        if (mViewDragHelper.smoothSlideViewTo(child, child.getLeft(), top)) {
            setStateInternal(BottomSheetBehavior.STATE_SETTLING);
            ViewCompat.postOnAnimation(child, new SettleRunnable(child, state));
        } else {
            setStateInternal(state);
        }
    }*/

    private void AnimateSlideUp(int id) {
        Animation slideUp = AnimationUtils.loadAnimation(MapActivity.this, R.anim.slide_up);
        ViewGroup showPanel = findViewById(id);
        showPanel.startAnimation(slideUp);
    }

    private void AnimateSlideDown(int id) {
        Animation slideDown = AnimationUtils.loadAnimation(MapActivity.this, R.anim.slide_down);
        ViewGroup hidePanel = findViewById(id);
        hidePanel.startAnimation(slideDown);
    }

    private void AnimateSlideDown(int id, boolean isVisible) {
        Animation slideDown = AnimationUtils.loadAnimation(MapActivity.this, R.anim.slide_down);
        ViewGroup hidePanel = findViewById(id);
        hidePanel.startAnimation(slideDown);

        if (isVisible)
            hidePanel.setVisibility(View.VISIBLE);
        else
            hidePanel.setVisibility(View.GONE);
    }


    private void showVisibleMarkers(Marker marker, ArrayList<SampleModel> arrayList, boolean isScroll) {

        int position = Integer.parseInt(marker.getSnippet());
        IconGenerator iconGenerator = new IconGenerator(MapActivity.this);

        LayoutInflater myInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        if (previousMarker != null) {
            iconGenerator.setStyle(IconGenerator.STYLE_BLUE);
            View view = myInflater.inflate(R.layout.layout_default_info_window, null, false);
            iconGenerator.setContentView(view);

            TextView textView = view.findViewById(R.id.txtName);
            textView.setText(arrayList.get(previousMarkerPosition).getName());

            Bitmap bmp1 = iconGenerator.makeIcon("");
            previousMarker.setIcon(BitmapDescriptorFactory.fromBitmap(bmp1));
        }

//        LayoutInflater myInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = myInflater.inflate(R.layout.layout_pressed_info_window, null, false);

        iconGenerator.setContentView(view);
        iconGenerator.setStyle(IconGenerator.STYLE_RED);

        TextView textView = view.findViewById(R.id.txtName);
        textView.setText(arrayList.get(position).getName());


        // If set background "null" then it takes background from layout file
//        iconGenerator.setBackground(null);
//        iconGenerator.setTextAppearance(R.style.iconGenText);
        Bitmap bmp = iconGenerator.makeIcon("Test" + 1);
        marker.setIcon(BitmapDescriptorFactory.fromBitmap(bmp));
        previousMarker = marker;
        previousMarkerPosition = position;

        if (isScroll)
            recyclerViewMap.smoothScrollToPosition(Integer.parseInt(marker.getSnippet()));
    }

    public void showMarkers(ArrayList<SampleModel> mArrayList) {

        ArrayList points = new ArrayList();
        LatLng latLng = null; //, latLngOrigin;
//        int mArraySize = mArrayList.size();
//        ArrayList<SampleModel> arrayListDayMarker = new ArrayList<>();
//        SampleModel dayMapMarker;

        /*String mDay = "";
        int count = 0;
        for (int i=0; i<mArraySize; i++) {

            CustomInfoWindow customInfoWindow = new CustomInfoWindow(this);
            mMap.setInfoWindowAdapter(customInfoWindow);


            if (!mArrayList.get(i).isDuplicate()) {

                mDay = "";
                for (int j = i + 1; j < mArraySize; j++) {

                    if (!mArrayList.get(j).isDuplicate()) {
                        if (mArrayList.get(i).getCityName().trim().equals(mArrayList.get(j).getCityName()) && (i != j)) {

                            mArrayList.get(i).setDuplicate(true);
                            mArrayList.get(j).setDuplicate(true);

                            if (mDay.equals("")) {
                                mDay = mArrayList.get(i).getDay().trim() + ", " + mArrayList.get(j).getDay();
                            } else {
                                mDay = mDay + ", " + mArrayList.get(j).getDay();
                            }
                        } else {

                            if (mDay.equals("")) {
                                mDay = mArrayList.get(i).getDay().trim();
                            }
                        }
                    }
                }
                if (mDay.equals("")) {
                    mDay = mArrayList.get(i).getDay().trim();
                }

                dayMapMarker = new SampleModel();
                dayMapMarker.setName(mDay);
                dayMapMarker.setLatitude(mArrayList.get(i).getLatitude());
                dayMapMarker.setLongitude(mArrayList.get(i).getLongitude());
                arrayListDayMarker.add(dayMapMarker);
            }
        }*/

        ArrayList<Marker> arrayListDayMarker = new ArrayList<>();
        for (int k = 0; k < mArrayList.size(); k++) {

            CustomInfoWindow customInfoWindow = new CustomInfoWindow(this);
            mMap.setInfoWindowAdapter(customInfoWindow);
            latLng = new LatLng(mArrayList.get(k).getLatitude(), mArrayList.get(k).getLongitude());

            Marker marker = mMap.addMarker(new MarkerOptions().position(latLng)
                    .title(mArrayList.get(k).getName())
                    .icon(BitmapDescriptorFactory.fromResource(android.R.drawable.ic_delete)));

            arrayListDayMarker.add(marker);
            // Only first position to displayed info window
            if (k == 0) {
//                marker.showInfoWindow();
                mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 17));
            }

            arrayListDayMarker.get(k).showInfoWindow();
            points.add(latLng);
        }
    }

    private void setMarkers(ArrayList<SampleModel> arrayList) {
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(23.012727, 72.514979), 16));

        IconGenerator iconGenerator = new IconGenerator(this);
        iconGenerator.setStyle(IconGenerator.STYLE_RED); // Set first position with RED color Marker
        iconGenerator.setTextAppearance(R.style.iconGenText); // Set first position with custom style
        addIcon(iconGenerator, "Deer Circle", new LatLng(23.012727, 72.514979), "0");

        iconGenerator.setStyle(IconGenerator.STYLE_BLUE);
        addIcon(iconGenerator, "Domino's Pizza", new LatLng(23.011937, 72.511135), "1");

//        iconGenerator.setRotation(90);
        iconGenerator.setStyle(IconGenerator.STYLE_BLUE);
        addIcon(iconGenerator, "Jay Bhavani", new LatLng(23.012658, 72.518045), "2");

//        iconGenerator.setContentRotation(-90);
        iconGenerator.setStyle(IconGenerator.STYLE_BLUE);
        addIcon(iconGenerator, "HP Petrol Pump", new LatLng(23.017432, 72.518650), "3");

        iconGenerator.setStyle(IconGenerator.STYLE_BLUE);
        addIcon(iconGenerator, "Corporate Road", new LatLng(23.009147, 72.506799), "4");

//        iconGenerator.setRotation(0);
//        iconGenerator.setContentRotation(0);
        iconGenerator.setStyle(IconGenerator.STYLE_BLUE);
        addIcon(iconGenerator, "Pizza Hut", new LatLng(23.012208, 72.510861), "5");
//        addIcon(iconGenerator, makeCharSequence(), new LatLng(-33.77720, 151.12412));
    }

    private void addIcon(IconGenerator iconGenerator, CharSequence text, LatLng position, String snippetText) {
        MarkerOptions markerOptions = new MarkerOptions().
                icon(BitmapDescriptorFactory.fromBitmap(iconGenerator.makeIcon(text))).
                snippet(snippetText).
                position(position).
                anchor(iconGenerator.getAnchorU(), iconGenerator.getAnchorV());

        Marker marker = mMap.addMarker(markerOptions);
        arrayListMarker.add(marker);

        // Set first position's marker
        if (snippetText.equals("0")) {
            previousMarker = marker;
            previousMarkerPosition = 0;
        }
    }

    private CharSequence makeCharSequence() {
        String prefix = "Mixing ";
        String suffix = "different fonts";
        String sequence = prefix + suffix;
        SpannableStringBuilder ssb = new SpannableStringBuilder(sequence);
        ssb.setSpan(new StyleSpan(ITALIC), 0, prefix.length(), SPAN_EXCLUSIVE_EXCLUSIVE);
        ssb.setSpan(new StyleSpan(BOLD), prefix.length(), sequence.length(), SPAN_EXCLUSIVE_EXCLUSIVE);
        return ssb;
    }
}
