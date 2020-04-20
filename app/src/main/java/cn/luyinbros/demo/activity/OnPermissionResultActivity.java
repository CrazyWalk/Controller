package cn.luyinbros.demo.activity;

import cn.luyinbros.android.controller.annotation.Controller;
import cn.luyinbros.android.controller.annotation.OnPermissionResult;
import cn.luyinbros.demo.R;
import cn.luyinbros.demo.base.BaseActivity;

@Controller(R.layout.activity_rx_permisison)
public class OnPermissionResultActivity extends BaseActivity {

//    private Logger logger = LoggerFactory.getLogger(OnPermissionResultActivity.class);
//
//    @OnSingleClick(R.id.requestPermissionText)
//    void doRequestPermissionClick() {
//        ActivityCompat.requestPermissions(this,
//                new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
//    }

    //
//    /***********************基本参数****************************/
//
    @OnPermissionResult(1)
    void onPermissionResult1() {
        //logger.debug("onPermissionResult1" + "granted");
    }
//
//    @OnPermissionResult(1)
//    void onPermissionResult2(int grantResult) {
//        if (grantResult == ControllerHelper.PERMISSION_GRANTED) {
//            logger.debug("onPermissionResult2: granted");
//        } else if (grantResult == ControllerHelper.PERMISSION_DENIED) {
//            logger.debug("onPermissionResult2: denied");
//        } else if (grantResult == ControllerHelper.PERMISSION_DENIED_APP_OP) {
//            logger.debug("onPermissionResult2: deniedAppOP");
//        }
//    }
//
//    @OnPermissionResult(3)
//    void onPermissionResult3(int[] grantResult) {
//
//    }
//
//    @OnPermissionResult(4)
//    void onPermissionResult4(int requestCode, String[] permissions, int grantResult) {
//
//    }
//
//    @OnPermissionResult(5)
//    void onPermissionResult5(int requestCode, String[] permissions, int[] grantResults) {
//
//    }
//
//
//    @OnPermissionResult(value = 6, permissions = {Manifest.permission.READ_CONTACTS})
//    void onPermissionResult6() {
//
//    }
//
//
//
//    @OnPermissionResult(permissions = {Manifest.permission.READ_CONTACTS})
//    void onPermissionResult7() {
//
//    }
//
//    @OnPermissionResult
//    void onPermissionResult8() {
//
//    }
//
//    @OnPermissionResult(after = false)
//    void onPermissionResult9(int requestCode, String[] permissions, int[] grantResults) {
//
//    }


}
