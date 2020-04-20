# @DidChangeLifecycleEvent
视图生命周期,仅仅只有在视图存在的时候才会调用

```
SampleActivity.java
public class  SampleActivity extends AppCompatActivity{

@DidChangeLifecycleEvent(Lifecycle.Event.ON_CREATE)
void onViewCreated() {
        logger.debug("onViewCreate");
}

}
```


