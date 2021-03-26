package cn.luyinbros.demo.viewmodel;


import android.widget.TextView;


import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleEventObserver;
import androidx.lifecycle.Observer;


import java.util.Timer;
import java.util.TimerTask;

import cn.luyinbros.demo.R;
import cn.luyinbros.demo.base.BaseActivity;
import cn.luyinbros.logger.Logger;
import cn.luyinbros.logger.LoggerFactory;
import cn.luyinbros.valleyframework.controller.annotation.InjectViewModel;
import cn.luyinbros.valleyframework.controller.annotation.BindView;
import cn.luyinbros.valleyframework.controller.annotation.Controller;
import cn.luyinbros.valleyframework.controller.annotation.DidChangeLifecycleEvent;
import cn.luyinbros.valleyframework.controller.annotation.Dispose;
import cn.luyinbros.valleyframework.controller.annotation.InitState;
import cn.luyinbros.valleyframework.controller.annotation.InitViewModel;
import cn.luyinbros.valleyframework.controller.annotation.LiveOB;

@Controller(value = R.layout.activity_live_data, scanViewModel = true)
public class LiveDataActivity extends BaseActivity {
    private final Logger logger = LoggerFactory.getLogger(LiveDataActivity.class);
    @BindView(R.id.textView)
    TextView textView;
    @InjectViewModel
    ResolveViewModel resolveViewModel;
    private Timer timer;

    @InitState
    void init() {

        timer = new Timer();
        timer.schedule(new TimerTask() {
            int index = 0;

            @Override
            public void run() {
                logger.debug("input:" + index++);
                resolveViewModel.a.postValue(index + "");
            }
        }, 0, 1000);
    }

    @InitViewModel
    void initViewModel() {

    }

    @DidChangeLifecycleEvent(value = Lifecycle.Event.ON_CREATE, count = 1)
    void initView() {
        logger.debug("initView");
        resolveViewModel.a.observe(this, (data) -> textView.setText(data));
        getLifecycle().addObserver((LifecycleEventObserver) (source, event) -> {
            if (event == Lifecycle.Event.ON_CREATE) {
                logger.debug("putObserver.create: " + textView);
            } else if (event == Lifecycle.Event.ON_DESTROY) {
                logger.debug("putObserver.destroy: " + textView);
            }
        });
    }


    @DidChangeLifecycleEvent(value = Lifecycle.Event.ON_DESTROY)
    void exitView() {
        logger.debug("exitView:" + textView);
    }

    @Dispose
    void dispose() {
        timer.cancel();
    }


    @LiveOB("resolveViewModel.a")
    void onAChanged(String a) {

    }

//    @LiveOB("resolveViewModel.getB()")
//    void onBChanged(String b) {
//
//    }


    public ResolveViewModel getResolveViewModel() {
        return resolveViewModel;
    }
}
