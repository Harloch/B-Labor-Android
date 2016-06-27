package com.barelabor.barelabor.data.model;

import java.util.List;

/**
 * Created by mac on 6/5/2016.
 */
public class PriceModel extends DataObject{

    private List<String> priceList;
    private String ratingJSONString;
    public List<String> getPriceList() {
        return priceList;
    }
    public String getRatingString() {
        return ratingJSONString;
    }
    public void setPriceList(List<String> priceList) {
        this.priceList = priceList;
    }
    public void setRatingString(String ratingJSONString) {
        this.ratingJSONString = ratingJSONString;
    }
}