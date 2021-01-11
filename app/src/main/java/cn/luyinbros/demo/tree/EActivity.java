package cn.luyinbros.demo.tree;


import android.widget.TextView;

import cn.luyinbros.valleyframework.controller.annotation.BindView;
import cn.luyinbros.valleyframework.controller.annotation.BuildView;
import cn.luyinbros.valleyframework.controller.annotation.Controller;
import cn.luyinbros.demo.R;

//@Controller
public class EActivity extends DActivity {
    @BindView(R.id.text1)
    TextView text1;

    //TODO 修复这个问题
    @BuildView
    void initView() {

    }
}
