package com.fyp.hassan.almari.SingleProductAvtivity;

/**
 * Created by Hassan on 26-Dec-17.
 */

public class productList
{
    //for Glider class

    String title,company,price;
    String image;

    public productList(String title, String company, String price, String image) {
        this.title = title;
        this.company = company;
        this.price = price;
        this.image = image;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getCompany() {
        return company;
    }

    public void setCompany(String company) {
        this.company = company;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }
}
