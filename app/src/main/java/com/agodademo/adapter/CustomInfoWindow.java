package com.agodademo.adapter;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.widget.TextView;

import com.agodademo.R;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;

/**
 * Created by Pratik on 21/3/18.
 */

public class CustomInfoWindow implements GoogleMap.InfoWindowAdapter {

    private Context context;

    public CustomInfoWindow(Context ctx){
        context = ctx;
    }

    @Override
    public View getInfoWindow(Marker marker) {
        View view = ((Activity)context).getLayoutInflater()
                .inflate(R.layout.layout_custom_info_window, null);

        TextView txtName = (TextView) view.findViewById(R.id.txtName);
        txtName.setText(marker.getTitle());

        return view;
    }

    @Override
    public View getInfoContents(Marker marker) {
        return null;
    }
}
