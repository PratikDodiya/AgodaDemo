package com.agodademo.adapter;

import android.app.Activity;
import android.content.Context;
import android.graphics.Point;
import android.support.v7.widget.RecyclerView;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.agodademo.R;
import com.agodademo.helper.CustomRatingBar;
import com.agodademo.interfaces.RecyclerClickListener;
import com.agodademo.model.SampleModel;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Pratik on 12/12/18.
 */
public class ListAdapter extends RecyclerView.Adapter<ListAdapter.ViewHolder> {

    private ArrayList<SampleModel> arrayList;
    private Context context;
    private RecyclerClickListener clickListener;
    private int mScreenWidth, mScreenHeight;

    public ListAdapter(Activity context, ArrayList<SampleModel> arrayList) {
        super();
        this.context = context;
        this.arrayList = arrayList;

        Display display = context.getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        mScreenWidth = size.x;
        mScreenHeight = size.y;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.adapter_list, viewGroup, false);
        ViewHolder viewHolder = new ViewHolder(v);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, final int i) {


        SampleModel model = arrayList.get(i);
        viewHolder.tvName.setText(model.getName());

//        viewHolder.llItem.getLayoutParams().width = (mScreenWidth * 90) / 100;

        // Last Divider line hide
        /*if (i == arrayList.size() - 1) {
            viewHolder.viewSeparator.setVisibility(View.INVISIBLE);
        } else {
            viewHolder.viewSeparator.setVisibility(View.VISIBLE);
        }*/

    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.imgHotel)
        ImageView imgHotel;
        @BindView(R.id.tvName)
        TextView tvName;
        @BindView(R.id.ratingBar)
        CustomRatingBar ratingBar;
        @BindView(R.id.llItem)
        LinearLayout llItem;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        /*@OnClick({R.id.llItemMessage})
        void onClick(View view) {

            switch (view.getId()) {
                case R.id.llItemMessage:
                    clickListener.onClickEvent(view, getAdapterPosition());
                    break;
            }
        }*/
    }

    public void setClickListener(RecyclerClickListener recyclerClickListener) {
        clickListener = recyclerClickListener;
    }

    public ArrayList<SampleModel> getList() {
        return arrayList;
    }
}
