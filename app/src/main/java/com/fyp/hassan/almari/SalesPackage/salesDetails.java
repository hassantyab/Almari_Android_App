package com.fyp.hassan.almari.SalesPackage;

public class salesDetails
{
    private String id,salesPercentage,salesImage;
    public salesDetails(String id, String salesPercentage, String salesImage)
    {
        this.id=id;
        this.salesPercentage =salesPercentage;
        this.salesImage = salesImage;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getSalesPercentage() {
        return salesPercentage;
    }

    public void setSalesPercentage(String salesPercentage) {
        this.salesPercentage = salesPercentage;
    }

    public String getSalesImage() {
        return salesImage;
    }

    public void setSalesImage(String salesImage) {
        this.salesImage = salesImage;
    }
}
