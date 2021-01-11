package cn.luyinbros.demo.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.view.View;


import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.Arrays;

import cn.luyinbros.valleyframework.controller.annotation.BuildView;
import cn.luyinbros.valleyframework.controller.annotation.BundleValue;
import cn.luyinbros.valleyframework.controller.annotation.Controller;
import cn.luyinbros.valleyframework.controller.annotation.InitState;
import cn.luyinbros.valleyframework.controller.annotation.Option;
import cn.luyinbros.demo.base.BaseActivity;
import cn.luyinbros.demo.data.ParcelableObject;
import cn.luyinbros.demo.data.SerializableObject;
import cn.luyinbros.logger.Logger;
import cn.luyinbros.logger.LoggerFactory;

//@Controller
public class BundleValueActivity extends BaseActivity {
    private Logger logger = LoggerFactory.getLogger(BundleValueActivity.class);

    @BundleValue("charKey")
    char charValue;
    @BundleValue("byteKey")
    byte byteValue;
    @BundleValue("intKey")
    int intValue;
    @BundleValue("longKey")
    long longValue;
    @BundleValue("floatKey")
    float floatValue;
    @BundleValue("doubleKey")
    double doubleValue;
    @BundleValue("booleanKey")
    boolean booleanValue;
    @BundleValue("stringKey")
    String stringValue;
    @BundleValue("serializableKey")
    SerializableObject serializableValue;
    @BundleValue("parcelableKey")
    ParcelableObject parcelableValue;
    @BundleValue("charSequenceKey")
    CharSequence charSequenceValue;


    @Option
    @BundleValue("charArrayKey")
    char[] charArrayValue;
    @BundleValue("byteArrayKey")
    byte[] byteArrayValue;
    @BundleValue("intArrayKey")
    int[] intArrayValue;
    @BundleValue("longArrayKey")
    long[] longArrayValue;
    @BundleValue("floatArrayKey")
    float[] floatArrayValue;
    @BundleValue("doubleArrayKey")
    double[] doubleArrayValue;
    @BundleValue("booleanArrayKey")
    boolean[] booleanArrayValue;
    @BundleValue("stringArrayKey")
    String[] stringArrayValue;
    @BundleValue("charSequenceArrayKey")
    CharSequence[] charSequenceArrayValue;
    @BundleValue("parcelableArrayKey")
    Parcelable[] parcelableArrayKey;


    @BundleValue("integerArrayListKey")
    ArrayList<Integer> integerArrayListValue;
    @BundleValue("stringArrayListKey")
    ArrayList<String> stringArrayListValue;
    @BundleValue("charSequenceListKey")
    ArrayList<CharSequence> charSequenceListValue;
    @BundleValue("parcelableListKey")
    ArrayList<ParcelableObject> parcelableListValue;
    @BundleValue("SerializableListKey")
    ArrayList<SerializableObject> serializableObjectList;
    @BundleValue("bundleKey")
    Bundle bundleValue;

    @InitState
    void initState() {
        logger.debug(toString());
    }


    @NonNull
    @Override
    public String toString() {
        return "BundleValueActivity{" +
                "charValue=" + charValue +
                ", byteValue=" + byteValue +
                ", intValue=" + intValue +
                ", longValue=" + longValue +
                ", floatValue=" + floatValue +
                ", doubleValue=" + doubleValue +
                ", booleanValue=" + booleanValue +
                ", stringValue='" + stringValue + '\'' +
                ", serializableValue=" + serializableValue +
                ", parcelableValue=" + parcelableValue +
                ", charSequenceValue=" + charSequenceValue +
                ", charArrayValue=" + Arrays.toString(charArrayValue) +
                ", byteArrayValue=" + Arrays.toString(byteArrayValue) +
                ", intArrayValue=" + Arrays.toString(intArrayValue) +
                ", longArrayValue=" + Arrays.toString(longArrayValue) +
                ", floatArrayValue=" + Arrays.toString(floatArrayValue) +
                ", doubleArrayValue=" + Arrays.toString(doubleArrayValue) +
                ", booleanArrayValue=" + Arrays.toString(booleanArrayValue) +
                ", stringArrayValue=" + Arrays.toString(stringArrayValue) +
                ", charSequenceArrayValue=" + Arrays.toString(charSequenceArrayValue) +
                ", parcelableArrayKey=" + Arrays.toString(parcelableArrayKey) +
                ", integerArrayListValue=" + integerArrayListValue +
                ", stringArrayListValue=" + stringArrayListValue +
                ", charSequenceListValue=" + charSequenceListValue +
                ", parcelableListValue=" + parcelableListValue +
                ", serializableObjectList=" + serializableObjectList +
                ", bundle=" + bundleValue +
                '}';
    }
}
