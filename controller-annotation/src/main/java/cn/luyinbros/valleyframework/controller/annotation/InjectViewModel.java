package cn.luyinbros.valleyframework.controller.annotation;


import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;


@Retention(RUNTIME)
@Target({FIELD})
public @interface InjectViewModel {
    String value() default "";

    FactoryType generateType() default FactoryType.DEFAULT;

    ProviderType providerType() default ProviderType.DEFAULT;


    enum FactoryType {
        /**
         * 空构造器工厂
         */
        NONE,
        /**
         * 使用默认方式生成
         */
        DEFAULT,
    }

    enum ProviderType {
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
