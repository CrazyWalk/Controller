package cn.luyinbros.valleyframework.controller.annotation;


import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Retention(RUNTIME)
@Target({FIELD, METHOD})
public @interface ViewModel {
    String value() default "";

    GenerateType generateType() default GenerateType.NONE;

    ProviderType providerType() default ProviderType.NONE;


    enum GenerateType {
        /**
         * 不生成
         */
        NONE,
        /**
         * 使用默认方式生成
         */
        DEFAULT,
    }

    enum ProviderType {
        /**
         * call ViewModel#constructor
         */
        NONE,
        /**
         * by ViewModelProvider
         */
        DEFAULT,
        /**
         * by activity modelStore
         */
        ACTIVITY,
    }
}
