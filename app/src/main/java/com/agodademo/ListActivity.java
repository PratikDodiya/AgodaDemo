package com.agodademo;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.agodademo.interfaces.RecyclerClickListener;
import com.agodademo.adapter.ListAdapter;
import com.agodademo.model.SampleModel;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ListActivity extends AppCompatActivity implements RecyclerClickListener, OnMapReadyCallback {

    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;


    private GoogleMap mMap;
    private LinearLayoutManager mLayoutManager = null;
    private ArrayList<SampleModel> arrayList = null;
    private ListAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);
        ButterKnife.bind(this);

        initializeViews();
    }

    private void initializeViews() {

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        //Set Header
//        tbTxtTitle.setText("List");


        setDummyList();

    }

    private void setDummyList() {

        arrayList = new ArrayList<>();

        SampleModel model;
        for (int i = 0; i < 15; i++) {
            model = new SampleModel();
            model.setName("Test " + (i + 1));
            arrayList.add(model);
        }

        setUsersListAdapter(arrayList);
    }


    private void setUsersListAdapter(ArrayList<SampleModel> arrayList) {

        adapter = new ListAdapter(ListActivity.this, arrayList);

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

    @Override
    public void onClickEvent(View view, int position) {

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Add a marker in Ahmedabad, and move the camera.
        LatLng latLng = new LatLng(23.066343, 72.532043);
        mMap.addMarker(new MarkerOptions().position(latLng).title("Marker in Ahmedabad"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
        mMap.animateCamera(CameraUpdateFactory.zoomTo(16));

    }
}
