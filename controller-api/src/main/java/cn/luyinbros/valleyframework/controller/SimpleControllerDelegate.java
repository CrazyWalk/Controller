package cn.luyinbros.valleyframework.controller;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.checkerframework.checker.nullness.qual.Nullable;

public abstract class SimpleControllerDelegate {
    private Object target;
    private View mView;

    public SimpleControllerDelegate(Object target) {
        this.target = target;
    }

    public void onCreate(Context context,
                         Bundle savedInstanceState,
                         ViewGroup parent) {
        BuildContext buildContext = new BuildContextImpl(context,
                savedInstanceState,
                LayoutInflater.from(context),
                parent);
        initState(buildContext);
        mView = buildView(buildContext);
    }


    public void unbind() {
        target = null;
        if (mView != null) {
            mView = null;
            dispose();
        }
    }

    protected void dispose() {

    }


    protected void initState(BuildContext buildContext) {
        //empty
    }

    @Nullable
    protected View buildView(BuildContext buildContext) {
        return null;
    }


    public final View getView() {
        return mView;
    }
}

