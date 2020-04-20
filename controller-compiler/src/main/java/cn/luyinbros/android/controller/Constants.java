package cn.luyinbros.android.controller;

import androidx.lifecycle.Lifecycle;

import com.squareup.javapoet.ClassName;

public class Constants {
    public static final String PACKAGE_COMMON = "cn.luyinbros.common";
    public static final String PACKAGE_LIBRARY = "cn.luyinbros.android";
    public static final String PACKAGE_CONTROLLER_LIBRARY = PACKAGE_LIBRARY + ".controller";


    public static final String TYPE_VIEW = "android.view.View";
    public static final String TYPE_ACTIVITY = "android.app.Activity";
    public static final String TYPE_FRAGMENT = "androidx.fragment.app.Fragment";
    public static final String TYPE_BUILD_CONTEXT = PACKAGE_CONTROLLER_LIBRARY + ".BuildContext";
    public static final String TYPE_INTENT = "android.content.Intent";
    public static final String TYPE_BUNDLE = "android.os.Bundle";
    public static final String TYPE_LIFECYCLE_EVENT = Lifecycle.Event.class.getCanonicalName();
    public static final String TYPE_STRING = "java.lang.String";
    public static final String TYPE_LISTENER_CLASS_ANNOTATION = PACKAGE_CONTROLLER_LIBRARY + ".annotation.ListenerClass";
    public static final String TYPE_PARCELABLE="android.os.Parcelable";
    public static final String TYPE_SERIALIZABLE="java.io.Serializable";
    public static final String TYPE_CHAR_SEQUENCE="java.lang.CharSequence";
    public static final String TYPE_INTEGER="java.lang.Integer";
    public static final String TYPE_ARRAY_LIST="java.util.ArrayList";


    public static final ClassName CLASS_DELEGATE_ACTIVITY = ClassName.get(PACKAGE_CONTROLLER_LIBRARY, "ControllerActivityDelegate");
    public static final ClassName CLASS_DELEGATE_FRAGMENT = ClassName.get(PACKAGE_CONTROLLER_LIBRARY, "ControllerFragmentDelegate");
    public static final ClassName CLASS_DELEGATE_COMMON = ClassName.get(PACKAGE_CONTROLLER_LIBRARY, "SimpleControllerDelegate");
    public static final ClassName INTERFACE_BUILD_CONTEXT = ClassName.get(PACKAGE_CONTROLLER_LIBRARY, "BuildContext");
    public static final ClassName CLASS_CONTROLLER_HELPER = ClassName.get(PACKAGE_CONTROLLER_LIBRARY, "Controllers");
    public static final ClassName ENUM_LIFECYCLE_EVENT = ClassName.get(Lifecycle.Event.class);
    public static final ClassName CLASS_OBJECTS = ClassName.get("java.util", "Objects");
    public static final ClassName CLASS_STRING = ClassName.get("java.lang", "String");

    //sdk
    public static final ClassName ANNOTATION_UI_THREAD = ClassName.get("androidx.annotation", "UiThread");
    public static final ClassName ANNOTATION_KEEP = ClassName.get("androidx.annotation", "Keep");
    public static final ClassName INTERFACE_CLICK_LISTENER = ClassName.get("android.view.View", "OnClickListener");
    public static final ClassName CLASS_VIEW = ClassName.get("android.view", "View");
    public static final ClassName CLASS_INTENT = ClassName.get("android.content", "Intent");
    public static final ClassName CLASS_BUNDLE = ClassName.bestGuess(TYPE_BUNDLE);
}
