package cn.luyinbros.demo.mock;

import android.os.Bundle;
import android.os.Parcelable;

import java.util.ArrayList;

import cn.luyinbros.demo.data.ParcelableObject;
import cn.luyinbros.demo.data.SerializableObject;

public class Mock {

    public static Bundle testBundle() {
        char charValue = 'a';
        byte byteValue = 1;
        int intValue = 2;
        long longValue = 3L;
        float floatValue = 4.0f;
        double doubleValue = 5.0f;
        boolean booleanValue = false;
        String stringValue = "string";
        SerializableObject serializableValue = new SerializableObject("serializableValue");
        ParcelableObject parcelableValue = new ParcelableObject("parcelableValue");
        CharSequence charSequenceValue = "charSequence";


        char[] charArrayValue = {'a', 'b'};
        byte[] byteArrayValue = {1, 2};
        int[] intArrayValue = {3, 4};
        long[] longArrayValue = {5L, 6L};
        float[] floatArrayValue = {7.0f, 8.0f};
        double[] doubleArrayValue = {9.0f, 10f};
        boolean[] booleanArrayValue = {true, false};
        String[] stringArrayValue = {"string1", "string2"};
        CharSequence[] charSequenceArrayValue = {"charSequence1", "charSequence2"};
        Parcelable[] parcelableArrayKey = {
                new ParcelableObject("parcelableValue1"),
                new ParcelableObject("parcelableValue2")
        };


        ArrayList<Integer> integerArrayListValue = new ArrayList<>();
        integerArrayListValue.add(1);
        integerArrayListValue.add(2);

        ArrayList<String> stringArrayListValue = new ArrayList<>();
        stringArrayListValue.add("string1");
        stringArrayListValue.add("string2");


        ArrayList<CharSequence> charSequenceListValue = new ArrayList<>();
        charSequenceListValue.add("charSequence1");
        charSequenceListValue.add("charSequence2");

        ArrayList<ParcelableObject> parcelableListValue = new ArrayList<>();
        parcelableListValue.add(new ParcelableObject("parcelableValue1"));
        parcelableListValue.add(new ParcelableObject("parcelableValue2"));

        ArrayList<SerializableObject> serializableObjectList = new ArrayList<>();
        serializableObjectList.add(new SerializableObject("serializableValue1"));
        serializableObjectList.add(new SerializableObject("serializableValue2"));

        Bundle bundle = new Bundle();
        bundle.putChar("charValue", charValue);
        bundle.putByte("byteKey", byteValue);
        bundle.putInt("intKey", intValue);
        bundle.putLong("longKey", longValue);
        bundle.putFloat("floatKey", floatValue);
        bundle.putDouble("doubleKey", doubleValue);
        bundle.putBoolean("booleanKey", booleanValue);
        bundle.putString("stringKey", stringValue);
        bundle.putSerializable("serializableKey", serializableValue);
        bundle.putParcelable("parcelableKey", parcelableValue);
        bundle.putCharSequence("charSequenceKey", charSequenceValue);
        bundle.putCharArray("charArrayKey", charArrayValue);
        bundle.putByteArray("byteArrayKey", byteArrayValue);
        bundle.putIntArray("intArrayKey", intArrayValue);
        bundle.putLongArray("longArrayKey", longArrayValue);
        bundle.putFloatArray("floatArrayKey", floatArrayValue);
        bundle.putDoubleArray("doubleArrayKey", doubleArrayValue);
        bundle.putBooleanArray("booleanArrayKey", booleanArrayValue);
        bundle.putStringArray("stringArrayKey", stringArrayValue);
        bundle.putCharSequenceArray("charSequenceArrayKey", charSequenceArrayValue);
        bundle.putParcelableArray("parcelableArrayKey", parcelableArrayKey);
        bundle.putIntegerArrayList("integerArrayListKey", integerArrayListValue);
        bundle.putStringArrayList("stringArrayListKey", stringArrayListValue);
        bundle.putCharSequenceArrayList("charSequenceListKey", charSequenceListValue);
        bundle.putParcelableArrayList("parcelableListKey", parcelableListValue);
        bundle.putSerializable("SerializableListKey", serializableObjectList);
        bundle.putBundle("bundleKey", new Bundle());
        return bundle;
    }
}
