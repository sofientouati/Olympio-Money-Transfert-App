package com.sofientouati.olympio.fragments;


import android.animation.ArgbEvaluator;
import android.animation.ValueAnimator;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.sofientouati.olympio.Activities.ConfirmTransactionActivity;
import com.sofientouati.olympio.Adapters.ActivityRecyclerViewAdapter;
import com.sofientouati.olympio.Methods;
import com.sofientouati.olympio.Objects.ActivityObject;
import com.sofientouati.olympio.R;

import io.realm.Realm;
import io.realm.RealmResults;
import it.sephiroth.android.library.viewrevealanimator.ViewRevealAnimator;

/**
 * Created by SOFIENTOUATI on 17/06/17.
 * OLYMPIO APP
 */

public class ActivityFragment extends Fragment {

    private ViewGroup viewGroup;

    private RecyclerView recyclerView;
    private ActivityRecyclerViewAdapter activityRecyclerViewAdapter;
    private RealmResults<ActivityObject> list;
    private SwipeRefreshLayout refresh;
    private TextView empty;
    private Realm realm;
    private FloatingActionButton fab;
    private ViewRevealAnimator mViewAnimator;
    private FrameLayout first, second;
    private RelativeLayout parent, colorview1, colorview2;


    private String
            red = "#C62828",
            yellow = "#F9A825",
            blue = "#0072ff";
    private ValueAnimator coloAnimator;
    private boolean isVisible, isStarted, firsts = true;
    private FragmentTransaction fragmentTransaction;
    private ConfirmTransactionActivity confirmTransactionActivity = new ConfirmTransactionActivity();
    private ActivityListFragment activityListFragment = new ActivityListFragment();
    private ValueAnimator valueAnimator;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        viewGroup = (ViewGroup) inflater.inflate(R.layout.fragment_activity, container, false);

        realm = Realm.getDefaultInstance();

        first = (FrameLayout) viewGroup.findViewById(R.id.first);
        mViewAnimator = (ViewRevealAnimator) viewGroup.findViewById(R.id.animator);
        parent = (RelativeLayout) viewGroup.findViewById(R.id.parent);
        fab = (FloatingActionButton) viewGroup.findViewById(R.id.fab);

//        colorview1 = (RelativeLayout) viewGroup.findViewById(R.id.colorview1);
//        colorview2 = (RelativeLayout) viewGroup.findViewById(R.id.colorview2);

        fragmentTransaction = getChildFragmentManager().beginTransaction();
        fragmentTransaction.add(R.id.first, activityListFragment);
        fragmentTransaction.commit();


        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getContext(), ConfirmTransactionActivity.class));
            }
        });

        setColor();
//        mViewAnimator.setOnViewAnimationListener(new ViewRevealAnimator.OnViewAnimationListener() {
//            @Override
//            public void onViewAnimationStarted(int i, int i1) {
//
//            }
//
//            @Override
//            public void onViewAnimationCompleted(int i, int i1) {
//
//                Log.i("onViewAnimationCompleted: before", String.valueOf(mViewAnimator.getHideBeforeReveal()));
//                if (firsts) {
//                    Log.i("onViewAnimationCompleted: inside", String.valueOf(mViewAnimator.getHideBeforeReveal()));
//                    mViewAnimator.setHideBeforeReveal(true);
//
//                    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP)
//                        mViewAnimator.setDisplayedChild(mViewAnimator.getDisplayedChild() + 1, true,
//                                new Point((int) fab.getX(), (int) fab.getY()));
//
//                }
//                Log.i("onViewAnimationCompleted: after", String.valueOf(mViewAnimator.getHideBeforeReveal()));
//
//                firsts = !firsts;
//                Log.i("onViewAnimationCompleted: ", String.valueOf(mViewAnimator.getDisplayedChild()));
//
//                if (mViewAnimator.getDisplayedChild() == 2)
////                    fab.setImageDrawable(getResources().getDrawable(R.drawable.ic_close_white_48dp));
//                    animateFab(R.drawable.ic_close_white_48dp);
//                if (mViewAnimator.getDisplayedChild() == 0)
////                    fab.setImageDrawable(getResources().getDrawable(R.drawable.ic_notifications_white_24dp));
//                    animateFab(R.drawable.ic_notifications_white_24dp);
//
//            }
//        });


        animateTextView(2000f, Methods.getSolde());


        return viewGroup;
    }


    //animations
    private void animateTextView(float initVal, float finalVal) {
        final ValueAnimator valueAnimator = ValueAnimator.ofFloat(initVal, finalVal);
        valueAnimator.setDuration(2000);
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                Log.e("onAnimationUpdate: ", String.valueOf(animation.getAnimatedValue()));
                if (Float.valueOf(animation.getAnimatedValue().toString()) <= Methods.getSeuil())
                    animateAppAndStatusBar(Color.parseColor(blue), Color.parseColor(red));
            }

        });
        valueAnimator.start();
    }

    private void setColor() {
        if (Methods.checkSolde()) {
            fab.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor(red)));
//            colorview1.setBackgroundColor(Color.parseColor(red));
//            colorview2.setBackgroundColor(Color.parseColor(red));
        } else {
            fab.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor(blue)));
//            colorview1.setBackgroundColor(Color.parseColor(blue));
//            colorview2.setBackgroundColor(Color.parseColor(blue));
        }
    }

    private void animateAppAndStatusBar(Integer fromColor, final Integer toColor) {
        coloAnimator = ValueAnimator.ofObject(new ArgbEvaluator(), fromColor, toColor);

        coloAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                fab.setBackgroundTintList(ColorStateList.valueOf(toColor));

            }
        });
        coloAnimator.setDuration(200);

        coloAnimator.start();

    }


    @Override
    public void onStart() {
        super.onStart();
        isStarted = true;
        if (isVisible) {
            setColor();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        isStarted = false;
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        isVisible = isVisibleToUser;
        if (isVisible && isStarted) {
//            activityRecyclerViewAdapter.notifyDataSetChanged();
            setColor();
            FragmentTransaction fragmentTransactions = getChildFragmentManager().beginTransaction();
            fragmentTransactions.detach(activityListFragment);
            fragmentTransactions.attach(activityListFragment);
            fragmentTransactions.commit();


        }
    }


    private void animateFab(final int target) {
//        valueAnimator = ValueAnimator.ofInt(0, 1).setDuration(400);
//        valueAnimator.setInterpolator(new LinearInterpolator());
//        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
//            @Override
//            public void onAnimationUpdate(ValueAnimator valueAnimator) {
        fab.setImageDrawable(getResources().getDrawable(target));
//            }
//        });
//        valueAnimator.start();
    }

}
