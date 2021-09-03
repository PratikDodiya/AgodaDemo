package com.agodademo;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.agodademo.interfaces.VisibleToggleClickListener;
import com.transitionseverywhere.TransitionManager;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends AppCompatActivity {

    @BindView(R.id.txtMsg)
    TextView txtMsg;
    @BindView(R.id.btnSearch)
    Button btnSearch;
    @BindView(R.id.btnMap)
    Button btnMap;
    @BindView(R.id.txtFilter)
    TextView txtFilter;
    @BindView(R.id.llBottomFilter)
    LinearLayout llBottomFilter;
    @BindView(R.id.llMapList)
    LinearLayout llMapList;


    float amountToMoveRight = 100, amountToMoveDown = 500;
    boolean flag = false, flagBottom = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        final ViewGroup transitionsContainer = (ViewGroup) findViewById(R.id.transitions_container);
        final View button = transitionsContainer.findViewById(R.id.button);

        final TextView text = (TextView) transitionsContainer.findViewById(R.id.text);

        /*button.setOnClickListener(new View.OnClickListener() {

            boolean mToRightAnimation;

            @Override
            public void onClick(View v) {
                mToRightAnimation = !mToRightAnimation;

                Transition transition = new ChangeBounds();
                transition.setDuration(mToRightAnimation ? 700 : 300);
                transition.setInterpolator(mToRightAnimation ? new FastOutSlowInInterpolator() : new AccelerateInterpolator());
                transition.setStartDelay(mToRightAnimation ? 0 : 500);
                TransitionManager.beginDelayedTransition(transitionsContainer, transition);

                FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) button.getLayoutParams();
                params.gravity = mToRightAnimation ? (Gravity.RIGHT | Gravity.TOP) : (Gravity.LEFT | Gravity.TOP);
                button.setLayoutParams(params);
            }

        });*/

        transitionsContainer.findViewById(R.id.button).setOnClickListener(new VisibleToggleClickListener() {

            @Override
            protected void changeVisibility(boolean visible) {
                TransitionManager.beginDelayedTransition(transitionsContainer);
                // it is the same as
                // TransitionManager.beginDelayedTransition(transitionsContainer, new AutoTransition());
                // where AutoTransition is the set of Fade and ChangeBounds transitions
                text.setVisibility(visible ? View.VISIBLE : View.GONE);
            }

        });

        llMapList.setVisibility(View.VISIBLE);
    }

    @OnClick({R.id.btnSearch, R.id.btnMap, R.id.llBottomFilter})
    public void onViewClicked(View view) {


        switch (view.getId()) {
            case R.id.btnSearch:
                startActivity(new Intent(MainActivity.this, ListActivity.class));

//                view.animate().alpha(0.0f);
                Log.e("TAG", ":: call :: view.getTop(): " + view.getTop() + ":::view.getRotationX():" + view.getRotationX() + ":::view.getBottom():" + view.getBottom());
                if (flag) {
                    view.animate().translationY(200);
                    view.animate().alpha(1.0f);
                    flag = false;
                } else {
                    view.animate().translationY(0);

                    flag = true;
                }
                llBottomFilter.animate().translationY(0);
//                view.animate().translationY(0);
//                slideToBottom(view);

//                view.animate()
//                        .translationY(view.getHeight())
//                        .alpha(0.0f)
//                        .setDuration(300);

                /*TranslateAnimation anim = new TranslateAnimation(0, amountToMoveRight, 0, amountToMoveDown);
                anim.setDuration(1000);

                anim.setAnimationListener(new TranslateAnimation.AnimationListener() {

                    @Override
                    public void onAnimationStart(Animation animation) { }

                    @Override
                    public void onAnimationRepeat(Animation animation) { }

                    @Override
                    public void onAnimationEnd(Animation animation)
                    {
                        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams)btnSearch.getLayoutParams();
                        params.topMargin += amountToMoveDown;
                        params.leftMargin += amountToMoveRight;
                        btnSearch.setLayoutParams(params);
                    }
                });

                view.startAnimation(anim);*/

                break;

            case R.id.btnMap:
                startActivity(new Intent(MainActivity.this, MapActivity.class));

//                view.animate().translationX(btnSearch.getHeight());
//                view.animate().alpha(1.0f);
//                slideToTop(btnSearch);

                break;

            case R.id.llBottomFilter:
//                slideToTop(llBottomFilter);
//                slideToTop(llMapList);
                /*if (flagBottom) {
                    llMapList.setVisibility(View.GONE);
                    llMapList.animate().translationY(0);
                    flagBottom = false;
                    llBottomFilter.animate()
                            .translationY(100)
//                            .alpha(10.0f)
                            .setDuration(300)
                            .setListener(new AnimatorListenerAdapter() {
                                @Override
                                public void onAnimationEnd(Animator animation) {
                                    super.onAnimationEnd(animation);
                                    llBottomFilter.setVisibility(View.VISIBLE);
                                }
                            });
                } else {
                    llBottomFilter.animate().translationY(00);



                    llMapList.animate()
                            .translationY(300)
//                            .alpha(10.0f)
                            .setDuration(300)
                            .setListener(new AnimatorListenerAdapter() {
                                @Override
                                public void onAnimationEnd(Animator animation) {
                                    super.onAnimationEnd(animation);
                                    llMapList.setVisibility(View.VISIBLE);
                                }
                            });

                    flagBottom = true;
                }*/

                llMapList.animate()
                        .translationY(llMapList.getHeight())
//                            .alpha(10.0f)
                        .setDuration(300)
                        .setListener(new AnimatorListenerAdapter() {
                            @Override
                            public void onAnimationEnd(Animator animation) {
                                super.onAnimationEnd(animation);
                                llMapList.setVisibility(View.GONE);
                            }
                        });

                break;

            case R.id.llMapList:
                slideToBottom(llMapList);
                slideToBottom(llBottomFilter);
                break;
        }
    }

    public void slideToBottom(View view) {
        TranslateAnimation animate = new TranslateAnimation(0, 0, 0, view.getHeight());
        animate.setDuration(500);
        animate.setFillAfter(true);
        view.startAnimation(animate);
        view.setVisibility(View.GONE);
    }

    // To animate view slide out from bottom to top
    public void slideToTop(View view) {
        TranslateAnimation animate = new TranslateAnimation(0, 0, 0, -view.getHeight());
        animate.setDuration(500);
        animate.setFillAfter(true);
        view.startAnimation(animate);
        view.setVisibility(View.VISIBLE);
    }


}
