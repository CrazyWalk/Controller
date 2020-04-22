package cn.luyinbros.demo.base;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import cn.luyinbros.android.controller.ControllerDelegate;
import cn.luyinbros.android.controller.ControllerFragmentDelegate;
import cn.luyinbros.logger.Logger;
import cn.luyinbros.logger.LoggerFactory;

public class BaseFragment extends Fragment {
    private ControllerFragmentDelegate mDelegate = ControllerDelegate.create(this);
    private Logger logger = LoggerFactory.getLogger(BaseFragment.class);

    {
        logger.debug(" " + mDelegate.getClass());
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return mDelegate.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        mDelegate.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        mDelegate.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        mDelegate.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mDelegate.unbind();
    }
}
