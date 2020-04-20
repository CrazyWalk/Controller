# OnActivityResult
方便处理Activity和Fragment的onActivityResult回调
下面简单的例子
```
  @OnActivityResult(1)
  void onActivityResult1() {
     // handle requestCode==1&&resultCode=-1  success
  }
```
## 使用说明
@OnActivityResult 提供了三个参数
### value
跳转activity 的 requestCode

如果requestCode大于0，那onActivityResult的requestCode等于改值时被注解的方法会被处理

### resultCode
activity resultCode ,那onActivityResult的resultCode等于改值时被注解的方法会被处理

### after
被注解的方法执行在判断requestCode之前还是之后


被注解的方法提供多种声明的方式

无参数
```
  @OnActivityResult(1)
  void onPermissionResult() {
    // handle requestCode==1&&resultCode=-1  success
}
```

一个参数
```
 @OnActivityResult(1)
  void onPermissionResult(int resultCode ) {
    
}

```
or
```
  @OnActivityResult(1)
  void onPermissionResult(Intent data) {
       // handle data!=null
}

```


三参数：

```
@OnPermissionResult
void onPermissionResult(int requestCode ,int resultCode,@Nullable Intent data) {
  
  
}

```


## 处理顺序
如果@OnActivityResult 仅仅是after为false

会将该方法放置在最前面执行

接下来处理requestCode > 0

接着处理 resultCode >=-1

最后处理 剩下的方法

```

 @OnActivityResult(after=false)
 void onBefore(){
 
 }
 
 @OnActivityResult(1)
 void hasRequestCode(){
 
 }

  @OnActivityResult(resultCode=0)
  void hasResultCode() {

  }
  
 @OnPermissionResult
 void onLast(){
 
 }
 
最终的结果
public void onActivityResult(int requestCode ,int resultCode,@Nullable Intent data){
      onBefore();
      if(requestCode==1){
           //handle hasRequestCode()
      }else{
          if(resultCode==0){
                //handle hasResultCode()
          }else{
             onLast()
          }
      }
       
      
}


```