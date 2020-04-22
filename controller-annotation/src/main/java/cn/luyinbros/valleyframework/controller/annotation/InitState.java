package cn.luyinbros.valleyframework.controller.annotation;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * <per>
 * <code>
 *
 * @InitState public void initState(BuildContext buildContext)
 * @InitState public void initState()
 * <per/>
 * <code/>
 */

@Retention(RUNTIME)
@Target({METHOD})
public @interface InitState {

}
