package cn.luyinbros.android.controller.annotation;

import androidx.annotation.IdRes;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;


@Retention(RUNTIME)
@Target(METHOD)
@ListenerClass(
        targetType = "android.widget.CompoundButton",
        setter = "setOnCheckedChangeListener",
        remover = "",
        type = "android.widget.CompoundButton.OnCheckedChangeListener",
        method = @ListenerMethod(
                name = "onCheckedChanged",
                parameters = {"android.widget.CompoundButton",
                        "boolean"})
)
public @interface OnCheckedChanged {
    @IdRes int[] value();
}
