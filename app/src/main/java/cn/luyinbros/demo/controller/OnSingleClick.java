package cn.luyinbros.demo.controller;

import androidx.annotation.IdRes;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import cn.luyinbros.android.controller.annotation.ListenerClass;
import cn.luyinbros.android.controller.annotation.ListenerMethod;
import kotlin.jvm.JvmDefault;
import kotlin.jvm.JvmField;
import kotlin.jvm.JvmName;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Retention(RUNTIME)
@Target(METHOD)
@ListenerClass(
        targetType = "android.view.View",
        setter = "setOnClickListener",
        remover = "",
        type = "cn.luyinbros.demo.controller.OnSingleClickListener",
        method = @ListenerMethod(
                name = "doOnClick",
                parameters = "android.view.View")
)

public @interface OnSingleClick {
    /**
     * View ID to which the field will be bound.
     */
    @IdRes int[] value();
}
