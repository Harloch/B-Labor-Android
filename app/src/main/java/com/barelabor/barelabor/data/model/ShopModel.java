package com.barelabor.barelabor.data.model;

import java.util.List;

/**
 * Created by mac on 4/5/2016.
 */
public class ShopModel extends DataObject{

    private List<ShopObject> shopList;

    public List<ShopObject> getShopList() {
        return shopList;
    }

    public void setShopList(List<ShopObject> shopList) {
        this.shopList = shopList;
    }
}
