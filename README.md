# Controller
通过生成Controller代理类减少Activity、Fragment、其他Controller一些样板代码，尽可能的保证Activity/Fragment
在存在视图的时候稳定配置。

目前不支持继承


## 依赖
当前版本 1.0.0

## 支持的功能
* findViewById
* activity 读取intent携带的值
* fragment 读取getArguments携带的值
* 设置视图监听器
* 准确的指向创建的视图
* 准确的视图控制器的生命周期，并且可以指定次数

## 即将支持的功能
* 自动注入bean
* 获取android资源文件，包括字符串，数组等


## 使用
代理类的关系

AppCompatActivity ->  ControllerActivityDelegate

Fragment   ->  ControllerFragmentDelegate

其他类 ->  SimpleControllerDelegate



## 注意项
* 文档提到@Nullable or @Option，只要你的注解的名字是Nullable 和 Option即可，不需要关注是哪个包名的
* 兼容kotlin有些问题，等待后续修复。
## doc
* @Controller [doc](doc/Controller.md)
* @InitState [doc](doc/InitState.md)
* @BuildView [doc](doc/BuildView.md)
* @Dispose [doc](doc/Dispose.md)
* @OnPermissionResult [doc](doc/OnPermissionResult.md)
* @OnActivityResult [doc](doc/OnActivityResult.md)
* @BundleValue [doc](doc/BundleValue.md)
* @DidChangeLifecycleEvent  [doc](doc/DidChangeLifecycleEvent.md)

## Thanks
ButterKnife




