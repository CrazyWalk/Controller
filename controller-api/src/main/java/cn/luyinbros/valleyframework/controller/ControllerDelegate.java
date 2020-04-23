package cn.luyinbros.valleyframework.controller;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;

public class ControllerDelegate {
    private static Map<String, Constructor> cache = new HashMap<>();
    private static final Constructor<EmptyActivityDelegate> EMPTY_ACTIVITY;
    private static final Constructor<EmptyFragmentDelegate> EMPTY_FRAGMENT;
    private static final Constructor<EmptySimpleControllerDelegate> EMPTY_OTHER;
    private static boolean isDebug = false;

    static {
        try {
            EMPTY_ACTIVITY = EmptyActivityDelegate.class.getConstructor(AppCompatActivity.class);
            EMPTY_FRAGMENT = EmptyFragmentDelegate.class.getConstructor(Fragment.class);
            EMPTY_OTHER = EmptySimpleControllerDelegate.class.getConstructor(Object.class);
        } catch (NoSuchMethodException e) {
            throw new IllegalStateException("初始化失败", e);
        }
    }


    public static void setDebug(boolean debug) {
        isDebug = debug;
    }

    @NonNull
    public static ControllerActivityDelegate create(@NonNull AppCompatActivity activity) {
        String name = activity.getClass().getCanonicalName();
        try {
            Constructor constructor = findConstructor(activity.getClass());
            if (constructor != null) {
                return (ControllerActivityDelegate) constructor.newInstance(activity);
            } else {
                cache.put(name, EMPTY_ACTIVITY);
                return EMPTY_ACTIVITY.newInstance(activity);
            }
        } catch (IllegalAccessException | InstantiationException | InvocationTargetException e) {
            throw new IllegalStateException(activity + " not found controller delegate", e);
        }

    }

    @NonNull
    public static ControllerFragmentDelegate create(Fragment fragment) {
        String name = fragment.getClass().getCanonicalName();
        try {
            Constructor constructor = findConstructor(fragment.getClass());
            if (constructor != null) {
                return (ControllerFragmentDelegate) constructor.newInstance(fragment);
            }else{
                cache.put(name, EMPTY_FRAGMENT);
                return EMPTY_FRAGMENT.newInstance(fragment);
            }
        } catch (IllegalAccessException | InstantiationException | InvocationTargetException e) {
            throw new IllegalStateException(fragment + " not found controller delegate", e);
        }
    }

    @NonNull
    public static SimpleControllerDelegate create(Object target) {
        String name = target.getClass().getCanonicalName();
        try {
            Constructor constructor = findConstructor(target.getClass());
            if (constructor != null) {
                return (SimpleControllerDelegate) constructor.newInstance(target);
            }else{
                cache.put(name, EMPTY_OTHER);
                return EMPTY_OTHER.newInstance(target);
            }
        } catch (IllegalAccessException | InstantiationException | InvocationTargetException e) {
            throw new IllegalStateException(target + " not found controller delegate", e);
        }

    }

    @Nullable
    private static Constructor findConstructor(Class targetClass) {
        String name = targetClass.getCanonicalName();
        Constructor constructor = cache.get(name);
        if (constructor == null) {
            try {
                String className = targetClass.getName() + "_ControllerDelegate";
                ClassLoader classLoader = targetClass.getClassLoader();
                if (classLoader != null) {
                    constructor = classLoader.loadClass(className).getConstructors()[0];
                } else {
                    constructor = Class.forName(className).getConstructors()[0];
                }
                cache.put(name, constructor);
            } catch (ClassNotFoundException e) {
                Class cls = targetClass.getSuperclass();
                if (cls != null) {
                    constructor = findConstructor(targetClass.getSuperclass());
                } else {
                    return null;
                }
            }
        }
        return constructor;
    }


    private static class EmptyActivityDelegate implements ControllerActivityDelegate {

        public EmptyActivityDelegate(AppCompatActivity activity) {

        }

        @Override
        public void onCreate(@org.checkerframework.checker.nullness.qual.Nullable Bundle savedInstanceState) {

        }

        @Override
        public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {

        }

        @Override
        public void onActivityResult(int requestCode, int resultCode, @org.checkerframework.checker.nullness.qual.Nullable Intent data) {

        }

        @Override
        public void unbind() {

        }
    }


    private static class EmptyFragmentDelegate implements ControllerFragmentDelegate {

        public EmptyFragmentDelegate(Fragment fragment) {

        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            return null;
        }

        @Override
        public void onViewCreated(View view, Bundle savedInstanceState) {

        }

        @Override
        public void onRequestPermissionsResult(int requestCode,String[] permissions,  int[] grantResults) {

        }

        @Override
        public void onActivityResult(int requestCode, int resultCode, Intent data) {

        }

        @Override
        public void unbind() {

        }
    }

    private static class EmptySimpleControllerDelegate implements SimpleControllerDelegate {

        public EmptySimpleControllerDelegate(Object object) {

        }
        @Override
        public void onCreate(Context context, Bundle savedInstanceState, ViewGroup parent) {

        }

        @Override
        public void unbind() {

        }

        @Override
        public View getView() {
            return null;
        }
    }





}
