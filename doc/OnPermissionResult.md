# @OnPermissionResult
方便处理Activity和Fragment的onRequestPermissionsResult回调

下面简单的例子
```
 @OnPermissionResult(1)
  void onPermissionResult1() {
     // handle requestCode==1 success
  }
```

## 使用说明
@OnPermissionResult 提供了三个参数
### value
请求权限的 requestCode

如果requestCode大于0，那onRequestPermissionsResult的requestCode等于改值时被注解的方法会被处理

### permissions
请求权限的列表,只有在onRequestPermissionsResult permissions 相等时才会执行

### after
被注解的方法执行在判断requestCode之前还是之后


被注解的方法提供多种声明的方式

无参数,只要onRequestPermissionsResult grantResult提供的是成功结果就会执行
```
@OnPermissionResult
  void onPermissionResult() {
     // handle success
}
```

一个参数
```
@OnPermissionResult
  void onPermissionResult(int grantResult) {
     if(grantResult==ControllerHelper.PERMISSION_GRANTED){
           //权限通过
     }else if(grantResult== ControllerHelper.PERMISSION_DENIED){
          //权限拒绝，但是下次还可以申请
     }else if(grantResult== ControllerHelper.PERMISSION_DENIED_APP_OP){
          //权限拒绝，下次无法申请
     }
}
其中 grantResult 是onRequestPermissionsResult grantResults处理后的结果。
```
or
```
@OnPermissionResult
  void onPermissionResult(int[] grantResults) {
     
}
其中 grantResults 是onRequestPermissionsResult grantResults原始结果
```


三参数：
```
@OnPermissionResult
  void onPermissionResult(int requestCode ,String[] permissions，int grantResult) {
     if(grantResult==ControllerHelper.PERMISSION_GRANTED){
           //权限通过
     }else if(grantResult== ControllerHelper.PERMISSION_DENIED){
          //权限拒绝，但是下次还可以申请
     }else if(grantResult== ControllerHelper.PERMISSION_DENIED_APP_OP){
          //权限拒绝，下次无法申请
     }
}
其中 grantResult 是onRequestPermissionsResult grantResults处理后的结果。
```
or
```
@OnPermissionResult
void onPermissionResult(int requestCode ,String[] permissions,int[] grantResults) {
     if(grantResult==ControllerHelper.PERMISSION_GRANTED){
           //权限通过
     }else if(grantResult== ControllerHelper.PERMISSION_DENIED){
          //权限拒绝，但是下次还可以申请
     }else if(grantResult== ControllerHelper.PERMISSION_DENIED_APP_OP){
          //权限拒绝，下次无法申请
     }
  
}
其中 grantResults 是onRequestPermissionsResult grantResults原始结果
```


## 处理顺序
如果@OnPermissionResult 仅仅是after为false

会将该方法放置在最前面执行

接下来处理requestCode > 0

接着处理 permissions不为空的

最后处理 剩下的方法

```

 @OnPermissionResult(after=false)
 void onBefore(){
 
 }
 
 @OnPermissionResult(1)
 void hasRequestCode(){
 
 }

  @OnPermissionResult(permissions = {Manifest.permission.READ_CONTACTS})
  void hasPermissionResult() {

  }
  
 @OnPermissionResult
 void onLast(){
 
 }
 
最终的结果
public void onRequestPermissionsResult(int requestCode, String[] permissions,
      int[] grantResults){
      onBefore();
      if(requestCode==1){
           //handle hasRequestCode()
      }else{
          if(permissions==hasPermissionResult.permissions ){
                //handle hasPermissionResult()
          }else{
             onLast()
          }
      }
        
      
}


```


