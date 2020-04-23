package cn.luyinbros.demo.activity;

import androidx.lifecycle.Lifecycle;

import cn.luyinbros.valleyframework.controller.annotation.Controller;
import cn.luyinbros.valleyframework.controller.annotation.DidChangeLifecycleEvent;
import cn.luyinbros.valleyframework.controller.annotation.Dispose;
import cn.luyinbros.demo.R;
import cn.luyinbros.demo.base.BaseActivity;
import cn.luyinbros.logger.Logger;
import cn.luyinbros.logger.LoggerFactory;


@Controller(R.layout.activity_lifecycle_event)
public class LifecycleEventActivity extends BaseActivity {
    private Logger logger = LoggerFactory.getLogger(LifecycleEventActivity.class);

    @DidChangeLifecycleEvent(Lifecycle.Event.ON_CREATE)
    void onViewCreate() {
        logger.debug("onViewCreate");
    }

//    @DidChangeLifecycleEvent(Lifecycle.Event.ON_START)
//    void onViewStart() {
//        logger.debug("onViewStart");
//    }
//
//    @DidChangeLifecycleEvent(Lifecycle.Event.ON_RESUME)
//    void onViewResume() {
//        logger.debug("onViewResume");
//    }
//
//    @DidChangeLifecycleEvent(value = Lifecycle.Event.ON_RESUME, count = 1)
//    void onViewResumeByOnce() {
//        logger.debug("onViewResumeByOnce");
//    }
//
//    @DidChangeLifecycleEvent(Lifecycle.Event.ON_PAUSE)
//    void onViewPause() {
//        logger.debug("onViewPause");
//    }
//
//    @DidChangeLifecycleEvent(Lifecycle.Event.ON_STOP)
//    void onViewStop() {
//        logger.debug("onViewStop");
//    }
//
//    @DidChangeLifecycleEvent(Lifecycle.Event.ON_DESTROY)
//    void onViewDestroy() {
//        logger.debug("onViewDestroy");
//    }

    @DidChangeLifecycleEvent(value = Lifecycle.Event.ON_ANY)
    void onAny(Lifecycle.Event event){

    }
    @Dispose
    void onDispose() {
        logger.debug("onDispose");
    }

}
