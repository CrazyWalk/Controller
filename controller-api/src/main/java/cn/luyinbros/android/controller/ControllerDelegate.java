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
    private static List<String> noDelegate = new ArrayList<>();
    private static boolean isDebug = false;

    public static void setDebug(boolean debug) {
        isDebug = debug;
    }

    @NonNull
    public static ControllerActivityDelegate create(@NonNull AppCompatActivity activity) {
        String name = activity.getClass().getCanonicalName();
        if (noDelegate.contains(name)) {
            return new EmptyActivityDelegate(activity);
        }
        try {
            Constructor constructor = findConstructor(activity.getClass());
            if (constructor != null) {
                return (ControllerActivityDelegate) constructor.newInstance(activity);
            }
        } catch (IllegalAccessException | InstantiationException | InvocationTargetException e) {
            if (isDebug) {
                new IllegalStateException(activity + " not found controller delegate", e).printStackTrace();
            }
        }

        noDelegate.add(name);
        return new EmptyActivityDelegate(activity);

    }

    @NonNull
    public static ControllerFragmentDelegate create(Fragment fragment) {
        String name = fragment.getClass().getCanonicalName();
        if (noDelegate.contains(name)) {
            return new EmptyFragmentDelegate(fragment);
        }
        try {
            Constructor constructor = findConstructor(fragment.getClass());
            if (constructor != null) {
                return (ControllerFragmentDelegate) constructor.newInstance(fragment);
            }
        } catch (IllegalAccessException | InstantiationException | InvocationTargetException e) {
            if (isDebug) {
                new IllegalStateException(fragment + " not found controller delegate", e).printStackTrace();
            }
        }

        noDelegate.add(name);
        return new EmptyFragmentDelegate(fragment);
    }

    @NonNull
    public static SimpleControllerDelegate create(Object target) {
        String name = target.getClass().getCanonicalName();
        if (noDelegate.contains(name)) {
            return new EmptySimpleControllerDelegate(target);
        }
        try {
            Constructor constructor = findConstructor(target.getClass());
            if (constructor != null) {
                return (EmptySimpleControllerDelegate) constructor.newInstance(target);
            }
        } catch (IllegalAccessException | InstantiationException | InvocationTargetException e) {
            if (isDebug) {
                new IllegalStateException(target + " not found controller delegate", e).printStackTrace();
            }
        }

        noDelegate.add(name);
        return new EmptySimpleControllerDelegate(target);
    }


    private static class EmptyActivityDelegate extends ControllerActivityDelegate {

        private EmptyActivityDelegate(AppCompatActivity activity) {
            super(activity);
        }


    }


    private static class EmptyFragmentDelegate extends ControllerFragmentDelegate {

        private EmptyFragmentDelegate(Fragment fragment) {
            super(fragment);
        }
    }

    private static class EmptySimpleControllerDelegate extends SimpleControllerDelegate {

        private EmptySimpleControllerDelegate(Object object) {
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
