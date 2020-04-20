package cn.luyinbros.android.controller.annotation;

import androidx.annotation.IdRes;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;


@Retention(RUNTIME)
@Target(METHOD)
@ListenerClass(
        targetType = "android.view.View",
        setter = "setOnLongClickListener",
        remover = "",
        type = "android.view.View.OnLongClickListener",
        method = @ListenerMethod(
                name = "onLongClick",
                parameters = {
                        "android.view.View"},
                defaultReturn = "false",
                returnType = "boolean"
        )
)
public @interface OnLongClick {
    @IdRes int[] value();
}
