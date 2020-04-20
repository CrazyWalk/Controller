package cn.luyinbros.android.controller;

import android.app.Application;
import android.content.Context;
import android.os.Bundle;
import android.view.View;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;


public interface BuildContext {

    @NonNull
    Context getContext();

    @NonNull
    Application getApplication();

    @Nullable
    Bundle getSavedInstanceState();

    @NonNull
    View inflate(int layoutRes);

}
