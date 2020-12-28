package cn.luyinbros.demo.tree;


import android.view.View;
import android.widget.TextView;

import cn.luyinbros.valleyframework.controller.annotation.BindView;
import cn.luyinbros.valleyframework.controller.annotation.BuildView;
import cn.luyinbros.valleyframework.controller.annotation.Controller;
import cn.luyinbros.demo.R;

@Controller
public class FActivity extends EActivity {
    @BindView(R.id.text2)
    TextView text2;


}
