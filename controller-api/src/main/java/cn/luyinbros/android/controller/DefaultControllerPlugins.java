package cn.luyinbros.android.controller;

import android.content.Context;

import androidx.annotation.BoolRes;

public class DefaultControllerPlugins implements BinderFactory{

    @Override
    public boolean getBoolean(Context context, @BoolRes int boolRes) {
        return context.getResources().getBoolean(boolRes);
    }



}
