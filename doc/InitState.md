# @InitState
初始化Controller状态，此时视图没有创建，允许多个@InitState，并且提供一个BuildContext作为初始化的帮助
定义
```
void  (methodName)([option](BuildContext buildContext) )
```
例子
```
@Controller(R.layout.activity_target)
public final class TargetActivity extends ControllerDelegateActivity{

    @InitState
   void initState(){
     //init
   }
   
   @InitState
   void initState2(BuildContext buildContext){
     //init2
   }
}

```
