package cn.luyinbros.android.controller.annotation;

import androidx.annotation.IdRes;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;


@Retention(RUNTIME)
@Target(METHOD)
@ListenerClass(
        targetType = "android.widget.TextView",
        setter = "setOnEditorActionListener",
        remover = "",
        type = "android.widget.TextView.OnEditorActionListener",
        method = @ListenerMethod(
                name = "onEditorAction",
                parameters = {
                        "android.widget.TextView",
                        "int",
                        "android.view.KeyEvent"},
                defaultReturn = "false",
                returnType = "boolean"
        )
)
public @interface OnEditorAction {
    @IdRes int[] value();
}
