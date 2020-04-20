package cn.luyinbros.android.controller.annotation;

import androidx.annotation.IntRange;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;


/**
 * @see android.app.Activity#onRequestPermissionsResult(int, String[], int[]) (int, int, android.content.Intent)
 * @see androidx.fragment.app.Fragment#onRequestPermissionsResult(int, String[], int[]) (int, int, android.content.Intent)
 */
@Retention(RUNTIME)
@Target(METHOD)
public @interface OnPermissionResult {
    /**
     * requestPermission requestCode
     *
     * @return 如果 &lt 1 则代表接收任意 requestCode
     */
    @IntRange(from = 1) int value()  default 0;

    /**
     * request permission list
     *
     * @return 你需要请求的权限 如果为空代表接受任何权限返回结果
     */
    String[] permissions() default {};




    boolean after() default true;
}
