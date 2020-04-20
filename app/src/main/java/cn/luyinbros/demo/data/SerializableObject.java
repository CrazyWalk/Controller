package cn.luyinbros.demo.data;

import java.io.Serializable;

public class SerializableObject implements Serializable {
    private String name;

    public SerializableObject(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "SerializableObject{" +
                "name='" + name + '\'' +
                '}';
    }
}
