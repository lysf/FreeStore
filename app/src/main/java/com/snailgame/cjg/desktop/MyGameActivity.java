package com.snailgame.cjg.desktop;

import android.os.Bundle;
import android.support.v4.app.FixedSupportV4BugFragmentActivity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.RelativeLayout;

import com.snailgame.cjg.R;
import com.snailgame.cjg.desktop.fragment.DeskGameFragment;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by sunxy on 2014/9/1.
 */
public class MyGameActivity extends FixedSupportV4BugFragmentActivity implements View.OnTouchListener {
    @Bind(R.id.root_view)
    RelativeLayout rootView;
    @Bind(R.id.myGameContainer)
    RelativeLayout myGameContainer;
    private DeskGameFragment deskGameFragment;
    private float x, y;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mygame);
        ButterKnife.bind(this);
        rootView.setOnTouchListener(this);
        deskGameFragment = new DeskGameFragment();
        getSupportFragmentManager().beginTransaction().add(R.id.myGameContainer, deskGameFragment)
                .commitAllowingStateLoss();

    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                x = event.getX();
                y = event.getY();
                if (x < myGameContainer.getLeft() || x > myGameContainer.getRight() || y < myGameContainer.getTop()
                        || y > myGameContainer.getBottom()) {
                    finish();
                    return true;
                }
                break;
        }
        return false;
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(0, 0);//无动画
    }

    @Override
    public void onBackPressed() {
        if (deskGameFragment != null && deskGameFragment.isEditEable()) {
            deskGameFragment.cancleEditMode();
        } else {
            if (getSupportFragmentManager().getBackStackEntryCount() > 0)
                getSupportFragmentManager().popBackStack();
            else {
                super.onBackPressed();
            }
        }
    }
}