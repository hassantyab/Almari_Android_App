package com.fyp.hassan.almari.Order_Activities;

public class OrderProduct
{
    String productId;
    String quantity;

    public OrderProduct(String pid,String quan)
    {
        this.productId=pid;
        this.quantity=quan;
    }

    public String getProductid() {
        return productId;
    }

    public void setProductid(String productid) {
        this.productId = productid;
    }

    public String getQuantity() {
        return quantity;
    }

    public void setQuantity(String quantity) {
        this.quantity = quantity;
    }
}
