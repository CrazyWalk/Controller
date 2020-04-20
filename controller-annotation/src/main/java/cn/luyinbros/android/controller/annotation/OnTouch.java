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
        setter = "setOnTouchListener",
        remover = "",
        type = "android.view.View.OnTouchListener",
        method = @ListenerMethod(
                name = "onTouch",
                parameters = {
                        "android.view.View",
                "android.view.MotionEvent"},
                defaultReturn = "false",
                returnType = "boolean"
        )
)
public @interface OnTouch {
    @IdRes int[] value();
}
