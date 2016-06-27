package com.barelabor.barelabor.data.model;

import java.util.List;

/**
 * Created by mac on 5/5/2016.
 */
public class FeatureModel extends DataObject{

    private List<String> featureList;

    public List<String> getFeatureList() {
        return featureList;
    }

    public void setFeatureList(List<String> featureList) {
        this.featureList = featureList;
    }
}
