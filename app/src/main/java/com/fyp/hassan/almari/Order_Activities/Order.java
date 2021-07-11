package com.fyp.hassan.almari.Order_Activities;

import com.fyp.hassan.almari.SingleProductAvtivity.Product;

import java.util.ArrayList;
import java.util.List;

public class Order
{
    String Id;
    String paymentType;
    List<OrderProduct> pList;
    String ShippingAddress;

    public String getUserId() {
        return Id;
    }

    public void setUserId(String userId) {
        this.Id = userId;
    }

    public String getPaymentType() {
        return paymentType;
    }

    public void setPaymentType(String paymentType) {
        this.paymentType = paymentType;
    }

    public String getId() {
        return Id;
    }

    public void setId(String id) {
        Id = id;
    }

    public List<OrderProduct> getpList() {
        return pList;
    }

    public void setpList(List<OrderProduct> pList) {
        this.pList = pList;
    }

    public String getShippingAddress() {
        return ShippingAddress;
    }

    public void setShippingAddress(String shippingAddress) {
        ShippingAddress = shippingAddress;
    }

    public Order()
    {

    }

    public Order(String id, String paymentType , List<OrderProduct> orderlist,String ShipAddress)
    {
        this.Id =id;
        this.paymentType = paymentType;
        this.pList=orderlist;
        this.ShippingAddress=ShipAddress;
    }



}
