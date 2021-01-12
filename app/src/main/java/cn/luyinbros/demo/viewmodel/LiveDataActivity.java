package cn.luyinbros.demo.viewmodel;

import android.annotation.SuppressLint;
import android.widget.TextView;


import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import java.util.Timer;
import java.util.TimerTask;

import cn.luyinbros.demo.R;
import cn.luyinbros.demo.base.BaseActivity;
import cn.luyinbros.valleyframework.controller.annotation.BindView;
import cn.luyinbros.valleyframework.controller.annotation.Controller;
import cn.luyinbros.valleyframework.controller.annotation.DidChangeLifecycleEvent;
import cn.luyinbros.valleyframework.controller.annotation.Dispose;
import cn.luyinbros.valleyframework.controller.annotation.InitState;
import cn.luyinbros.valleyframework.controller.annotation.LiveOB;

//@Controller(R.layout.activity_live_data)
public class LiveDataActivity extends BaseActivity {
    @BindView(R.id.textView)
    TextView textView;
    ResolveViewModel resolveViewModel = new ResolveViewModel();
    private Timer timer;

    @InitState
    void init() {
        timer = new Timer();
//        timer.schedule(new TimerTask() {
//            int index = 0;
//
//            @Override
//            public void run() {
//                data.postValue((index++) + "");
//            }
//        }, 0, 1000);
    }

    @DidChangeLifecycleEvent(value = Lifecycle.Event.ON_CREATE, count = 1)
    void initView() {
        System.out.println("initView");
    }

    @Dispose
    void dispose() {
        timer.cancel();
    }


    @LiveOB("resolveViewModel:a")
    void onAChanged(String a) {

    }

    @LiveOB("resolveViewModel:b")
    void onBChanged(String b) {

    }


    public ResolveViewModel getResolveViewModel() {
        return resolveViewModel;
    }
}
