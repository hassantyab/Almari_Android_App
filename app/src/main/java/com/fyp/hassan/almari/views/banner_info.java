package com.fyp.hassan.almari.views;

public class banner_info
{
    private String pId,category,img,title,color;

    public banner_info(String pId,String category,String img,String title,String color)
    {
        this.pId=pId;
        this.category =category;
        this.img=img;
        this.title=title;
        this.color=color;
    }

    public String getpId() {
        return pId;
    }

    public String getCategory() {
        return category;
    }

    public String getImg() {
        return img;
    }

    public String getTitle() {
        return title;
    }

    public String getColor() {
        return color;
    }
}
