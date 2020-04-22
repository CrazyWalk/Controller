package cn.luyinbros.valleyframework.controller.annotation;

import androidx.annotation.IdRes;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;


@Retention(RUNTIME)
@Target(METHOD)
@ListenerClass(
        targetType = "android.view.View",
        setter = "setOnFocusChangeListener",
        remover = "",
        type = "android.view.View.OnFocusChangeListener",
        method = @ListenerMethod(
                name = "onFocusChange",
                parameters = {
                        "android.view.View",
                        "boolean"}

        )
)
public @interface OnFocusChange {
    @IdRes int[] value();
}
