package cn.luyinbros.demo.fragment;

import android.Manifest;

import java.util.Arrays;

import cn.luyinbros.valleyframework.controller.Controllers;
import cn.luyinbros.valleyframework.controller.annotation.Controller;
import cn.luyinbros.valleyframework.controller.annotation.OnPermissionResult;
import cn.luyinbros.valleyframework.controller.annotation.OnTextChanged;
import cn.luyinbros.demo.R;
import cn.luyinbros.demo.base.BaseFragment;
import cn.luyinbros.demo.controller.OnSingleClick;
import cn.luyinbros.logger.Logger;
import cn.luyinbros.logger.LoggerFactory;

//@Controller(R.layout.activity_rx_permisison)
public class OnPermissionResultFragment extends BaseFragment {

    private Logger logger = LoggerFactory.getLogger(OnPermissionResultFragment.class);
    private int requestCode = 1;
    private String[][] permissions = new String[][]{
            new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
            new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
            new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
            new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
            new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
            new String[]{Manifest.permission.READ_CONTACTS},
            new String[]{Manifest.permission.READ_CONTACTS}

    };

    @OnSingleClick(R.id.requestButton)
    void onRequestClick() {
        if (requestCode >= 1) {
            if (requestCode <= 7) {
               requestPermissions(permissions[requestCode], requestCode);
            } else {
                requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, requestCode);
            }
        }
    }

    @OnTextChanged(value = R.id.requestCodeEditText,
            callBack = OnTextChanged.CallBack.TEXT_CHANGED)
    void onRequestCodeChanged(CharSequence s, int start, int before, int count) {
        try {
            requestCode = Integer.parseInt(s.toString());
        } catch (Exception e) {
            requestCode = 1;
        }
    }


    @OnPermissionResult(1)
    void onPermissionResult1() {
        logger.debug("request code 1 " + "granted");
    }

    @OnPermissionResult(2)
    void onPermissionResult2(int grantResult) {
        if (grantResult == Controllers.PERMISSION_GRANTED) {
            logger.debug("request code 2 : granted");
        } else if (grantResult == Controllers.PERMISSION_DENIED) {
            logger.debug("request code 2 : denied");
        } else if (grantResult == Controllers.PERMISSION_DENIED_APP_OP) {
            logger.debug("request code 2 : deniedAppOP");
        }
    }

    @OnPermissionResult(3)
    void onPermissionResult3(int[] grantResult) {
        logger.debug("request code 3 : " + Arrays.toString(grantResult));
    }

    @OnPermissionResult(4)
    void onPermissionResult4(int requestCode, String[] permissions, int grantResult) {
        logger.debug("request code 4 : " + Arrays.toString(permissions) + " " + grantResult);
    }

    @OnPermissionResult(5)
    void onPermissionResult5(int requestCode, String[] permissions, int[] grantResults) {
        logger.debug("request code 5 : " + Arrays.toString(permissions) + " " + Arrays.toString(grantResults));
    }


    @OnPermissionResult(value = 6, permissions = {Manifest.permission.READ_CONTACTS})
    void onPermissionResult6() {
        logger.debug("request code 6 : " + "granted");
    }

    @OnPermissionResult(permissions = {Manifest.permission.READ_CONTACTS})
    void onPermissionResult7() {
        logger.debug("onPermissionResult7 : " + "granted");
    }

    @OnPermissionResult
    void onPermissionResult8() {
        logger.debug("onPermissionResult8 : " + "granted");
    }

    @OnPermissionResult(after = false)
    void onPermissionResult9(int requestCode, String[] permissions, int[] grantResults) {
        logger.debug("onPermissionResult");
    }


}
