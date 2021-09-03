package com.agodademo.interfaces;

import android.view.View;

/**
 * Created by Pratik on 13/12/18.
 */
public abstract class VisibleToggleClickListener implements View.OnClickListener {

    private boolean mVisible;

    @Override
    public void onClick(View v) {
        mVisible = !mVisible;
        changeVisibility(mVisible);
    }

    protected abstract void changeVisibility(boolean visible);

}