# @BundleValue
获取跳转过来的activity intent的数据和fragment getArguments数据，对应类型调用的方法与
原API一致。

```
SampleActivity.java

public class  SampleActivity extends AppCompatActivity{
   @BundleValue("intKey")
   int intValue;
}


SampleFragment.java

public class  SampleFragment extends Fragment{
   @BundleValue("intKey")
   int intValue;
}

```

## 使用
如果是Primitive类型的，默认值为你说定义的值

```

public class  SampleActivity extends AppCompatActivity{
   //如果intValue没有获取的到，则值仍然是3
   @BundleValue("intKey")
   int intValue=3;
}



```
如果是object类型的，默认不能为空，且默认值是你指定的值

```

public class  SampleActivity extends AppCompatActivity{
   //如果intValue没有获取的到，则值仍然是"abc"
   @BundleValue("stringKey")
   String stringValue="abc";
   //如果charSequenceValue没有获取到值，则抛出空指针异常。
   @BundleValue("charSequenceKey")
   String charSequenceValue;

}

```
如果你确定这个值有可能为null，则可以加上@Nullable 或者@Option
```
public class  SampleActivity extends AppCompatActivity{
   //该值允许为空，需要开发者注意
   @Nullable @BundleValue("charSequenceKey")
   String charSequenceValue;

}

```

## 支持的方法
| activity |  fragment |
|:---------|:----|
|getBooleanExtra(String name, boolean defaultValue)            |    public boolean getBoolean(String key, boolean defaultValue)|
|public byte getByteExtra(String name, byte defaultValue)     |   public Byte getByte(String key, byte defaultValue)|
|public short getShortExtra(String name, short defaultValue)   |public short getShort(String key, short defaultValue);|
|public char getCharExtra(String name, char defaultValue)      | public char getChar(String key, char defaultValue);|
|public int getIntExtra(String name, int defaultValue)         | public int getInt(String key, int defaultValue)|
|public long getLongExtra(String name, long defaultValue)      | public long getLong(String key, long defaultValue)|
|public float getFloatExtra(String name, float defaultValue)   | public float getFloat(String key, float defaultValue)|
|public double getDoubleExtra(String name, double defaultValue) | public double getDouble(String key, double defaultValue)|
|public @Nullable String getStringExtra(String name)      |     public String getString(@Nullable String key)|
|public @Nullable CharSequence getCharSequenceExtra(String name))    | public CharSequence getCharSequence(@Nullable String key)|
|public @Nullable <T extends Parcelable> T getParcelableExtra(String name)   |  public <T extends Parcelable> T getParcelable(@Nullable String key)|
| public @Nullable Serializable getSerializableExtra(String name)     |   public Serializable getSerializable(@Nullable String key)|
|public @Nullable ArrayList<Integer> getIntegerArrayListExtra(String name)  | public ArrayList<Integer> getIntegerArrayList(@Nullable String key)|
| public @Nullable ArrayList<String> getStringArrayListExtra(String name)   | public ArrayList<String> getStringArrayList(@Nullable String key)|
|public @Nullable ArrayList<CharSequence> getCharSequenceArrayListExtra(String name) | public ArrayList<CharSequence> getCharSequenceArrayList(@Nullable String key)|
|public @Nullable <T extends Parcelable> ArrayList<T> getParcelableArrayListExtra(String name)  | public <T extends Parcelable> ArrayList<T> getParcelableArrayList(@Nullable String key)|
| public @Nullable boolean[] getBooleanArrayExtra(String name)   | public boolean[] getBooleanArray(@Nullable String key)|
| public @Nullable byte[] getByteArrayExtra(String name)        | public byte[] getByteArray(@Nullable String key)|
|public @Nullable short[] getShortArrayExtra(String name)     |  public short[] getShortArray(@Nullable String key)|
|public @Nullable char[] getCharArrayExtra(String name)       |   public char[] getCharArray(@Nullable String key)|
|public @Nullable int[] getIntArrayExtra(String name)       | public int[] getIntArray(@Nullable String key)|
|public @Nullable long[] getLongArrayExtra(String name)     |  public long[] getLongArray(@Nullable String key)|
|public @Nullable float[] getFloatArrayExtra(String name)   | public float[] getFloatArray(@Nullable String key)|
|public @Nullable double[] getDoubleArrayExtra(String name)  |   public double[] getDoubleArray(@Nullable String key)|
|public  String[] getStringArrayExtra(String name)          | public String[] getStringArray(@Nullable String key)|
|public @Nullable CharSequence[] getCharSequenceArrayExtra(String name)  | public CharSequence[] getCharSequenceArray(@Nullable String key)|
|public @Nullable Parcelable[] getParcelableArrayExtra(String name)   | public Parcelable[] getParcelableArray(@Nullable String key) |
|public @Nullable Bundle getBundleExtra(String name)             |   public Bundle getBundle(@Nullable String key)|
|public @Nullable <T extends Parcelable> T getParcelableExtra(String name)    |   public <T extends Parcelable> T getParcelable(@Nullable String key)|
|public boolean hasExtra(String name)          | public boolean containsKey(String key) |





