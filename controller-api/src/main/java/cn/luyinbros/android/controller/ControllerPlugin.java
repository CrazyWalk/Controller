package cn.luyinbros.android.controller;

import android.app.Activity;
import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.drawable.Drawable;
import android.view.View;

import androidx.annotation.ArrayRes;
import androidx.annotation.BoolRes;
import androidx.annotation.ColorRes;
import androidx.annotation.DimenRes;
import androidx.annotation.DrawableRes;
import androidx.annotation.IdRes;
import androidx.annotation.IntegerRes;
import androidx.annotation.StringRes;

import org.checkerframework.checker.nullness.qual.NonNull;

public interface ControllerPlugin {

    boolean isGranted(int[] grantResults);

    int getPermissionResult(@NonNull Activity activity, @NonNull String[] permissions, @NonNull int[] grantResults);

    <T extends View> T findViewById(@NonNull View parent, @IdRes int id);

    <T extends View> T requiredViewById(@NonNull View parent, @IdRes int id);

    int getInteger(Context context, @IntegerRes int id);

    int getColor(Context context, @ColorRes int id);

    ColorStateList getColorStateList(Context context, @ColorRes int id);

    Drawable getDrawable(Context context, @DrawableRes int id);

    String getString(Context context, @StringRes int id);

    boolean getBoolean(Context context, @BoolRes int id);

    float getFloat(Context context,@DimenRes int id);

    int[] getIntArray(Context context,@ArrayRes int id);

    String[] getStringArray(Context context,@ArrayRes int id);
}
