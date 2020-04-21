package cn.luyinbros.demo.tree;


import android.widget.TextView;

import cn.luyinbros.android.controller.annotation.BindView;
import cn.luyinbros.android.controller.annotation.Controller;
import cn.luyinbros.demo.R;

@Controller
public class FActivity extends EActivity {
    @BindView(R.id.text2)
    TextView text2;
}
