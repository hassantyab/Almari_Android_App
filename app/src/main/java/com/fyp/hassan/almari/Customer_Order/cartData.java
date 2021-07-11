package com.fyp.hassan.almari.Customer_Order;

public class cartData
{
    String productId;
    String Quantity;

    public cartData(String productId,String Quantity)
    {
        this.productId=productId;
        this.Quantity=Quantity;
    }

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public String getQuantity() {
        return Quantity;
    }

    public void setQuantity(String quantity) {
        Quantity = quantity;
    }
}
