package com.fyp.hassan.almari.SingleProductAvtivity;

public class attribute_class
{
    private String name,value;

    public attribute_class(String n,String v)
    {
        this.name = n;
        this.value = v;
    }

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
}
