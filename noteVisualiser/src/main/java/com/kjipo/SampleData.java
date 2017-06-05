package com.kjipo;

/**
 * Created by student on 10/14/14.
 */
public class SampleData {
    private String name;
    private String value;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return "name:"  +name +", value: " +value +" ";
    }
}
