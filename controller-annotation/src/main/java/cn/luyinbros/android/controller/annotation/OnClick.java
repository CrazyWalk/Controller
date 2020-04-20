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
        setter = "setOnClickListener",
        remover = "",
        type = "android.view.View.OnClickListener",
        method = @ListenerMethod(
                name = "onClick",
                parameters = "android.view.View")
)
public @interface OnClick {
    /**
     * View ID to which the field will be bound.
     */
    @IdRes int[] value();
}
