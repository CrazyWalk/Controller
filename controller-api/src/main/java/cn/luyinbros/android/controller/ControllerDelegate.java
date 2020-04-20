package cn.luyinbros.android.controller;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;

public class ControllerDelegate {
    private static Map<String, Constructor> cache = new HashMap<>();

    public static ControllerActivityDelegate create(AppCompatActivity activity) {
        try {
            return (ControllerActivityDelegate) findConstructor(activity.getClass()).newInstance(activity);
        } catch (IllegalAccessException | InstantiationException | InvocationTargetException e) {
            throw new IllegalStateException("not found controller delegate");
        }
    }


    public static ControllerFragmentDelegate create(Fragment fragment) {
        try {
            return (ControllerFragmentDelegate) findConstructor(fragment.getClass()).newInstance(fragment);
        } catch (IllegalAccessException | InstantiationException | InvocationTargetException e) {
            throw new IllegalStateException("not found controller delegate");
        }
    }

    public static SimpleControllerDelegate create(Object target) {
        try {
            return (SimpleControllerDelegate) findConstructor(target.getClass()).newInstance(target);
        } catch (IllegalAccessException | InstantiationException | InvocationTargetException e) {
            throw new IllegalStateException("not found controller delegate");
        }
    }


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
                if (cls!=null){
                    constructor = findConstructor(targetClass.getSuperclass());
                }else{
                    throw new IllegalStateException("not found controller delegate");
                }
            }
        }
        return constructor;
    }


}
