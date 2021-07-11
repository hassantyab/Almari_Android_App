package com.fyp.hassan.almari.views;

import java.util.List;

public class banner_data
{
    private String bannerId;
    private List<banner_info> banner_info;

    public banner_data( String bannerId, List<banner_info> banner_info)
    {
        this.bannerId=bannerId;
        this.banner_info=banner_info;
    }


    public String getBannerId() {
        return bannerId;
    }

    public List<banner_info> getBanner_info() {
        return banner_info;
    }
}
