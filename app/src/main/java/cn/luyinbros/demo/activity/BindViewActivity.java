package cn.luyinbros.demo.activity;


import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.TextView;

import cn.luyinbros.valleyframework.controller.annotation.Controller;
import cn.luyinbros.valleyframework.controller.annotation.OnCheckedChanged;
import cn.luyinbros.valleyframework.controller.annotation.OnClick;
import cn.luyinbros.valleyframework.controller.annotation.OnEditorAction;
import cn.luyinbros.valleyframework.controller.annotation.OnFocusChange;
import cn.luyinbros.valleyframework.controller.annotation.OnLongClick;
import cn.luyinbros.valleyframework.controller.annotation.OnTextChanged;
import cn.luyinbros.valleyframework.controller.annotation.OnTouch;
import cn.luyinbros.demo.R;
import cn.luyinbros.demo.base.BaseActivity;
import cn.luyinbros.demo.controller.OnSingleClick;
import cn.luyinbros.logger.Logger;
import cn.luyinbros.logger.LoggerFactory;


@Controller(R.layout.activity_bindview)
public class BindViewActivity extends BaseActivity {
    private Logger logger = LoggerFactory.getLogger(BindViewActivity.class);

    @OnClick({R.id.clickButton})
    void onClickButtonClick(Button button) {
        logger.debug("onClickButtonClick " + button.toString());
    }

    @OnSingleClick({R.id.selfClickButton})
    void onSelfClickButtonClick() {
        logger.debug("onSelfClickButtonClick");
    }

    @OnTextChanged(value = R.id.editTextView,
            callBack = OnTextChanged.CallBack.TEXT_CHANGED)
    void onEditTextTextChanged(CharSequence s, int start, int before, int count) {
        logger.debug("onEditTextTextChanged: " + s);
    }

    @OnLongClick(R.id.longClickButton)
    void onLongClick() {
        logger.debug("onLongClick");
    }

    @OnTouch(R.id.touchButton)
    boolean onTextView1Touch(View v, MotionEvent motionEvent) {
        logger.debug("touch:" + motionEvent.getAction());
        return false;
    }

    @OnFocusChange(R.id.focusEditText)
    void onFocusEditTextFocusChange(View view, boolean hasFocus) {
        logger.debug("onFocusEditTextFocusChange hasFocus:" + hasFocus);
    }

    @OnCheckedChanged(R.id.checkbox)
    void onCheckBoxCheckedChanged(CompoundButton button, boolean isChecked) {
        logger.debug("onCheckBoxCheckedChanged isChecked:" + isChecked);
    }

    @OnEditorAction(R.id.focusEditText)
    boolean onFocusEditTextOnEditor(TextView textView,
                                    int actionId,
                                    KeyEvent event) {
        if (actionId == EditorInfo.IME_ACTION_DONE) {
            logger.debug("onFocusEditTextOnEditor IME_ACTION_DONE");
        }
        return true;
    }


    @OnTextChanged(value = R.id.focusEditText)
    TextWatcher onTextView2Changed2() {
        return new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                logger.debug("onTextView2Changed2:" + "beforeTextChanged");
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                logger.debug("onTextView2Changed2:" + "onTextChanged");
            }

            @Override
            public void afterTextChanged(Editable s) {
                logger.debug("onTextView2Changed2:" + "afterTextChanged");
            }
        };
    }
}
