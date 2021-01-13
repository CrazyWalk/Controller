package cn.luyinbros.valleyframework.controller.annotation;

import androidx.annotation.LayoutRes;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Retention(RUNTIME)
@Target(TYPE)
public @interface Controller {
    @LayoutRes int value() default -1;

    boolean scanViewModel() default false;
}
