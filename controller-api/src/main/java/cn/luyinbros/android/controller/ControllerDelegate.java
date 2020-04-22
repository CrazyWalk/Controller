package cn.luyinbros.android.controller;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
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


    private static class EmptyActivityDelegate extends ControllerActivityDelegate {

        public EmptyActivityDelegate(AppCompatActivity activity) {
            super(activity);
        }

    }


    private static class EmptyFragmentDelegate extends ControllerFragmentDelegate {

        public EmptyFragmentDelegate(Fragment fragment) {
            super(fragment);
        }
    }

    private static class EmptySimpleControllerDelegate extends SimpleControllerDelegate {

        public EmptySimpleControllerDelegate(Object object) {
            super(object);
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


}
