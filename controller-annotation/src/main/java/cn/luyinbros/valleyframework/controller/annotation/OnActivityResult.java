package cn.luyinbros.valleyframework.controller.annotation;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;


/**
 *
 * @see android.app.Activity#onActivityResult(int, int, android.content.Intent)
 * @see androidx.fragment.app.Fragment#onActivityResult(int, int, android.content.Intent)
 */
@Retention(RUNTIME)
@Target(METHOD)
public @interface OnActivityResult {

    /**
     * startActivity requestCode
     *
     * @return 如果小于1 则代表接收任意 requestCode
     */
    int value() default 0;

    /**
     * RESULT_CANCELED:0
     * RESULT_OK:-1
     * RESULT_FIRST_USER:1
     *
     * @return 如果小于- 1 则代表接收任意 resultCode
     * @see android.app.Activity
     */
    int resultCode() default -1;


    boolean after() default true;

}
