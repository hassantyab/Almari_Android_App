package com.fyp.hassan.almari.SingleProductAvtivity;


import java.util.ArrayList;
import java.util.Comparator;

public class Product {

    private String Id, Title, Description, Quantity, Price, BrandName, CategoryName, subCategoryName,numOfStars;

    ArrayList<String> Imgs = new ArrayList<>();


    public String getId() {
        return Id;
    }

    public void setId(String id) {
        Id = id;
    }

    public String getTitle() {
        return Title;
    }

    public void setTitle(String title) {
        Title = title;
    }

    public String getDescription() {
        return Description;
    }

    public void setDescription(String description) {
        Description = description;
    }

    public String getQuantity() {
        return Quantity;
    }

    public void setQuantity(String quantity) {
        Quantity = quantity;
    }

    public String getPrice() {
        return Price;
    }

    public void setPrice(String price) {
        Price = price;
    }

    public String getBrandName() {
        return BrandName;
    }

    public void setBrandName(String brandName) {
        BrandName = brandName;
    }

    public String getCategoryName() {
        return CategoryName;
    }

    public void setCategoryName(String categoryName) {
        CategoryName = categoryName;
    }

    public String getSubCategoryName() {
        return subCategoryName;
    }

    public static Comparator<Product> ratingComparator_asc = new Comparator<Product>() {
        @Override
        public int compare(Product p1, Product p2) {
            return (Double.parseDouble(p1.getNumOfStars()) < Double.parseDouble(p2.getNumOfStars()) ? -1 :
                    (p1.getNumOfStars() == p2.getNumOfStars() ? 0 : 1));
        }
    };

    public static Comparator<Product> ratingComparator_desc = new Comparator<Product>() {
        @Override
        public int compare(Product p1, Product p2) {
            return (Double.parseDouble(p1.getNumOfStars()) > Double.parseDouble(p2.getNumOfStars()) ? -1 :
                    (p1.getNumOfStars() == p2.getNumOfStars() ? 0 : 1));
        }
    };

    public static Comparator<Product> priceComparator_asc = new Comparator<Product>() {
        @Override
        public int compare(Product p1, Product p2) {
            return (Double.parseDouble(p1.getPrice()) < Double.parseDouble(p2.getPrice()) ? -1 :
                    (p1.getPrice() == p2.getPrice() ? 0 : 1));
        }
    };

    public static Comparator<Product> priceComparator_desc = new Comparator<Product>() {
        @Override
        public int compare(Product p1, Product p2) {
            return (Double.parseDouble(p1.getPrice()) > Double.parseDouble(p2.getPrice()) ? -1 :
                    (p1.getPrice() == p2.getPrice() ? 0 : 1));
        }
    };


    public void setSubCategoryName(String subCategoryName) {
        this.subCategoryName = subCategoryName;
    }

    public ArrayList<String> getImgs() {
        return Imgs;
    }

    public void setImgs(ArrayList<String> imgs) {
        Imgs = imgs;
    }

    public String getNumOfStars() {
        return numOfStars;
    }

    public void setNumOfStars(String numOfStars) {
        this.numOfStars = numOfStars;
    }

    public Product(String id, String title, String description, String quantity, String price, String brandName, String CategoryName, String subcatName, ArrayList<String> imgs, String numOfStars )
    {
        this.Id=id;
        this.Title=title;
        this.Description=description;
        this.Quantity=quantity;
        this.Price=price;
        this.BrandName=brandName;
        this.CategoryName=CategoryName;
        this.subCategoryName=subcatName;
        this.Imgs=imgs;
        this.numOfStars=numOfStars;


    }

}