package cn.luyinbros.valleyframework.controller;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

public interface ControllerFragmentDelegate {
    /**
     * 初始化及视图创建
     *
     * @see Fragment#onCreateView(LayoutInflater, ViewGroup, Bundle)
     */
    View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState);

    /**
     * 视图已被创建
     * @see  Fragment#onViewCreated(View, Bundle)
     */
    void onViewCreated(View view, Bundle savedInstanceState);

    /**
     * 接收onRequestPermissionsResult 回调
     *
     * @see Fragment#onRequestPermissionsResult(int, String[], int[])
     */
    void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults);

    /**
     * onActivityResult 回调
     *
     * @see Fragment#onActivityResult(int, int, Intent)
     */
    void onActivityResult(int requestCode, int resultCode, @Nullable Intent data);

    /**
     * 解除fragment与delegate的关系
     *
     * @see Fragment#onDetach()
     */
    void unbind();


}
