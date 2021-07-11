package com.fyp.hassan.almari.User_Management.feedback_package;

public class reviewData
{
    private String status,reviewId,reviewString,reviewTitle,stars,userId,productId;

    public reviewData(String status,String reviewId,String reviewString,String  reviewTitle, String stars,String userId, String productId)
    {
        this.status=status;
        this.reviewId=reviewId;
        this.reviewString=reviewString;
        this.reviewTitle=reviewTitle;
        this.stars=stars;
        this.userId=userId;
        this.productId=productId;
    }


    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getReviewId() {
        return reviewId;
    }

    public void setReviewId(String reviewId) {
        this.reviewId = reviewId;
    }

    public String getReviewString() {
        return reviewString;
    }

    public void setReviewString(String reviewString) {
        this.reviewString = reviewString;
    }

    public String getReviewTitle() {
        return reviewTitle;
    }

    public void setReviewTitle(String reviewTitle) {
        this.reviewTitle = reviewTitle;
    }

    public String getStars() {
        return stars;
    }

    public void setStars(String stars) {
        this.stars = stars;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }
}
