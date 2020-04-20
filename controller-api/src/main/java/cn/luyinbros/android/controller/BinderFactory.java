package cn.luyinbros.android.controller;

import android.content.Context;

import androidx.annotation.IdRes;

public interface BinderFactory {

    boolean getBoolean(Context context, @IdRes int boolRes);
}
