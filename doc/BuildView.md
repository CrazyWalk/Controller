# @BuildView
创建控制器视图或者初始化视图 允许多个@BuildView,如果buildView方法存在View返回值，会覆盖@Controller指定的布局文件，该方法只允许一个

定义
```
1. 
void  [methodName] ([option]BuildContext,
                    [option]<T extends View>)
2.
<T extends View>  [methodName] ([option](BuildContext buildContext))
        
多个参数的需要严格按照顺序定义        
           
```
例子
```
@Controller(R.layout.activity_target)
public final class TargetActivity extends ControllerDelegateActivity{

   @BuildView
   TargetView buildView(BuildContext buildContext){
        //override Controller define layout
       return new TargetView(this);
   }

   @BuildView
   void buildView1(BuildContext buildContext,TargetView contentView){
     //initView
   }
   
   @BuildView
   void buildView2(View contentView){
     //initView
   } 
}

```