package cn.luyinbros.valleyframework.controller.annotation;

import androidx.lifecycle.Lifecycle;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Retention(RUNTIME)
@Target({METHOD})
public @interface DidChangeLifecycleEvent {

    Lifecycle.Event value() default Lifecycle.Event.ON_ANY;

    int count() default 0;
}
