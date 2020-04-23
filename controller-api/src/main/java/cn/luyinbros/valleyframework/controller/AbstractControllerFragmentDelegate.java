package cn.luyinbros.valleyframework.controller;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleEventObserver;
import androidx.lifecycle.LifecycleOwner;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

public abstract class AbstractControllerFragmentDelegate implements ControllerFragmentDelegate{
    private Fragment sourceFragment;
    private Fragment mFragment;
    private BuildContext mBuildContext;
    private View mView;

    public AbstractControllerFragmentDelegate(Fragment fragment) {
        this.sourceFragment = fragment;
    }



    @Nullable
    @Override
    public final View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mFragment = sourceFragment;
        mBuildContext = new BuildContextImpl(mFragment.requireContext(),
                savedInstanceState,
                inflater,
                container);
        initState(mBuildContext);
        mView = buildView(mBuildContext);
        return mView;
    }


    @Override
    public final void onViewCreated(@androidx.annotation.NonNull View view, @androidx.annotation.Nullable Bundle savedInstanceState) {

        mFragment.getViewLifecycleOwner().getLifecycle().addObserver(new LifecycleEventObserver() {
            @Override
            public void onStateChanged(@androidx.annotation.NonNull LifecycleOwner source, @androidx.annotation.NonNull Lifecycle.Event event) {
                didChangeAppLifecycleState(event);
                if (event == Lifecycle.Event.ON_DESTROY) {
                    destroy();
                }
            }
        });
        mBuildContext = null;
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
    public final void unbind() {
        if (sourceFragment != null) {
            sourceFragment = null;
            mFragment = null;
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
