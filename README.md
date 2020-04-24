# Valley-Controller
通过生成Controller代理类减少Activity、Fragment、其他Controller一些样板代码，尽可能的保证Activity/Fragment
在存在视图的时候稳定配置。

## 依赖
当前版本 1.0.2

## 支持的功能
* findViewById
* activity 读取intent携带的值
* fragment 读取getArguments携带的值
* 配置视图监听器
* 准确的视图控制器的生命周期，并且可以指定次数
* OnPermissionResult回调注解
* OnActivityResult回调注解


## 即将支持的功能
* 自动注入bean

## 考虑支持的功能
* 获取android资源文件，包括字符串，数组等

## 即将修复的问题
* 自定义listenerClass无法获取默认值。。

## 安装
根目录的build.gradle
```
allprojects {
    repositories {
        ......        
        maven { url 'https://jitpack.io' }
    }
}

```
依赖
```
implementation 'com.github.CrazyWalk:Controller:{latestVersion}'
annotationProcessor 'com.github.CrazyWalk:Controller:{latestVersion}'
```

## 使用
代理类的关系

AppCompatActivity ->  ControllerActivityDelegate

Fragment   ->  ControllerFragmentDelegate

其他类 ->  SimpleControllerDelegate

```

public abstract class BaseActivity extends AppCompatActivity {
    private ControllerActivityDelegate mDelegate = ControllerDelegate.create(this);

    @Override
    protected final void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mDelegate.onCreate(savedInstanceState);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        mDelegate.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        mDelegate.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }
    
    @Override
    protected void onDestroy() {
        super.onDestroy();
        //解除delegate 和 activity之间的关系
        mDelegate.unbind();
    }


}

//自动配置 activity_sample
@Controller(R.layout.activity_sample)
public abstract class SampleActivity extends BaseActivity {
        
}
```

## Library projects
这里我们暂时使用ButterKnife插件。

To use Butter Knife in a library, add the plugin to your buildscript:
```
buildscript {
  repositories {
    mavenCentral()
    google()
   }
  dependencies {
    classpath 'com.jakewharton:butterknife-gradle-plugin:10.2.1'
  }
}
```
and then apply it in your module:
```
apply plugin: 'com.android.library'
apply plugin: 'com.jakewharton.butterknife'
```
Now make sure you use R2 instead of R inside all Butter Knife annotations.
```
class ExampleActivity extends Activity {
  @BindView(R2.id.user) EditText username;
  @BindView(R2.id.pass) EditText password;
...
}
```



## 注意项
* 文档提到@Nullable or @Option，只要你的注解的名字是Nullable 和 Option即可，不需要关注是哪个包名的
* kapt 在遇到注解值是数组的时候，会创建一个新的数组给processor解析，导致R.xx.xx这种形式无法解析，ButterKnife也有这个问题。等待后续兼容。

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
ButterKnife [source](https://github.com/JakeWharton/butterknife)

## License
<pre>
Copyright (c) 2020, Andy Hong. All rights reserved.

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in
all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.
</pre>

ButterKnife R2-Plugins

<pre>
Copyright 2013 Jake Wharton

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

   http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.

</pre>
