# @Dispose
视图已被销毁 允许多个@Dispose
```
@Controller(R.layout.activity_target)
public final class TargetActivity extends ControllerDelegateActivity{

   @Dispose
   void onViewDestroyed(){
     //view will destroy
   }
}

```