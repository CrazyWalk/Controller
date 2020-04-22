package cn.luyinbros.valleyframework.controller.annotation;

import androidx.annotation.IdRes;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;


@Retention(RUNTIME)
@Target(METHOD)
@ListenerClass(
        targetType = "android.widget.TextView",
        setter = "addTextChangedListener",
        remover = "removeTextChangedListener",
        type = "android.text.TextWatcher",
        callbacks = OnTextChanged.CallBack.class
)
public @interface OnTextChanged {
    @IdRes int[] value();

    CallBack callBack() default CallBack.NONE;


    enum CallBack {

        NONE,

        /**
         * {@link TextWatcher#onTextChanged(CharSequence, int, int, int)}
         */
        @ListenerMethod(
                name = "onTextChanged",
                parameters = {
                        "java.lang.CharSequence",
                        "int",
                        "int",
                        "int"
                }
        )
        TEXT_CHANGED,

        /**
         * {@link TextWatcher#beforeTextChanged(CharSequence, int, int, int)}
         */
        @ListenerMethod(
                name = "beforeTextChanged",
                parameters = {
                        "java.lang.CharSequence",
                        "int",
                        "int",
                        "int"
                }
        )
        BEFORE_TEXT_CHANGED,

        /**
         * {@link TextWatcher#afterTextChanged(android.text.Editable)}
         */
        @ListenerMethod(
                name = "afterTextChanged",
                parameters = "android.text.Editable"
        )
        AFTER_TEXT_CHANGED,
    }

}
