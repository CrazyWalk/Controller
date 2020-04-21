package cn.luyinbros.demo.kt

import cn.luyinbros.android.controller.annotation.Controller
import cn.luyinbros.android.controller.annotation.OnClick
import cn.luyinbros.demo.R
import cn.luyinbros.demo.base.BaseActivity
import cn.luyinbros.demo.controller.OnSingleClick

@Controller(R.layout.activity_kt_sample)
class KtSampleActivity : BaseActivity() {


    @OnClick(R.id.textView1)
    fun onClick() {

    }

}