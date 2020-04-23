package cn.luyinbros.valleyframework.controller;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;

public interface SimpleControllerDelegate {

    /**
     * 视图创建
     */
    void onCreate(Context context,
                  Bundle savedInstanceState,
                  ViewGroup parent);

    /**
     * 解除关联
     */
    void unbind();

    /**
     * 获取创建的视图
     *
     * @return 返回创建的视图
     */
    View getView();
}
