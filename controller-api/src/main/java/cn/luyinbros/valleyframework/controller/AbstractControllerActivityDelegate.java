package cn.luyinbros.valleyframework.controller;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleEventObserver;
import androidx.lifecycle.LifecycleOwner;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

public abstract class AbstractControllerActivityDelegate implements ControllerActivityDelegate{
    private AppCompatActivity sourceActivity;
    private Activity mActivity;
    private View mView;

    protected AbstractControllerActivityDelegate(AppCompatActivity activity) {
        this.sourceActivity = activity;
    }


    @Override
    public final void onCreate(@Nullable Bundle savedInstanceState) {
        this.mActivity = sourceActivity;
        final BuildContext buildContext = new BuildContextImpl(mActivity,
                savedInstanceState,
                mActivity.getLayoutInflater(),
                (ViewGroup) mActivity.findViewById(android.R.id.content));
        initState(buildContext);
        if (!mActivity.isFinishing()) {
            mView = buildView(buildContext);
            if (mView != null) {
                //buildView->initView()
                mActivity.setContentView(mView);
                sourceActivity.getLifecycle().addObserver(new LifecycleEventObserver() {
                    @Override
                    public void onStateChanged(@NonNull LifecycleOwner source, @androidx.annotation.NonNull Lifecycle.Event event) {
                        didChangeAppLifecycleState(event);
                        if (event == Lifecycle.Event.ON_DESTROY) {
                            destroy();
                        }
                    }
                });
            }
        }

    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        //empty
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        //empty
    }

    @Override
    public void setIntent(Intent data) {

    }



    @Override
    public final void unbind() {
        if (sourceActivity != null) {
            sourceActivity = null;
            mActivity = null;
        }
    }

    private void destroy() {
        if (mView != null) {
            mView = null;
            dispose();
        }
    }


    protected void initState(BuildContext buildContext) {
        //empty
    }

    @Nullable
    protected View buildView(BuildContext buildContext) {
        return null;
    }


    protected void didChangeAppLifecycleState(Lifecycle.Event event) {
        //empty
    }

    protected void dispose() {
        //empty
    }

    public final View getView() {
        return mView;
    }


}
