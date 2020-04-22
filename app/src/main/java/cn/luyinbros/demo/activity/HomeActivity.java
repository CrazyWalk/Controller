package cn.luyinbros.demo.activity;


import android.content.Intent;

import cn.luyinbros.valleyframework.controller.annotation.Controller;
import cn.luyinbros.demo.R;
import cn.luyinbros.demo.base.BaseActivity;
import cn.luyinbros.demo.controller.OnSingleClick;
import cn.luyinbros.demo.mock.Mock;
import cn.luyinbros.demo.tree.RootActivity;
import cn.luyinbros.logger.Logger;
import cn.luyinbros.logger.LoggerFactory;

@Controller(R.layout.activity_home)
public class HomeActivity extends BaseActivity {
    private Logger logger = LoggerFactory.getLogger(HomeActivity.class);


    @OnSingleClick(R.id.demoButton)
    void doDemoClick() {
        logger.debug("doDemoClick");
    }


    @OnSingleClick(R.id.bindViewButton)
    void doBindViewClick() {
        startActivity(new Intent(this, BindViewActivity.class));
    }

    @OnSingleClick(R.id.bundleButton)
    void doBundleClick() {
        startActivity(new Intent(this, BundleValueActivity.class)
                .putExtras(Mock.testBundle()));
    }

    @OnSingleClick(R.id.activityResultButton)
    void doActivityResultClick() {
        startActivity(new Intent(this, OnActivityResultActivity.class));
    }


    @OnSingleClick(R.id.permissionResultButton)
    void doPermissionResultClick() {
        startActivity(new Intent(this, OnPermissionResultActivity.class));
    }


    @OnSingleClick(R.id.lifecycleButton)
    void doLifecycleClick() {
        startActivity(new Intent(this, LifecycleEventActivity.class));
    }


    @OnSingleClick(R.id.fragmentOrOtherButton)
    void doFragmentOrOtherClick() {
        startActivity(new Intent(this, FragmentAndOtherActivity.class));
    }

    @OnSingleClick(R.id.treeButton)
    void doTreeClick() {
        startActivity(new Intent(this, RootActivity.class));
    }

}
