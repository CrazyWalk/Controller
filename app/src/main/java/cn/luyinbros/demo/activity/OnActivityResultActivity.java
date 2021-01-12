package cn.luyinbros.demo.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import cn.luyinbros.valleyframework.controller.annotation.Controller;
import cn.luyinbros.valleyframework.controller.annotation.OnActivityResult;
import cn.luyinbros.valleyframework.controller.annotation.OnTextChanged;
import cn.luyinbros.demo.R;
import cn.luyinbros.demo.base.BaseActivity;
import cn.luyinbros.demo.controller.OnSingleClick;
import cn.luyinbros.logger.Logger;
import cn.luyinbros.logger.LoggerFactory;

@Controller(R.layout.activity_on_result)
public class OnActivityResultActivity extends BaseActivity {
    private Logger logger = LoggerFactory.getLogger(OnActivityResultActivity.class);
    private int requestCode = 1;


    @OnSingleClick(R.id.requestButton)
    void onRequestClick() {
        if (requestCode >= 1) {
            startActivityForResult(new Intent(this, StubActivity.class)
                            .putExtra("requestCode", requestCode),
                    requestCode);
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


    @OnActivityResult(1)
    void onActivityResult1() {
        logger.debug("requestCode 1 success");
    }

    @OnActivityResult(2)
    void onActivityResult2(Intent data) {
        logger.debug("requestCode 2 success and has data");
    }

    @OnActivityResult(value = 3, resultCode = -2)
    void onActivityResult3(int resultCode) {
        logger.debug("requestCode 3 resultCode: " + resultCode);
    }

    @OnActivityResult(value = 4, resultCode = -2)
    void onActivityResult4(int resultCode, Intent data) {
        logger.debug("requestCode 4 resultCode: " + resultCode + " has data");
    }

    @OnActivityResult(5)
    void onActivityResult5(Intent data, int resultCode) {
        logger.debug("requestCode 5 resultCode: " + resultCode + " has data");
    }


    @OnActivityResult
    void onActivityResult6(int requestCode, int resultCode, @Nullable Intent data) {
        logger.debug("any requestCode: " + requestCode + " resultCode -1" + " extra:" + data);
    }

    @OnActivityResult(resultCode = -2, after = false)
    void onActivityResult7(int requestCode, int resultCode, @Nullable Intent data) {
        logger.debug("before requestCode " + requestCode + " resultCode: " + resultCode + " extra: " + data);
    }

    @OnActivityResult(resultCode = -2, after = false)
    void onActivityResult8() {
        logger.debug("onActivityResult");
    }

    @OnActivityResult(resultCode = -2)
    void onActivityResult9(int requestCode, int resultCode, @Nullable Intent data) {
        logger.debug("final requestCode " + requestCode + " resultCode: " + resultCode + " extra: " + data);
    }


    public static class StubActivity extends AppCompatActivity {


        @Override
        protected void onCreate(@Nullable Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            Intent intent = getIntent();
            int requestCode = intent.getIntExtra("requestCode", 1);
            if (requestCode == 1) {
                setResult(Activity.RESULT_OK);
            } else if (requestCode == 2) {
                setResult(Activity.RESULT_OK, new Intent());
            } else if (requestCode == 3) {
                setResult(3);
            } else if (requestCode == 4) {
                setResult(3, new Intent());
            } else if (requestCode == 5) {
                setResult(3, new Intent());
            }
            finish();

        }


    }
}
