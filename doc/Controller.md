# @Controller
指定对象为视图控制器。
如果@Controller没有指定资源文件，那么创建视图就交给@BuildView 指定的方法进行创建
使用规则
```
@Controller([option](int layoutRes)])
(Class) (methodName) {
}

```
例子
```
TargetActivity.java

@Controller(R.layout.activity_target)
public final class TargetActivity extends ControllerDelegateActivity{
    
}