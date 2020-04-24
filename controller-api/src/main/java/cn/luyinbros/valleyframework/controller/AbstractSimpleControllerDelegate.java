package cn.luyinbros.valleyframework.controller;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.checkerframework.checker.nullness.qual.Nullable;

public abstract class AbstractSimpleControllerDelegate implements SimpleControllerDelegate {
    private Object target;
    private View mView;

    protected AbstractSimpleControllerDelegate(Object target) {
        this.target = target;
    }

    @Override
    public final void onCreate(Context context,
                               Bundle savedInstanceState,
                               ViewGroup parent) {
        BuildContext buildContext = new BuildContextImpl(context,
                savedInstanceState,
                LayoutInflater.from(context),
                parent);
        initState(buildContext);
        mView = buildView(buildContext);
    }


    @Override
    public final void unbind() {
        target = null;
        if (mView != null) {
            mView = null;
            dispose();
        }
    }

    @Override
    public final View getView() {
        return mView;
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


}

