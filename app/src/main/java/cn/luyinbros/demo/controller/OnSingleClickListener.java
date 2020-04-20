package cn.luyinbros.demo.controller;

import android.view.View;

public abstract class OnSingleClickListener implements View.OnClickListener {
    private long mTime = -1;

    @Override
    public final void onClick(View v) {
        final long currentTime = System.currentTimeMillis();
        if (currentTime - mTime > 500) {
            doOnClick(v);
        }
        mTime = currentTime;
    }

    public abstract void doOnClick(View v);
}
