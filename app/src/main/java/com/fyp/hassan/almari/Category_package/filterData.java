package com.fyp.hassan.almari.Category_package;

import java.util.ArrayList;

public class filterData
{
    private ArrayList<String> attributeList;
    private String attributeName;

    public filterData(ArrayList<String> attributeList, String attributeName) {
        this.attributeList = attributeList;
        this.attributeName = attributeName;
    }

    public ArrayList<String> getAttributeList() {
        return attributeList;
    }

    public void setAttributeList(ArrayList<String> attributeList) {
        this.attributeList = attributeList;
    }

    public String getAttributeName() {
        return attributeName;
    }

    public void setAttributeName(String attributeName) {
        this.attributeName = attributeName;
    }
}
