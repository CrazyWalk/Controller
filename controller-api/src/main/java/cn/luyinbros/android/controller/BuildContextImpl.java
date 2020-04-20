package cn.luyinbros.android.controller;

import android.app.Application;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

public final class BuildContextImpl implements BuildContext {
    private final Context mContext;
    private final Bundle mSavedInstanceState;
    private final LayoutInflater mInflater;
    private final ViewGroup mParent;

    public BuildContextImpl(Context context,
                            Bundle savedInstanceState,
                            LayoutInflater inflater,
                            ViewGroup parent) {
        this.mContext = context;
        this.mSavedInstanceState = savedInstanceState;
        this.mInflater = inflater;
        this.mParent = parent;
    }

    @NonNull
    @Override
    public Context getContext() {
        return mContext;
    }

    @NonNull
    @Override
    public Application getApplication() {
        return (Application) mContext.getApplicationContext();
    }

    @Nullable
    @Override
    public Bundle getSavedInstanceState() {
        return mSavedInstanceState;
    }

    @NonNull
    @Override
    public View inflate(int layoutRes) {
        return mInflater.inflate(layoutRes, mParent, false);
    }
}
